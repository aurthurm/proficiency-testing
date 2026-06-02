package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.ShipmentSampleDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ShipmentSample}.
 */
public interface ShipmentSampleService {
    /**
     * Save a shipmentSample.
     *
     * @param shipmentSampleDTO the entity to save.
     * @return the persisted entity.
     */
    ShipmentSampleDTO save(ShipmentSampleDTO shipmentSampleDTO);

    /**
     * Updates a shipmentSample.
     *
     * @param shipmentSampleDTO the entity to update.
     * @return the persisted entity.
     */
    ShipmentSampleDTO update(ShipmentSampleDTO shipmentSampleDTO);

    /**
     * Partially updates a shipmentSample.
     *
     * @param shipmentSampleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShipmentSampleDTO> partialUpdate(ShipmentSampleDTO shipmentSampleDTO);

    /**
     * Get all the shipmentSamples.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ShipmentSampleDTO> findAll(Pageable pageable);

    /**
     * Get the "id" shipmentSample.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShipmentSampleDTO> findOne(Long id);

    /**
     * Delete the "id" shipmentSample.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
