package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.ReportConfigurationDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ReportConfiguration}.
 */
public interface ReportConfigurationService {
    /**
     * Save a reportConfiguration.
     *
     * @param reportConfigurationDTO the entity to save.
     * @return the persisted entity.
     */
    ReportConfigurationDTO save(ReportConfigurationDTO reportConfigurationDTO);

    /**
     * Updates a reportConfiguration.
     *
     * @param reportConfigurationDTO the entity to update.
     * @return the persisted entity.
     */
    ReportConfigurationDTO update(ReportConfigurationDTO reportConfigurationDTO);

    /**
     * Partially updates a reportConfiguration.
     *
     * @param reportConfigurationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReportConfigurationDTO> partialUpdate(ReportConfigurationDTO reportConfigurationDTO);

    /**
     * Get all the reportConfigurations.
     *
     * @return the list of entities.
     */
    List<ReportConfigurationDTO> findAll();

    /**
     * Get the "id" reportConfiguration.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReportConfigurationDTO> findOne(Long id);

    /**
     * Delete the "id" reportConfiguration.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
