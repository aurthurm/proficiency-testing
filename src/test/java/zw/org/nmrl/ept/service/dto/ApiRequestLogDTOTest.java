package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ApiRequestLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApiRequestLogDTO.class);
        ApiRequestLogDTO apiRequestLogDTO1 = new ApiRequestLogDTO();
        apiRequestLogDTO1.setId(1L);
        ApiRequestLogDTO apiRequestLogDTO2 = new ApiRequestLogDTO();
        assertThat(apiRequestLogDTO1).isNotEqualTo(apiRequestLogDTO2);
        apiRequestLogDTO2.setId(apiRequestLogDTO1.getId());
        assertThat(apiRequestLogDTO1).isEqualTo(apiRequestLogDTO2);
        apiRequestLogDTO2.setId(2L);
        assertThat(apiRequestLogDTO1).isNotEqualTo(apiRequestLogDTO2);
        apiRequestLogDTO1.setId(null);
        assertThat(apiRequestLogDTO1).isNotEqualTo(apiRequestLogDTO2);
    }
}
