package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DataManagerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static DataManager getDataManagerSample1() {
        return new DataManager()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .institute("institute1")
            .primaryEmail("primaryEmail1")
            .secondaryEmail("secondaryEmail1")
            .phone("phone1")
            .mobile("mobile1")
            .language("language1");
    }

    public static DataManager getDataManagerSample2() {
        return new DataManager()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .institute("institute2")
            .primaryEmail("primaryEmail2")
            .secondaryEmail("secondaryEmail2")
            .phone("phone2")
            .mobile("mobile2")
            .language("language2");
    }

    public static DataManager getDataManagerRandomSampleGenerator() {
        return new DataManager()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .institute(UUID.randomUUID().toString())
            .primaryEmail(UUID.randomUUID().toString())
            .secondaryEmail(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .mobile(UUID.randomUUID().toString())
            .language(UUID.randomUUID().toString());
    }
}
