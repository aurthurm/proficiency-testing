package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ApiRequestLog;

/**
 * Spring Data JPA repository for the ApiRequestLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApiRequestLogRepository extends JpaRepository<ApiRequestLog, Long> {}
