package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduledJobTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ScheduledJob getScheduledJobSample1() {
        return new ScheduledJob().id(1L).requestedBy("requestedBy1").progressCompleted(1).progressTotal(1);
    }

    public static ScheduledJob getScheduledJobSample2() {
        return new ScheduledJob().id(2L).requestedBy("requestedBy2").progressCompleted(2).progressTotal(2);
    }

    public static ScheduledJob getScheduledJobRandomSampleGenerator() {
        return new ScheduledJob()
            .id(longCount.incrementAndGet())
            .requestedBy(UUID.randomUUID().toString())
            .progressCompleted(intCount.incrementAndGet())
            .progressTotal(intCount.incrementAndGet());
    }
}
