package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Assay;
import zw.org.nmrl.ept.domain.SampleReferenceResult;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.service.dto.AssayDTO;
import zw.org.nmrl.ept.service.dto.SampleReferenceResultDTO;
import zw.org.nmrl.ept.service.dto.ShipmentSampleDTO;

/**
 * Mapper for the entity {@link SampleReferenceResult} and its DTO {@link SampleReferenceResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface SampleReferenceResultMapper extends EntityMapper<SampleReferenceResultDTO, SampleReferenceResult> {
    @Mapping(target = "assay", source = "assay", qualifiedByName = "assayId")
    @Mapping(target = "sample", source = "sample", qualifiedByName = "shipmentSampleId")
    SampleReferenceResultDTO toDto(SampleReferenceResult s);

    @Named("assayId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AssayDTO toDtoAssayId(Assay assay);

    @Named("shipmentSampleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentSampleDTO toDtoShipmentSampleId(ShipmentSample shipmentSample);
}
