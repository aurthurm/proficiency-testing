package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ScheduledJobAsserts.*;
import static zw.org.nmrl.ept.domain.ScheduledJobTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScheduledJobMapperTest {

    private ScheduledJobMapper scheduledJobMapper;

    @BeforeEach
    void setUp() {
        scheduledJobMapper = new ScheduledJobMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScheduledJobSample1();
        var actual = scheduledJobMapper.toEntity(scheduledJobMapper.toDto(expected));
        assertScheduledJobAllPropertiesEquals(expected, actual);
    }
}
