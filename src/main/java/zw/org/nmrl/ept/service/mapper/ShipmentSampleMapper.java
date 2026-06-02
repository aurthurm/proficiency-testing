package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.service.dto.ShipmentDTO;
import zw.org.nmrl.ept.service.dto.ShipmentSampleDTO;

/**
 * Mapper for the entity {@link ShipmentSample} and its DTO {@link ShipmentSampleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentSampleMapper extends EntityMapper<ShipmentSampleDTO, ShipmentSample> {
    @Mapping(target = "shipment", source = "shipment", qualifiedByName = "shipmentId")
    ShipmentSampleDTO toDto(ShipmentSample s);

    @Named("shipmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentDTO toDtoShipmentId(Shipment shipment);
}
