package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CertificateBatchTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CertificateBatchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CertificateBatch.class);
        CertificateBatch certificateBatch1 = getCertificateBatchSample1();
        CertificateBatch certificateBatch2 = new CertificateBatch();
        assertThat(certificateBatch1).isNotEqualTo(certificateBatch2);

        certificateBatch2.setId(certificateBatch1.getId());
        assertThat(certificateBatch1).isEqualTo(certificateBatch2);

        certificateBatch2 = getCertificateBatchSample2();
        assertThat(certificateBatch1).isNotEqualTo(certificateBatch2);
    }

    @Test
    void shipmentsTest() {
        CertificateBatch certificateBatch = getCertificateBatchRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        certificateBatch.addShipments(shipmentBack);
        assertThat(certificateBatch.getShipmentses()).containsOnly(shipmentBack);

        certificateBatch.removeShipments(shipmentBack);
        assertThat(certificateBatch.getShipmentses()).doesNotContain(shipmentBack);

        certificateBatch.shipmentses(new HashSet<>(Set.of(shipmentBack)));
        assertThat(certificateBatch.getShipmentses()).containsOnly(shipmentBack);

        certificateBatch.setShipmentses(new HashSet<>());
        assertThat(certificateBatch.getShipmentses()).doesNotContain(shipmentBack);
    }
}
