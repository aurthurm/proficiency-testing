package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ParticipantFeedback;

/**
 * Spring Data JPA repository for the ParticipantFeedback entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParticipantFeedbackRepository extends JpaRepository<ParticipantFeedback, Long> {}
