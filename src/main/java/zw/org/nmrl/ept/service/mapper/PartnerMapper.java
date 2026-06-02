package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Partner;
import zw.org.nmrl.ept.service.dto.PartnerDTO;

/**
 * Mapper for the entity {@link Partner} and its DTO {@link PartnerDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartnerMapper extends EntityMapper<PartnerDTO, Partner> {}
