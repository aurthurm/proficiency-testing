package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.CapaRecord;
import zw.org.nmrl.ept.domain.CorrectiveAction;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.service.dto.CapaRecordDTO;
import zw.org.nmrl.ept.service.dto.CorrectiveActionDTO;
import zw.org.nmrl.ept.service.dto.ShipmentParticipantMapDTO;

/**
 * Mapper for the entity {@link CapaRecord} and its DTO {@link CapaRecordDTO}.
 */
@Mapper(componentModel = "spring")
public interface CapaRecordMapper extends EntityMapper<CapaRecordDTO, CapaRecord> {
    @Mapping(target = "correctiveAction", source = "correctiveAction", qualifiedByName = "correctiveActionId")
    @Mapping(target = "shipmentParticipantMap", source = "shipmentParticipantMap", qualifiedByName = "shipmentParticipantMapId")
    CapaRecordDTO toDto(CapaRecord s);

    @Named("correctiveActionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorrectiveActionDTO toDtoCorrectiveActionId(CorrectiveAction correctiveAction);

    @Named("shipmentParticipantMapId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentParticipantMapDTO toDtoShipmentParticipantMapId(ShipmentParticipantMap shipmentParticipantMap);
}
