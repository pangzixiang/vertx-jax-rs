package io.github.pangzixiang.whatsit.vertx.jax.rs.local;

import io.github.pangzixiang.whatsit.vertx.jax.rs.ApplicationConfiguration;
import io.github.pangzixiang.whatsit.vertx.jax.rs.ApplicationRunner;
import io.github.pangzixiang.whatsit.vertx.jax.rs.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.jax.rs.constant.ConfigurationConstants;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

/**
 * The type Run whatsit core local test.
 */
@Slf4j
public class RunLocalTest {
    /**
     * -Dconfig.resource=local.conf
     * -Dcom.sun.management.jmxremote.port=8088
     * -Dcom.sun.management.jmxremote.authenticate=false
     * -Dcom.sun.management.jmxremote.ssl=false
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
//        applicationContext.getApplicationConfiguration().setHttpServerOptions(new HttpServerOptions().setLogActivity(true));
//        applicationContext.getApplicationConfiguration().setVertxOptions(new VertxOptions());
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
        ApplicationRunner.run(ApplicationContext.getApplicationContext());
    }
}
