package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ModeOfReceiptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ModeOfReceipt getModeOfReceiptSample1() {
        return new ModeOfReceipt().id(1L).name("name1");
    }

    public static ModeOfReceipt getModeOfReceiptSample2() {
        return new ModeOfReceipt().id(2L).name("name2");
    }

    public static ModeOfReceipt getModeOfReceiptRandomSampleGenerator() {
        return new ModeOfReceipt().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
