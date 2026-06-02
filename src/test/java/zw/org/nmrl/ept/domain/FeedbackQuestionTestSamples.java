package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FeedbackQuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static FeedbackQuestion getFeedbackQuestionSample1() {
        return new FeedbackQuestion().id(1L).questionText("questionText1").displayOrder(1);
    }

    public static FeedbackQuestion getFeedbackQuestionSample2() {
        return new FeedbackQuestion().id(2L).questionText("questionText2").displayOrder(2);
    }

    public static FeedbackQuestion getFeedbackQuestionRandomSampleGenerator() {
        return new FeedbackQuestion()
            .id(longCount.incrementAndGet())
            .questionText(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
