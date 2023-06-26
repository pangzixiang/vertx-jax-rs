package io.github.pangzixiang.whatsit.vertx.jax.rs.local;

import io.github.pangzixiang.whatsit.vertx.jax.rs.ApplicationRunner;
import io.github.pangzixiang.whatsit.vertx.jax.rs.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Run whatsit core local test.
 */
@Slf4j
public class RunWhatsitCoreLocalTest {
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
        ApplicationRunner.run(ApplicationContext.getApplicationContext());
    }
}
