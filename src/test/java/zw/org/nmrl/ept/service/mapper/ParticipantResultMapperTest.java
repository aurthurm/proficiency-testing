package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ParticipantResultAsserts.*;
import static zw.org.nmrl.ept.domain.ParticipantResultTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParticipantResultMapperTest {

    private ParticipantResultMapper participantResultMapper;

    @BeforeEach
    void setUp() {
        participantResultMapper = new ParticipantResultMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParticipantResultSample1();
        var actual = participantResultMapper.toEntity(participantResultMapper.toDto(expected));
        assertParticipantResultAllPropertiesEquals(expected, actual);
    }
}
