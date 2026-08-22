package zw.org.nmrl.ept.migration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import zw.org.nmrl.ept.config.ApplicationProperties;

@Testcontainers(disabledWithoutDocker = true)
class LegacyBatchMigrationIntegrationTest {

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17");

    private static HikariDataSource source;
    private static HikariDataSource targetDataSource;

    @BeforeAll
    static void prepareDatabases() throws Exception {
        source = dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
        targetDataSource = dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        targetDataSource.setAutoCommit(false);

        try (Connection connection = source.getConnection(); Statement sql = connection.createStatement()) {
            sql.execute("SET sql_mode = ''");
            sql.execute("CREATE TABLE system_config (config varchar(256) PRIMARY KEY, value longtext, display_name longtext)");
            sql.execute("INSERT INTO system_config(config, value) VALUES ('app_version', '7.6.20')");
            sql.execute("CREATE TABLE legacy_dates (id int PRIMARY KEY, happened_on date, note varchar(255))");
            sql.execute("INSERT INTO legacy_dates VALUES (1, '0000-00-00', 'preserve me')");
        }

        JdbcTemplate target = new JdbcTemplate(targetDataSource);
        new MigrationTarget(targetDataSource).write(() -> {
            target.execute(
                "CREATE TABLE migration_batch (id uuid PRIMARY KEY, source_version varchar(32), source_database varchar(255), " +
                "status varchar(32) NOT NULL, started_at timestamp NOT NULL, completed_at timestamp, tables_discovered integer NOT NULL, " +
                "tables_completed integer NOT NULL DEFAULT 0, rows_archived bigint NOT NULL DEFAULT 0, error_count bigint NOT NULL DEFAULT 0, notes text)"
            );
            target.execute(
                "CREATE TABLE migration_table_result (id bigserial PRIMARY KEY, batch_id uuid NOT NULL REFERENCES migration_batch(id), " +
                "source_table varchar(128) NOT NULL, status varchar(32) NOT NULL, source_rows bigint NOT NULL DEFAULT 0, " +
                "archived_rows bigint NOT NULL DEFAULT 0, stale_rows bigint NOT NULL DEFAULT 0, table_checksum varchar(64), " +
                "started_at timestamp NOT NULL, completed_at timestamp, error_message text, UNIQUE(batch_id, source_table))"
            );
            target.execute(
                "CREATE TABLE legacy_record_archive (id bigserial PRIMARY KEY, source_table varchar(128) NOT NULL, " +
                "source_primary_key text NOT NULL, source_primary_key_hash varchar(64) NOT NULL, payload text NOT NULL, " +
                "row_checksum varchar(64) NOT NULL, first_migration_batch_id uuid NOT NULL, last_seen_batch_id uuid NOT NULL, " +
                "first_migrated_at timestamp NOT NULL, last_migrated_at timestamp NOT NULL, UNIQUE(source_table, source_primary_key_hash))"
            );
            target.execute(
                "CREATE TABLE migration_error (id bigserial PRIMARY KEY, batch_id uuid NOT NULL REFERENCES migration_batch(id), " +
                "source_table varchar(128), source_primary_key text, stage varchar(64) NOT NULL, error_type varchar(255), " +
                "error_message text NOT NULL, payload text, created_at timestamp NOT NULL)"
            );
        });
    }

    @AfterAll
    static void closePools() {
        if (source != null) {
            source.close();
        }
        if (targetDataSource != null) {
            targetDataSource.close();
        }
    }

    @Test
    void commitsEveryRowAndPreservesMysqlZeroDates() throws Exception {
        ApplicationProperties application = new ApplicationProperties();
        application.getMigration().setEnabled(true);
        application.getMigration().setRequiredSourceTables(java.util.List.of("system_config", "legacy_dates"));

        MigrationTarget target = new MigrationTarget(targetDataSource);
        LegacyMigrationReconciliationService reconciliation = new LegacyMigrationReconciliationService(target, application);
        StaticListableBeanFactory beans = new StaticListableBeanFactory();
        LegacyBatchMigrationService service = new LegacyBatchMigrationService(
            source,
            target,
            new ObjectMapper(),
            application,
            beans.getBeanProvider(LegacyCorePromoter.class),
            beans.getBeanProvider(LegacyFileMigrationService.class),
            reconciliation
        );

        LegacyMigrationSummary summary = service.migrate();

        assertThat(summary.errorCount()).isZero();
        assertThat(summary.tablesDiscovered()).isEqualTo(2);
        JdbcTemplate verification = new JdbcTemplate(targetDataSource);
        assertThat(verification.queryForObject("SELECT COUNT(*) FROM legacy_record_archive", Long.class)).isEqualTo(2);
        assertThat(
            verification.queryForObject(
                "SELECT payload FROM legacy_record_archive WHERE source_table = 'legacy_dates'",
                String.class
            )
        ).contains("\"happened_on\":\"0000-00-00\"");
        assertThat(verification.queryForObject("SELECT status FROM migration_batch", String.class)).isEqualTo("COMPLETED");
    }

    private static HikariDataSource dataSource(String url, String username, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
