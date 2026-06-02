package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.AssayTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantResultTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentParticipantMapTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentSampleTestSamples.*;
import static zw.org.nmrl.ept.domain.TestKitTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantResultTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParticipantResult.class);
        ParticipantResult participantResult1 = getParticipantResultSample1();
        ParticipantResult participantResult2 = new ParticipantResult();
        assertThat(participantResult1).isNotEqualTo(participantResult2);

        participantResult2.setId(participantResult1.getId());
        assertThat(participantResult1).isEqualTo(participantResult2);

        participantResult2 = getParticipantResultSample2();
        assertThat(participantResult1).isNotEqualTo(participantResult2);
    }

    @Test
    void assayTest() {
        ParticipantResult participantResult = getParticipantResultRandomSampleGenerator();
        Assay assayBack = getAssayRandomSampleGenerator();

        participantResult.setAssay(assayBack);
        assertThat(participantResult.getAssay()).isEqualTo(assayBack);

        participantResult.assay(null);
        assertThat(participantResult.getAssay()).isNull();
    }

    @Test
    void testKitTest() {
        ParticipantResult participantResult = getParticipantResultRandomSampleGenerator();
        TestKit testKitBack = getTestKitRandomSampleGenerator();

        participantResult.setTestKit(testKitBack);
        assertThat(participantResult.getTestKit()).isEqualTo(testKitBack);

        participantResult.testKit(null);
        assertThat(participantResult.getTestKit()).isNull();
    }

    @Test
    void sampleTest() {
        ParticipantResult participantResult = getParticipantResultRandomSampleGenerator();
        ShipmentSample shipmentSampleBack = getShipmentSampleRandomSampleGenerator();

        participantResult.setSample(shipmentSampleBack);
        assertThat(participantResult.getSample()).isEqualTo(shipmentSampleBack);

        participantResult.sample(null);
        assertThat(participantResult.getSample()).isNull();
    }

    @Test
    void shipmentParticipantMapTest() {
        ParticipantResult participantResult = getParticipantResultRandomSampleGenerator();
        ShipmentParticipantMap shipmentParticipantMapBack = getShipmentParticipantMapRandomSampleGenerator();

        participantResult.setShipmentParticipantMap(shipmentParticipantMapBack);
        assertThat(participantResult.getShipmentParticipantMap()).isEqualTo(shipmentParticipantMapBack);

        participantResult.shipmentParticipantMap(null);
        assertThat(participantResult.getShipmentParticipantMap()).isNull();
    }
}
