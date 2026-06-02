package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ReportConfigurationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReportConfigurationDTO.class);
        ReportConfigurationDTO reportConfigurationDTO1 = new ReportConfigurationDTO();
        reportConfigurationDTO1.setId(1L);
        ReportConfigurationDTO reportConfigurationDTO2 = new ReportConfigurationDTO();
        assertThat(reportConfigurationDTO1).isNotEqualTo(reportConfigurationDTO2);
        reportConfigurationDTO2.setId(reportConfigurationDTO1.getId());
        assertThat(reportConfigurationDTO1).isEqualTo(reportConfigurationDTO2);
        reportConfigurationDTO2.setId(2L);
        assertThat(reportConfigurationDTO1).isNotEqualTo(reportConfigurationDTO2);
        reportConfigurationDTO1.setId(null);
        assertThat(reportConfigurationDTO1).isNotEqualTo(reportConfigurationDTO2);
    }
}
