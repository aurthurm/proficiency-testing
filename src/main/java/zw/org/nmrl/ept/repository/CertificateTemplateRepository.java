package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.CertificateTemplate;

/**
 * Spring Data JPA repository for the CertificateTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, Long> {}
