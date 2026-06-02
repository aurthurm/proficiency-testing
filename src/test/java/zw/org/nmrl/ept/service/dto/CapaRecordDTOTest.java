package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CapaRecordDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CapaRecordDTO.class);
        CapaRecordDTO capaRecordDTO1 = new CapaRecordDTO();
        capaRecordDTO1.setId(1L);
        CapaRecordDTO capaRecordDTO2 = new CapaRecordDTO();
        assertThat(capaRecordDTO1).isNotEqualTo(capaRecordDTO2);
        capaRecordDTO2.setId(capaRecordDTO1.getId());
        assertThat(capaRecordDTO1).isEqualTo(capaRecordDTO2);
        capaRecordDTO2.setId(2L);
        assertThat(capaRecordDTO1).isNotEqualTo(capaRecordDTO2);
        capaRecordDTO1.setId(null);
        assertThat(capaRecordDTO1).isNotEqualTo(capaRecordDTO2);
    }
}
