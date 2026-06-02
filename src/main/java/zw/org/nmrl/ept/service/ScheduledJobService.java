package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.ScheduledJobDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ScheduledJob}.
 */
public interface ScheduledJobService {
    /**
     * Save a scheduledJob.
     *
     * @param scheduledJobDTO the entity to save.
     * @return the persisted entity.
     */
    ScheduledJobDTO save(ScheduledJobDTO scheduledJobDTO);

    /**
     * Updates a scheduledJob.
     *
     * @param scheduledJobDTO the entity to update.
     * @return the persisted entity.
     */
    ScheduledJobDTO update(ScheduledJobDTO scheduledJobDTO);

    /**
     * Partially updates a scheduledJob.
     *
     * @param scheduledJobDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ScheduledJobDTO> partialUpdate(ScheduledJobDTO scheduledJobDTO);

    /**
     * Get the "id" scheduledJob.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ScheduledJobDTO> findOne(Long id);

    /**
     * Delete the "id" scheduledJob.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
