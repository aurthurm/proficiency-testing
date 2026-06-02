package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.CapaRecordDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.CapaRecord}.
 */
public interface CapaRecordService {
    /**
     * Save a capaRecord.
     *
     * @param capaRecordDTO the entity to save.
     * @return the persisted entity.
     */
    CapaRecordDTO save(CapaRecordDTO capaRecordDTO);

    /**
     * Updates a capaRecord.
     *
     * @param capaRecordDTO the entity to update.
     * @return the persisted entity.
     */
    CapaRecordDTO update(CapaRecordDTO capaRecordDTO);

    /**
     * Partially updates a capaRecord.
     *
     * @param capaRecordDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CapaRecordDTO> partialUpdate(CapaRecordDTO capaRecordDTO);

    /**
     * Get the "id" capaRecord.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CapaRecordDTO> findOne(Long id);

    /**
     * Delete the "id" capaRecord.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
