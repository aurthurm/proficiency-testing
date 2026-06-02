package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.NotTestedReason;
import zw.org.nmrl.ept.service.dto.NotTestedReasonDTO;

/**
 * Mapper for the entity {@link NotTestedReason} and its DTO {@link NotTestedReasonDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotTestedReasonMapper extends EntityMapper<NotTestedReasonDTO, NotTestedReason> {}
