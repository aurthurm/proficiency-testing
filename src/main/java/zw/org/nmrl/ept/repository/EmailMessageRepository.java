package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.EmailMessage;

/**
 * Spring Data JPA repository for the EmailMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long>, JpaSpecificationExecutor<EmailMessage> {}
