package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.GlobalConfigurationDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.GlobalConfiguration}.
 */
public interface GlobalConfigurationService {
    /**
     * Save a globalConfiguration.
     *
     * @param globalConfigurationDTO the entity to save.
     * @return the persisted entity.
     */
    GlobalConfigurationDTO save(GlobalConfigurationDTO globalConfigurationDTO);

    /**
     * Updates a globalConfiguration.
     *
     * @param globalConfigurationDTO the entity to update.
     * @return the persisted entity.
     */
    GlobalConfigurationDTO update(GlobalConfigurationDTO globalConfigurationDTO);

    /**
     * Partially updates a globalConfiguration.
     *
     * @param globalConfigurationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GlobalConfigurationDTO> partialUpdate(GlobalConfigurationDTO globalConfigurationDTO);

    /**
     * Get all the globalConfigurations.
     *
     * @return the list of entities.
     */
    List<GlobalConfigurationDTO> findAll();

    /**
     * Get the "id" globalConfiguration.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GlobalConfigurationDTO> findOne(Long id);

    /**
     * Delete the "id" globalConfiguration.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
