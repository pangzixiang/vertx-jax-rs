package io.github.pangzixiang.vertx.jax.rs.websocket.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * The type Websocket filter object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class WebsocketFilterObject {
    private Object instance;
    private Method doFilter;
}
