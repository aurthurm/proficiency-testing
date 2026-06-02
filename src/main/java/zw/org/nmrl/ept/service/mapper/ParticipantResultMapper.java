package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Assay;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.domain.TestKit;
import zw.org.nmrl.ept.service.dto.AssayDTO;
import zw.org.nmrl.ept.service.dto.ParticipantResultDTO;
import zw.org.nmrl.ept.service.dto.ShipmentParticipantMapDTO;
import zw.org.nmrl.ept.service.dto.ShipmentSampleDTO;
import zw.org.nmrl.ept.service.dto.TestKitDTO;

/**
 * Mapper for the entity {@link ParticipantResult} and its DTO {@link ParticipantResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParticipantResultMapper extends EntityMapper<ParticipantResultDTO, ParticipantResult> {
    @Mapping(target = "assay", source = "assay", qualifiedByName = "assayId")
    @Mapping(target = "testKit", source = "testKit", qualifiedByName = "testKitId")
    @Mapping(target = "sample", source = "sample", qualifiedByName = "shipmentSampleId")
    @Mapping(target = "shipmentParticipantMap", source = "shipmentParticipantMap", qualifiedByName = "shipmentParticipantMapId")
    ParticipantResultDTO toDto(ParticipantResult s);

    @Named("assayId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AssayDTO toDtoAssayId(Assay assay);

    @Named("testKitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TestKitDTO toDtoTestKitId(TestKit testKit);

    @Named("shipmentSampleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentSampleDTO toDtoShipmentSampleId(ShipmentSample shipmentSample);

    @Named("shipmentParticipantMapId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentParticipantMapDTO toDtoShipmentParticipantMapId(ShipmentParticipantMap shipmentParticipantMap);
}
