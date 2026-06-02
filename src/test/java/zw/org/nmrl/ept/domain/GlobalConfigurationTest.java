package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.GlobalConfigurationTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class GlobalConfigurationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GlobalConfiguration.class);
        GlobalConfiguration globalConfiguration1 = getGlobalConfigurationSample1();
        GlobalConfiguration globalConfiguration2 = new GlobalConfiguration();
        assertThat(globalConfiguration1).isNotEqualTo(globalConfiguration2);

        globalConfiguration2.setId(globalConfiguration1.getId());
        assertThat(globalConfiguration1).isEqualTo(globalConfiguration2);

        globalConfiguration2 = getGlobalConfigurationSample2();
        assertThat(globalConfiguration1).isNotEqualTo(globalConfiguration2);
    }
}
