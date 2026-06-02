package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.SchemeConfigurationTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class SchemeConfigurationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SchemeConfiguration.class);
        SchemeConfiguration schemeConfiguration1 = getSchemeConfigurationSample1();
        SchemeConfiguration schemeConfiguration2 = new SchemeConfiguration();
        assertThat(schemeConfiguration1).isNotEqualTo(schemeConfiguration2);

        schemeConfiguration2.setId(schemeConfiguration1.getId());
        assertThat(schemeConfiguration1).isEqualTo(schemeConfiguration2);

        schemeConfiguration2 = getSchemeConfigurationSample2();
        assertThat(schemeConfiguration1).isNotEqualTo(schemeConfiguration2);
    }

    @Test
    void schemeTest() {
        SchemeConfiguration schemeConfiguration = getSchemeConfigurationRandomSampleGenerator();
        Scheme schemeBack = getSchemeRandomSampleGenerator();

        schemeConfiguration.setScheme(schemeBack);
        assertThat(schemeConfiguration.getScheme()).isEqualTo(schemeBack);

        schemeConfiguration.scheme(null);
        assertThat(schemeConfiguration.getScheme()).isNull();
    }
}
