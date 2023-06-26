package io.github.pangzixiang.whatsit.vertx.jax.rs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Web socket annotation.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebSocketAnnotation {
    /**
     * Path string.
     *
     * @return the string
     */
    String path();

    /**
     * Filter class [ ].
     *
     * @return the class [ ]
     */
    Class<?> [] filter() default {};
}
