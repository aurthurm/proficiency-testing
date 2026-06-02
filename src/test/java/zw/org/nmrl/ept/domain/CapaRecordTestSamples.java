package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CapaRecordTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static CapaRecord getCapaRecordSample1() {
        return new CapaRecord().id(1L).rootCause("rootCause1").actionTaken("actionTaken1");
    }

    public static CapaRecord getCapaRecordSample2() {
        return new CapaRecord().id(2L).rootCause("rootCause2").actionTaken("actionTaken2");
    }

    public static CapaRecord getCapaRecordRandomSampleGenerator() {
        return new CapaRecord()
            .id(longCount.incrementAndGet())
            .rootCause(UUID.randomUUID().toString())
            .actionTaken(UUID.randomUUID().toString());
    }
}
