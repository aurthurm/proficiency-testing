package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.PartnerAsserts.*;
import static zw.org.nmrl.ept.domain.PartnerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartnerMapperTest {

    private PartnerMapper partnerMapper;

    @BeforeEach
    void setUp() {
        partnerMapper = new PartnerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPartnerSample1();
        var actual = partnerMapper.toEntity(partnerMapper.toDto(expected));
        assertPartnerAllPropertiesEquals(expected, actual);
    }
}
