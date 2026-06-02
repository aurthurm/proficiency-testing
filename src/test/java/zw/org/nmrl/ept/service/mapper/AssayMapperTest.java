package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.AssayAsserts.*;
import static zw.org.nmrl.ept.domain.AssayTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssayMapperTest {

    private AssayMapper assayMapper;

    @BeforeEach
    void setUp() {
        assayMapper = new AssayMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAssaySample1();
        var actual = assayMapper.toEntity(assayMapper.toDto(expected));
        assertAssayAllPropertiesEquals(expected, actual);
    }
}
