package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.GlobalConfigurationAsserts.*;
import static zw.org.nmrl.ept.domain.GlobalConfigurationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GlobalConfigurationMapperTest {

    private GlobalConfigurationMapper globalConfigurationMapper;

    @BeforeEach
    void setUp() {
        globalConfigurationMapper = new GlobalConfigurationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGlobalConfigurationSample1();
        var actual = globalConfigurationMapper.toEntity(globalConfigurationMapper.toDto(expected));
        assertGlobalConfigurationAllPropertiesEquals(expected, actual);
    }
}
