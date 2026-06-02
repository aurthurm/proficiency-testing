package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CertificateBatchTestSamples.*;
import static zw.org.nmrl.ept.domain.DistributionTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentParticipantMapTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentSampleTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ShipmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shipment.class);
        Shipment shipment1 = getShipmentSample1();
        Shipment shipment2 = new Shipment();
        assertThat(shipment1).isNotEqualTo(shipment2);

        shipment2.setId(shipment1.getId());
        assertThat(shipment1).isEqualTo(shipment2);

        shipment2 = getShipmentSample2();
        assertThat(shipment1).isNotEqualTo(shipment2);
    }

    @Test
    void samplesTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        ShipmentSample shipmentSampleBack = getShipmentSampleRandomSampleGenerator();

        shipment.addSamples(shipmentSampleBack);
        assertThat(shipment.getSampleses()).containsOnly(shipmentSampleBack);
        assertThat(shipmentSampleBack.getShipment()).isEqualTo(shipment);

        shipment.removeSamples(shipmentSampleBack);
        assertThat(shipment.getSampleses()).doesNotContain(shipmentSampleBack);
        assertThat(shipmentSampleBack.getShipment()).isNull();

        shipment.sampleses(new HashSet<>(Set.of(shipmentSampleBack)));
        assertThat(shipment.getSampleses()).containsOnly(shipmentSampleBack);
        assertThat(shipmentSampleBack.getShipment()).isEqualTo(shipment);

        shipment.setSampleses(new HashSet<>());
        assertThat(shipment.getSampleses()).doesNotContain(shipmentSampleBack);
        assertThat(shipmentSampleBack.getShipment()).isNull();
    }

    @Test
    void participantMapsTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        ShipmentParticipantMap shipmentParticipantMapBack = getShipmentParticipantMapRandomSampleGenerator();

        shipment.addParticipantMaps(shipmentParticipantMapBack);
        assertThat(shipment.getParticipantMapses()).containsOnly(shipmentParticipantMapBack);
        assertThat(shipmentParticipantMapBack.getShipment()).isEqualTo(shipment);

        shipment.removeParticipantMaps(shipmentParticipantMapBack);
        assertThat(shipment.getParticipantMapses()).doesNotContain(shipmentParticipantMapBack);
        assertThat(shipmentParticipantMapBack.getShipment()).isNull();

        shipment.participantMapses(new HashSet<>(Set.of(shipmentParticipantMapBack)));
        assertThat(shipment.getParticipantMapses()).containsOnly(shipmentParticipantMapBack);
        assertThat(shipmentParticipantMapBack.getShipment()).isEqualTo(shipment);

        shipment.setParticipantMapses(new HashSet<>());
        assertThat(shipment.getParticipantMapses()).doesNotContain(shipmentParticipantMapBack);
        assertThat(shipmentParticipantMapBack.getShipment()).isNull();
    }

    @Test
    void distributionTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Distribution distributionBack = getDistributionRandomSampleGenerator();

        shipment.setDistribution(distributionBack);
        assertThat(shipment.getDistribution()).isEqualTo(distributionBack);

        shipment.distribution(null);
        assertThat(shipment.getDistribution()).isNull();
    }

    @Test
    void schemeTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Scheme schemeBack = getSchemeRandomSampleGenerator();

        shipment.setScheme(schemeBack);
        assertThat(shipment.getScheme()).isEqualTo(schemeBack);

        shipment.scheme(null);
        assertThat(shipment.getScheme()).isNull();
    }

    @Test
    void certificateBatchesTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        CertificateBatch certificateBatchBack = getCertificateBatchRandomSampleGenerator();

        shipment.addCertificateBatches(certificateBatchBack);
        assertThat(shipment.getCertificateBatcheses()).containsOnly(certificateBatchBack);
        assertThat(certificateBatchBack.getShipmentses()).containsOnly(shipment);

        shipment.removeCertificateBatches(certificateBatchBack);
        assertThat(shipment.getCertificateBatcheses()).doesNotContain(certificateBatchBack);
        assertThat(certificateBatchBack.getShipmentses()).doesNotContain(shipment);

        shipment.certificateBatcheses(new HashSet<>(Set.of(certificateBatchBack)));
        assertThat(shipment.getCertificateBatcheses()).containsOnly(certificateBatchBack);
        assertThat(certificateBatchBack.getShipmentses()).containsOnly(shipment);

        shipment.setCertificateBatcheses(new HashSet<>());
        assertThat(shipment.getCertificateBatcheses()).doesNotContain(certificateBatchBack);
        assertThat(certificateBatchBack.getShipmentses()).doesNotContain(shipment);
    }
}
