package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ScheduledJob;

/**
 * Spring Data JPA repository for the ScheduledJob entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long>, JpaSpecificationExecutor<ScheduledJob> {}
