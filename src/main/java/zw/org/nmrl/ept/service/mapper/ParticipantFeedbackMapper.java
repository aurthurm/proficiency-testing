package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.FeedbackQuestion;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.ParticipantFeedback;
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.service.dto.FeedbackQuestionDTO;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.dto.ParticipantFeedbackDTO;
import zw.org.nmrl.ept.service.dto.ShipmentDTO;

/**
 * Mapper for the entity {@link ParticipantFeedback} and its DTO {@link ParticipantFeedbackDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParticipantFeedbackMapper extends EntityMapper<ParticipantFeedbackDTO, ParticipantFeedback> {
    @Mapping(target = "question", source = "question", qualifiedByName = "feedbackQuestionId")
    @Mapping(target = "participant", source = "participant", qualifiedByName = "participantId")
    @Mapping(target = "shipment", source = "shipment", qualifiedByName = "shipmentId")
    ParticipantFeedbackDTO toDto(ParticipantFeedback s);

    @Named("feedbackQuestionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FeedbackQuestionDTO toDtoFeedbackQuestionId(FeedbackQuestion feedbackQuestion);

    @Named("participantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParticipantDTO toDtoParticipantId(Participant participant);

    @Named("shipmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShipmentDTO toDtoShipmentId(Shipment shipment);
}
