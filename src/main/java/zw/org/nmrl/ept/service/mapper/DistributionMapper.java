package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Distribution;
import zw.org.nmrl.ept.service.dto.DistributionDTO;

/**
 * Mapper for the entity {@link Distribution} and its DTO {@link DistributionDTO}.
 */
@Mapper(componentModel = "spring")
public interface DistributionMapper extends EntityMapper<DistributionDTO, Distribution> {}
