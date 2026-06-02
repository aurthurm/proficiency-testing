package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PartnerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Partner getPartnerSample1() {
        return new Partner().id(1L).name("name1").link("link1").logoRef("logoRef1").sortOrder(1);
    }

    public static Partner getPartnerSample2() {
        return new Partner().id(2L).name("name2").link("link2").logoRef("logoRef2").sortOrder(2);
    }

    public static Partner getPartnerRandomSampleGenerator() {
        return new Partner()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .link(UUID.randomUUID().toString())
            .logoRef(UUID.randomUUID().toString())
            .sortOrder(intCount.incrementAndGet());
    }
}
