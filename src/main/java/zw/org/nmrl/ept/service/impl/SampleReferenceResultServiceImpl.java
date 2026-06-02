package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.SampleReferenceResult;
import zw.org.nmrl.ept.repository.SampleReferenceResultRepository;
import zw.org.nmrl.ept.service.SampleReferenceResultService;
import zw.org.nmrl.ept.service.dto.SampleReferenceResultDTO;
import zw.org.nmrl.ept.service.mapper.SampleReferenceResultMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.SampleReferenceResult}.
 */
@Service
@Transactional
public class SampleReferenceResultServiceImpl implements SampleReferenceResultService {

    private static final Logger LOG = LoggerFactory.getLogger(SampleReferenceResultServiceImpl.class);

    private final SampleReferenceResultRepository sampleReferenceResultRepository;

    private final SampleReferenceResultMapper sampleReferenceResultMapper;

    public SampleReferenceResultServiceImpl(
        SampleReferenceResultRepository sampleReferenceResultRepository,
        SampleReferenceResultMapper sampleReferenceResultMapper
    ) {
        this.sampleReferenceResultRepository = sampleReferenceResultRepository;
        this.sampleReferenceResultMapper = sampleReferenceResultMapper;
    }

    @Override
    public SampleReferenceResultDTO save(SampleReferenceResultDTO sampleReferenceResultDTO) {
        LOG.debug("Request to save SampleReferenceResult : {}", sampleReferenceResultDTO);
        SampleReferenceResult sampleReferenceResult = sampleReferenceResultMapper.toEntity(sampleReferenceResultDTO);
        sampleReferenceResult = sampleReferenceResultRepository.save(sampleReferenceResult);
        return sampleReferenceResultMapper.toDto(sampleReferenceResult);
    }

    @Override
    public SampleReferenceResultDTO update(SampleReferenceResultDTO sampleReferenceResultDTO) {
        LOG.debug("Request to update SampleReferenceResult : {}", sampleReferenceResultDTO);
        SampleReferenceResult sampleReferenceResult = sampleReferenceResultMapper.toEntity(sampleReferenceResultDTO);
        sampleReferenceResult = sampleReferenceResultRepository.save(sampleReferenceResult);
        return sampleReferenceResultMapper.toDto(sampleReferenceResult);
    }

    @Override
    public Optional<SampleReferenceResultDTO> partialUpdate(SampleReferenceResultDTO sampleReferenceResultDTO) {
        LOG.debug("Request to partially update SampleReferenceResult : {}", sampleReferenceResultDTO);

        return sampleReferenceResultRepository
            .findById(sampleReferenceResultDTO.getId())
            .map(existingSampleReferenceResult -> {
                sampleReferenceResultMapper.partialUpdate(existingSampleReferenceResult, sampleReferenceResultDTO);

                return existingSampleReferenceResult;
            })
            .map(sampleReferenceResultRepository::save)
            .map(sampleReferenceResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SampleReferenceResultDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SampleReferenceResults");
        return sampleReferenceResultRepository.findAll(pageable).map(sampleReferenceResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SampleReferenceResultDTO> findOne(Long id) {
        LOG.debug("Request to get SampleReferenceResult : {}", id);
        return sampleReferenceResultRepository.findById(id).map(sampleReferenceResultMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SampleReferenceResult : {}", id);
        sampleReferenceResultRepository.deleteById(id);
    }
}
