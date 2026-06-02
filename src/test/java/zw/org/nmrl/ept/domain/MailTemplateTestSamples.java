package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MailTemplateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MailTemplate getMailTemplateSample1() {
        return new MailTemplate().id(1L).code("code1").subject("subject1");
    }

    public static MailTemplate getMailTemplateSample2() {
        return new MailTemplate().id(2L).code("code2").subject("subject2");
    }

    public static MailTemplate getMailTemplateRandomSampleGenerator() {
        return new MailTemplate().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).subject(UUID.randomUUID().toString());
    }
}
