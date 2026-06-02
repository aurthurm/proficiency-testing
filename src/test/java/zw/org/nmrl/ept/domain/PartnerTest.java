package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.PartnerTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class PartnerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Partner.class);
        Partner partner1 = getPartnerSample1();
        Partner partner2 = new Partner();
        assertThat(partner1).isNotEqualTo(partner2);

        partner2.setId(partner1.getId());
        assertThat(partner1).isEqualTo(partner2);

        partner2 = getPartnerSample2();
        assertThat(partner1).isNotEqualTo(partner2);
    }
}
