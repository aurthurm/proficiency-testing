package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ParticipantMessageAsserts.*;
import static zw.org.nmrl.ept.domain.ParticipantMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParticipantMessageMapperTest {

    private ParticipantMessageMapper participantMessageMapper;

    @BeforeEach
    void setUp() {
        participantMessageMapper = new ParticipantMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParticipantMessageSample1();
        var actual = participantMessageMapper.toEntity(participantMessageMapper.toDto(expected));
        assertParticipantMessageAllPropertiesEquals(expected, actual);
    }
}
