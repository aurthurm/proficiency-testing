package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.TestKitAsserts.*;
import static zw.org.nmrl.ept.domain.TestKitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestKitMapperTest {

    private TestKitMapper testKitMapper;

    @BeforeEach
    void setUp() {
        testKitMapper = new TestKitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTestKitSample1();
        var actual = testKitMapper.toEntity(testKitMapper.toDto(expected));
        assertTestKitAllPropertiesEquals(expected, actual);
    }
}
