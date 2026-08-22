package zw.org.nmrl.ept.migration;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.config.ApplicationProperties;

@Service
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class LegacyMigrationReconciliationService {

    private static final BigInteger CHECKSUM_MASK = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);

    private static final List<CoreMapping> CORE_MAPPINGS = List.of(
        new CoreMapping("countries", "country"),
        new CoreMapping("scheme_list", "scheme"),
        new CoreMapping("participant", "participant"),
        new CoreMapping("data_manager", "data_manager"),
        new CoreMapping("data_manager", "jhi_user"),
        new CoreMapping("system_admin", "jhi_user"),
        new CoreMapping("r_modes_of_receipt", "mode_of_receipt"),
        new CoreMapping("r_response_not_tested_reasons", "not_tested_reason"),
        new CoreMapping("distributions", "distribution"),
        new CoreMapping("enrollments", "enrollment"),
        new CoreMapping("shipment", "shipment"),
        new CoreMapping("shipment_participant_map", "shipment_participant_map"),
        new CoreMapping("mail_template", "mail_template"),
        new CoreMapping("global_config", "global_configuration"),
        new CoreMapping("system_config", "global_configuration"),
        new CoreMapping("scheme_config", "scheme_configuration"),
        new CoreMapping("r_testkitnames", "test_kit"),
        new CoreMapping("r_dbs_eia", "assay"),
        new CoreMapping("r_dbs_wb", "assay"),
        new CoreMapping("r_eid_detection_assay", "assay"),
        new CoreMapping("r_eid_extraction_assay", "assay"),
        new CoreMapping("r_recency_assay", "assay"),
        new CoreMapping("r_tb_assay", "assay"),
        new CoreMapping("r_vl_assay", "assay"),
        new CoreMapping("r_covid19_gene_types", "assay"),
        new CoreMapping("r_test_type_covid19", "assay"),
        new CoreMapping("certificate_batches", "certificate_batch"),
        new CoreMapping("report_config", "report_configuration"),
        new CoreMapping("track_report_downloaded_history", "legacy_report_download")
    );

    private final MigrationTarget target;
    private final ApplicationProperties.Migration properties;

    public LegacyMigrationReconciliationService(
        MigrationTarget target,
        ApplicationProperties applicationProperties
    ) {
        this.target = target;
        this.properties = applicationProperties.getMigration();
    }

    public MigrationReconciliationReport reconcile(UUID batchId, long expectedTables, long expectedFiles) {
        List<MigrationReconciliationIssue> issues = new ArrayList<>();
        reconcileArchivedTables(batchId, expectedTables, issues);
        if (properties.isPromoteCore()) {
            reconcileCoreMappings(batchId, issues);
            reconcileOperationalRelationships(batchId, issues);
            reconcileSchemeResults(batchId, issues);
            reconcileCertificateTemplates(batchId, issues);
        }
        if (properties.getFiles().isEnabled()) {
            reconcileFiles(batchId, expectedFiles, issues);
        }
        issues.forEach(issue -> record(batchId, issue));
        return new MigrationReconciliationReport(List.copyOf(issues));
    }

    private void reconcileArchivedTables(UUID batchId, long expectedTables, List<MigrationReconciliationIssue> issues) {
        long tableResults = count("SELECT COUNT(*) FROM migration_table_result WHERE batch_id = ?", batchId);
        long discovered = count("SELECT tables_discovered FROM migration_batch WHERE id = ?", batchId);
        if (tableResults != expectedTables || discovered != expectedTables) {
            issues.add(
                new MigrationReconciliationIssue(
                    "TABLE_COVERAGE",
                    "*",
                    expectedTables,
                    tableResults,
                    "Every discovered table must have exactly one migration result; batch recorded " + discovered
                )
            );
        }
        target.jdbc().query(
            "SELECT source_table, status, source_rows, archived_rows, stale_rows, table_checksum " +
            "FROM migration_table_result WHERE batch_id = ?",
            result -> {
                String table = result.getString("source_table");
                long sourceRows = result.getLong("source_rows");
                long archivedRows = result.getLong("archived_rows");
                long staleRows = result.getLong("stale_rows");
                long actualArchiveRows = count(
                    "SELECT COUNT(*) FROM legacy_record_archive WHERE source_table = ? AND last_seen_batch_id = ?",
                    table,
                    batchId
                );
                if (
                    !"COMPLETED".equals(result.getString("status")) ||
                    sourceRows != archivedRows ||
                    sourceRows != actualArchiveRows
                ) {
                    issues.add(
                        new MigrationReconciliationIssue(
                            "ARCHIVE_COUNT",
                            table,
                            sourceRows,
                            actualArchiveRows,
                            "Every source row must be committed to the lossless archive; writer reported " + archivedRows
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
                String expectedChecksum = result.getString("table_checksum");
                String actualChecksum = archiveChecksum(table, batchId);
                if (expectedChecksum == null || !expectedChecksum.equals(actualChecksum)) {
                    issues.add(
                        new MigrationReconciliationIssue(
                            "ARCHIVE_CHECKSUM",
                            table,
                            sourceRows,
                            actualArchiveRows,
                            "Archive checksum mismatch; expected=" + expectedChecksum + ", actual=" + actualChecksum
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
            long resolvableRows = count(
                "SELECT COUNT(*) FROM migration_id_map m JOIN " + mapping.targetTable() +
                " t ON t.id = m.target_id WHERE m.source_table = ? AND m.target_table = ? AND m.migration_batch_id = ?",
                mapping.sourceTable(),
                mapping.targetTable(),
                batchId
            );
            if (sourceRows != mappedRows || mappedRows != resolvableRows) {
                issues.add(
                    new MigrationReconciliationIssue(
                        "CORE_PROMOTION",
                        mapping.sourceTable(),
                        sourceRows,
                        resolvableRows,
                        "Expected one resolvable typed " + mapping.targetTable() + " identity mapping per archived source row; maps=" + mappedRows
                    )
                );
            }
        }
    }

    private void reconcileFiles(UUID batchId, long expectedFiles, List<MigrationReconciliationIssue> issues) {
        long recordedFiles = count("SELECT COUNT(*) FROM migration_file_result WHERE batch_id = ?", batchId);
        if (recordedFiles != expectedFiles) {
            issues.add(
                new MigrationReconciliationIssue(
                    "FILE_COVERAGE",
                    "files",
                    expectedFiles,
                    recordedFiles,
                    "Every discovered file must have exactly one migration result"
                )
            );
        }
        target.jdbc().query(
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

    private void reconcileOperationalRelationships(UUID batchId, List<MigrationReconciliationIssue> issues) {
        reconcileRelationship(
            batchId,
            issues,
            "participant_manager_map",
            "SELECT COUNT(*) FROM legacy_record_archive a " +
            "LEFT JOIN data_manager d ON d.legacy_source_id = a.payload::jsonb ->> 'dm_id' " +
            "LEFT JOIN participant p ON p.legacy_source_id = a.payload::jsonb ->> 'participant_id' " +
            "WHERE a.source_table = 'participant_manager_map' AND a.last_seen_batch_id = ? " +
            "AND (d.id IS NULL OR p.id IS NULL OR NOT EXISTS (SELECT 1 FROM rel_data_manager__participants r " +
            "WHERE r.data_manager_id = d.id AND r.participants_id = p.id))"
        );
        reconcileRelationship(
            batchId,
            issues,
            "ptcc_countries_map",
            "SELECT COUNT(*) FROM legacy_record_archive a " +
            "LEFT JOIN data_manager d ON d.legacy_source_id = a.payload::jsonb ->> 'ptcc_id' " +
            "LEFT JOIN country c ON c.legacy_source_id = a.payload::jsonb ->> 'country_id' " +
            "WHERE a.source_table = 'ptcc_countries_map' AND a.last_seen_batch_id = ? " +
            "AND (d.id IS NULL OR c.id IS NULL OR NOT EXISTS (SELECT 1 FROM rel_data_manager__ptcc_countries r " +
            "WHERE r.data_manager_id = d.id AND r.country_id = c.id))"
        );
    }

    private void reconcileRelationship(
        UUID batchId,
        List<MigrationReconciliationIssue> issues,
        String sourceTable,
        String missingSql
    ) {
        long missing = count(missingSql, batchId);
        if (missing != 0) {
            long sourceRows = count(
                "SELECT COUNT(*) FROM legacy_record_archive WHERE source_table = ? AND last_seen_batch_id = ?",
                sourceTable,
                batchId
            );
            issues.add(
                new MigrationReconciliationIssue(
                    "OPERATIONAL_RELATIONSHIP",
                    sourceTable,
                    sourceRows,
                    sourceRows - missing,
                    "Every archived relationship must resolve to typed target rows"
                )
            );
        }
    }

    private void reconcileSchemeResults(UUID batchId, List<MigrationReconciliationIssue> issues) {
        long sourceRows = count(
            "WITH scheme_tables AS (" +
            "SELECT payload::jsonb ->> 'response_table' response_table, payload::jsonb ->> 'reference_result_table' reference_table " +
            "FROM legacy_record_archive WHERE source_table = 'scheme_list' AND last_seen_batch_id = ?) " +
            "SELECT COUNT(*) FROM legacy_record_archive a WHERE a.last_seen_batch_id = ? AND EXISTS (" +
            "SELECT 1 FROM scheme_tables s WHERE a.source_table = s.response_table OR a.source_table = s.reference_table)",
            batchId,
            batchId
        );
        long typedRows = count(
            "SELECT COUNT(*) FROM migration_id_map WHERE target_table = 'legacy_scheme_result' AND migration_batch_id = ?",
            batchId
        );
        long resolvableRows = count(
            "SELECT COUNT(*) FROM migration_id_map m JOIN legacy_scheme_result r ON r.id = m.target_id " +
            "WHERE m.target_table = 'legacy_scheme_result' AND m.migration_batch_id = ?",
            batchId
        );
        if (sourceRows != typedRows || typedRows != resolvableRows) {
            issues.add(
                new MigrationReconciliationIssue(
                    "SCHEME_RESULTS",
                    "scheme-specific-results",
                    sourceRows,
                    resolvableRows,
                    "Every configured response/reference result row must have a resolvable typed envelope; maps=" + typedRows
                )
            );
        }
    }

    private void reconcileCertificateTemplates(UUID batchId, List<MigrationReconciliationIssue> issues) {
        long sourceRows = count(
            "SELECT COUNT(*) FROM legacy_record_archive WHERE source_table = 'certificate_templates' AND last_seen_batch_id = ?",
            batchId
        );
        long expectedTemplates = sourceRows * 2;
        long templates = count(
            "SELECT COUNT(*) FROM migration_id_map WHERE source_table = 'certificate_templates' " +
            "AND target_table = 'certificate_template' AND migration_batch_id = ?",
            batchId
        );
        long resolvable = count(
            "SELECT COUNT(*) FROM migration_id_map m JOIN certificate_template t ON t.id = m.target_id " +
            "WHERE m.source_table = 'certificate_templates' AND m.target_table = 'certificate_template' " +
            "AND m.migration_batch_id = ?",
            batchId
        );
        if (expectedTemplates != templates || templates != resolvable) {
            issues.add(
                new MigrationReconciliationIssue(
                    "CERTIFICATE_TEMPLATES",
                    "certificate_templates",
                    expectedTemplates,
                    resolvable,
                    "Each legacy certificate-template row must produce participation and excellence templates; maps=" + templates
                )
            );
        }
    }

    private long count(String sql, Object... parameters) {
        Long value = target.jdbc().queryForObject(sql, Long.class, parameters);
        return value == null ? 0 : value;
    }

    private String archiveChecksum(String table, UUID batchId) {
        BigInteger[] checksum = { BigInteger.ZERO };
        target.jdbc().query(
            "SELECT row_checksum FROM legacy_record_archive WHERE source_table = ? AND last_seen_batch_id = ?",
            result -> checksum[0] = checksum[0].add(new BigInteger(result.getString(1), 16)).and(CHECKSUM_MASK),
            table,
            batchId
        );
        return String.format("%064x", checksum[0]);
    }

    private void record(UUID batchId, MigrationReconciliationIssue issue) {
        target.write(() -> target.jdbc().update(
            "INSERT INTO migration_error " +
            "(batch_id, source_table, stage, error_type, error_message, created_at) VALUES (?, ?, 'RECONCILE', ?, ?, ?)",
            batchId,
            issue.sourceTable(),
            issue.category(),
            issue.detail() + "; expected=" + issue.expected() + ", actual=" + issue.actual(),
            Instant.now()
        ));
    }

    private record CoreMapping(String sourceTable, String targetTable) {}
}
