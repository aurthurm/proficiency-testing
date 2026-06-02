package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class NotTestedReasonDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotTestedReasonDTO.class);
        NotTestedReasonDTO notTestedReasonDTO1 = new NotTestedReasonDTO();
        notTestedReasonDTO1.setId(1L);
        NotTestedReasonDTO notTestedReasonDTO2 = new NotTestedReasonDTO();
        assertThat(notTestedReasonDTO1).isNotEqualTo(notTestedReasonDTO2);
        notTestedReasonDTO2.setId(notTestedReasonDTO1.getId());
        assertThat(notTestedReasonDTO1).isEqualTo(notTestedReasonDTO2);
        notTestedReasonDTO2.setId(2L);
        assertThat(notTestedReasonDTO1).isNotEqualTo(notTestedReasonDTO2);
        notTestedReasonDTO1.setId(null);
        assertThat(notTestedReasonDTO1).isNotEqualTo(notTestedReasonDTO2);
    }
}
