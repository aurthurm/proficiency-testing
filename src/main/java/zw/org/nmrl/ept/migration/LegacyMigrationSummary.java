package zw.org.nmrl.ept.migration;

import java.util.List;
import java.util.UUID;

public record LegacyMigrationSummary(
    UUID batchId,
    String sourceVersion,
    int tablesDiscovered,
    int tablesCompleted,
    long rowsArchived,
    long errorCount,
    List<LegacyTableMigrationSummary> tables
) {}
