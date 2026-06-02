package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DistributionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Distribution getDistributionSample1() {
        return new Distribution().id(1L).code("code1");
    }

    public static Distribution getDistributionSample2() {
        return new Distribution().id(2L).code("code2");
    }

    public static Distribution getDistributionRandomSampleGenerator() {
        return new Distribution().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString());
    }
}
