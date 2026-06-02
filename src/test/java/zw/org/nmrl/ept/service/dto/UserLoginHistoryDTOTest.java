package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class UserLoginHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserLoginHistoryDTO.class);
        UserLoginHistoryDTO userLoginHistoryDTO1 = new UserLoginHistoryDTO();
        userLoginHistoryDTO1.setId(1L);
        UserLoginHistoryDTO userLoginHistoryDTO2 = new UserLoginHistoryDTO();
        assertThat(userLoginHistoryDTO1).isNotEqualTo(userLoginHistoryDTO2);
        userLoginHistoryDTO2.setId(userLoginHistoryDTO1.getId());
        assertThat(userLoginHistoryDTO1).isEqualTo(userLoginHistoryDTO2);
        userLoginHistoryDTO2.setId(2L);
        assertThat(userLoginHistoryDTO1).isNotEqualTo(userLoginHistoryDTO2);
        userLoginHistoryDTO1.setId(null);
        assertThat(userLoginHistoryDTO1).isNotEqualTo(userLoginHistoryDTO2);
    }
}
