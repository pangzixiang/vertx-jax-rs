package io.github.pangzixiang.whatsit.vertx.jax.rs.local.filter;

import io.github.pangzixiang.whatsit.vertx.jax.rs.websocket.filter.WebsocketFilter;
import io.vertx.core.http.ServerWebSocket;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Web socket test filter.
 */
@Slf4j
public class WebSocketTestFilter implements WebsocketFilter {
    @Override
    public boolean doFilter(ServerWebSocket serverWebSocket) {
        log.info("invoke");
        return true;
    }
}
