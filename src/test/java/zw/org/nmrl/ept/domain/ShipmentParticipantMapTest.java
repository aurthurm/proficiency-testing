package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CapaRecordTestSamples.*;
import static zw.org.nmrl.ept.domain.ModeOfReceiptTestSamples.*;
import static zw.org.nmrl.ept.domain.NotTestedReasonTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantResultTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentParticipantMapTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ShipmentParticipantMapTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentParticipantMap.class);
        ShipmentParticipantMap shipmentParticipantMap1 = getShipmentParticipantMapSample1();
        ShipmentParticipantMap shipmentParticipantMap2 = new ShipmentParticipantMap();
        assertThat(shipmentParticipantMap1).isNotEqualTo(shipmentParticipantMap2);

        shipmentParticipantMap2.setId(shipmentParticipantMap1.getId());
        assertThat(shipmentParticipantMap1).isEqualTo(shipmentParticipantMap2);

        shipmentParticipantMap2 = getShipmentParticipantMapSample2();
        assertThat(shipmentParticipantMap1).isNotEqualTo(shipmentParticipantMap2);
    }

    @Test
    void resultsTest() {
        ShipmentParticipantMap shipmentParticipantMap = getShipmentParticipantMapRandomSampleGenerator();
        ParticipantResult participantResultBack = getParticipantResultRandomSampleGenerator();

        shipmentParticipantMap.addResults(participantResultBack);
        assertThat(shipmentParticipantMap.getResultses()).containsOnly(participantResultBack);
        assertThat(participantResultBack.getShipmentParticipantMap()).isEqualTo(shipmentParticipantMap);

        shipmentParticipantMap.removeResults(participantResultBack);
        assertThat(shipmentParticipantMap.getResultses()).doesNotContain(participantResultBack);
        assertThat(participantResultBack.getShipmentParticipantMap()).isNull();

        shipmentParticipantMap.resultses(new HashSet<>(Set.of(participantResultBack)));
        assertThat(shipmentParticipantMap.getResultses()).containsOnly(participantResultBack);
        assertThat(participantResultBack.getShipmentParticipantMap()).isEqualTo(shipmentParticipantMap);

        shipmentParticipantMap.setResultses(new HashSet<>());
        assertThat(shipmentParticipantMap.getResultses()).doesNotContain(participantResultBack);
        assertThat(participantResultBack.getShipmentParticipantMap()).isNull();
    }

    @Test
    void capaRecordsTest() {
        ShipmentParticipantMap shipmentParticipantMap = getShipmentParticipantMapRandomSampleGenerator();
        CapaRecord capaRecordBack = getCapaRecordRandomSampleGenerator();

        shipmentParticipantMap.addCapaRecords(capaRecordBack);
        assertThat(shipmentParticipantMap.getCapaRecordses()).containsOnly(capaRecordBack);
        assertThat(capaRecordBack.getShipmentParticipantMap()).isEqualTo(shipmentParticipantMap);

        shipmentParticipantMap.removeCapaRecords(capaRecordBack);
        assertThat(shipmentParticipantMap.getCapaRecordses()).doesNotContain(capaRecordBack);
        assertThat(capaRecordBack.getShipmentParticipantMap()).isNull();

        shipmentParticipantMap.capaRecordses(new HashSet<>(Set.of(capaRecordBack)));
        assertThat(shipmentParticipantMap.getCapaRecordses()).containsOnly(capaRecordBack);
        assertThat(capaRecordBack.getShipmentParticipantMap()).isEqualTo(shipmentParticipantMap);

        shipmentParticipantMap.setCapaRecordses(new HashSet<>());
        assertThat(shipmentParticipantMap.getCapaRecordses()).doesNotContain(capaRecordBack);
        assertThat(capaRecordBack.getShipmentParticipantMap()).isNull();
    }

    @Test
    void modeOfReceiptTest() {
        ShipmentParticipantMap shipmentParticipantMap = getShipmentParticipantMapRandomSampleGenerator();
        ModeOfReceipt modeOfReceiptBack = getModeOfReceiptRandomSampleGenerator();

        shipmentParticipantMap.setModeOfReceipt(modeOfReceiptBack);
        assertThat(shipmentParticipantMap.getModeOfReceipt()).isEqualTo(modeOfReceiptBack);

        shipmentParticipantMap.modeOfReceipt(null);
        assertThat(shipmentParticipantMap.getModeOfReceipt()).isNull();
    }

    @Test
    void notTestedReasonTest() {
        ShipmentParticipantMap shipmentParticipantMap = getShipmentParticipantMapRandomSampleGenerator();
        NotTestedReason notTestedReasonBack = getNotTestedReasonRandomSampleGenerator();

        shipmentParticipantMap.setNotTestedReason(notTestedReasonBack);
        assertThat(shipmentParticipantMap.getNotTestedReason()).isEqualTo(notTestedReasonBack);

        shipmentParticipantMap.notTestedReason(null);
        assertThat(shipmentParticipantMap.getNotTestedReason()).isNull();
    }

    @Test
    void shipmentTest() {
        ShipmentParticipantMap shipmentParticipantMap = getShipmentParticipantMapRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        shipmentParticipantMap.setShipment(shipmentBack);
        assertThat(shipmentParticipantMap.getShipment()).isEqualTo(shipmentBack);

        shipmentParticipantMap.shipment(null);
        assertThat(shipmentParticipantMap.getShipment()).isNull();
    }

    @Test
    void participantTest() {
        ShipmentParticipantMap shipmentParticipantMap = getShipmentParticipantMapRandomSampleGenerator();
        Participant participantBack = getParticipantRandomSampleGenerator();

        shipmentParticipantMap.setParticipant(participantBack);
        assertThat(shipmentParticipantMap.getParticipant()).isEqualTo(participantBack);

        shipmentParticipantMap.participant(null);
        assertThat(shipmentParticipantMap.getParticipant()).isNull();
    }
}
