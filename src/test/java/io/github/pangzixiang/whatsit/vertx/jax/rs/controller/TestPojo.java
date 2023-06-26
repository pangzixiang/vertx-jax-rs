package io.github.pangzixiang.whatsit.vertx.jax.rs.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Test pojo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestPojo {
    private String key;
    private String value;
}
