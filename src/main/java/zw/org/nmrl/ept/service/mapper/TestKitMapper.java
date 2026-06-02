package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.domain.TestKit;
import zw.org.nmrl.ept.service.dto.SchemeDTO;
import zw.org.nmrl.ept.service.dto.TestKitDTO;

/**
 * Mapper for the entity {@link TestKit} and its DTO {@link TestKitDTO}.
 */
@Mapper(componentModel = "spring")
public interface TestKitMapper extends EntityMapper<TestKitDTO, TestKit> {
    @Mapping(target = "scheme", source = "scheme", qualifiedByName = "schemeId")
    TestKitDTO toDto(TestKit s);

    @Named("schemeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SchemeDTO toDtoSchemeId(Scheme scheme);
}
