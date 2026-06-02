package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.SampleReferenceResultAsserts.*;
import static zw.org.nmrl.ept.domain.SampleReferenceResultTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SampleReferenceResultMapperTest {

    private SampleReferenceResultMapper sampleReferenceResultMapper;

    @BeforeEach
    void setUp() {
        sampleReferenceResultMapper = new SampleReferenceResultMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSampleReferenceResultSample1();
        var actual = sampleReferenceResultMapper.toEntity(sampleReferenceResultMapper.toDto(expected));
        assertSampleReferenceResultAllPropertiesEquals(expected, actual);
    }
}
