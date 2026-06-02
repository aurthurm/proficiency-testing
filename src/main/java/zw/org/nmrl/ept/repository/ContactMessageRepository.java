package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ContactMessage;

/**
 * Spring Data JPA repository for the ContactMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {}
