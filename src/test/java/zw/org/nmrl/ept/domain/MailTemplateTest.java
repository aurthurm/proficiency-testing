package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.MailTemplateTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class MailTemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MailTemplate.class);
        MailTemplate mailTemplate1 = getMailTemplateSample1();
        MailTemplate mailTemplate2 = new MailTemplate();
        assertThat(mailTemplate1).isNotEqualTo(mailTemplate2);

        mailTemplate2.setId(mailTemplate1.getId());
        assertThat(mailTemplate1).isEqualTo(mailTemplate2);

        mailTemplate2 = getMailTemplateSample2();
        assertThat(mailTemplate1).isNotEqualTo(mailTemplate2);
    }
}
