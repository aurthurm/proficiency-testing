package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class GlobalConfigurationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GlobalConfigurationDTO.class);
        GlobalConfigurationDTO globalConfigurationDTO1 = new GlobalConfigurationDTO();
        globalConfigurationDTO1.setId(1L);
        GlobalConfigurationDTO globalConfigurationDTO2 = new GlobalConfigurationDTO();
        assertThat(globalConfigurationDTO1).isNotEqualTo(globalConfigurationDTO2);
        globalConfigurationDTO2.setId(globalConfigurationDTO1.getId());
        assertThat(globalConfigurationDTO1).isEqualTo(globalConfigurationDTO2);
        globalConfigurationDTO2.setId(2L);
        assertThat(globalConfigurationDTO1).isNotEqualTo(globalConfigurationDTO2);
        globalConfigurationDTO1.setId(null);
        assertThat(globalConfigurationDTO1).isNotEqualTo(globalConfigurationDTO2);
    }
}
