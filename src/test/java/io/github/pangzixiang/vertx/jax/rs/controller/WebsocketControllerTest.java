package io.github.pangzixiang.vertx.jax.rs.controller;

import io.github.pangzixiang.vertx.jax.rs.ApplicationContext;
import io.github.pangzixiang.vertx.jax.rs.BaseAppTest;
import io.vertx.core.http.HttpClient;
import io.vertx.junit5.VertxTestContext;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WebsocketControllerTest extends BaseAppTest {

    private HttpClient httpClient;

    @BeforeAll
    void init() {
        httpClient = ApplicationContext.getApplicationContext().getVertx().createHttpClient();
    }

    @Test
    void test(VertxTestContext vertxTestContext) {
        httpClient.webSocket(ApplicationContext.getApplicationContext().getPort(), "localhost", "/ws")
                .onComplete(vertxTestContext.succeeding(ws -> {
                    String testString = String.valueOf(RandomUtils.nextDouble());
                    ws.writeTextMessage(testString);
                    vertxTestContext.verify(() -> {
                        ws.frameHandler(frame -> {
                            assertThat(frame.textData()).isEqualTo(testString);
                            vertxTestContext.completeNow();
                        });
                    });
                }));
    }
}
