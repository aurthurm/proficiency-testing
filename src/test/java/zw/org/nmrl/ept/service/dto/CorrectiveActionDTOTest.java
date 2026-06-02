package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CorrectiveActionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CorrectiveActionDTO.class);
        CorrectiveActionDTO correctiveActionDTO1 = new CorrectiveActionDTO();
        correctiveActionDTO1.setId(1L);
        CorrectiveActionDTO correctiveActionDTO2 = new CorrectiveActionDTO();
        assertThat(correctiveActionDTO1).isNotEqualTo(correctiveActionDTO2);
        correctiveActionDTO2.setId(correctiveActionDTO1.getId());
        assertThat(correctiveActionDTO1).isEqualTo(correctiveActionDTO2);
        correctiveActionDTO2.setId(2L);
        assertThat(correctiveActionDTO1).isNotEqualTo(correctiveActionDTO2);
        correctiveActionDTO1.setId(null);
        assertThat(correctiveActionDTO1).isNotEqualTo(correctiveActionDTO2);
    }
}
