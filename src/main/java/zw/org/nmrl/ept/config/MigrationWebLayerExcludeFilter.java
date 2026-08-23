package zw.org.nmrl.ept.config;

import java.io.IOException;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Migration mode runs as a one-shot CLI process with {@code spring.main.web-application-type=none}:
 * there is no HTTP server, so the REST layer serves no purpose. Component scanning still
 * instantiates {@code @RestController} beans as plain beans regardless of web application type,
 * and several of them depend on beans that Spring Security only registers for a servlet web
 * application (e.g. {@code AuthenticationManagerBuilder}), which fails application startup.
 * Exclude the REST layer from scanning while migration mode is enabled.
 */
public class MigrationWebLayerExcludeFilter implements TypeFilter, EnvironmentAware {

    private static final String REST_PACKAGE_PREFIX = "zw.org.nmrl.ept.web.rest.";

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        if (environment == null || !environment.getProperty("application.migration.enabled", Boolean.class, false)) {
            return false;
        }
        return metadataReader.getClassMetadata().getClassName().startsWith(REST_PACKAGE_PREFIX);
    }
}
