package zw.org.nmrl.ept.migration;

import java.util.List;

public record MigrationReconciliationReport(List<MigrationReconciliationIssue> issues) {
    public boolean isClean() {
        return issues.isEmpty();
    }
}
