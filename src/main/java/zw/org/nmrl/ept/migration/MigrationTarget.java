package zw.org.nmrl.ept.migration;

import java.sql.Connection;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Transactional access to the PostgreSQL migration target.
 *
 * <p>The production Hikari pool deliberately has auto-commit disabled. Every
 * migration write must therefore execute inside an explicit Spring-managed
 * transaction; otherwise Hikari rolls the connection back when it is returned
 * to the pool.</p>
 */
@Component
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class MigrationTarget {

    private final DataSource dataSource;
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;

    public MigrationTarget(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbc = new JdbcTemplate(dataSource);
        this.transaction = new TransactionTemplate(new JdbcTransactionManager(dataSource));
    }

    JdbcTemplate jdbc() {
        return jdbc;
    }

    void write(Runnable operation) {
        transaction.executeWithoutResult(status -> operation.run());
    }

    <T> T write(Supplier<T> operation) {
        return transaction.execute(status -> operation.get());
    }

    Connection connection() throws Exception {
        return dataSource.getConnection();
    }
}
