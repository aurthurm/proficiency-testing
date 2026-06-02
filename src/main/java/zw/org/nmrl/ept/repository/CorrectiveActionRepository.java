package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.CorrectiveAction;

/**
 * Spring Data JPA repository for the CorrectiveAction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction, Long> {}
