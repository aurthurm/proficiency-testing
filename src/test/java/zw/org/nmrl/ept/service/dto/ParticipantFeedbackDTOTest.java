package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantFeedbackDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParticipantFeedbackDTO.class);
        ParticipantFeedbackDTO participantFeedbackDTO1 = new ParticipantFeedbackDTO();
        participantFeedbackDTO1.setId(1L);
        ParticipantFeedbackDTO participantFeedbackDTO2 = new ParticipantFeedbackDTO();
        assertThat(participantFeedbackDTO1).isNotEqualTo(participantFeedbackDTO2);
        participantFeedbackDTO2.setId(participantFeedbackDTO1.getId());
        assertThat(participantFeedbackDTO1).isEqualTo(participantFeedbackDTO2);
        participantFeedbackDTO2.setId(2L);
        assertThat(participantFeedbackDTO1).isNotEqualTo(participantFeedbackDTO2);
        participantFeedbackDTO1.setId(null);
        assertThat(participantFeedbackDTO1).isNotEqualTo(participantFeedbackDTO2);
    }
}
