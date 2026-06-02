package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.CapaRecord;
import zw.org.nmrl.ept.repository.CapaRecordRepository;
import zw.org.nmrl.ept.service.CapaRecordService;
import zw.org.nmrl.ept.service.dto.CapaRecordDTO;
import zw.org.nmrl.ept.service.mapper.CapaRecordMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.CapaRecord}.
 */
@Service
@Transactional
public class CapaRecordServiceImpl implements CapaRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(CapaRecordServiceImpl.class);

    private final CapaRecordRepository capaRecordRepository;

    private final CapaRecordMapper capaRecordMapper;

    public CapaRecordServiceImpl(CapaRecordRepository capaRecordRepository, CapaRecordMapper capaRecordMapper) {
        this.capaRecordRepository = capaRecordRepository;
        this.capaRecordMapper = capaRecordMapper;
    }

    @Override
    public CapaRecordDTO save(CapaRecordDTO capaRecordDTO) {
        LOG.debug("Request to save CapaRecord : {}", capaRecordDTO);
        CapaRecord capaRecord = capaRecordMapper.toEntity(capaRecordDTO);
        capaRecord = capaRecordRepository.save(capaRecord);
        return capaRecordMapper.toDto(capaRecord);
    }

    @Override
    public CapaRecordDTO update(CapaRecordDTO capaRecordDTO) {
        LOG.debug("Request to update CapaRecord : {}", capaRecordDTO);
        CapaRecord capaRecord = capaRecordMapper.toEntity(capaRecordDTO);
        capaRecord = capaRecordRepository.save(capaRecord);
        return capaRecordMapper.toDto(capaRecord);
    }

    @Override
    public Optional<CapaRecordDTO> partialUpdate(CapaRecordDTO capaRecordDTO) {
        LOG.debug("Request to partially update CapaRecord : {}", capaRecordDTO);

        return capaRecordRepository
            .findById(capaRecordDTO.getId())
            .map(existingCapaRecord -> {
                capaRecordMapper.partialUpdate(existingCapaRecord, capaRecordDTO);

                return existingCapaRecord;
            })
            .map(capaRecordRepository::save)
            .map(capaRecordMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CapaRecordDTO> findOne(Long id) {
        LOG.debug("Request to get CapaRecord : {}", id);
        return capaRecordRepository.findById(id).map(capaRecordMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CapaRecord : {}", id);
        capaRecordRepository.deleteById(id);
    }
}
