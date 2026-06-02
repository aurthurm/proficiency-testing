package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.FeedbackQuestion;

/**
 * Spring Data JPA repository for the FeedbackQuestion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeedbackQuestionRepository extends JpaRepository<FeedbackQuestion, Long> {}
