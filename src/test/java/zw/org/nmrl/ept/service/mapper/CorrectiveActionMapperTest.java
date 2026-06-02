package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.CorrectiveActionAsserts.*;
import static zw.org.nmrl.ept.domain.CorrectiveActionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CorrectiveActionMapperTest {

    private CorrectiveActionMapper correctiveActionMapper;

    @BeforeEach
    void setUp() {
        correctiveActionMapper = new CorrectiveActionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCorrectiveActionSample1();
        var actual = correctiveActionMapper.toEntity(correctiveActionMapper.toDto(expected));
        assertCorrectiveActionAllPropertiesEquals(expected, actual);
    }
}
