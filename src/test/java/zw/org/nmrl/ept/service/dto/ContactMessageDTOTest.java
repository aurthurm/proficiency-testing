package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ContactMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContactMessageDTO.class);
        ContactMessageDTO contactMessageDTO1 = new ContactMessageDTO();
        contactMessageDTO1.setId(1L);
        ContactMessageDTO contactMessageDTO2 = new ContactMessageDTO();
        assertThat(contactMessageDTO1).isNotEqualTo(contactMessageDTO2);
        contactMessageDTO2.setId(contactMessageDTO1.getId());
        assertThat(contactMessageDTO1).isEqualTo(contactMessageDTO2);
        contactMessageDTO2.setId(2L);
        assertThat(contactMessageDTO1).isNotEqualTo(contactMessageDTO2);
        contactMessageDTO1.setId(null);
        assertThat(contactMessageDTO1).isNotEqualTo(contactMessageDTO2);
    }
}
