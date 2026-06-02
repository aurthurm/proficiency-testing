package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.CertificateTemplateDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.CertificateTemplate}.
 */
public interface CertificateTemplateService {
    /**
     * Save a certificateTemplate.
     *
     * @param certificateTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    CertificateTemplateDTO save(CertificateTemplateDTO certificateTemplateDTO);

    /**
     * Updates a certificateTemplate.
     *
     * @param certificateTemplateDTO the entity to update.
     * @return the persisted entity.
     */
    CertificateTemplateDTO update(CertificateTemplateDTO certificateTemplateDTO);

    /**
     * Partially updates a certificateTemplate.
     *
     * @param certificateTemplateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CertificateTemplateDTO> partialUpdate(CertificateTemplateDTO certificateTemplateDTO);

    /**
     * Get all the certificateTemplates.
     *
     * @return the list of entities.
     */
    List<CertificateTemplateDTO> findAll();

    /**
     * Get all the CertificateTemplateDTO where Scheme is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<CertificateTemplateDTO> findAllWhereSchemeIsNull();

    /**
     * Get the "id" certificateTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CertificateTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" certificateTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
