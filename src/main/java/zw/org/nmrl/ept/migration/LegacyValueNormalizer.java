package zw.org.nmrl.ept.migration;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Base64;

final class LegacyValueNormalizer {

    private LegacyValueNormalizer() {}

    static Object normalize(Object value) throws Exception {
        if (value == null) {
            return null;
        }
        if (value instanceof byte[] bytes) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        if (value instanceof Blob blob) {
            return Base64.getEncoder().encodeToString(readBlob(blob));
        }
        if (value instanceof Clob clob) {
            return clob.getSubString(1, Math.toIntExact(clob.length()));
        }
        if (value instanceof Timestamp timestamp) {
            // MySQL DATETIME is a wall-clock value. Do not apply a timezone shift.
            return timestamp.toLocalDateTime().toString();
        }
        if (value instanceof Date date) {
            return date.toLocalDate().toString();
        }
        if (value instanceof Time time) {
            return time.toLocalTime().toString();
        }
        if (value instanceof BigDecimal || value instanceof Number || value instanceof Boolean || value instanceof String) {
            return value;
        }
        return value.toString();
    }

    private static byte[] readBlob(Blob blob) throws Exception {
        long length = blob.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("Legacy BLOB exceeds the supported in-memory migration size");
        }
        return blob.getBytes(1, (int) length);
    }
}
