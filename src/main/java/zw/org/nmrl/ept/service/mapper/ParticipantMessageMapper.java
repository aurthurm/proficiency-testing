package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.ParticipantMessage;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.dto.ParticipantMessageDTO;

/**
 * Mapper for the entity {@link ParticipantMessage} and its DTO {@link ParticipantMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParticipantMessageMapper extends EntityMapper<ParticipantMessageDTO, ParticipantMessage> {
    @Mapping(target = "participant", source = "participant", qualifiedByName = "participantId")
    ParticipantMessageDTO toDto(ParticipantMessage s);

    @Named("participantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParticipantDTO toDtoParticipantId(Participant participant);
}
