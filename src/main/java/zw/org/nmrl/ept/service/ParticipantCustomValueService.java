package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.ParticipantCustomValueDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ParticipantCustomValue}.
 */
public interface ParticipantCustomValueService {
    /**
     * Save a participantCustomValue.
     *
     * @param participantCustomValueDTO the entity to save.
     * @return the persisted entity.
     */
    ParticipantCustomValueDTO save(ParticipantCustomValueDTO participantCustomValueDTO);

    /**
     * Updates a participantCustomValue.
     *
     * @param participantCustomValueDTO the entity to update.
     * @return the persisted entity.
     */
    ParticipantCustomValueDTO update(ParticipantCustomValueDTO participantCustomValueDTO);

    /**
     * Partially updates a participantCustomValue.
     *
     * @param participantCustomValueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParticipantCustomValueDTO> partialUpdate(ParticipantCustomValueDTO participantCustomValueDTO);

    /**
     * Get all the participantCustomValues.
     *
     * @return the list of entities.
     */
    List<ParticipantCustomValueDTO> findAll();

    /**
     * Get the "id" participantCustomValue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParticipantCustomValueDTO> findOne(Long id);

    /**
     * Delete the "id" participantCustomValue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
