package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.DistributionTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class DistributionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Distribution.class);
        Distribution distribution1 = getDistributionSample1();
        Distribution distribution2 = new Distribution();
        assertThat(distribution1).isNotEqualTo(distribution2);

        distribution2.setId(distribution1.getId());
        assertThat(distribution1).isEqualTo(distribution2);

        distribution2 = getDistributionSample2();
        assertThat(distribution1).isNotEqualTo(distribution2);
    }

    @Test
    void shipmentsTest() {
        Distribution distribution = getDistributionRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        distribution.addShipments(shipmentBack);
        assertThat(distribution.getShipmentses()).containsOnly(shipmentBack);
        assertThat(shipmentBack.getDistribution()).isEqualTo(distribution);

        distribution.removeShipments(shipmentBack);
        assertThat(distribution.getShipmentses()).doesNotContain(shipmentBack);
        assertThat(shipmentBack.getDistribution()).isNull();

        distribution.shipmentses(new HashSet<>(Set.of(shipmentBack)));
        assertThat(distribution.getShipmentses()).containsOnly(shipmentBack);
        assertThat(shipmentBack.getDistribution()).isEqualTo(distribution);

        distribution.setShipmentses(new HashSet<>());
        assertThat(distribution.getShipmentses()).doesNotContain(shipmentBack);
        assertThat(shipmentBack.getDistribution()).isNull();
    }
}
