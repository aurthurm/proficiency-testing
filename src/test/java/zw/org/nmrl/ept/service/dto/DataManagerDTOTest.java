package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class DataManagerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataManagerDTO.class);
        DataManagerDTO dataManagerDTO1 = new DataManagerDTO();
        dataManagerDTO1.setId(1L);
        DataManagerDTO dataManagerDTO2 = new DataManagerDTO();
        assertThat(dataManagerDTO1).isNotEqualTo(dataManagerDTO2);
        dataManagerDTO2.setId(dataManagerDTO1.getId());
        assertThat(dataManagerDTO1).isEqualTo(dataManagerDTO2);
        dataManagerDTO2.setId(2L);
        assertThat(dataManagerDTO1).isNotEqualTo(dataManagerDTO2);
        dataManagerDTO1.setId(null);
        assertThat(dataManagerDTO1).isNotEqualTo(dataManagerDTO2);
    }
}
