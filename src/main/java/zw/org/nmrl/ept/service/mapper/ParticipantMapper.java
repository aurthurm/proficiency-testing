package zw.org.nmrl.ept.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Country;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.service.dto.CountryDTO;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;

/**
 * Mapper for the entity {@link Participant} and its DTO {@link ParticipantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParticipantMapper extends EntityMapper<ParticipantDTO, Participant> {
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    @Mapping(target = "dataManagerses", source = "dataManagerses", qualifiedByName = "dataManagerIdSet")
    ParticipantDTO toDto(Participant s);

    @Mapping(target = "dataManagerses", ignore = true)
    @Mapping(target = "removeDataManagers", ignore = true)
    Participant toEntity(ParticipantDTO participantDTO);

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);

    @Named("dataManagerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DataManagerDTO toDtoDataManagerId(DataManager dataManager);

    @Named("dataManagerIdSet")
    default Set<DataManagerDTO> toDtoDataManagerIdSet(Set<DataManager> dataManager) {
        return dataManager.stream().map(this::toDtoDataManagerId).collect(Collectors.toSet());
    }
}
