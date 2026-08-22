package zw.org.nmrl.ept.migration;

public record LegacyFileMigrationSummary(long discovered, long copied, long unchanged, long failed) {}
