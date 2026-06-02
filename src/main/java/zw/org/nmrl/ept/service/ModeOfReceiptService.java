package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.ModeOfReceiptDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ModeOfReceipt}.
 */
public interface ModeOfReceiptService {
    /**
     * Save a modeOfReceipt.
     *
     * @param modeOfReceiptDTO the entity to save.
     * @return the persisted entity.
     */
    ModeOfReceiptDTO save(ModeOfReceiptDTO modeOfReceiptDTO);

    /**
     * Updates a modeOfReceipt.
     *
     * @param modeOfReceiptDTO the entity to update.
     * @return the persisted entity.
     */
    ModeOfReceiptDTO update(ModeOfReceiptDTO modeOfReceiptDTO);

    /**
     * Partially updates a modeOfReceipt.
     *
     * @param modeOfReceiptDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ModeOfReceiptDTO> partialUpdate(ModeOfReceiptDTO modeOfReceiptDTO);

    /**
     * Get all the modeOfReceipts.
     *
     * @return the list of entities.
     */
    List<ModeOfReceiptDTO> findAll();

    /**
     * Get the "id" modeOfReceipt.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ModeOfReceiptDTO> findOne(Long id);

    /**
     * Delete the "id" modeOfReceipt.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
