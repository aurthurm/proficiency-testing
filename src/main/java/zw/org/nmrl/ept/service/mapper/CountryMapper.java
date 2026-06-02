package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Country;
import zw.org.nmrl.ept.service.dto.CountryDTO;

/**
 * Mapper for the entity {@link Country} and its DTO {@link CountryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CountryMapper extends EntityMapper<CountryDTO, Country> {}
