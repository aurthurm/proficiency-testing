package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.SampleReferenceResultDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.SampleReferenceResult}.
 */
public interface SampleReferenceResultService {
    /**
     * Save a sampleReferenceResult.
     *
     * @param sampleReferenceResultDTO the entity to save.
     * @return the persisted entity.
     */
    SampleReferenceResultDTO save(SampleReferenceResultDTO sampleReferenceResultDTO);

    /**
     * Updates a sampleReferenceResult.
     *
     * @param sampleReferenceResultDTO the entity to update.
     * @return the persisted entity.
     */
    SampleReferenceResultDTO update(SampleReferenceResultDTO sampleReferenceResultDTO);

    /**
     * Partially updates a sampleReferenceResult.
     *
     * @param sampleReferenceResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SampleReferenceResultDTO> partialUpdate(SampleReferenceResultDTO sampleReferenceResultDTO);

    /**
     * Get all the sampleReferenceResults.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SampleReferenceResultDTO> findAll(Pageable pageable);

    /**
     * Get the "id" sampleReferenceResult.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SampleReferenceResultDTO> findOne(Long id);

    /**
     * Delete the "id" sampleReferenceResult.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
