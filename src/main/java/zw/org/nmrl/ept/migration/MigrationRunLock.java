package zw.org.nmrl.ept.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Holds a PostgreSQL session advisory lock for the complete migration run. */
final class MigrationRunLock implements AutoCloseable {

    private static final long LOCK_ID = 0x4550544D49475241L; // "EPTMIGRA"

    private final Connection connection;

    private MigrationRunLock(Connection connection) {
        this.connection = connection;
    }

    static MigrationRunLock acquire(MigrationTarget target) throws Exception {
        Connection connection = target.connection();
        boolean acquired = false;
        try {
            String product = connection.getMetaData().getDatabaseProductName();
            if (!"PostgreSQL".equalsIgnoreCase(product)) {
                throw new IllegalStateException("Legacy migration target must be PostgreSQL, not " + product);
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT pg_try_advisory_lock(?)")) {
                statement.setLong(1, LOCK_ID);
                try (ResultSet result = statement.executeQuery()) {
                    acquired = result.next() && result.getBoolean(1);
                }
            }
            if (!acquired) {
                throw new IllegalStateException("Another legacy ePT migration is already running against this target database");
            }
            return new MigrationRunLock(connection);
        } catch (Exception e) {
            connection.close();
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT pg_advisory_unlock(?)")) {
                statement.setLong(1, LOCK_ID);
                statement.execute();
            }
        } finally {
            connection.close();
        }
    }
}
