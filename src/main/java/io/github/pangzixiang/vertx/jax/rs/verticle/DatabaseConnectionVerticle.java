package io.github.pangzixiang.vertx.jax.rs.verticle;

import io.github.pangzixiang.vertx.jax.rs.ApplicationConfiguration;
import io.github.pangzixiang.vertx.jax.rs.ApplicationContext;
import io.github.pangzixiang.vertx.jax.rs.scheduler.DatabaseHealthCheckScheduleJob;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.jdbcclient.JDBCPool;
import lombok.extern.slf4j.Slf4j;

import static io.github.pangzixiang.vertx.jax.rs.utils.CoreUtils.createCircuitBreaker;
import static io.github.pangzixiang.vertx.jax.rs.utils.VerticleUtils.deployVerticle;

/**
 * The type Database connection verticle.
 */
@Slf4j
public class DatabaseConnectionVerticle extends CoreVerticle {

    private final String VERIFICATION_SQL;

    private final CircuitBreaker circuitBreaker;

    private final ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();

    /**
     * Instantiates a new Database connection verticle.
     *
     */
    public DatabaseConnectionVerticle() {
        this.circuitBreaker = createCircuitBreaker(ApplicationContext.getApplicationContext().getVertx());
        VERIFICATION_SQL = applicationConfiguration.getHealthCheckSql();
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start();
        log.info("Starting to connect to database");
        connect(startPromise);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        super.stop();
        if (ApplicationContext.getApplicationContext().getJdbcPool() != null) {
            ApplicationContext.getApplicationContext().getJdbcPool()
                    .close()
                    .onSuccess(success -> {
                        log.info("Database Connection Closed!");
                        stopPromise.complete();
                    });
        } else {
            stopPromise.complete();
        }
    }

    private void connect(Promise<Void> startPromise) {
        JDBCPool jdbcPool = JDBCPool.pool(getVertx(),
                applicationConfiguration.getJDBCConnectOptions(),
                applicationConfiguration.getJDBCPoolOptions());

        verify(jdbcPool)
                .onComplete(booleanAsyncResult -> {
                    if (booleanAsyncResult.succeeded()) {
                        ApplicationContext.getApplicationContext().setJdbcPool(jdbcPool);
                        log.info("Database Connected [ {} ]!", booleanAsyncResult.result());
                        healthCheckSchedule()
                                .onComplete(compositeFutureAsyncResult -> {
                                    if (compositeFutureAsyncResult.succeeded()) {
                                        startPromise.complete();
                                        log.info("Database setup done!");
                                    } else {
                                        startPromise.fail(compositeFutureAsyncResult.cause());
                                    }
                                });
                    } else {
                        startPromise.fail(booleanAsyncResult.cause());
                        log.error("Database Connection FAILED!!!");
                        System.exit(-1);
                    }
                });
    }

    private Future<Boolean> verify(JDBCPool jdbcPool) {
        return circuitBreaker.execute(promise -> {
            jdbcPool
                    .preparedQuery(VERIFICATION_SQL)
                    .execute()
                    .compose(rows -> {
                        Integer result = rows.iterator().next().getInteger(0);
                        if (result.equals(1)) {
                            log.debug("Database Verification passed! [expect: 1, result: {}]", result);
                            return Future.succeededFuture(true);
                        } else {
                            String err = String.format("Database Verification Failed! [expect: 1, result: %s]", result);
                            log.error(err);
                            return Future.succeededFuture(false);
                        }
                    })
                    .onFailure(throwable -> {
                        log.error("Database Connection Failed with ERROR: {}, ", throwable.getMessage(), throwable);
                        promise.fail(throwable);
                    })
                    .onSuccess(promise::complete);
        });
    }

    private Future<String> healthCheckSchedule() {
        return deployVerticle(getVertx(), new DatabaseHealthCheckScheduleJob());
    }
}
