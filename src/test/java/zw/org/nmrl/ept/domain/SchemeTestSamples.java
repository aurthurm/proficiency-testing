package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SchemeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Scheme getSchemeSample1() {
        return new Scheme().id(1L).code("code1").name("name1");
    }

    public static Scheme getSchemeSample2() {
        return new Scheme().id(2L).code("code2").name("name2");
    }

    public static Scheme getSchemeRandomSampleGenerator() {
        return new Scheme().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).name(UUID.randomUUID().toString());
    }
}
