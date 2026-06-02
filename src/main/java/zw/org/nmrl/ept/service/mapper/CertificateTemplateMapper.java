package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.CertificateTemplate;
import zw.org.nmrl.ept.service.dto.CertificateTemplateDTO;

/**
 * Mapper for the entity {@link CertificateTemplate} and its DTO {@link CertificateTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface CertificateTemplateMapper extends EntityMapper<CertificateTemplateDTO, CertificateTemplate> {}
