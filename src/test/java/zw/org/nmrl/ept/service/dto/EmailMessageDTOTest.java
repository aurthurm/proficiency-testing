package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class EmailMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmailMessageDTO.class);
        EmailMessageDTO emailMessageDTO1 = new EmailMessageDTO();
        emailMessageDTO1.setId(1L);
        EmailMessageDTO emailMessageDTO2 = new EmailMessageDTO();
        assertThat(emailMessageDTO1).isNotEqualTo(emailMessageDTO2);
        emailMessageDTO2.setId(emailMessageDTO1.getId());
        assertThat(emailMessageDTO1).isEqualTo(emailMessageDTO2);
        emailMessageDTO2.setId(2L);
        assertThat(emailMessageDTO1).isNotEqualTo(emailMessageDTO2);
        emailMessageDTO1.setId(null);
        assertThat(emailMessageDTO1).isNotEqualTo(emailMessageDTO2);
    }
}
