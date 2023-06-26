package io.github.pangzixiang.whatsit.vertx.jax.rs.local.controller;

import io.github.pangzixiang.whatsit.vertx.jax.rs.annotation.Filter;
import io.github.pangzixiang.whatsit.vertx.jax.rs.annotation.RequestBody;
import io.github.pangzixiang.whatsit.vertx.jax.rs.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.jax.rs.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.jax.rs.controller.TestPojo;
import io.github.pangzixiang.whatsit.vertx.jax.rs.local.filter.EchoFilter;
import io.github.pangzixiang.whatsit.vertx.jax.rs.local.filter.TestFilter;
import io.github.pangzixiang.whatsit.vertx.jax.rs.model.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Echo controller.
 */
@Slf4j
@Path("/v1")
@RestController
public class EchoController extends BaseController {
    /**
     * Instantiates a new Echo controller.
     *
     * @param router the router
     */
    public EchoController(Router router) {
        super(router);
    }

    @Override
    public void start() throws Exception {
        getRouter().route().handler(BodyHandler.create());
        getRouter().route().handler(SessionHandler.create(LocalSessionStore.create(getVertx())));
        super.start();
    }

    /**
     * Echo test http response.
     *
     * @return the http response
     */
    @Path("/echoTest")
    @GET
    @Filter(filter = {EchoFilter.class, TestFilter.class})
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse echoTest() {
        log.info("Echo Controller handle request!");
        return HttpResponse.builder().status(HttpResponseStatus.OK).data("echo").build();
    }

    /**
     * Echo test header http response.
     *
     * @param test the test
     * @return the http response
     */
    @Path("/echoHeader")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse echoTestHeader(@HeaderParam("test") String test) {
        log.info("Echo Controller handle request!");
        return HttpResponse.builder().status(HttpResponseStatus.OK).data(test).build();
    }

    /**
     * Path param test http response.
     *
     * @param test  the test
     * @param test2 the test 2
     * @return the http response
     */
    @Path("/echo/{test}/{test2}")
    @GET
    public HttpResponse pathParamTest(@PathParam("test") boolean test, @PathParam("test2") boolean test2) {
        log.info("received path param {} - {}", test, test2);
        return HttpResponse.builder().status(HttpResponseStatus.OK).data(test&&test2).build();
    }

    /**
     * Query param test http response.
     *
     * @param test  the test
     * @param test2 the test 2
     * @return the http response
     */
    @Path("/echo/query")
    @GET
    public HttpResponse queryParamTest(@QueryParam("test") String test, @QueryParam("test2") String test2) {
        log.info("received path param {} - {}", test, test2);
        return HttpResponse.builder().status(HttpResponseStatus.OK).data(test + test2).build();
    }

    /**
     * Query param test form http response.
     *
     * @param test  the test
     * @param test2 the test 2
     * @return the http response
     */
    @Path("/echo/form")
    @POST
    public HttpResponse queryParamTestForm(@FormParam("test") int test, @FormParam("test2") int test2) {
        log.info("received path param {} - {}", test, test2);
        return HttpResponse.builder().status(HttpResponseStatus.OK).data(test + test2).build();
    }

    /**
     * Post test.
     *
     * @param routingContext the routing context
     */
    @Path("/post")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void postTest(RoutingContext routingContext) {
        log.info(routingContext.body().asString());
        sendJsonResponse(routingContext, HttpResponseStatus.OK, routingContext.body().asString());
    }

    /**
     * Post test body http response.
     *
     * @param body the body
     * @return the http response
     */
    @Path("/postBody")
    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse postTestBody(@RequestBody TestPojo body) {
        log.info(body.toString());
        return HttpResponse.builder().status(HttpResponseStatus.OK).data(body).build();
    }


}
