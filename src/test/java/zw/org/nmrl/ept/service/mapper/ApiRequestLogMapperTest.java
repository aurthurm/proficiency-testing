package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ApiRequestLogAsserts.*;
import static zw.org.nmrl.ept.domain.ApiRequestLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiRequestLogMapperTest {

    private ApiRequestLogMapper apiRequestLogMapper;

    @BeforeEach
    void setUp() {
        apiRequestLogMapper = new ApiRequestLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getApiRequestLogSample1();
        var actual = apiRequestLogMapper.toEntity(apiRequestLogMapper.toDto(expected));
        assertApiRequestLogAllPropertiesEquals(expected, actual);
    }
}
