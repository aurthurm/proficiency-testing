package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CorrectiveActionTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CorrectiveActionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CorrectiveAction.class);
        CorrectiveAction correctiveAction1 = getCorrectiveActionSample1();
        CorrectiveAction correctiveAction2 = new CorrectiveAction();
        assertThat(correctiveAction1).isNotEqualTo(correctiveAction2);

        correctiveAction2.setId(correctiveAction1.getId());
        assertThat(correctiveAction1).isEqualTo(correctiveAction2);

        correctiveAction2 = getCorrectiveActionSample2();
        assertThat(correctiveAction1).isNotEqualTo(correctiveAction2);
    }

    @Test
    void schemeTest() {
        CorrectiveAction correctiveAction = getCorrectiveActionRandomSampleGenerator();
        Scheme schemeBack = getSchemeRandomSampleGenerator();

        correctiveAction.setScheme(schemeBack);
        assertThat(correctiveAction.getScheme()).isEqualTo(schemeBack);

        correctiveAction.scheme(null);
        assertThat(correctiveAction.getScheme()).isNull();
    }
}
