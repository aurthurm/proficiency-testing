package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.Distribution;

/**
 * Spring Data JPA repository for the Distribution entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Long>, JpaSpecificationExecutor<Distribution> {}
