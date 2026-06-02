package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.NotTestedReasonAsserts.*;
import static zw.org.nmrl.ept.domain.NotTestedReasonTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotTestedReasonMapperTest {

    private NotTestedReasonMapper notTestedReasonMapper;

    @BeforeEach
    void setUp() {
        notTestedReasonMapper = new NotTestedReasonMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotTestedReasonSample1();
        var actual = notTestedReasonMapper.toEntity(notTestedReasonMapper.toDto(expected));
        assertNotTestedReasonAllPropertiesEquals(expected, actual);
    }
}
