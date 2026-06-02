package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.NotTestedReasonTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class NotTestedReasonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotTestedReason.class);
        NotTestedReason notTestedReason1 = getNotTestedReasonSample1();
        NotTestedReason notTestedReason2 = new NotTestedReason();
        assertThat(notTestedReason1).isNotEqualTo(notTestedReason2);

        notTestedReason2.setId(notTestedReason1.getId());
        assertThat(notTestedReason1).isEqualTo(notTestedReason2);

        notTestedReason2 = getNotTestedReasonSample2();
        assertThat(notTestedReason1).isNotEqualTo(notTestedReason2);
    }
}
