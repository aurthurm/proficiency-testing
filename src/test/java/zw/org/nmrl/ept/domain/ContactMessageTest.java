package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.ContactMessageTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ContactMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContactMessage.class);
        ContactMessage contactMessage1 = getContactMessageSample1();
        ContactMessage contactMessage2 = new ContactMessage();
        assertThat(contactMessage1).isNotEqualTo(contactMessage2);

        contactMessage2.setId(contactMessage1.getId());
        assertThat(contactMessage1).isEqualTo(contactMessage2);

        contactMessage2 = getContactMessageSample2();
        assertThat(contactMessage1).isNotEqualTo(contactMessage2);
    }
}
