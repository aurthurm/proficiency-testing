package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TestKitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static TestKit getTestKitSample1() {
        return new TestKit().id(1L).name("name1").manufacturer("manufacturer1");
    }

    public static TestKit getTestKitSample2() {
        return new TestKit().id(2L).name("name2").manufacturer("manufacturer2");
    }

    public static TestKit getTestKitRandomSampleGenerator() {
        return new TestKit().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).manufacturer(UUID.randomUUID().toString());
    }
}
