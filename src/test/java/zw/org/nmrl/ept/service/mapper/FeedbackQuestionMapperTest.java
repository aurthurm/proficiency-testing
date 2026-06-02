package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.FeedbackQuestionAsserts.*;
import static zw.org.nmrl.ept.domain.FeedbackQuestionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FeedbackQuestionMapperTest {

    private FeedbackQuestionMapper feedbackQuestionMapper;

    @BeforeEach
    void setUp() {
        feedbackQuestionMapper = new FeedbackQuestionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFeedbackQuestionSample1();
        var actual = feedbackQuestionMapper.toEntity(feedbackQuestionMapper.toDto(expected));
        assertFeedbackQuestionAllPropertiesEquals(expected, actual);
    }
}
