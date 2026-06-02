package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.FeedbackQuestionDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.FeedbackQuestion}.
 */
public interface FeedbackQuestionService {
    /**
     * Save a feedbackQuestion.
     *
     * @param feedbackQuestionDTO the entity to save.
     * @return the persisted entity.
     */
    FeedbackQuestionDTO save(FeedbackQuestionDTO feedbackQuestionDTO);

    /**
     * Updates a feedbackQuestion.
     *
     * @param feedbackQuestionDTO the entity to update.
     * @return the persisted entity.
     */
    FeedbackQuestionDTO update(FeedbackQuestionDTO feedbackQuestionDTO);

    /**
     * Partially updates a feedbackQuestion.
     *
     * @param feedbackQuestionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FeedbackQuestionDTO> partialUpdate(FeedbackQuestionDTO feedbackQuestionDTO);

    /**
     * Get all the feedbackQuestions.
     *
     * @return the list of entities.
     */
    List<FeedbackQuestionDTO> findAll();

    /**
     * Get the "id" feedbackQuestion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FeedbackQuestionDTO> findOne(Long id);

    /**
     * Delete the "id" feedbackQuestion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
