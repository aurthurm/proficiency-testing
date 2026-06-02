package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.FeedbackQuestionTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantFeedbackTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class FeedbackQuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeedbackQuestion.class);
        FeedbackQuestion feedbackQuestion1 = getFeedbackQuestionSample1();
        FeedbackQuestion feedbackQuestion2 = new FeedbackQuestion();
        assertThat(feedbackQuestion1).isNotEqualTo(feedbackQuestion2);

        feedbackQuestion2.setId(feedbackQuestion1.getId());
        assertThat(feedbackQuestion1).isEqualTo(feedbackQuestion2);

        feedbackQuestion2 = getFeedbackQuestionSample2();
        assertThat(feedbackQuestion1).isNotEqualTo(feedbackQuestion2);
    }

    @Test
    void answersTest() {
        FeedbackQuestion feedbackQuestion = getFeedbackQuestionRandomSampleGenerator();
        ParticipantFeedback participantFeedbackBack = getParticipantFeedbackRandomSampleGenerator();

        feedbackQuestion.addAnswers(participantFeedbackBack);
        assertThat(feedbackQuestion.getAnswerses()).containsOnly(participantFeedbackBack);
        assertThat(participantFeedbackBack.getQuestion()).isEqualTo(feedbackQuestion);

        feedbackQuestion.removeAnswers(participantFeedbackBack);
        assertThat(feedbackQuestion.getAnswerses()).doesNotContain(participantFeedbackBack);
        assertThat(participantFeedbackBack.getQuestion()).isNull();

        feedbackQuestion.answerses(new HashSet<>(Set.of(participantFeedbackBack)));
        assertThat(feedbackQuestion.getAnswerses()).containsOnly(participantFeedbackBack);
        assertThat(participantFeedbackBack.getQuestion()).isEqualTo(feedbackQuestion);

        feedbackQuestion.setAnswerses(new HashSet<>());
        assertThat(feedbackQuestion.getAnswerses()).doesNotContain(participantFeedbackBack);
        assertThat(participantFeedbackBack.getQuestion()).isNull();
    }
}
