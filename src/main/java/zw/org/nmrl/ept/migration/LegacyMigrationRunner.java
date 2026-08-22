package zw.org.nmrl.ept.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.web.context.WebApplicationContext;

@Component
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationRunner implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(LegacyMigrationRunner.class);
    private final LegacyBatchMigrationService migrationService;
    private final ConfigurableApplicationContext applicationContext;

    public LegacyMigrationRunner(
        LegacyBatchMigrationService migrationService,
        ConfigurableApplicationContext applicationContext
    ) {
        this.migrationService = migrationService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (applicationContext instanceof WebApplicationContext) {
            throw new IllegalStateException(
                "Legacy migration must run with --spring.main.web-application-type=none"
            );
        }
        LegacyMigrationSummary summary;
        try {
            summary = migrationService.migrate();
        } catch (Exception e) {
            throw new IllegalStateException("Legacy ePT migration failed", e);
        }
        LOG.info(
            "Legacy ePT migration {} completed: {} tables, {} rows archived and {} files verified with {} errors",
            summary.batchId(),
            summary.tablesCompleted(),
            summary.rowsArchived(),
            summary.filesMigrated(),
            summary.errorCount()
        );
        if (summary.errorCount() != 0) {
            throw new IllegalStateException("Legacy migration completed with " + summary.errorCount() + " errors");
        }
        SpringApplication.exit(applicationContext, () -> 0);
    }
}
