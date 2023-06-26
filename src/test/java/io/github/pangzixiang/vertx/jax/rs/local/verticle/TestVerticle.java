package io.github.pangzixiang.vertx.jax.rs.local.verticle;

import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        log.info("{} started", this.getClass().getSimpleName());
    }

    @Override
    public void stop() throws Exception {
        log.info("{} stopped", this.getClass().getSimpleName());
    }
}
