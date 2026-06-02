package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantCustomValueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParticipantCustomValueDTO.class);
        ParticipantCustomValueDTO participantCustomValueDTO1 = new ParticipantCustomValueDTO();
        participantCustomValueDTO1.setId(1L);
        ParticipantCustomValueDTO participantCustomValueDTO2 = new ParticipantCustomValueDTO();
        assertThat(participantCustomValueDTO1).isNotEqualTo(participantCustomValueDTO2);
        participantCustomValueDTO2.setId(participantCustomValueDTO1.getId());
        assertThat(participantCustomValueDTO1).isEqualTo(participantCustomValueDTO2);
        participantCustomValueDTO2.setId(2L);
        assertThat(participantCustomValueDTO1).isNotEqualTo(participantCustomValueDTO2);
        participantCustomValueDTO1.setId(null);
        assertThat(participantCustomValueDTO1).isNotEqualTo(participantCustomValueDTO2);
    }
}
