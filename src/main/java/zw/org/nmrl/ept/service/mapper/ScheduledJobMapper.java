package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.service.dto.ScheduledJobDTO;

/**
 * Mapper for the entity {@link ScheduledJob} and its DTO {@link ScheduledJobDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduledJobMapper extends EntityMapper<ScheduledJobDTO, ScheduledJob> {}
