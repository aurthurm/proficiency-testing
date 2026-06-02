package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.repository.ShipmentSampleRepository;
import zw.org.nmrl.ept.service.ShipmentSampleService;
import zw.org.nmrl.ept.service.dto.ShipmentSampleDTO;
import zw.org.nmrl.ept.service.mapper.ShipmentSampleMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ShipmentSample}.
 */
@Service
@Transactional
public class ShipmentSampleServiceImpl implements ShipmentSampleService {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentSampleServiceImpl.class);

    private final ShipmentSampleRepository shipmentSampleRepository;

    private final ShipmentSampleMapper shipmentSampleMapper;

    public ShipmentSampleServiceImpl(ShipmentSampleRepository shipmentSampleRepository, ShipmentSampleMapper shipmentSampleMapper) {
        this.shipmentSampleRepository = shipmentSampleRepository;
        this.shipmentSampleMapper = shipmentSampleMapper;
    }

    @Override
    public ShipmentSampleDTO save(ShipmentSampleDTO shipmentSampleDTO) {
        LOG.debug("Request to save ShipmentSample : {}", shipmentSampleDTO);
        ShipmentSample shipmentSample = shipmentSampleMapper.toEntity(shipmentSampleDTO);
        shipmentSample = shipmentSampleRepository.save(shipmentSample);
        return shipmentSampleMapper.toDto(shipmentSample);
    }

    @Override
    public ShipmentSampleDTO update(ShipmentSampleDTO shipmentSampleDTO) {
        LOG.debug("Request to update ShipmentSample : {}", shipmentSampleDTO);
        ShipmentSample shipmentSample = shipmentSampleMapper.toEntity(shipmentSampleDTO);
        shipmentSample = shipmentSampleRepository.save(shipmentSample);
        return shipmentSampleMapper.toDto(shipmentSample);
    }

    @Override
    public Optional<ShipmentSampleDTO> partialUpdate(ShipmentSampleDTO shipmentSampleDTO) {
        LOG.debug("Request to partially update ShipmentSample : {}", shipmentSampleDTO);

        return shipmentSampleRepository
            .findById(shipmentSampleDTO.getId())
            .map(existingShipmentSample -> {
                shipmentSampleMapper.partialUpdate(existingShipmentSample, shipmentSampleDTO);

                return existingShipmentSample;
            })
            .map(shipmentSampleRepository::save)
            .map(shipmentSampleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentSampleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ShipmentSamples");
        return shipmentSampleRepository.findAll(pageable).map(shipmentSampleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShipmentSampleDTO> findOne(Long id) {
        LOG.debug("Request to get ShipmentSample : {}", id);
        return shipmentSampleRepository.findById(id).map(shipmentSampleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ShipmentSample : {}", id);
        shipmentSampleRepository.deleteById(id);
    }
}
