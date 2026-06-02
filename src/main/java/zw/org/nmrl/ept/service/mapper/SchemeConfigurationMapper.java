package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.domain.SchemeConfiguration;
import zw.org.nmrl.ept.service.dto.SchemeConfigurationDTO;
import zw.org.nmrl.ept.service.dto.SchemeDTO;

/**
 * Mapper for the entity {@link SchemeConfiguration} and its DTO {@link SchemeConfigurationDTO}.
 */
@Mapper(componentModel = "spring")
public interface SchemeConfigurationMapper extends EntityMapper<SchemeConfigurationDTO, SchemeConfiguration> {
    @Mapping(target = "scheme", source = "scheme", qualifiedByName = "schemeId")
    SchemeConfigurationDTO toDto(SchemeConfiguration s);

    @Named("schemeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SchemeDTO toDtoSchemeId(Scheme scheme);
}
