package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParticipantCustomValueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ParticipantCustomValue getParticipantCustomValueSample1() {
        return new ParticipantCustomValue().id(1L).value("value1");
    }

    public static ParticipantCustomValue getParticipantCustomValueSample2() {
        return new ParticipantCustomValue().id(2L).value("value2");
    }

    public static ParticipantCustomValue getParticipantCustomValueRandomSampleGenerator() {
        return new ParticipantCustomValue().id(longCount.incrementAndGet()).value(UUID.randomUUID().toString());
    }
}
