package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.SchemeDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.Scheme}.
 */
public interface SchemeService {
    /**
     * Save a scheme.
     *
     * @param schemeDTO the entity to save.
     * @return the persisted entity.
     */
    SchemeDTO save(SchemeDTO schemeDTO);

    /**
     * Updates a scheme.
     *
     * @param schemeDTO the entity to update.
     * @return the persisted entity.
     */
    SchemeDTO update(SchemeDTO schemeDTO);

    /**
     * Partially updates a scheme.
     *
     * @param schemeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SchemeDTO> partialUpdate(SchemeDTO schemeDTO);

    /**
     * Get all the schemes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SchemeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" scheme.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SchemeDTO> findOne(Long id);

    /**
     * Delete the "id" scheme.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
