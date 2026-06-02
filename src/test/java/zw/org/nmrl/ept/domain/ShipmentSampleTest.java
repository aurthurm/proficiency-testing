package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.ParticipantResultTestSamples.*;
import static zw.org.nmrl.ept.domain.SampleReferenceResultTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentSampleTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ShipmentSampleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentSample.class);
        ShipmentSample shipmentSample1 = getShipmentSampleSample1();
        ShipmentSample shipmentSample2 = new ShipmentSample();
        assertThat(shipmentSample1).isNotEqualTo(shipmentSample2);

        shipmentSample2.setId(shipmentSample1.getId());
        assertThat(shipmentSample1).isEqualTo(shipmentSample2);

        shipmentSample2 = getShipmentSampleSample2();
        assertThat(shipmentSample1).isNotEqualTo(shipmentSample2);
    }

    @Test
    void referenceResultsTest() {
        ShipmentSample shipmentSample = getShipmentSampleRandomSampleGenerator();
        SampleReferenceResult sampleReferenceResultBack = getSampleReferenceResultRandomSampleGenerator();

        shipmentSample.addReferenceResults(sampleReferenceResultBack);
        assertThat(shipmentSample.getReferenceResultses()).containsOnly(sampleReferenceResultBack);
        assertThat(sampleReferenceResultBack.getSample()).isEqualTo(shipmentSample);

        shipmentSample.removeReferenceResults(sampleReferenceResultBack);
        assertThat(shipmentSample.getReferenceResultses()).doesNotContain(sampleReferenceResultBack);
        assertThat(sampleReferenceResultBack.getSample()).isNull();

        shipmentSample.referenceResultses(new HashSet<>(Set.of(sampleReferenceResultBack)));
        assertThat(shipmentSample.getReferenceResultses()).containsOnly(sampleReferenceResultBack);
        assertThat(sampleReferenceResultBack.getSample()).isEqualTo(shipmentSample);

        shipmentSample.setReferenceResultses(new HashSet<>());
        assertThat(shipmentSample.getReferenceResultses()).doesNotContain(sampleReferenceResultBack);
        assertThat(sampleReferenceResultBack.getSample()).isNull();
    }

    @Test
    void participantResultsTest() {
        ShipmentSample shipmentSample = getShipmentSampleRandomSampleGenerator();
        ParticipantResult participantResultBack = getParticipantResultRandomSampleGenerator();

        shipmentSample.addParticipantResults(participantResultBack);
        assertThat(shipmentSample.getParticipantResultses()).containsOnly(participantResultBack);
        assertThat(participantResultBack.getSample()).isEqualTo(shipmentSample);

        shipmentSample.removeParticipantResults(participantResultBack);
        assertThat(shipmentSample.getParticipantResultses()).doesNotContain(participantResultBack);
        assertThat(participantResultBack.getSample()).isNull();

        shipmentSample.participantResultses(new HashSet<>(Set.of(participantResultBack)));
        assertThat(shipmentSample.getParticipantResultses()).containsOnly(participantResultBack);
        assertThat(participantResultBack.getSample()).isEqualTo(shipmentSample);

        shipmentSample.setParticipantResultses(new HashSet<>());
        assertThat(shipmentSample.getParticipantResultses()).doesNotContain(participantResultBack);
        assertThat(participantResultBack.getSample()).isNull();
    }

    @Test
    void shipmentTest() {
        ShipmentSample shipmentSample = getShipmentSampleRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        shipmentSample.setShipment(shipmentBack);
        assertThat(shipmentSample.getShipment()).isEqualTo(shipmentBack);

        shipmentSample.shipment(null);
        assertThat(shipmentSample.getShipment()).isNull();
    }
}
