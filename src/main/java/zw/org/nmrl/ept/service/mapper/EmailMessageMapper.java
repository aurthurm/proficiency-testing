package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.EmailMessage;
import zw.org.nmrl.ept.domain.MailTemplate;
import zw.org.nmrl.ept.service.dto.EmailMessageDTO;
import zw.org.nmrl.ept.service.dto.MailTemplateDTO;

/**
 * Mapper for the entity {@link EmailMessage} and its DTO {@link EmailMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmailMessageMapper extends EntityMapper<EmailMessageDTO, EmailMessage> {
    @Mapping(target = "template", source = "template", qualifiedByName = "mailTemplateId")
    EmailMessageDTO toDto(EmailMessage s);

    @Named("mailTemplateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MailTemplateDTO toDtoMailTemplateId(MailTemplate mailTemplate);
}
