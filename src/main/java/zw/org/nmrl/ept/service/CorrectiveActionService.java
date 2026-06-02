package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.CorrectiveActionDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.CorrectiveAction}.
 */
public interface CorrectiveActionService {
    /**
     * Save a correctiveAction.
     *
     * @param correctiveActionDTO the entity to save.
     * @return the persisted entity.
     */
    CorrectiveActionDTO save(CorrectiveActionDTO correctiveActionDTO);

    /**
     * Updates a correctiveAction.
     *
     * @param correctiveActionDTO the entity to update.
     * @return the persisted entity.
     */
    CorrectiveActionDTO update(CorrectiveActionDTO correctiveActionDTO);

    /**
     * Partially updates a correctiveAction.
     *
     * @param correctiveActionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CorrectiveActionDTO> partialUpdate(CorrectiveActionDTO correctiveActionDTO);

    /**
     * Get all the correctiveActions.
     *
     * @return the list of entities.
     */
    List<CorrectiveActionDTO> findAll();

    /**
     * Get the "id" correctiveAction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CorrectiveActionDTO> findOne(Long id);

    /**
     * Delete the "id" correctiveAction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
