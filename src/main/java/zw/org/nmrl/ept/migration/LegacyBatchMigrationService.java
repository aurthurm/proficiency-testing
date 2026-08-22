package zw.org.nmrl.ept.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.config.ApplicationProperties;

@Service
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class LegacyBatchMigrationService {

    private static final Logger LOG = LoggerFactory.getLogger(LegacyBatchMigrationService.class);
    private static final Pattern SAFE_IDENTIFIER = Pattern.compile("[A-Za-z0-9_]+", Pattern.CASE_INSENSITIVE);
    private static final String ARCHIVE_SQL = """
        INSERT INTO legacy_record_archive
            (source_table, source_primary_key, source_primary_key_hash, payload, row_checksum,
             first_migration_batch_id, last_seen_batch_id, first_migrated_at, last_migrated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (source_table, source_primary_key_hash) DO UPDATE SET
            source_primary_key = EXCLUDED.source_primary_key,
            payload = EXCLUDED.payload,
            row_checksum = EXCLUDED.row_checksum,
            last_seen_batch_id = EXCLUDED.last_seen_batch_id,
            last_migrated_at = EXCLUDED.last_migrated_at
        """;

    private final DataSource legacySource;
    private final JdbcTemplate target;
    private final ObjectMapper objectMapper;
    private final ApplicationProperties.Migration properties;
    private final ObjectProvider<LegacyCorePromoter> corePromoter;

    public LegacyBatchMigrationService(
        @Qualifier("legacySourceDataSource") DataSource legacySource,
        @Qualifier("dataSource") DataSource targetDataSource,
        ObjectMapper objectMapper,
        ApplicationProperties applicationProperties,
        ObjectProvider<LegacyCorePromoter> corePromoter
    ) {
        this.legacySource = legacySource;
        this.target = new JdbcTemplate(targetDataSource);
        this.objectMapper = objectMapper;
        this.properties = applicationProperties.getMigration();
        this.corePromoter = corePromoter;
    }

    public LegacyMigrationSummary migrate() throws Exception {
        validateSettings();
        UUID batchId = UUID.randomUUID();
        String sourceVersion;
        String sourceDatabase;
        List<String> tables;

        try (Connection source = legacySource.getConnection()) {
            sourceVersion = readSourceVersion(source);
            sourceDatabase = source.getCatalog();
            tables = discoverTables(source);
        }

        startBatch(batchId, sourceVersion, sourceDatabase, tables.size());
        List<LegacyTableMigrationSummary> summaries = new ArrayList<>();
        long archivedRows = 0;
        long errors = 0;

        try {
            for (String table : tables) {
                LegacyTableMigrationSummary summary = archiveTable(batchId, table);
                summaries.add(summary);
                archivedRows += summary.archivedRows();
                if (!"COMPLETED".equals(summary.status())) {
                    errors++;
                    if (properties.isFailOnError()) {
                        throw new IllegalStateException("Failed to archive legacy table " + table + ": " + summary.errorMessage());
                    }
                }
            }

            if (properties.isPromoteCore()) {
                LegacyCorePromoter promoter = corePromoter.getIfAvailable();
                if (promoter == null) {
                    throw new IllegalStateException("Core promotion was requested but no LegacyCorePromoter is configured");
                }
                promoter.promote(batchId);
            }

            completeBatch(batchId, "COMPLETED", summaries.size(), archivedRows, errors, null);
            return new LegacyMigrationSummary(
                batchId,
                sourceVersion,
                tables.size(),
                summaries.size(),
                archivedRows,
                errors,
                List.copyOf(summaries)
            );
        } catch (Exception e) {
            completeBatch(batchId, "FAILED", summaries.size(), archivedRows, errors + 1, e.getMessage());
            throw e;
        }
    }

    private LegacyTableMigrationSummary archiveTable(UUID batchId, String table) {
        Instant startedAt = Instant.now();
        startTable(batchId, table, startedAt);
        try (Connection source = legacySource.getConnection()) {
            List<String> primaryKeys = primaryKeys(source, table);
            String sql = selectSql(table, primaryKeys);
            long sourceRows = 0;
            long archivedRows = 0;
            byte[] tableChecksum = new byte[32];
            List<Object[]> writeBatch = new ArrayList<>(properties.getBatchSize());

            try (PreparedStatement statement = source.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                statement.setFetchSize(Integer.MIN_VALUE);
                try (ResultSet rows = statement.executeQuery()) {
                    ResultSetMetaData metadata = rows.getMetaData();
                    while (rows.next()) {
                        sourceRows++;
                        ObjectNode payload = rowAsJson(rows, metadata);
                        String payloadJson = objectMapper.writeValueAsString(payload);
                        String rowChecksum = sha256(payloadJson);
                        ObjectNode sourcePrimaryKey = sourcePrimaryKey(payload, primaryKeys, rowChecksum, sourceRows);
                        String sourcePrimaryKeyJson = objectMapper.writeValueAsString(sourcePrimaryKey);
                        String sourcePrimaryKeyHash = sha256(sourcePrimaryKeyJson);
                        xorChecksum(tableChecksum, HexFormat.of().parseHex(rowChecksum));
                        Instant now = Instant.now();
                        writeBatch.add(
                            new Object[] {
                                table,
                                sourcePrimaryKeyJson,
                                sourcePrimaryKeyHash,
                                payloadJson,
                                rowChecksum,
                                batchId,
                                batchId,
                                now,
                                now,
                            }
                        );
                        if (writeBatch.size() >= properties.getBatchSize()) {
                            archivedRows += flush(writeBatch);
                        }
                    }
                }
            }
            archivedRows += flush(writeBatch);
            long staleRows = countStaleRows(table, batchId);
            String checksum = HexFormat.of().formatHex(tableChecksum);
            LegacyTableMigrationSummary summary = new LegacyTableMigrationSummary(
                table,
                "COMPLETED",
                sourceRows,
                archivedRows,
                staleRows,
                checksum,
                null
            );
            finishTable(batchId, summary);
            return summary;
        } catch (Exception e) {
            LOG.error("Failed to archive legacy table {}", table, e);
            recordError(batchId, table, null, "ARCHIVE", e, null);
            LegacyTableMigrationSummary summary = new LegacyTableMigrationSummary(table, "FAILED", 0, 0, 0, null, e.getMessage());
            finishTable(batchId, summary);
            return summary;
        }
    }

    private ObjectNode rowAsJson(ResultSet row, ResultSetMetaData metadata) throws Exception {
        ObjectNode payload = objectMapper.createObjectNode();
        for (int column = 1; column <= metadata.getColumnCount(); column++) {
            String name = metadata.getColumnLabel(column);
            Object value = LegacyValueNormalizer.normalize(row.getObject(column));
            payload.set(name, objectMapper.valueToTree(value));
        }
        return payload;
    }

    private ObjectNode sourcePrimaryKey(ObjectNode payload, List<String> primaryKeys, String rowChecksum, long ordinal) {
        ObjectNode key = objectMapper.createObjectNode();
        if (primaryKeys.isEmpty()) {
            key.put("__row_checksum", rowChecksum);
            key.put("__row_ordinal", ordinal);
            return key;
        }
        for (String primaryKey : primaryKeys) {
            key.set(primaryKey, payload.get(primaryKey));
        }
        return key;
    }

    private int flush(List<Object[]> writeBatch) {
        if (writeBatch.isEmpty()) {
            return 0;
        }
        int size = writeBatch.size();
        target.batchUpdate(ARCHIVE_SQL, writeBatch);
        writeBatch.clear();
        return size;
    }

    private List<String> discoverTables(Connection source) throws Exception {
        Set<String> requested = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        properties.getIncludeTables().stream().map(String::trim).filter(value -> !value.isEmpty()).forEach(requested::add);
        List<String> tables = new ArrayList<>();
        DatabaseMetaData metadata = source.getMetaData();
        try (ResultSet result = metadata.getTables(source.getCatalog(), null, "%", new String[] { "TABLE" })) {
            while (result.next()) {
                String table = result.getString("TABLE_NAME");
                requireSafeIdentifier(table);
                if (requested.isEmpty() || requested.contains(table)) {
                    tables.add(table);
                }
            }
        }
        tables.sort(String.CASE_INSENSITIVE_ORDER);
        if (!requested.isEmpty()) {
            Set<String> missing = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            missing.addAll(requested);
            missing.removeAll(tables);
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("Requested legacy tables do not exist: " + missing);
            }
        }
        return tables;
    }

    private List<String> primaryKeys(Connection source, String table) throws Exception {
        Map<Short, String> ordered = new LinkedHashMap<>();
        try (ResultSet keys = source.getMetaData().getPrimaryKeys(source.getCatalog(), null, table)) {
            while (keys.next()) {
                ordered.put(keys.getShort("KEY_SEQ"), keys.getString("COLUMN_NAME"));
            }
        }
        return ordered.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList();
    }

    private String selectSql(String table, List<String> primaryKeys) {
        requireSafeIdentifier(table);
        StringBuilder sql = new StringBuilder("SELECT * FROM `").append(table).append('`');
        if (!primaryKeys.isEmpty()) {
            sql.append(" ORDER BY ");
            for (int i = 0; i < primaryKeys.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                requireSafeIdentifier(primaryKeys.get(i));
                sql.append('`').append(primaryKeys.get(i)).append('`');
            }
        }
        return sql.toString();
    }

    private String readSourceVersion(Connection source) {
        try (PreparedStatement statement = source.prepareStatement("SELECT value FROM system_config WHERE config = 'app_version'")) {
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString(1) : "unknown";
            }
        } catch (Exception e) {
            LOG.warn("Could not determine the source ePT version", e);
            return "unknown";
        }
    }

    private void validateSettings() {
        if (properties.getBatchSize() < 1 || properties.getBatchSize() > 10_000) {
            throw new IllegalArgumentException("application.migration.batch-size must be between 1 and 10000");
        }
    }

    private void requireSafeIdentifier(String identifier) {
        if (identifier == null || !SAFE_IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException("Unsafe database identifier: " + identifier);
        }
    }

    private String sha256(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    }

    private void xorChecksum(byte[] aggregate, byte[] rowHash) {
        for (int i = 0; i < aggregate.length; i++) {
            aggregate[i] ^= rowHash[i];
        }
    }

    private void startBatch(UUID id, String sourceVersion, String sourceDatabase, int tableCount) {
        target.update(
            "INSERT INTO migration_batch (id, source_version, source_database, status, started_at, tables_discovered) VALUES (?, ?, ?, 'RUNNING', ?, ?)",
            id,
            sourceVersion,
            sourceDatabase,
            Instant.now(),
            tableCount
        );
    }

    private void completeBatch(UUID id, String status, int tablesCompleted, long rowsArchived, long errors, String notes) {
        target.update(
            "UPDATE migration_batch SET status = ?, completed_at = ?, tables_completed = ?, rows_archived = ?, error_count = ?, notes = ? WHERE id = ?",
            status,
            Instant.now(),
            tablesCompleted,
            rowsArchived,
            errors,
            notes,
            id
        );
    }

    private void startTable(UUID batchId, String table, Instant startedAt) {
        target.update(
            "INSERT INTO migration_table_result (batch_id, source_table, status, started_at) VALUES (?, ?, 'RUNNING', ?)",
            batchId,
            table,
            startedAt
        );
    }

    private void finishTable(UUID batchId, LegacyTableMigrationSummary summary) {
        target.update(
            "UPDATE migration_table_result SET status = ?, source_rows = ?, archived_rows = ?, stale_rows = ?, table_checksum = ?, completed_at = ?, error_message = ? WHERE batch_id = ? AND source_table = ?",
            summary.status(),
            summary.sourceRows(),
            summary.archivedRows(),
            summary.staleRows(),
            summary.checksum(),
            Instant.now(),
            summary.errorMessage(),
            batchId,
            summary.table()
        );
    }

    private long countStaleRows(String table, UUID batchId) {
        Long value = target.queryForObject(
            "SELECT COUNT(*) FROM legacy_record_archive WHERE source_table = ? AND last_seen_batch_id <> ?",
            Long.class,
            table,
            batchId
        );
        return value == null ? 0 : value;
    }

    private void recordError(UUID batchId, String table, String sourcePrimaryKey, String stage, Exception error, String payload) {
        target.update(
            "INSERT INTO migration_error (batch_id, source_table, source_primary_key, stage, error_type, error_message, payload, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            batchId,
            table,
            sourcePrimaryKey,
            stage,
            error.getClass().getName(),
            error.getMessage() == null ? error.toString() : error.getMessage(),
            payload,
            Instant.now()
        );
    }
}
