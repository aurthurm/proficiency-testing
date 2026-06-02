package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class AssayDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssayDTO.class);
        AssayDTO assayDTO1 = new AssayDTO();
        assayDTO1.setId(1L);
        AssayDTO assayDTO2 = new AssayDTO();
        assertThat(assayDTO1).isNotEqualTo(assayDTO2);
        assayDTO2.setId(assayDTO1.getId());
        assertThat(assayDTO1).isEqualTo(assayDTO2);
        assayDTO2.setId(2L);
        assertThat(assayDTO1).isNotEqualTo(assayDTO2);
        assayDTO1.setId(null);
        assertThat(assayDTO1).isNotEqualTo(assayDTO2);
    }
}
