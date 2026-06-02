package zw.org.nmrl.ept.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import zw.org.nmrl.ept.domain.CertificateBatch;
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.service.dto.CertificateBatchDTO;
import zw.org.nmrl.ept.service.dto.ShipmentDTO;

/**
 * Mapper for the entity {@link CertificateBatch} and its DTO {@link CertificateBatchDTO}.
 */
@Mapper(componentModel = "spring")
public interface CertificateBatchMapper extends EntityMapper<CertificateBatchDTO, CertificateBatch> {
    @Mapping(target = "shipmentses", source = "shipmentses", qualifiedByName = "shipmentIdSet")
    CertificateBatchDTO toDto(CertificateBatch s);

    @Mapping(target = "removeShipments", ignore = true)
    CertificateBatch toEntity(CertificateBatchDTO certificateBatchDTO);

    @Named("shipmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentDTO toDtoShipmentId(Shipment shipment);

    @Named("shipmentIdSet")
    default Set<ShipmentDTO> toDtoShipmentIdSet(Set<Shipment> shipment) {
        return shipment.stream().map(this::toDtoShipmentId).collect(Collectors.toSet());
    }
}
