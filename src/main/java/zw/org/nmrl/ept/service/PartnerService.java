package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.PartnerDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.Partner}.
 */
public interface PartnerService {
    /**
     * Save a partner.
     *
     * @param partnerDTO the entity to save.
     * @return the persisted entity.
     */
    PartnerDTO save(PartnerDTO partnerDTO);

    /**
     * Updates a partner.
     *
     * @param partnerDTO the entity to update.
     * @return the persisted entity.
     */
    PartnerDTO update(PartnerDTO partnerDTO);

    /**
     * Partially updates a partner.
     *
     * @param partnerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PartnerDTO> partialUpdate(PartnerDTO partnerDTO);

    /**
     * Get all the partners.
     *
     * @return the list of entities.
     */
    List<PartnerDTO> findAll();

    /**
     * Get the "id" partner.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PartnerDTO> findOne(Long id);

    /**
     * Delete the "id" partner.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
