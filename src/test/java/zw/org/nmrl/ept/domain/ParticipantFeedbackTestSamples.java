package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ParticipantFeedbackTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ParticipantFeedback getParticipantFeedbackSample1() {
        return new ParticipantFeedback().id(1L);
    }

    public static ParticipantFeedback getParticipantFeedbackSample2() {
        return new ParticipantFeedback().id(2L);
    }

    public static ParticipantFeedback getParticipantFeedbackRandomSampleGenerator() {
        return new ParticipantFeedback().id(longCount.incrementAndGet());
    }
}
