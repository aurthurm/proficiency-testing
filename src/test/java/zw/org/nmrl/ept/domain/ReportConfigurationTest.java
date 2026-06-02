package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.ReportConfigurationTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ReportConfigurationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReportConfiguration.class);
        ReportConfiguration reportConfiguration1 = getReportConfigurationSample1();
        ReportConfiguration reportConfiguration2 = new ReportConfiguration();
        assertThat(reportConfiguration1).isNotEqualTo(reportConfiguration2);

        reportConfiguration2.setId(reportConfiguration1.getId());
        assertThat(reportConfiguration1).isEqualTo(reportConfiguration2);

        reportConfiguration2 = getReportConfigurationSample2();
        assertThat(reportConfiguration1).isNotEqualTo(reportConfiguration2);
    }

    @Test
    void schemeTest() {
        ReportConfiguration reportConfiguration = getReportConfigurationRandomSampleGenerator();
        Scheme schemeBack = getSchemeRandomSampleGenerator();

        reportConfiguration.setScheme(schemeBack);
        assertThat(reportConfiguration.getScheme()).isEqualTo(schemeBack);

        reportConfiguration.scheme(null);
        assertThat(reportConfiguration.getScheme()).isNull();
    }
}
