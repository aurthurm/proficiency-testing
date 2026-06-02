package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class FeedbackQuestionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeedbackQuestionDTO.class);
        FeedbackQuestionDTO feedbackQuestionDTO1 = new FeedbackQuestionDTO();
        feedbackQuestionDTO1.setId(1L);
        FeedbackQuestionDTO feedbackQuestionDTO2 = new FeedbackQuestionDTO();
        assertThat(feedbackQuestionDTO1).isNotEqualTo(feedbackQuestionDTO2);
        feedbackQuestionDTO2.setId(feedbackQuestionDTO1.getId());
        assertThat(feedbackQuestionDTO1).isEqualTo(feedbackQuestionDTO2);
        feedbackQuestionDTO2.setId(2L);
        assertThat(feedbackQuestionDTO1).isNotEqualTo(feedbackQuestionDTO2);
        feedbackQuestionDTO1.setId(null);
        assertThat(feedbackQuestionDTO1).isNotEqualTo(feedbackQuestionDTO2);
    }
}
