package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CorrectiveActionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static CorrectiveAction getCorrectiveActionSample1() {
        return new CorrectiveAction().id(1L).title("title1").description("description1");
    }

    public static CorrectiveAction getCorrectiveActionSample2() {
        return new CorrectiveAction().id(2L).title("title2").description("description2");
    }

    public static CorrectiveAction getCorrectiveActionRandomSampleGenerator() {
        return new CorrectiveAction()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
