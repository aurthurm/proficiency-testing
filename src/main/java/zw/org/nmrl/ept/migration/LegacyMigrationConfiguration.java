package zw.org.nmrl.ept.migration;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zw.org.nmrl.ept.config.ApplicationProperties;

@Configuration
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationConfiguration {

    @Bean(name = "legacySourceDataSource", destroyMethod = "close")
    public DataSource legacySourceDataSource(ApplicationProperties properties) {
        ApplicationProperties.Migration.Source source = properties.getMigration().getSource();
        if (source.getUrl() == null || source.getUrl().isBlank()) {
            throw new IllegalStateException("EPT_LEGACY_JDBC_URL is required when legacy migration is enabled");
        }

        HikariDataSource dataSource = DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .driverClassName(source.getDriverClassName())
            .url(source.getUrl())
            .username(source.getUsername())
            .password(source.getPassword())
            .build();
        dataSource.setPoolName("LegacyEptMigration");
        dataSource.setMaximumPoolSize(2);
        dataSource.setReadOnly(true);
        dataSource.setAutoCommit(false);
        return dataSource;
    }
}
