package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CertificateTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CertificateTemplateDTO.class);
        CertificateTemplateDTO certificateTemplateDTO1 = new CertificateTemplateDTO();
        certificateTemplateDTO1.setId(1L);
        CertificateTemplateDTO certificateTemplateDTO2 = new CertificateTemplateDTO();
        assertThat(certificateTemplateDTO1).isNotEqualTo(certificateTemplateDTO2);
        certificateTemplateDTO2.setId(certificateTemplateDTO1.getId());
        assertThat(certificateTemplateDTO1).isEqualTo(certificateTemplateDTO2);
        certificateTemplateDTO2.setId(2L);
        assertThat(certificateTemplateDTO1).isNotEqualTo(certificateTemplateDTO2);
        certificateTemplateDTO1.setId(null);
        assertThat(certificateTemplateDTO1).isNotEqualTo(certificateTemplateDTO2);
    }
}
