package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.CertificateTemplate;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.service.dto.CertificateTemplateDTO;
import zw.org.nmrl.ept.service.dto.SchemeDTO;

/**
 * Mapper for the entity {@link Scheme} and its DTO {@link SchemeDTO}.
 */
@Mapper(componentModel = "spring")
public interface SchemeMapper extends EntityMapper<SchemeDTO, Scheme> {
    @Mapping(target = "certificateTemplate", source = "certificateTemplate", qualifiedByName = "certificateTemplateId")
    SchemeDTO toDto(Scheme s);

    @Named("certificateTemplateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CertificateTemplateDTO toDtoCertificateTemplateId(CertificateTemplate certificateTemplate);
}
