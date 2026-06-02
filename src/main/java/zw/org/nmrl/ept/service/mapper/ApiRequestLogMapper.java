package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.ApiRequestLog;
import zw.org.nmrl.ept.service.dto.ApiRequestLogDTO;

/**
 * Mapper for the entity {@link ApiRequestLog} and its DTO {@link ApiRequestLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApiRequestLogMapper extends EntityMapper<ApiRequestLogDTO, ApiRequestLog> {}
