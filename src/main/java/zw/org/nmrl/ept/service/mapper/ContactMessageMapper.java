package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.ContactMessage;
import zw.org.nmrl.ept.service.dto.ContactMessageDTO;

/**
 * Mapper for the entity {@link ContactMessage} and its DTO {@link ContactMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ContactMessageMapper extends EntityMapper<ContactMessageDTO, ContactMessage> {}
