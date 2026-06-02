package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotTestedReasonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static NotTestedReason getNotTestedReasonSample1() {
        return new NotTestedReason().id(1L).reason("reason1");
    }

    public static NotTestedReason getNotTestedReasonSample2() {
        return new NotTestedReason().id(2L).reason("reason2");
    }

    public static NotTestedReason getNotTestedReasonRandomSampleGenerator() {
        return new NotTestedReason().id(longCount.incrementAndGet()).reason(UUID.randomUUID().toString());
    }
}
