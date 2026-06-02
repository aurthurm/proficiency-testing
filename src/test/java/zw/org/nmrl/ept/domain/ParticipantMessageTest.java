package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.ParticipantMessageTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParticipantMessage.class);
        ParticipantMessage participantMessage1 = getParticipantMessageSample1();
        ParticipantMessage participantMessage2 = new ParticipantMessage();
        assertThat(participantMessage1).isNotEqualTo(participantMessage2);

        participantMessage2.setId(participantMessage1.getId());
        assertThat(participantMessage1).isEqualTo(participantMessage2);

        participantMessage2 = getParticipantMessageSample2();
        assertThat(participantMessage1).isNotEqualTo(participantMessage2);
    }

    @Test
    void participantTest() {
        ParticipantMessage participantMessage = getParticipantMessageRandomSampleGenerator();
        Participant participantBack = getParticipantRandomSampleGenerator();

        participantMessage.setParticipant(participantBack);
        assertThat(participantMessage.getParticipant()).isEqualTo(participantBack);

        participantMessage.participant(null);
        assertThat(participantMessage.getParticipant()).isNull();
    }
}
