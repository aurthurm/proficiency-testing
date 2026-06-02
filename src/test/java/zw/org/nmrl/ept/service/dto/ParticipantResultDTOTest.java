package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantResultDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParticipantResultDTO.class);
        ParticipantResultDTO participantResultDTO1 = new ParticipantResultDTO();
        participantResultDTO1.setId(1L);
        ParticipantResultDTO participantResultDTO2 = new ParticipantResultDTO();
        assertThat(participantResultDTO1).isNotEqualTo(participantResultDTO2);
        participantResultDTO2.setId(participantResultDTO1.getId());
        assertThat(participantResultDTO1).isEqualTo(participantResultDTO2);
        participantResultDTO2.setId(2L);
        assertThat(participantResultDTO1).isNotEqualTo(participantResultDTO2);
        participantResultDTO1.setId(null);
        assertThat(participantResultDTO1).isNotEqualTo(participantResultDTO2);
    }
}
