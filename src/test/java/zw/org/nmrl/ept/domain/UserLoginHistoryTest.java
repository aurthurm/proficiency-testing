package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.UserLoginHistoryTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class UserLoginHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserLoginHistory.class);
        UserLoginHistory userLoginHistory1 = getUserLoginHistorySample1();
        UserLoginHistory userLoginHistory2 = new UserLoginHistory();
        assertThat(userLoginHistory1).isNotEqualTo(userLoginHistory2);

        userLoginHistory2.setId(userLoginHistory1.getId());
        assertThat(userLoginHistory1).isEqualTo(userLoginHistory2);

        userLoginHistory2 = getUserLoginHistorySample2();
        assertThat(userLoginHistory1).isNotEqualTo(userLoginHistory2);
    }
}
