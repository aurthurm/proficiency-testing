package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Assay;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.service.dto.AssayDTO;
import zw.org.nmrl.ept.service.dto.SchemeDTO;

/**
 * Mapper for the entity {@link Assay} and its DTO {@link AssayDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssayMapper extends EntityMapper<AssayDTO, Assay> {
    @Mapping(target = "scheme", source = "scheme", qualifiedByName = "schemeId")
    AssayDTO toDto(Assay s);

    @Named("schemeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SchemeDTO toDtoSchemeId(Scheme scheme);
}
