package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.AuditLog;
import zw.org.nmrl.ept.service.dto.AuditLogDTO;

/**
 * Mapper for the entity {@link AuditLog} and its DTO {@link AuditLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditLogMapper extends EntityMapper<AuditLogDTO, AuditLog> {}
