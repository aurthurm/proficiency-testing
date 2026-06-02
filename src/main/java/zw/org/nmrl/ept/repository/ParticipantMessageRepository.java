package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ParticipantMessage;

/**
 * Spring Data JPA repository for the ParticipantMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParticipantMessageRepository extends JpaRepository<ParticipantMessage, Long> {}
