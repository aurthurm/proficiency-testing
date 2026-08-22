package zw.org.nmrl.ept.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.context.ConfigurableApplicationContext;

@Component
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationRunner implements ApplicationRunner {

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
    public void run(ApplicationArguments args) throws Exception {
        LegacyMigrationSummary summary = migrationService.migrate();
        LOG.info(
            "Legacy ePT migration {} completed: {} tables, {} rows archived and {} files verified with {} errors",
            summary.batchId(),
            summary.tablesCompleted(),
            summary.rowsArchived(),
            summary.filesMigrated(),
            summary.errorCount()
        );
        SpringApplication.exit(applicationContext, () -> summary.errorCount() == 0 ? 0 : 2);
    }
}
