package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.CertificateBatchDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.CertificateBatch}.
 */
public interface CertificateBatchService {
    /**
     * Save a certificateBatch.
     *
     * @param certificateBatchDTO the entity to save.
     * @return the persisted entity.
     */
    CertificateBatchDTO save(CertificateBatchDTO certificateBatchDTO);

    /**
     * Updates a certificateBatch.
     *
     * @param certificateBatchDTO the entity to update.
     * @return the persisted entity.
     */
    CertificateBatchDTO update(CertificateBatchDTO certificateBatchDTO);

    /**
     * Partially updates a certificateBatch.
     *
     * @param certificateBatchDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CertificateBatchDTO> partialUpdate(CertificateBatchDTO certificateBatchDTO);

    /**
     * Get all the certificateBatches.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CertificateBatchDTO> findAll(Pageable pageable);

    /**
     * Get all the certificateBatches with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CertificateBatchDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" certificateBatch.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CertificateBatchDTO> findOne(Long id);

    /**
     * Delete the "id" certificateBatch.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
