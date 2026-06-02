package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.ParticipantFeedbackDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ParticipantFeedback}.
 */
public interface ParticipantFeedbackService {
    /**
     * Save a participantFeedback.
     *
     * @param participantFeedbackDTO the entity to save.
     * @return the persisted entity.
     */
    ParticipantFeedbackDTO save(ParticipantFeedbackDTO participantFeedbackDTO);

    /**
     * Updates a participantFeedback.
     *
     * @param participantFeedbackDTO the entity to update.
     * @return the persisted entity.
     */
    ParticipantFeedbackDTO update(ParticipantFeedbackDTO participantFeedbackDTO);

    /**
     * Partially updates a participantFeedback.
     *
     * @param participantFeedbackDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParticipantFeedbackDTO> partialUpdate(ParticipantFeedbackDTO participantFeedbackDTO);

    /**
     * Get all the participantFeedbacks.
     *
     * @return the list of entities.
     */
    List<ParticipantFeedbackDTO> findAll();

    /**
     * Get the "id" participantFeedback.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParticipantFeedbackDTO> findOne(Long id);

    /**
     * Delete the "id" participantFeedback.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
