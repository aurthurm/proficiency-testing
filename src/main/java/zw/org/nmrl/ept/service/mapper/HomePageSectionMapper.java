package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.HomePageSection;
import zw.org.nmrl.ept.service.dto.HomePageSectionDTO;

/**
 * Mapper for the entity {@link HomePageSection} and its DTO {@link HomePageSectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface HomePageSectionMapper extends EntityMapper<HomePageSectionDTO, HomePageSection> {}
