package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ParticipantCustomValueAsserts.*;
import static zw.org.nmrl.ept.domain.ParticipantCustomValueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParticipantCustomValueMapperTest {

    private ParticipantCustomValueMapper participantCustomValueMapper;

    @BeforeEach
    void setUp() {
        participantCustomValueMapper = new ParticipantCustomValueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParticipantCustomValueSample1();
        var actual = participantCustomValueMapper.toEntity(participantCustomValueMapper.toDto(expected));
        assertParticipantCustomValueAllPropertiesEquals(expected, actual);
    }
}
