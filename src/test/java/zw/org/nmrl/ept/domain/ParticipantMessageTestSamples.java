package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParticipantMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ParticipantMessage getParticipantMessageSample1() {
        return new ParticipantMessage().id(1L).subject("subject1");
    }

    public static ParticipantMessage getParticipantMessageSample2() {
        return new ParticipantMessage().id(2L).subject("subject2");
    }

    public static ParticipantMessage getParticipantMessageRandomSampleGenerator() {
        return new ParticipantMessage().id(longCount.incrementAndGet()).subject(UUID.randomUUID().toString());
    }
}
