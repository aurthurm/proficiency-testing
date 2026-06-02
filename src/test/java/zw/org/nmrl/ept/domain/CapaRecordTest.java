package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CapaRecordTestSamples.*;
import static zw.org.nmrl.ept.domain.CorrectiveActionTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentParticipantMapTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CapaRecordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CapaRecord.class);
        CapaRecord capaRecord1 = getCapaRecordSample1();
        CapaRecord capaRecord2 = new CapaRecord();
        assertThat(capaRecord1).isNotEqualTo(capaRecord2);

        capaRecord2.setId(capaRecord1.getId());
        assertThat(capaRecord1).isEqualTo(capaRecord2);

        capaRecord2 = getCapaRecordSample2();
        assertThat(capaRecord1).isNotEqualTo(capaRecord2);
    }

    @Test
    void correctiveActionTest() {
        CapaRecord capaRecord = getCapaRecordRandomSampleGenerator();
        CorrectiveAction correctiveActionBack = getCorrectiveActionRandomSampleGenerator();

        capaRecord.setCorrectiveAction(correctiveActionBack);
        assertThat(capaRecord.getCorrectiveAction()).isEqualTo(correctiveActionBack);

        capaRecord.correctiveAction(null);
        assertThat(capaRecord.getCorrectiveAction()).isNull();
    }

    @Test
    void shipmentParticipantMapTest() {
        CapaRecord capaRecord = getCapaRecordRandomSampleGenerator();
        ShipmentParticipantMap shipmentParticipantMapBack = getShipmentParticipantMapRandomSampleGenerator();

        capaRecord.setShipmentParticipantMap(shipmentParticipantMapBack);
        assertThat(capaRecord.getShipmentParticipantMap()).isEqualTo(shipmentParticipantMapBack);

        capaRecord.shipmentParticipantMap(null);
        assertThat(capaRecord.getShipmentParticipantMap()).isNull();
    }
}
