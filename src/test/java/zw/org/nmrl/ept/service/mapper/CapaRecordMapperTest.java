package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.CapaRecordAsserts.*;
import static zw.org.nmrl.ept.domain.CapaRecordTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CapaRecordMapperTest {

    private CapaRecordMapper capaRecordMapper;

    @BeforeEach
    void setUp() {
        capaRecordMapper = new CapaRecordMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCapaRecordSample1();
        var actual = capaRecordMapper.toEntity(capaRecordMapper.toDto(expected));
        assertCapaRecordAllPropertiesEquals(expected, actual);
    }
}
