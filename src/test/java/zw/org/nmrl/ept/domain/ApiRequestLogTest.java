package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.ApiRequestLogTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ApiRequestLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApiRequestLog.class);
        ApiRequestLog apiRequestLog1 = getApiRequestLogSample1();
        ApiRequestLog apiRequestLog2 = new ApiRequestLog();
        assertThat(apiRequestLog1).isNotEqualTo(apiRequestLog2);

        apiRequestLog2.setId(apiRequestLog1.getId());
        assertThat(apiRequestLog1).isEqualTo(apiRequestLog2);

        apiRequestLog2 = getApiRequestLogSample2();
        assertThat(apiRequestLog1).isNotEqualTo(apiRequestLog2);
    }
}
