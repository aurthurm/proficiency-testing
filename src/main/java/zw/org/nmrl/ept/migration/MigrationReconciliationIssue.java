package zw.org.nmrl.ept.migration;

public record MigrationReconciliationIssue(String category, String sourceTable, long expected, long actual, String detail) {}
