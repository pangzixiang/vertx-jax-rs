package io.github.pangzixiang.vertx.jax.rs.local.filter;

import io.github.pangzixiang.vertx.jax.rs.filter.HttpFilter;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoFilter extends HttpFilter {
    @Override
    public void doFilter(RoutingContext routingContext) {
        log.info("Echo Filter handle request!");
        routingContext.next();
    }
}
