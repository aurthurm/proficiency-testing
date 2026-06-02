package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ShipmentParticipantMapAsserts.*;
import static zw.org.nmrl.ept.domain.ShipmentParticipantMapTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentParticipantMapMapperTest {

    private ShipmentParticipantMapMapper shipmentParticipantMapMapper;

    @BeforeEach
    void setUp() {
        shipmentParticipantMapMapper = new ShipmentParticipantMapMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShipmentParticipantMapSample1();
        var actual = shipmentParticipantMapMapper.toEntity(shipmentParticipantMapMapper.toDto(expected));
        assertShipmentParticipantMapAllPropertiesEquals(expected, actual);
    }
}
