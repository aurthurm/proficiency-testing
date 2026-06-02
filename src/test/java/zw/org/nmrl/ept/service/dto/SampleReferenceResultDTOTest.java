package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class SampleReferenceResultDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SampleReferenceResultDTO.class);
        SampleReferenceResultDTO sampleReferenceResultDTO1 = new SampleReferenceResultDTO();
        sampleReferenceResultDTO1.setId(1L);
        SampleReferenceResultDTO sampleReferenceResultDTO2 = new SampleReferenceResultDTO();
        assertThat(sampleReferenceResultDTO1).isNotEqualTo(sampleReferenceResultDTO2);
        sampleReferenceResultDTO2.setId(sampleReferenceResultDTO1.getId());
        assertThat(sampleReferenceResultDTO1).isEqualTo(sampleReferenceResultDTO2);
        sampleReferenceResultDTO2.setId(2L);
        assertThat(sampleReferenceResultDTO1).isNotEqualTo(sampleReferenceResultDTO2);
        sampleReferenceResultDTO1.setId(null);
        assertThat(sampleReferenceResultDTO1).isNotEqualTo(sampleReferenceResultDTO2);
    }
}
