package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;
import static zw.org.nmrl.ept.domain.TestKitTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class TestKitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TestKit.class);
        TestKit testKit1 = getTestKitSample1();
        TestKit testKit2 = new TestKit();
        assertThat(testKit1).isNotEqualTo(testKit2);

        testKit2.setId(testKit1.getId());
        assertThat(testKit1).isEqualTo(testKit2);

        testKit2 = getTestKitSample2();
        assertThat(testKit1).isNotEqualTo(testKit2);
    }

    @Test
    void schemeTest() {
        TestKit testKit = getTestKitRandomSampleGenerator();
        Scheme schemeBack = getSchemeRandomSampleGenerator();

        testKit.setScheme(schemeBack);
        assertThat(testKit.getScheme()).isEqualTo(schemeBack);

        testKit.scheme(null);
        assertThat(testKit.getScheme()).isNull();
    }
}
