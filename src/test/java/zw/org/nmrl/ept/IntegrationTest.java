package zw.org.nmrl.ept;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import zw.org.nmrl.ept.config.AsyncSyncConfiguration;
import zw.org.nmrl.ept.config.EmbeddedSQL;
import zw.org.nmrl.ept.config.JacksonConfiguration;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        EptApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        zw.org.nmrl.ept.config.JacksonHibernateConfiguration.class,
    }
)
@EmbeddedSQL
public @interface IntegrationTest {}
