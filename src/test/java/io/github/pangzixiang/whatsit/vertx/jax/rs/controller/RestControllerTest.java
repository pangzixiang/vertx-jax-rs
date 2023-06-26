package io.github.pangzixiang.whatsit.vertx.jax.rs.controller;

import io.github.pangzixiang.whatsit.vertx.jax.rs.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.jax.rs.BaseAppTest;
import io.github.pangzixiang.whatsit.vertx.jax.rs.local.model.TestPojo;
import io.github.pangzixiang.whatsit.vertx.jax.rs.local.model.TestTableEntity;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Rest controller test.
 */
public class RestControllerTest extends BaseAppTest {

    private WebClient webClient;

    /**
     * Init.
     */
    @BeforeAll
    void init() {
        webClient = WebClient.create(ApplicationContext.getApplicationContext().getVertx());
    }

    /**
     * Test echo test.
     *
     * @param vertxTestContext the vertx test context
     */
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

    /**
     * Test post test.
     *
     * @param vertxTestContext the vertx test context
     */
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

    @Test
    void testEchoHeader(VertxTestContext vertxTestContext) {
        String random = UUID.randomUUID().toString();
        webClient.get(ApplicationContext.getApplicationContext().getPort(), "localhost", "/v1/echoHeader")
                .putHeader("test", random)
                .send()
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.bodyAsString()).contains(random);
                        vertxTestContext.completeNow();
                    });
                }));
    }

    @Test
    void testPathParam(VertxTestContext vertxTestContext) {
        boolean a = true;
        boolean b = false;
        webClient.get(ApplicationContext.getApplicationContext().getPort(), "localhost", "/v1/echo/%s/%s".formatted(a, b))
                .send()
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.bodyAsString()).contains("false");
                        vertxTestContext.completeNow();
                    });
                }));
    }

    @Test
    void testQueryParam(VertxTestContext vertxTestContext) {
        String a = UUID.randomUUID().toString();
        String b = UUID.randomUUID().toString();
        webClient.get(ApplicationContext.getApplicationContext().getPort(), "localhost", "/v1/echo/query")
                .addQueryParam("test", a)
                .addQueryParam("test2", b)
                .send()
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.bodyAsString()).contains(a + b);
                        vertxTestContext.completeNow();
                    });
                }));
    }

    @Test
    void testFormParam(VertxTestContext vertxTestContext) {
        int a = RandomUtils.nextInt();
        int b = RandomUtils.nextInt();
        webClient.post(ApplicationContext.getApplicationContext().getPort(), "localhost", "/v1/echo/form")
                .sendForm(MultiMap.caseInsensitiveMultiMap().add("test", String.valueOf(a)).add("test2", String.valueOf(b)))
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.bodyAsString()).contains(String.valueOf(a + b));
                        vertxTestContext.completeNow();
                    });
                }));
    }

    @Test
    void testPostBody(VertxTestContext vertxTestContext) {
        TestPojo testPojo = TestPojo.builder().key(UUID.randomUUID().toString()).value(UUID.randomUUID().toString()).build();
        webClient.post(ApplicationContext.getApplicationContext().getPort(), "localhost", "/v1/postBody")
                .sendJson(testPojo)
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.bodyAsString()).contains(testPojo.getKey());
                        vertxTestContext.completeNow();
                    });
                }));
    }

    @Test
    void testDBInsert(VertxTestContext vertxTestContext) {
        TestTableEntity testTableEntity = TestTableEntity.builder()
                .testName(UUID.randomUUID().toString())
                .testValue(UUID.randomUUID().toString())
                .lastUpdatedTimestamp(LocalDateTime.now())
                .build();
        webClient.post(ApplicationContext.getApplicationContext().getPort(), "localhost", "/db/insert")
                .sendJson(testTableEntity)
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.bodyAsString()).contains("inserted");
                        vertxTestContext.completeNow();
                    });
                }));
    }

    @Test
    void testDBSelect(VertxTestContext vertxTestContext) {
        String value = UUID.randomUUID().toString();
        TestTableEntity testTableEntity = TestTableEntity.builder()
                .testName(UUID.randomUUID().toString())
                .testValue(value)
                .lastUpdatedTimestamp(LocalDateTime.now())
                .build();
        webClient.post(ApplicationContext.getApplicationContext().getPort(), "localhost", "/db/insert")
                .sendJson(testTableEntity)
                .onComplete(vertxTestContext.succeeding(response -> {
                    vertxTestContext.verify(() -> {
                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.bodyAsString()).contains("inserted");
                        vertxTestContext.completeNow();
                    });
                })).andThen(unused -> {
                    webClient.get(ApplicationContext.getApplicationContext().getPort(), "localhost", "/db/select/%s".formatted(value))
                            .send()
                            .onComplete(vertxTestContext.succeeding(response -> {
                                vertxTestContext.verify(() -> {
                                    assertThat(response.statusCode()).isEqualTo(200);
                                    assertThat(response.bodyAsJson(TestTableEntity.class)).isEqualTo(testTableEntity);
                                    vertxTestContext.completeNow();
                                });
                            }));
                });
    }
}
