package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ShipmentSample;

/**
 * Spring Data JPA repository for the ShipmentSample entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentSampleRepository extends JpaRepository<ShipmentSample, Long> {}
