package io.github.pangzixiang.whatsit.vertx.jax.rs.local.repository;

import io.github.pangzixiang.whatsit.vertx.jax.rs.local.model.TestTableEntity;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class TestRepository {
    private static final String INSERT_SQL = """
            insert into TEST_TABLE values(#{TEST_NAME},#{TEST_VALUE},#{LAST_UPDATED_TIMESTAMP})
            """;

    private static final String SELECT_SQL = """
            select * from TEST_TABLE where TEST_VALUE = #{TEST_VALUE}
            """;

    public Future<Void> insert(SqlConnection sqlConnection, TestTableEntity testTableEntity) {
        return SqlTemplate.forUpdate(sqlConnection, INSERT_SQL)
                .mapFrom(TestTableEntity.class)
                .execute(testTableEntity)
                .onSuccess(unused -> log.info("Entity {} inserted into db", testTableEntity))
                .onFailure(throwable -> log.error("Failed to insert Entity {}", testTableEntity, throwable))
                .mapEmpty();
    }

    public Future<TestTableEntity> select(SqlConnection sqlConnection, String testValue) {
        return SqlTemplate.forQuery(sqlConnection, SELECT_SQL)
                .mapTo(TestTableEntity.class)
                .execute(Map.of("TEST_VALUE", testValue))
                .map(rows -> {
                    if (rows.iterator().hasNext()) {
                        return rows.iterator().next();
                    }
                    return null;
                }).onSuccess(result -> log.info("Succeeded to query entity {}", result))
                .onFailure(throwable -> log.error("Failed to query [{}]", testValue, throwable));
    }
}
