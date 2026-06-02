package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.GlobalConfiguration;
import zw.org.nmrl.ept.service.dto.GlobalConfigurationDTO;

/**
 * Mapper for the entity {@link GlobalConfiguration} and its DTO {@link GlobalConfigurationDTO}.
 */
@Mapper(componentModel = "spring")
public interface GlobalConfigurationMapper extends EntityMapper<GlobalConfigurationDTO, GlobalConfiguration> {}
