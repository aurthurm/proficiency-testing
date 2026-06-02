package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.AssayTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class AssayTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Assay.class);
        Assay assay1 = getAssaySample1();
        Assay assay2 = new Assay();
        assertThat(assay1).isNotEqualTo(assay2);

        assay2.setId(assay1.getId());
        assertThat(assay1).isEqualTo(assay2);

        assay2 = getAssaySample2();
        assertThat(assay1).isNotEqualTo(assay2);
    }

    @Test
    void schemeTest() {
        Assay assay = getAssayRandomSampleGenerator();
        Scheme schemeBack = getSchemeRandomSampleGenerator();

        assay.setScheme(schemeBack);
        assertThat(assay.getScheme()).isEqualTo(schemeBack);

        assay.scheme(null);
        assertThat(assay.getScheme()).isNull();
    }
}
