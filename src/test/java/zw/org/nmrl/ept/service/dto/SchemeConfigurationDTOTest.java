package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class SchemeConfigurationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SchemeConfigurationDTO.class);
        SchemeConfigurationDTO schemeConfigurationDTO1 = new SchemeConfigurationDTO();
        schemeConfigurationDTO1.setId(1L);
        SchemeConfigurationDTO schemeConfigurationDTO2 = new SchemeConfigurationDTO();
        assertThat(schemeConfigurationDTO1).isNotEqualTo(schemeConfigurationDTO2);
        schemeConfigurationDTO2.setId(schemeConfigurationDTO1.getId());
        assertThat(schemeConfigurationDTO1).isEqualTo(schemeConfigurationDTO2);
        schemeConfigurationDTO2.setId(2L);
        assertThat(schemeConfigurationDTO1).isNotEqualTo(schemeConfigurationDTO2);
        schemeConfigurationDTO1.setId(null);
        assertThat(schemeConfigurationDTO1).isNotEqualTo(schemeConfigurationDTO2);
    }
}
