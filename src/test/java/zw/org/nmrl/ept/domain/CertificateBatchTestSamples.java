package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CertificateBatchTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CertificateBatch getCertificateBatchSample1() {
        return new CertificateBatch()
            .id(1L)
            .name("name1")
            .excellenceCount(1)
            .participationCount(1)
            .skippedCount(1)
            .downloadUrl("downloadUrl1")
            .errorMessage("errorMessage1")
            .approvedBy("approvedBy1");
    }

    public static CertificateBatch getCertificateBatchSample2() {
        return new CertificateBatch()
            .id(2L)
            .name("name2")
            .excellenceCount(2)
            .participationCount(2)
            .skippedCount(2)
            .downloadUrl("downloadUrl2")
            .errorMessage("errorMessage2")
            .approvedBy("approvedBy2");
    }

    public static CertificateBatch getCertificateBatchRandomSampleGenerator() {
        return new CertificateBatch()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .excellenceCount(intCount.incrementAndGet())
            .participationCount(intCount.incrementAndGet())
            .skippedCount(intCount.incrementAndGet())
            .downloadUrl(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString())
            .approvedBy(UUID.randomUUID().toString());
    }
}
