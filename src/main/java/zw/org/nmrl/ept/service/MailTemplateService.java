package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.MailTemplateDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.MailTemplate}.
 */
public interface MailTemplateService {
    /**
     * Save a mailTemplate.
     *
     * @param mailTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    MailTemplateDTO save(MailTemplateDTO mailTemplateDTO);

    /**
     * Updates a mailTemplate.
     *
     * @param mailTemplateDTO the entity to update.
     * @return the persisted entity.
     */
    MailTemplateDTO update(MailTemplateDTO mailTemplateDTO);

    /**
     * Partially updates a mailTemplate.
     *
     * @param mailTemplateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MailTemplateDTO> partialUpdate(MailTemplateDTO mailTemplateDTO);

    /**
     * Get all the mailTemplates.
     *
     * @return the list of entities.
     */
    List<MailTemplateDTO> findAll();

    /**
     * Get the "id" mailTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MailTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" mailTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
