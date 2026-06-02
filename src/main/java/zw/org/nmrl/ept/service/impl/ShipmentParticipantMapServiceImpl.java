package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;
import zw.org.nmrl.ept.service.ShipmentParticipantMapService;
import zw.org.nmrl.ept.service.dto.ShipmentParticipantMapDTO;
import zw.org.nmrl.ept.service.mapper.ShipmentParticipantMapMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ShipmentParticipantMap}.
 */
@Service
@Transactional
public class ShipmentParticipantMapServiceImpl implements ShipmentParticipantMapService {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentParticipantMapServiceImpl.class);

    private final ShipmentParticipantMapRepository shipmentParticipantMapRepository;

    private final ShipmentParticipantMapMapper shipmentParticipantMapMapper;

    public ShipmentParticipantMapServiceImpl(
        ShipmentParticipantMapRepository shipmentParticipantMapRepository,
        ShipmentParticipantMapMapper shipmentParticipantMapMapper
    ) {
        this.shipmentParticipantMapRepository = shipmentParticipantMapRepository;
        this.shipmentParticipantMapMapper = shipmentParticipantMapMapper;
    }

    @Override
    public ShipmentParticipantMapDTO save(ShipmentParticipantMapDTO shipmentParticipantMapDTO) {
        LOG.debug("Request to save ShipmentParticipantMap : {}", shipmentParticipantMapDTO);
        ShipmentParticipantMap shipmentParticipantMap = shipmentParticipantMapMapper.toEntity(shipmentParticipantMapDTO);
        shipmentParticipantMap = shipmentParticipantMapRepository.save(shipmentParticipantMap);
        return shipmentParticipantMapMapper.toDto(shipmentParticipantMap);
    }

    @Override
    public ShipmentParticipantMapDTO update(ShipmentParticipantMapDTO shipmentParticipantMapDTO) {
        LOG.debug("Request to update ShipmentParticipantMap : {}", shipmentParticipantMapDTO);
        ShipmentParticipantMap shipmentParticipantMap = shipmentParticipantMapMapper.toEntity(shipmentParticipantMapDTO);
        shipmentParticipantMap = shipmentParticipantMapRepository.save(shipmentParticipantMap);
        return shipmentParticipantMapMapper.toDto(shipmentParticipantMap);
    }

    @Override
    public Optional<ShipmentParticipantMapDTO> partialUpdate(ShipmentParticipantMapDTO shipmentParticipantMapDTO) {
        LOG.debug("Request to partially update ShipmentParticipantMap : {}", shipmentParticipantMapDTO);

        return shipmentParticipantMapRepository
            .findById(shipmentParticipantMapDTO.getId())
            .map(existingShipmentParticipantMap -> {
                shipmentParticipantMapMapper.partialUpdate(existingShipmentParticipantMap, shipmentParticipantMapDTO);

                return existingShipmentParticipantMap;
            })
            .map(shipmentParticipantMapRepository::save)
            .map(shipmentParticipantMapMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShipmentParticipantMapDTO> findOne(Long id) {
        LOG.debug("Request to get ShipmentParticipantMap : {}", id);
        return shipmentParticipantMapRepository.findById(id).map(shipmentParticipantMapMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ShipmentParticipantMap : {}", id);
        shipmentParticipantMapRepository.deleteById(id);
    }
}
