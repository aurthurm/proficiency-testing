package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SchemeConfigurationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SchemeConfiguration getSchemeConfigurationSample1() {
        return new SchemeConfiguration().id(1L).version(1);
    }

    public static SchemeConfiguration getSchemeConfigurationSample2() {
        return new SchemeConfiguration().id(2L).version(2);
    }

    public static SchemeConfiguration getSchemeConfigurationRandomSampleGenerator() {
        return new SchemeConfiguration().id(longCount.incrementAndGet()).version(intCount.incrementAndGet());
    }
}
