package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.CustomFieldDefinitionDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.CustomFieldDefinition}.
 */
public interface CustomFieldDefinitionService {
    /**
     * Save a customFieldDefinition.
     *
     * @param customFieldDefinitionDTO the entity to save.
     * @return the persisted entity.
     */
    CustomFieldDefinitionDTO save(CustomFieldDefinitionDTO customFieldDefinitionDTO);

    /**
     * Updates a customFieldDefinition.
     *
     * @param customFieldDefinitionDTO the entity to update.
     * @return the persisted entity.
     */
    CustomFieldDefinitionDTO update(CustomFieldDefinitionDTO customFieldDefinitionDTO);

    /**
     * Partially updates a customFieldDefinition.
     *
     * @param customFieldDefinitionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CustomFieldDefinitionDTO> partialUpdate(CustomFieldDefinitionDTO customFieldDefinitionDTO);

    /**
     * Get all the customFieldDefinitions.
     *
     * @return the list of entities.
     */
    List<CustomFieldDefinitionDTO> findAll();

    /**
     * Get the "id" customFieldDefinition.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CustomFieldDefinitionDTO> findOne(Long id);

    /**
     * Delete the "id" customFieldDefinition.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
