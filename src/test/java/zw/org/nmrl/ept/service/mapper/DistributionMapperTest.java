package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.DistributionAsserts.*;
import static zw.org.nmrl.ept.domain.DistributionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DistributionMapperTest {

    private DistributionMapper distributionMapper;

    @BeforeEach
    void setUp() {
        distributionMapper = new DistributionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDistributionSample1();
        var actual = distributionMapper.toEntity(distributionMapper.toDto(expected));
        assertDistributionAllPropertiesEquals(expected, actual);
    }
}
