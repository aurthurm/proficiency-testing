package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CertificateTemplateTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CertificateTemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CertificateTemplate.class);
        CertificateTemplate certificateTemplate1 = getCertificateTemplateSample1();
        CertificateTemplate certificateTemplate2 = new CertificateTemplate();
        assertThat(certificateTemplate1).isNotEqualTo(certificateTemplate2);

        certificateTemplate2.setId(certificateTemplate1.getId());
        assertThat(certificateTemplate1).isEqualTo(certificateTemplate2);

        certificateTemplate2 = getCertificateTemplateSample2();
        assertThat(certificateTemplate1).isNotEqualTo(certificateTemplate2);
    }

    @Test
    void schemeTest() {
        CertificateTemplate certificateTemplate = getCertificateTemplateRandomSampleGenerator();
        Scheme schemeBack = getSchemeRandomSampleGenerator();

        certificateTemplate.setScheme(schemeBack);
        assertThat(certificateTemplate.getScheme()).isEqualTo(schemeBack);
        assertThat(schemeBack.getCertificateTemplate()).isEqualTo(certificateTemplate);

        certificateTemplate.scheme(null);
        assertThat(certificateTemplate.getScheme()).isNull();
        assertThat(schemeBack.getCertificateTemplate()).isNull();
    }
}
