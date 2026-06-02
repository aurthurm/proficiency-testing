package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.EmailMessageDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.EmailMessage}.
 */
public interface EmailMessageService {
    /**
     * Save a emailMessage.
     *
     * @param emailMessageDTO the entity to save.
     * @return the persisted entity.
     */
    EmailMessageDTO save(EmailMessageDTO emailMessageDTO);

    /**
     * Updates a emailMessage.
     *
     * @param emailMessageDTO the entity to update.
     * @return the persisted entity.
     */
    EmailMessageDTO update(EmailMessageDTO emailMessageDTO);

    /**
     * Partially updates a emailMessage.
     *
     * @param emailMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EmailMessageDTO> partialUpdate(EmailMessageDTO emailMessageDTO);

    /**
     * Get the "id" emailMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EmailMessageDTO> findOne(Long id);

    /**
     * Delete the "id" emailMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
