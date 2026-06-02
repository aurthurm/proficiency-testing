package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.DataManager}.
 */
public interface DataManagerService {
    /**
     * Save a dataManager.
     *
     * @param dataManagerDTO the entity to save.
     * @return the persisted entity.
     */
    DataManagerDTO save(DataManagerDTO dataManagerDTO);

    /**
     * Updates a dataManager.
     *
     * @param dataManagerDTO the entity to update.
     * @return the persisted entity.
     */
    DataManagerDTO update(DataManagerDTO dataManagerDTO);

    /**
     * Partially updates a dataManager.
     *
     * @param dataManagerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DataManagerDTO> partialUpdate(DataManagerDTO dataManagerDTO);

    /**
     * Get all the dataManagers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DataManagerDTO> findAll(Pageable pageable);

    /**
     * Get all the dataManagers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DataManagerDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" dataManager.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DataManagerDTO> findOne(Long id);

    /**
     * Delete the "id" dataManager.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
