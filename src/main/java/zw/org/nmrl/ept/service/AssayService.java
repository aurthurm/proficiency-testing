package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.AssayDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.Assay}.
 */
public interface AssayService {
    /**
     * Save a assay.
     *
     * @param assayDTO the entity to save.
     * @return the persisted entity.
     */
    AssayDTO save(AssayDTO assayDTO);

    /**
     * Updates a assay.
     *
     * @param assayDTO the entity to update.
     * @return the persisted entity.
     */
    AssayDTO update(AssayDTO assayDTO);

    /**
     * Partially updates a assay.
     *
     * @param assayDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AssayDTO> partialUpdate(AssayDTO assayDTO);

    /**
     * Get all the assays.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AssayDTO> findAll(Pageable pageable);

    /**
     * Get the "id" assay.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AssayDTO> findOne(Long id);

    /**
     * Delete the "id" assay.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
