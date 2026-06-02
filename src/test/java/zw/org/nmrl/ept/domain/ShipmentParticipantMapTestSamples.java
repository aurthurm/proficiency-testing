package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ShipmentParticipantMapTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ShipmentParticipantMap getShipmentParticipantMapSample1() {
        return new ShipmentParticipantMap()
            .id(1L)
            .ptTestNotPerformedComments("ptTestNotPerformedComments1")
            .participantSupervisor("participantSupervisor1")
            .userComment("userComment1")
            .evaluationComment("evaluationComment1")
            .qcDoneBy("qcDoneBy1");
    }

    public static ShipmentParticipantMap getShipmentParticipantMapSample2() {
        return new ShipmentParticipantMap()
            .id(2L)
            .ptTestNotPerformedComments("ptTestNotPerformedComments2")
            .participantSupervisor("participantSupervisor2")
            .userComment("userComment2")
            .evaluationComment("evaluationComment2")
            .qcDoneBy("qcDoneBy2");
    }

    public static ShipmentParticipantMap getShipmentParticipantMapRandomSampleGenerator() {
        return new ShipmentParticipantMap()
            .id(longCount.incrementAndGet())
            .ptTestNotPerformedComments(UUID.randomUUID().toString())
            .participantSupervisor(UUID.randomUUID().toString())
            .userComment(UUID.randomUUID().toString())
            .evaluationComment(UUID.randomUUID().toString())
            .qcDoneBy(UUID.randomUUID().toString());
    }
}
