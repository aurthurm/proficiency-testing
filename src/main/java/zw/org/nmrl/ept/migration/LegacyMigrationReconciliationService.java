package zw.org.nmrl.ept.migration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.config.ApplicationProperties;

@Service
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationReconciliationService {

    private static final List<CoreMapping> CORE_MAPPINGS = List.of(
        new CoreMapping("countries", "country"),
        new CoreMapping("scheme_list", "scheme"),
        new CoreMapping("participant", "participant"),
        new CoreMapping("data_manager", "data_manager"),
        new CoreMapping("r_modes_of_receipt", "mode_of_receipt"),
        new CoreMapping("r_response_not_tested_reasons", "not_tested_reason"),
        new CoreMapping("distributions", "distribution"),
        new CoreMapping("enrollments", "enrollment"),
        new CoreMapping("shipment", "shipment"),
        new CoreMapping("shipment_participant_map", "shipment_participant_map"),
        new CoreMapping("mail_template", "mail_template")
    );

    private final JdbcTemplate target;
    private final ApplicationProperties.Migration properties;

    public LegacyMigrationReconciliationService(
        @Qualifier("dataSource") DataSource targetDataSource,
        ApplicationProperties applicationProperties
    ) {
        this.target = new JdbcTemplate(targetDataSource);
        this.properties = applicationProperties.getMigration();
    }

    public MigrationReconciliationReport reconcile(UUID batchId) {
        List<MigrationReconciliationIssue> issues = new ArrayList<>();
        reconcileArchivedTables(batchId, issues);
        if (properties.isPromoteCore()) {
            reconcileCoreMappings(batchId, issues);
        }
        if (properties.getFiles().isEnabled()) {
            reconcileFiles(batchId, issues);
        }
        issues.forEach(issue -> record(batchId, issue));
        return new MigrationReconciliationReport(List.copyOf(issues));
    }

    private void reconcileArchivedTables(UUID batchId, List<MigrationReconciliationIssue> issues) {
        target.query(
            "SELECT source_table, status, source_rows, archived_rows, stale_rows " +
            "FROM migration_table_result WHERE batch_id = ?",
            result -> {
                String table = result.getString("source_table");
                long sourceRows = result.getLong("source_rows");
                long archivedRows = result.getLong("archived_rows");
                long staleRows = result.getLong("stale_rows");
                if (!"COMPLETED".equals(result.getString("status")) || sourceRows != archivedRows) {
                    issues.add(
                        new MigrationReconciliationIssue(
                            "ARCHIVE_COUNT",
                            table,
                            sourceRows,
                            archivedRows,
                            "Every source row must be represented in the lossless archive"
                        )
                    );
                }
                if (staleRows != 0) {
                    issues.add(
                        new MigrationReconciliationIssue(
                            "STALE_ROWS",
                            table,
                            0,
                            staleRows,
                            "Rows from an earlier batch are absent from the current source snapshot"
                        )
                    );
                }
            },
            batchId
        );
    }

    private void reconcileCoreMappings(UUID batchId, List<MigrationReconciliationIssue> issues) {
        for (CoreMapping mapping : CORE_MAPPINGS) {
            long sourceRows = count(
                "SELECT COUNT(*) FROM legacy_record_archive WHERE source_table = ? AND last_seen_batch_id = ?",
                mapping.sourceTable(),
                batchId
            );
            long mappedRows = count(
                "SELECT COUNT(*) FROM migration_id_map WHERE source_table = ? AND target_table = ? AND migration_batch_id = ?",
                mapping.sourceTable(),
                mapping.targetTable(),
                batchId
            );
            if (sourceRows != mappedRows) {
                issues.add(
                    new MigrationReconciliationIssue(
                        "CORE_PROMOTION",
                        mapping.sourceTable(),
                        sourceRows,
                        mappedRows,
                        "Expected one typed " + mapping.targetTable() + " identity mapping per archived source row"
                    )
                );
            }
        }
    }

    private void reconcileFiles(UUID batchId, List<MigrationReconciliationIssue> issues) {
        target.query(
            "SELECT relative_path, status, source_checksum, target_checksum FROM migration_file_result WHERE batch_id = ?",
            result -> {
                String status = result.getString("status");
                String sourceChecksum = result.getString("source_checksum");
                String targetChecksum = result.getString("target_checksum");
                if (
                    !List.of("COPIED", "UNCHANGED").contains(status) ||
                    sourceChecksum == null ||
                    !sourceChecksum.equals(targetChecksum)
                ) {
                    issues.add(
                        new MigrationReconciliationIssue(
                            "FILE_CHECKSUM",
                            "files",
                            1,
                            0,
                            "Source and target file checksums must match for " + result.getString("relative_path")
                        )
                    );
                }
            },
            batchId
        );
    }

    private long count(String sql, Object... parameters) {
        Long value = target.queryForObject(sql, Long.class, parameters);
        return value == null ? 0 : value;
    }

    private void record(UUID batchId, MigrationReconciliationIssue issue) {
        target.update(
            "INSERT INTO migration_error " +
            "(batch_id, source_table, stage, error_type, error_message, created_at) VALUES (?, ?, 'RECONCILE', ?, ?, ?)",
            batchId,
            issue.sourceTable(),
            issue.category(),
            issue.detail() + "; expected=" + issue.expected() + ", actual=" + issue.actual(),
            Instant.now()
        );
    }

    private record CoreMapping(String sourceTable, String targetTable) {}
}
