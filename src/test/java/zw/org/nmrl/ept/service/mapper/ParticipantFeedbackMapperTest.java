package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ParticipantFeedbackAsserts.*;
import static zw.org.nmrl.ept.domain.ParticipantFeedbackTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParticipantFeedbackMapperTest {

    private ParticipantFeedbackMapper participantFeedbackMapper;

    @BeforeEach
    void setUp() {
        participantFeedbackMapper = new ParticipantFeedbackMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParticipantFeedbackSample1();
        var actual = participantFeedbackMapper.toEntity(participantFeedbackMapper.toDto(expected));
        assertParticipantFeedbackAllPropertiesEquals(expected, actual);
    }
}
