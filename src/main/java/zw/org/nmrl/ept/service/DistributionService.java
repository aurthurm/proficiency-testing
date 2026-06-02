package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.DistributionDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.Distribution}.
 */
public interface DistributionService {
    /**
     * Save a distribution.
     *
     * @param distributionDTO the entity to save.
     * @return the persisted entity.
     */
    DistributionDTO save(DistributionDTO distributionDTO);

    /**
     * Updates a distribution.
     *
     * @param distributionDTO the entity to update.
     * @return the persisted entity.
     */
    DistributionDTO update(DistributionDTO distributionDTO);

    /**
     * Partially updates a distribution.
     *
     * @param distributionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DistributionDTO> partialUpdate(DistributionDTO distributionDTO);

    /**
     * Get the "id" distribution.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DistributionDTO> findOne(Long id);

    /**
     * Delete the "id" distribution.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
