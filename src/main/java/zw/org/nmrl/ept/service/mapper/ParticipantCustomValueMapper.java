package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.CustomFieldDefinition;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.ParticipantCustomValue;
import zw.org.nmrl.ept.service.dto.CustomFieldDefinitionDTO;
import zw.org.nmrl.ept.service.dto.ParticipantCustomValueDTO;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;

/**
 * Mapper for the entity {@link ParticipantCustomValue} and its DTO {@link ParticipantCustomValueDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParticipantCustomValueMapper extends EntityMapper<ParticipantCustomValueDTO, ParticipantCustomValue> {
    @Mapping(target = "participant", source = "participant", qualifiedByName = "participantId")
    @Mapping(target = "definition", source = "definition", qualifiedByName = "customFieldDefinitionId")
    ParticipantCustomValueDTO toDto(ParticipantCustomValue s);

    @Named("participantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParticipantDTO toDtoParticipantId(Participant participant);

    @Named("customFieldDefinitionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomFieldDefinitionDTO toDtoCustomFieldDefinitionId(CustomFieldDefinition customFieldDefinition);
}
