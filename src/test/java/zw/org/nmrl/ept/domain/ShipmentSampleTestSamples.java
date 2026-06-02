package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ShipmentSampleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ShipmentSample getShipmentSampleSample1() {
        return new ShipmentSample().id(1L).label("label1").displayOrder(1);
    }

    public static ShipmentSample getShipmentSampleSample2() {
        return new ShipmentSample().id(2L).label("label2").displayOrder(2);
    }

    public static ShipmentSample getShipmentSampleRandomSampleGenerator() {
        return new ShipmentSample()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
