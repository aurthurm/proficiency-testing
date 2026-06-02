package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.ParticipantMessageDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ParticipantMessage}.
 */
public interface ParticipantMessageService {
    /**
     * Save a participantMessage.
     *
     * @param participantMessageDTO the entity to save.
     * @return the persisted entity.
     */
    ParticipantMessageDTO save(ParticipantMessageDTO participantMessageDTO);

    /**
     * Updates a participantMessage.
     *
     * @param participantMessageDTO the entity to update.
     * @return the persisted entity.
     */
    ParticipantMessageDTO update(ParticipantMessageDTO participantMessageDTO);

    /**
     * Partially updates a participantMessage.
     *
     * @param participantMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ParticipantMessageDTO> partialUpdate(ParticipantMessageDTO participantMessageDTO);

    /**
     * Get all the participantMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ParticipantMessageDTO> findAll(Pageable pageable);

    /**
     * Get the "id" participantMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ParticipantMessageDTO> findOne(Long id);

    /**
     * Delete the "id" participantMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
