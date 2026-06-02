package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ShipmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Shipment getShipmentSample1() {
        return new Shipment()
            .id(1L)
            .code("code1")
            .issuingAuthority("issuingAuthority1")
            .coordinatorName("coordinatorName1")
            .coordinatorEmail("coordinatorEmail1")
            .coordinatorPhone("coordinatorPhone1")
            .numberOfSamples(1)
            .maxScore(1);
    }

    public static Shipment getShipmentSample2() {
        return new Shipment()
            .id(2L)
            .code("code2")
            .issuingAuthority("issuingAuthority2")
            .coordinatorName("coordinatorName2")
            .coordinatorEmail("coordinatorEmail2")
            .coordinatorPhone("coordinatorPhone2")
            .numberOfSamples(2)
            .maxScore(2);
    }

    public static Shipment getShipmentRandomSampleGenerator() {
        return new Shipment()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .issuingAuthority(UUID.randomUUID().toString())
            .coordinatorName(UUID.randomUUID().toString())
            .coordinatorEmail(UUID.randomUUID().toString())
            .coordinatorPhone(UUID.randomUUID().toString())
            .numberOfSamples(intCount.incrementAndGet())
            .maxScore(intCount.incrementAndGet());
    }
}
