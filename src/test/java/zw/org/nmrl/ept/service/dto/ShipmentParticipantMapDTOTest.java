package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ShipmentParticipantMapDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShipmentParticipantMapDTO.class);
        ShipmentParticipantMapDTO shipmentParticipantMapDTO1 = new ShipmentParticipantMapDTO();
        shipmentParticipantMapDTO1.setId(1L);
        ShipmentParticipantMapDTO shipmentParticipantMapDTO2 = new ShipmentParticipantMapDTO();
        assertThat(shipmentParticipantMapDTO1).isNotEqualTo(shipmentParticipantMapDTO2);
        shipmentParticipantMapDTO2.setId(shipmentParticipantMapDTO1.getId());
        assertThat(shipmentParticipantMapDTO1).isEqualTo(shipmentParticipantMapDTO2);
        shipmentParticipantMapDTO2.setId(2L);
        assertThat(shipmentParticipantMapDTO1).isNotEqualTo(shipmentParticipantMapDTO2);
        shipmentParticipantMapDTO1.setId(null);
        assertThat(shipmentParticipantMapDTO1).isNotEqualTo(shipmentParticipantMapDTO2);
    }
}
