package io.github.pangzixiang.whatsit.vertx.jax.rs.local.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestTableEntity {
    @JsonProperty("TEST_NAME")
    private String testName;
    @JsonProperty("TEST_VALUE")
    private String testValue;
    @JsonProperty("LAST_UPDATED_TIMESTAMP")
    private LocalDateTime lastUpdatedTimestamp;
}
