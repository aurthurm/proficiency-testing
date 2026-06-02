package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class MailTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MailTemplateDTO.class);
        MailTemplateDTO mailTemplateDTO1 = new MailTemplateDTO();
        mailTemplateDTO1.setId(1L);
        MailTemplateDTO mailTemplateDTO2 = new MailTemplateDTO();
        assertThat(mailTemplateDTO1).isNotEqualTo(mailTemplateDTO2);
        mailTemplateDTO2.setId(mailTemplateDTO1.getId());
        assertThat(mailTemplateDTO1).isEqualTo(mailTemplateDTO2);
        mailTemplateDTO2.setId(2L);
        assertThat(mailTemplateDTO1).isNotEqualTo(mailTemplateDTO2);
        mailTemplateDTO1.setId(null);
        assertThat(mailTemplateDTO1).isNotEqualTo(mailTemplateDTO2);
    }
}
