package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.SchemeAsserts.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchemeMapperTest {

    private SchemeMapper schemeMapper;

    @BeforeEach
    void setUp() {
        schemeMapper = new SchemeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSchemeSample1();
        var actual = schemeMapper.toEntity(schemeMapper.toDto(expected));
        assertSchemeAllPropertiesEquals(expected, actual);
    }
}
