package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.GlobalConfiguration;

/**
 * Spring Data JPA repository for the GlobalConfiguration entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration, Long> {}
