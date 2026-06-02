package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.CorrectiveAction;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.service.dto.CorrectiveActionDTO;
import zw.org.nmrl.ept.service.dto.SchemeDTO;

/**
 * Mapper for the entity {@link CorrectiveAction} and its DTO {@link CorrectiveActionDTO}.
 */
@Mapper(componentModel = "spring")
public interface CorrectiveActionMapper extends EntityMapper<CorrectiveActionDTO, CorrectiveAction> {
    @Mapping(target = "scheme", source = "scheme", qualifiedByName = "schemeId")
    CorrectiveActionDTO toDto(CorrectiveAction s);

    @Named("schemeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SchemeDTO toDtoSchemeId(Scheme scheme);
}
