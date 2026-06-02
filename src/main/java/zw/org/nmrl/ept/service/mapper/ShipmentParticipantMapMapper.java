package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.ModeOfReceipt;
import zw.org.nmrl.ept.domain.NotTestedReason;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.service.dto.ModeOfReceiptDTO;
import zw.org.nmrl.ept.service.dto.NotTestedReasonDTO;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.dto.ShipmentDTO;
import zw.org.nmrl.ept.service.dto.ShipmentParticipantMapDTO;

/**
 * Mapper for the entity {@link ShipmentParticipantMap} and its DTO {@link ShipmentParticipantMapDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentParticipantMapMapper extends EntityMapper<ShipmentParticipantMapDTO, ShipmentParticipantMap> {
    @Mapping(target = "modeOfReceipt", source = "modeOfReceipt", qualifiedByName = "modeOfReceiptId")
    @Mapping(target = "notTestedReason", source = "notTestedReason", qualifiedByName = "notTestedReasonId")
    @Mapping(target = "shipment", source = "shipment", qualifiedByName = "shipmentId")
    @Mapping(target = "participant", source = "participant", qualifiedByName = "participantId")
    ShipmentParticipantMapDTO toDto(ShipmentParticipantMap s);

    @Named("modeOfReceiptId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ModeOfReceiptDTO toDtoModeOfReceiptId(ModeOfReceipt modeOfReceipt);

    @Named("notTestedReasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotTestedReasonDTO toDtoNotTestedReasonId(NotTestedReason notTestedReason);

    @Named("shipmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentDTO toDtoShipmentId(Shipment shipment);

    @Named("participantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParticipantDTO toDtoParticipantId(Participant participant);
}
