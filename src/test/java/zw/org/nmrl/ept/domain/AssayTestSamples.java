package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssayTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Assay getAssaySample1() {
        return new Assay().id(1L).name("name1").manufacturer("manufacturer1");
    }

    public static Assay getAssaySample2() {
        return new Assay().id(2L).name("name2").manufacturer("manufacturer2");
    }

    public static Assay getAssayRandomSampleGenerator() {
        return new Assay().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).manufacturer(UUID.randomUUID().toString());
    }
}
