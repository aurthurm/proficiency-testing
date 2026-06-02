package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.EmailMessageTestSamples.*;
import static zw.org.nmrl.ept.domain.MailTemplateTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class EmailMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmailMessage.class);
        EmailMessage emailMessage1 = getEmailMessageSample1();
        EmailMessage emailMessage2 = new EmailMessage();
        assertThat(emailMessage1).isNotEqualTo(emailMessage2);

        emailMessage2.setId(emailMessage1.getId());
        assertThat(emailMessage1).isEqualTo(emailMessage2);

        emailMessage2 = getEmailMessageSample2();
        assertThat(emailMessage1).isNotEqualTo(emailMessage2);
    }

    @Test
    void templateTest() {
        EmailMessage emailMessage = getEmailMessageRandomSampleGenerator();
        MailTemplate mailTemplateBack = getMailTemplateRandomSampleGenerator();

        emailMessage.setTemplate(mailTemplateBack);
        assertThat(emailMessage.getTemplate()).isEqualTo(mailTemplateBack);

        emailMessage.template(null);
        assertThat(emailMessage.getTemplate()).isNull();
    }
}
