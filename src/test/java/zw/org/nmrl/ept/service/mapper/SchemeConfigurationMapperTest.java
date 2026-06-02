package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.SchemeConfigurationAsserts.*;
import static zw.org.nmrl.ept.domain.SchemeConfigurationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchemeConfigurationMapperTest {

    private SchemeConfigurationMapper schemeConfigurationMapper;

    @BeforeEach
    void setUp() {
        schemeConfigurationMapper = new SchemeConfigurationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSchemeConfigurationSample1();
        var actual = schemeConfigurationMapper.toEntity(schemeConfigurationMapper.toDto(expected));
        assertSchemeConfigurationAllPropertiesEquals(expected, actual);
    }
}
