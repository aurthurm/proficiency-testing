package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.ParticipantResultDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ParticipantResult}.
 */
public interface ParticipantResultService {
    /**
     * Save a participantResult.
     *
     * @param participantResultDTO the entity to save.
     * @return the persisted entity.
     */
    ParticipantResultDTO save(ParticipantResultDTO participantResultDTO);

    /**
     * Updates a participantResult.
     *
     * @param participantResultDTO the entity to update.
     * @return the persisted entity.
     */
    ParticipantResultDTO update(ParticipantResultDTO participantResultDTO);

    /**
     * Partially updates a participantResult.
     *
     * @param participantResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParticipantResultDTO> partialUpdate(ParticipantResultDTO participantResultDTO);

    /**
     * Get the "id" participantResult.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParticipantResultDTO> findOne(Long id);

    /**
     * Delete the "id" participantResult.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
