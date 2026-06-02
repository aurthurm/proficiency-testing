package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.Assay;

/**
 * Spring Data JPA repository for the Assay entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssayRepository extends JpaRepository<Assay, Long> {}
