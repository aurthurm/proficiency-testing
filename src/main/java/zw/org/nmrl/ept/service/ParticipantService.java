package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.Participant}.
 */
public interface ParticipantService {
    /**
     * Save a participant.
     *
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    ParticipantDTO save(ParticipantDTO participantDTO);

    /**
     * Updates a participant.
     *
     * @param participantDTO the entity to update.
     * @return the persisted entity.
     */
    ParticipantDTO update(ParticipantDTO participantDTO);

    /**
     * Partially updates a participant.
     *
     * @param participantDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParticipantDTO> partialUpdate(ParticipantDTO participantDTO);

    /**
     * Get the "id" participant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParticipantDTO> findOne(Long id);

    /**
     * Delete the "id" participant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
