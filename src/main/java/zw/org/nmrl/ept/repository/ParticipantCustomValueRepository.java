package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ParticipantCustomValue;

/**
 * Spring Data JPA repository for the ParticipantCustomValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParticipantCustomValueRepository extends JpaRepository<ParticipantCustomValue, Long> {}
