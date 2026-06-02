package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.ContactMessageDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ContactMessage}.
 */
public interface ContactMessageService {
    /**
     * Save a contactMessage.
     *
     * @param contactMessageDTO the entity to save.
     * @return the persisted entity.
     */
    ContactMessageDTO save(ContactMessageDTO contactMessageDTO);

    /**
     * Updates a contactMessage.
     *
     * @param contactMessageDTO the entity to update.
     * @return the persisted entity.
     */
    ContactMessageDTO update(ContactMessageDTO contactMessageDTO);

    /**
     * Partially updates a contactMessage.
     *
     * @param contactMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ContactMessageDTO> partialUpdate(ContactMessageDTO contactMessageDTO);

    /**
     * Get all the contactMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ContactMessageDTO> findAll(Pageable pageable);

    /**
     * Get the "id" contactMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ContactMessageDTO> findOne(Long id);

    /**
     * Delete the "id" contactMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
