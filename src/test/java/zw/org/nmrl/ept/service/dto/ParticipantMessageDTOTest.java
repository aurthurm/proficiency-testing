package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParticipantMessageDTO.class);
        ParticipantMessageDTO participantMessageDTO1 = new ParticipantMessageDTO();
        participantMessageDTO1.setId(1L);
        ParticipantMessageDTO participantMessageDTO2 = new ParticipantMessageDTO();
        assertThat(participantMessageDTO1).isNotEqualTo(participantMessageDTO2);
        participantMessageDTO2.setId(participantMessageDTO1.getId());
        assertThat(participantMessageDTO1).isEqualTo(participantMessageDTO2);
        participantMessageDTO2.setId(2L);
        assertThat(participantMessageDTO1).isNotEqualTo(participantMessageDTO2);
        participantMessageDTO1.setId(null);
        assertThat(participantMessageDTO1).isNotEqualTo(participantMessageDTO2);
    }
}
