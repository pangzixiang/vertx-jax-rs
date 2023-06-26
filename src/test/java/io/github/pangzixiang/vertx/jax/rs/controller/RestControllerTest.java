package io.github.pangzixiang.vertx.jax.rs.controller;

import io.github.pangzixiang.vertx.jax.rs.ApplicationContext;
import io.github.pangzixiang.vertx.jax.rs.BaseAppTest;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RestControllerTest extends BaseAppTest {

    private WebClient webClient;

    @BeforeAll
    void init() {
        webClient = WebClient.create(ApplicationContext.getApplicationContext().getVertx());
    }

    @Test
    void testEchoTest(VertxTestContext vertxTestContext) {
        webClient.get(ApplicationContext.getApplicationContext().getPort(), "localhost", "/v1/echoTest")
                .send()
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                       assertThat(response.statusCode()).isEqualTo(200);
                       assertThat(response.bodyAsString()).contains("echo");
                       vertxTestContext.completeNow();
                    });
                }));
    }

    @Test
    void testPostTest(VertxTestContext vertxTestContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("test", "test");
        webClient.post(ApplicationContext.getApplicationContext().getPort(), "localhost", "/v1/post")
                .putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)
                .sendBuffer(Buffer.buffer(jsonObject.encode()))
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.bodyAsString()).contains("test");
                        vertxTestContext.completeNow();
                    });
                }));
    }
}
