package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ShipmentSampleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentSampleDTO.class);
        ShipmentSampleDTO shipmentSampleDTO1 = new ShipmentSampleDTO();
        shipmentSampleDTO1.setId(1L);
        ShipmentSampleDTO shipmentSampleDTO2 = new ShipmentSampleDTO();
        assertThat(shipmentSampleDTO1).isNotEqualTo(shipmentSampleDTO2);
        shipmentSampleDTO2.setId(shipmentSampleDTO1.getId());
        assertThat(shipmentSampleDTO1).isEqualTo(shipmentSampleDTO2);
        shipmentSampleDTO2.setId(2L);
        assertThat(shipmentSampleDTO1).isNotEqualTo(shipmentSampleDTO2);
        shipmentSampleDTO1.setId(null);
        assertThat(shipmentSampleDTO1).isNotEqualTo(shipmentSampleDTO2);
    }
}
