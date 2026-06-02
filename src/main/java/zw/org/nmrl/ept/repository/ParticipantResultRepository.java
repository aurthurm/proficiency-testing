package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ParticipantResult;

/**
 * Spring Data JPA repository for the ParticipantResult entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParticipantResultRepository extends JpaRepository<ParticipantResult, Long>, JpaSpecificationExecutor<ParticipantResult> {}
