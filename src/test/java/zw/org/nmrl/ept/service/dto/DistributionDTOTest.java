package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class DistributionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DistributionDTO.class);
        DistributionDTO distributionDTO1 = new DistributionDTO();
        distributionDTO1.setId(1L);
        DistributionDTO distributionDTO2 = new DistributionDTO();
        assertThat(distributionDTO1).isNotEqualTo(distributionDTO2);
        distributionDTO2.setId(distributionDTO1.getId());
        assertThat(distributionDTO1).isEqualTo(distributionDTO2);
        distributionDTO2.setId(2L);
        assertThat(distributionDTO1).isNotEqualTo(distributionDTO2);
        distributionDTO1.setId(null);
        assertThat(distributionDTO1).isNotEqualTo(distributionDTO2);
    }
}
