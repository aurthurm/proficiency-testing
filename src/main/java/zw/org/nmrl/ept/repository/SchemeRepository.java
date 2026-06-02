package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.Scheme;

/**
 * Spring Data JPA repository for the Scheme entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {}
