package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.DataManagerAsserts.*;
import static zw.org.nmrl.ept.domain.DataManagerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataManagerMapperTest {

    private DataManagerMapper dataManagerMapper;

    @BeforeEach
    void setUp() {
        dataManagerMapper = new DataManagerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDataManagerSample1();
        var actual = dataManagerMapper.toEntity(dataManagerMapper.toDto(expected));
        assertDataManagerAllPropertiesEquals(expected, actual);
    }
}
