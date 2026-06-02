package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EmailMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static EmailMessage getEmailMessageSample1() {
        return new EmailMessage()
            .id(1L)
            .fromEmail("fromEmail1")
            .fromName("fromName1")
            .replyTo("replyTo1")
            .toEmail("toEmail1")
            .cc("cc1")
            .bcc("bcc1")
            .subject("subject1")
            .attachmentRef("attachmentRef1")
            .failureType("failureType1")
            .failureReason("failureReason1")
            .retryCount(1);
    }

    public static EmailMessage getEmailMessageSample2() {
        return new EmailMessage()
            .id(2L)
            .fromEmail("fromEmail2")
            .fromName("fromName2")
            .replyTo("replyTo2")
            .toEmail("toEmail2")
            .cc("cc2")
            .bcc("bcc2")
            .subject("subject2")
            .attachmentRef("attachmentRef2")
            .failureType("failureType2")
            .failureReason("failureReason2")
            .retryCount(2);
    }

    public static EmailMessage getEmailMessageRandomSampleGenerator() {
        return new EmailMessage()
            .id(longCount.incrementAndGet())
            .fromEmail(UUID.randomUUID().toString())
            .fromName(UUID.randomUUID().toString())
            .replyTo(UUID.randomUUID().toString())
            .toEmail(UUID.randomUUID().toString())
            .cc(UUID.randomUUID().toString())
            .bcc(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString())
            .attachmentRef(UUID.randomUUID().toString())
            .failureType(UUID.randomUUID().toString())
            .failureReason(UUID.randomUUID().toString())
            .retryCount(intCount.incrementAndGet());
    }
}
