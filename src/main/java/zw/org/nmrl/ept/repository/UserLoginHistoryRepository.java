package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.UserLoginHistory;

/**
 * Spring Data JPA repository for the UserLoginHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long>, JpaSpecificationExecutor<UserLoginHistory> {}
