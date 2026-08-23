package zw.org.nmrl.ept.migration;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import zw.org.nmrl.ept.config.ApplicationProperties;

@Configuration
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationConfiguration {

    /**
     * The presence of {@code legacySourceDataSource} below satisfies Spring Boot's
     * {@code @ConditionalOnMissingBean(DataSource.class)} guard on its own datasource
     * auto-configuration, silently suppressing the application's real (Postgres) datasource
     * bean whenever migration mode is enabled. Redefine it explicitly here, scoped to the
     * same condition, so migration mode does not disable the rest of the application's
     * database wiring (Liquibase, JPA/Hibernate, JdbcTemplate, ...).
     */
    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

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
