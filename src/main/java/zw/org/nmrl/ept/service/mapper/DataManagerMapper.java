package zw.org.nmrl.ept.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Country;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.User;
import zw.org.nmrl.ept.service.dto.CountryDTO;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.dto.UserDTO;

/**
 * Mapper for the entity {@link DataManager} and its DTO {@link DataManagerDTO}.
 */
@Mapper(componentModel = "spring")
public interface DataManagerMapper extends EntityMapper<DataManagerDTO, DataManager> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    @Mapping(target = "participantses", source = "participantses", qualifiedByName = "participantIdSet")
    DataManagerDTO toDto(DataManager s);

    @Mapping(target = "removeParticipants", ignore = true)
    DataManager toEntity(DataManagerDTO dataManagerDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);

    @Named("participantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParticipantDTO toDtoParticipantId(Participant participant);

    @Named("participantIdSet")
    default Set<ParticipantDTO> toDtoParticipantIdSet(Set<Participant> participant) {
        return participant.stream().map(this::toDtoParticipantId).collect(Collectors.toSet());
    }
}
