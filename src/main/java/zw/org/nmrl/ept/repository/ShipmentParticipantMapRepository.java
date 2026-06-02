package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;

/**
 * Spring Data JPA repository for the ShipmentParticipantMap entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentParticipantMapRepository
    extends JpaRepository<ShipmentParticipantMap, Long>, JpaSpecificationExecutor<ShipmentParticipantMap> {}
