package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContactMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ContactMessage getContactMessageSample1() {
        return new ContactMessage().id(1L).name("name1").email("email1").subject("subject1").ipAddress("ipAddress1");
    }

    public static ContactMessage getContactMessageSample2() {
        return new ContactMessage().id(2L).name("name2").email("email2").subject("subject2").ipAddress("ipAddress2");
    }

    public static ContactMessage getContactMessageRandomSampleGenerator() {
        return new ContactMessage()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString());
    }
}
