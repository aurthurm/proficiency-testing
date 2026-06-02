package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.Partner;

/**
 * Spring Data JPA repository for the Partner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {}
