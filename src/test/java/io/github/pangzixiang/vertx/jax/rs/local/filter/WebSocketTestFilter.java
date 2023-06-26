package io.github.pangzixiang.vertx.jax.rs.local.filter;

import io.github.pangzixiang.vertx.jax.rs.websocket.filter.WebsocketFilter;
import io.vertx.core.http.ServerWebSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketTestFilter implements WebsocketFilter {
    @Override
    public boolean doFilter(ServerWebSocket serverWebSocket) {
        log.info("invoke");
        return true;
    }
}
