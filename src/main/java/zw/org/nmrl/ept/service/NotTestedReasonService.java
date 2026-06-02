package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.NotTestedReasonDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.NotTestedReason}.
 */
public interface NotTestedReasonService {
    /**
     * Save a notTestedReason.
     *
     * @param notTestedReasonDTO the entity to save.
     * @return the persisted entity.
     */
    NotTestedReasonDTO save(NotTestedReasonDTO notTestedReasonDTO);

    /**
     * Updates a notTestedReason.
     *
     * @param notTestedReasonDTO the entity to update.
     * @return the persisted entity.
     */
    NotTestedReasonDTO update(NotTestedReasonDTO notTestedReasonDTO);

    /**
     * Partially updates a notTestedReason.
     *
     * @param notTestedReasonDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotTestedReasonDTO> partialUpdate(NotTestedReasonDTO notTestedReasonDTO);

    /**
     * Get all the notTestedReasons.
     *
     * @return the list of entities.
     */
    List<NotTestedReasonDTO> findAll();

    /**
     * Get the "id" notTestedReason.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotTestedReasonDTO> findOne(Long id);

    /**
     * Delete the "id" notTestedReason.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
