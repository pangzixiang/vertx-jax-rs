package io.github.pangzixiang.vertx.jax.rs.model;

import io.github.pangzixiang.vertx.jax.rs.annotation.Filter;
import io.github.pangzixiang.vertx.jax.rs.annotation.RequestBody;
import io.github.pangzixiang.vertx.jax.rs.filter.HttpFilter;
import io.github.pangzixiang.vertx.jax.rs.utils.CoreUtils;
import io.github.pangzixiang.vertx.jax.rs.utils.VerticleUtils;
import io.github.pangzixiang.vertx.jax.rs.ApplicationConfiguration;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.pangzixiang.vertx.jax.rs.utils.VerticleUtils.deployVerticle;

@Slf4j
@Getter
public class EndpointInfo {
    private final String name;
    private final Method method;
    private final Class<?> returnType;
    private final Vertx vertx;
    private final String uri;
    private final String httpMethod;
    private final String[] consumeTypes;
    private final String[] produceTypes;
    private final List<String> filterNames = new ArrayList<>();
    private final List<Function<RoutingContext, Object>> filterFunctions = new ArrayList<>();
    private final List<Function<RoutingContext, Object>> parameterFunctions = new ArrayList<>();
    private final BiFunction<Object, RoutingContext, Object> executeResult;

    public EndpointInfo(Vertx vertx, Method method) {
        method.setAccessible(true);
        this.returnType = method.getReturnType();
        this.name = method.getName();
        this.vertx = vertx;
        this.method = method;
        this.uri = this.registerUri();
        this.registerFilter();
        this.httpMethod = this.registerHttpMethod();
        this.consumeTypes = registerConsumeTypes();
        this.produceTypes = registerProduceTypes();
        this.registerParameterFunctions();
        this.executeResult = (instance, routingContext) -> {
            Object[] inputParams = parameterFunctions.stream()
                    .map(routingContextObjectFunction -> routingContextObjectFunction.apply(routingContext))
                    .toArray();
            return CoreUtils.invokeMethod(method, instance, inputParams);
        };
    }

    private void registerParameterFunctions() {
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.getAnnotations().length == 0) {
                if (parameter.getType() == RoutingContext.class) {
                    parameterFunctions.add(routingContext -> routingContext);
                    continue;
                }
            } else {
                if (parameter.getAnnotation(PathParam.class) != null) {
                    String pathParamName = parameter.getAnnotation(PathParam.class).value();
                    parameterFunctions.add(routingContext -> this.decodeParam(routingContext.pathParam(pathParamName), parameter.getType()));
                    continue;
                } else if (parameter.getAnnotation(QueryParam.class) != null) {
                    String queryParamName = parameter.getAnnotation(QueryParam.class).value();
                    parameterFunctions.add(routingContext -> this.decodeParam(routingContext.queryParams().get(queryParamName), parameter.getType()));
                    continue;
                } else if (parameter.getAnnotation(FormParam.class) != null) {
                    String formParamName = parameter.getAnnotation(FormParam.class).value();
                    parameterFunctions.add(routingContext -> this.decodeParam(routingContext.request().formAttributes().get(formParamName), parameter.getType()));
                    continue;
                } else if (parameter.getAnnotation(HeaderParam.class) != null) {
                    String headerName = parameter.getAnnotation(HeaderParam.class).value();
                    parameterFunctions.add(routingContext -> this.decodeParam(routingContext.request().getHeader(headerName), parameter.getType()));
                    continue;
                } else if (parameter.getAnnotation(RequestBody.class) != null) {
                    parameterFunctions.add(routingContext -> this.decodeParam(routingContext.body().asString(), parameter.getType()));
                    continue;
                }
            }
            parameterFunctions.add(routingContext -> null);
        }
    }

    private Object decodeParam(String value, Class<?> type) {
        try {
            if (type == String.class) {
                return value;
            } else {
                return Json.decodeValue(value, type);
            }
        } catch (Exception e){
            log.error("Failed to convert value [{}] to type [{}]", value, type, e);
            throw new IllegalArgumentException("Invalid HTTP Request Parameter [%s] for Type [%s]".formatted(value, type));
        }
    }

    private String[] registerProduceTypes() {
        Produces produces = method.getAnnotation(Produces.class);
        if (produces != null) {
            return produces.value();
        }
        return null;
    }

    private String[] registerConsumeTypes() {
        Consumes consumes = method.getAnnotation(Consumes.class);
        if (consumes != null) {
            return consumes.value();
        }
        return null;
    }

    private String registerUri() {
        String uri = method.getAnnotation(Path.class).value();
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        return uri;
    }

    private void registerFilter() {
        Filter filterAnnotation = method.getAnnotation(Filter.class);
        if (filterAnnotation != null) {
            Class<? extends HttpFilter>[] httpFilters = filterAnnotation.filter();
            if (httpFilters.length > 0) {
                Arrays.stream(httpFilters)
                        .forEach(httpFilter -> {
                            try {
                                Method doFilter = httpFilter.getMethod("doFilter", RoutingContext.class);
                                if (!Modifier.isAbstract(doFilter.getModifiers())) {
                                    Object filterInstance = CoreUtils.createInstance(httpFilter);
                                    VerticleUtils.deployVerticle(vertx, (AbstractVerticle) filterInstance)
                                            .onFailure(failure -> {
                                                log.error(failure.getMessage(), failure);
                                                System.exit(-1);
                                            });
                                    doFilter.setAccessible(true);
                                    this.filterFunctions.add(routingContext -> CoreUtils.invokeMethod(doFilter, filterInstance, routingContext));
                                    this.filterNames.add(httpFilter.getName());
                                } else {
                                    log.warn("Skip Invalid Filter [{}]!", httpFilter.getSimpleName());
                                }
                            } catch (NoSuchMethodException e) {
                                log.warn("Skip Invalid Filter [{}]!", httpFilter.getSimpleName(), e);
                            }
                        });
            }
        }
    }

    private String registerHttpMethod() {
        Optional<Annotation> methodOptional = Arrays.stream(method.getAnnotations())
                .filter(annotation -> ApplicationConfiguration.getInstance().getSupportHttpMethodsList().contains(annotation.annotationType()))
                .findFirst();
        if (methodOptional.isPresent()) {
            Annotation annotation = methodOptional.get();
            jakarta.ws.rs.HttpMethod httpMethodAnnotation = annotation.annotationType().getAnnotation(jakarta.ws.rs.HttpMethod.class);
            if (httpMethodAnnotation != null) {
                return httpMethodAnnotation.value();
            }
        }
        return null;
    }
}
