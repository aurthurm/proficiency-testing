package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.ReportConfiguration;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.service.dto.ReportConfigurationDTO;
import zw.org.nmrl.ept.service.dto.SchemeDTO;

/**
 * Mapper for the entity {@link ReportConfiguration} and its DTO {@link ReportConfigurationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReportConfigurationMapper extends EntityMapper<ReportConfigurationDTO, ReportConfiguration> {
    @Mapping(target = "scheme", source = "scheme", qualifiedByName = "schemeId")
    ReportConfigurationDTO toDto(ReportConfiguration s);

    @Named("schemeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SchemeDTO toDtoSchemeId(Scheme scheme);
}
