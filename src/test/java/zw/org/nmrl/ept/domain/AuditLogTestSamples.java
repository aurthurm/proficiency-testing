package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static AuditLog getAuditLogSample1() {
        return new AuditLog()
            .id(1L)
            .statement("statement1")
            .performedBy("performedBy1")
            .performedByRole("performedByRole1")
            .ipAddress("ipAddress1")
            .userAgent("userAgent1")
            .sessionHash("sessionHash1");
    }

    public static AuditLog getAuditLogSample2() {
        return new AuditLog()
            .id(2L)
            .statement("statement2")
            .performedBy("performedBy2")
            .performedByRole("performedByRole2")
            .ipAddress("ipAddress2")
            .userAgent("userAgent2")
            .sessionHash("sessionHash2");
    }

    public static AuditLog getAuditLogRandomSampleGenerator() {
        return new AuditLog()
            .id(longCount.incrementAndGet())
            .statement(UUID.randomUUID().toString())
            .performedBy(UUID.randomUUID().toString())
            .performedByRole(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString())
            .userAgent(UUID.randomUUID().toString())
            .sessionHash(UUID.randomUUID().toString());
    }
}
