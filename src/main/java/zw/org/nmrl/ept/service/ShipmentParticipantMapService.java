package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.ShipmentParticipantMapDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ShipmentParticipantMap}.
 */
public interface ShipmentParticipantMapService {
    /**
     * Save a shipmentParticipantMap.
     *
     * @param shipmentParticipantMapDTO the entity to save.
     * @return the persisted entity.
     */
    ShipmentParticipantMapDTO save(ShipmentParticipantMapDTO shipmentParticipantMapDTO);

    /**
     * Updates a shipmentParticipantMap.
     *
     * @param shipmentParticipantMapDTO the entity to update.
     * @return the persisted entity.
     */
    ShipmentParticipantMapDTO update(ShipmentParticipantMapDTO shipmentParticipantMapDTO);

    /**
     * Partially updates a shipmentParticipantMap.
     *
     * @param shipmentParticipantMapDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShipmentParticipantMapDTO> partialUpdate(ShipmentParticipantMapDTO shipmentParticipantMapDTO);

    /**
     * Get the "id" shipmentParticipantMap.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShipmentParticipantMapDTO> findOne(Long id);

    /**
     * Delete the "id" shipmentParticipantMap.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
