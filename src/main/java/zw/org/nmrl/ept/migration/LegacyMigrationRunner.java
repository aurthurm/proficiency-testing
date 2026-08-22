package zw.org.nmrl.ept.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(LegacyMigrationRunner.class);
    private final LegacyBatchMigrationService migrationService;

    public LegacyMigrationRunner(LegacyBatchMigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LegacyMigrationSummary summary = migrationService.migrate();
        LOG.info(
            "Legacy ePT migration {} completed: {} tables and {} rows archived with {} errors",
            summary.batchId(),
            summary.tablesCompleted(),
            summary.rowsArchived(),
            summary.errorCount()
        );
    }
}
