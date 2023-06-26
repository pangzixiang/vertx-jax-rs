package io.github.pangzixiang.whatsit.vertx.jax.rs;

import io.github.pangzixiang.whatsit.vertx.jax.rs.constant.ConfigurationConstants;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The type Base app test.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(VertxExtension.class)
public class BaseAppTest {

    /**
     * Before all.
     *
     * @param vertxTestContext the vertx test context
     */
    @BeforeAll
    void beforeAll(VertxTestContext vertxTestContext) {
        System.setProperty("config.resource", "test.conf");
        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
        if (applicationConfiguration.isDatabaseEnable()) {
            String url = applicationConfiguration.getString(ConfigurationConstants.DATABASE_URL);
            String user = applicationConfiguration.getString(ConfigurationConstants.DATABASE_USER);
            String password = applicationConfiguration.getString(ConfigurationConstants.DATABASE_PASSWORD);
            Flyway flyway = Flyway.configure()
                    .locations("migrations")
                    .validateOnMigrate(true)
                    .baselineOnMigrate(true)
                    .dataSource(url, user, password)
                    .load();
            flyway.migrate();
        }
        ApplicationRunner.run().onComplete(vertxTestContext.succeeding(unused -> vertxTestContext.completeNow()));
    }
}
