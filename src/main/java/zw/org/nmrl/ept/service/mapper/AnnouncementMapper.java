package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Announcement;
import zw.org.nmrl.ept.service.dto.AnnouncementDTO;

/**
 * Mapper for the entity {@link Announcement} and its DTO {@link AnnouncementDTO}.
 */
@Mapper(componentModel = "spring")
public interface AnnouncementMapper extends EntityMapper<AnnouncementDTO, Announcement> {}
