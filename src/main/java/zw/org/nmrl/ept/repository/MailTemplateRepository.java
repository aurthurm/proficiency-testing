package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.MailTemplate;

/**
 * Spring Data JPA repository for the MailTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplate, Long> {}
