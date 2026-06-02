package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.FeedbackQuestionTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantFeedbackTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantFeedbackTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParticipantFeedback.class);
        ParticipantFeedback participantFeedback1 = getParticipantFeedbackSample1();
        ParticipantFeedback participantFeedback2 = new ParticipantFeedback();
        assertThat(participantFeedback1).isNotEqualTo(participantFeedback2);

        participantFeedback2.setId(participantFeedback1.getId());
        assertThat(participantFeedback1).isEqualTo(participantFeedback2);

        participantFeedback2 = getParticipantFeedbackSample2();
        assertThat(participantFeedback1).isNotEqualTo(participantFeedback2);
    }

    @Test
    void questionTest() {
        ParticipantFeedback participantFeedback = getParticipantFeedbackRandomSampleGenerator();
        FeedbackQuestion feedbackQuestionBack = getFeedbackQuestionRandomSampleGenerator();

        participantFeedback.setQuestion(feedbackQuestionBack);
        assertThat(participantFeedback.getQuestion()).isEqualTo(feedbackQuestionBack);

        participantFeedback.question(null);
        assertThat(participantFeedback.getQuestion()).isNull();
    }

    @Test
    void participantTest() {
        ParticipantFeedback participantFeedback = getParticipantFeedbackRandomSampleGenerator();
        Participant participantBack = getParticipantRandomSampleGenerator();

        participantFeedback.setParticipant(participantBack);
        assertThat(participantFeedback.getParticipant()).isEqualTo(participantBack);

        participantFeedback.participant(null);
        assertThat(participantFeedback.getParticipant()).isNull();
    }

    @Test
    void shipmentTest() {
        ParticipantFeedback participantFeedback = getParticipantFeedbackRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        participantFeedback.setShipment(shipmentBack);
        assertThat(participantFeedback.getShipment()).isEqualTo(shipmentBack);

        participantFeedback.shipment(null);
        assertThat(participantFeedback.getShipment()).isNull();
    }
}
