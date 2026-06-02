package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ShipmentSampleAsserts.*;
import static zw.org.nmrl.ept.domain.ShipmentSampleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentSampleMapperTest {

    private ShipmentSampleMapper shipmentSampleMapper;

    @BeforeEach
    void setUp() {
        shipmentSampleMapper = new ShipmentSampleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShipmentSampleSample1();
        var actual = shipmentSampleMapper.toEntity(shipmentSampleMapper.toDto(expected));
        assertShipmentSampleAllPropertiesEquals(expected, actual);
    }
}
