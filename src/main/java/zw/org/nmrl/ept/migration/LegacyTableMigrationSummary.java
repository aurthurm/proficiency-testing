package zw.org.nmrl.ept.migration;

public record LegacyTableMigrationSummary(
    String table,
    String status,
    long sourceRows,
    long archivedRows,
    long staleRows,
    String checksum,
    String errorMessage
) {}
