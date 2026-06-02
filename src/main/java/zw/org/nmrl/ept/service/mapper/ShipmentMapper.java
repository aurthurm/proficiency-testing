package zw.org.nmrl.ept.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import zw.org.nmrl.ept.domain.CertificateBatch;
import zw.org.nmrl.ept.domain.Distribution;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.service.dto.CertificateBatchDTO;
import zw.org.nmrl.ept.service.dto.DistributionDTO;
import zw.org.nmrl.ept.service.dto.SchemeDTO;
import zw.org.nmrl.ept.service.dto.ShipmentDTO;

/**
 * Mapper for the entity {@link Shipment} and its DTO {@link ShipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentMapper extends EntityMapper<ShipmentDTO, Shipment> {
    @Mapping(target = "distribution", source = "distribution", qualifiedByName = "distributionId")
    @Mapping(target = "scheme", source = "scheme", qualifiedByName = "schemeId")
    @Mapping(target = "certificateBatcheses", source = "certificateBatcheses", qualifiedByName = "certificateBatchIdSet")
    ShipmentDTO toDto(Shipment s);

    @Mapping(target = "certificateBatcheses", ignore = true)
    @Mapping(target = "removeCertificateBatches", ignore = true)
    Shipment toEntity(ShipmentDTO shipmentDTO);

    @Named("distributionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DistributionDTO toDtoDistributionId(Distribution distribution);

    @Named("schemeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SchemeDTO toDtoSchemeId(Scheme scheme);

    @Named("certificateBatchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CertificateBatchDTO toDtoCertificateBatchId(CertificateBatch certificateBatch);

    @Named("certificateBatchIdSet")
    default Set<CertificateBatchDTO> toDtoCertificateBatchIdSet(Set<CertificateBatch> certificateBatch) {
        return certificateBatch.stream().map(this::toDtoCertificateBatchId).collect(Collectors.toSet());
    }
}
