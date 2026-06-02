package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.SchemeConfigurationDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.SchemeConfiguration}.
 */
public interface SchemeConfigurationService {
    /**
     * Save a schemeConfiguration.
     *
     * @param schemeConfigurationDTO the entity to save.
     * @return the persisted entity.
     */
    SchemeConfigurationDTO save(SchemeConfigurationDTO schemeConfigurationDTO);

    /**
     * Updates a schemeConfiguration.
     *
     * @param schemeConfigurationDTO the entity to update.
     * @return the persisted entity.
     */
    SchemeConfigurationDTO update(SchemeConfigurationDTO schemeConfigurationDTO);

    /**
     * Partially updates a schemeConfiguration.
     *
     * @param schemeConfigurationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SchemeConfigurationDTO> partialUpdate(SchemeConfigurationDTO schemeConfigurationDTO);

    /**
     * Get all the schemeConfigurations.
     *
     * @return the list of entities.
     */
    List<SchemeConfigurationDTO> findAll();

    /**
     * Get the "id" schemeConfiguration.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SchemeConfigurationDTO> findOne(Long id);

    /**
     * Delete the "id" schemeConfiguration.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
