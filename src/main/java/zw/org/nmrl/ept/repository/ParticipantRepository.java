package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.Participant;

/**
 * Spring Data JPA repository for the Participant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long>, JpaSpecificationExecutor<Participant> {}
