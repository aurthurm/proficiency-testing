package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.MailTemplate;
import zw.org.nmrl.ept.service.dto.MailTemplateDTO;

/**
 * Mapper for the entity {@link MailTemplate} and its DTO {@link MailTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface MailTemplateMapper extends EntityMapper<MailTemplateDTO, MailTemplate> {}
