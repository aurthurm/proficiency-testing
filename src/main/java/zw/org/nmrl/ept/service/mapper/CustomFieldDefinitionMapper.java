package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.CustomFieldDefinition;
import zw.org.nmrl.ept.service.dto.CustomFieldDefinitionDTO;

/**
 * Mapper for the entity {@link CustomFieldDefinition} and its DTO {@link CustomFieldDefinitionDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomFieldDefinitionMapper extends EntityMapper<CustomFieldDefinitionDTO, CustomFieldDefinition> {}
