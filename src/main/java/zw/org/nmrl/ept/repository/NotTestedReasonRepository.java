package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.NotTestedReason;

/**
 * Spring Data JPA repository for the NotTestedReason entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotTestedReasonRepository extends JpaRepository<NotTestedReason, Long> {}
