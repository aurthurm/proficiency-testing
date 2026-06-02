package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class TestKitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TestKitDTO.class);
        TestKitDTO testKitDTO1 = new TestKitDTO();
        testKitDTO1.setId(1L);
        TestKitDTO testKitDTO2 = new TestKitDTO();
        assertThat(testKitDTO1).isNotEqualTo(testKitDTO2);
        testKitDTO2.setId(testKitDTO1.getId());
        assertThat(testKitDTO1).isEqualTo(testKitDTO2);
        testKitDTO2.setId(2L);
        assertThat(testKitDTO1).isNotEqualTo(testKitDTO2);
        testKitDTO1.setId(null);
        assertThat(testKitDTO1).isNotEqualTo(testKitDTO2);
    }
}
