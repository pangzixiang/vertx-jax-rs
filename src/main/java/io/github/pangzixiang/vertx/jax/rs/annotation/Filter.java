package io.github.pangzixiang.vertx.jax.rs.annotation;

import io.github.pangzixiang.vertx.jax.rs.filter.HttpFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
    /**
     * Filter class [ ].
     *
     * @return the class [ ]
     */
    Class<? extends HttpFilter>[] filter() default {};
}
