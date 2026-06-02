package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.repository.ScheduledJobRepository;
import zw.org.nmrl.ept.service.ScheduledJobService;
import zw.org.nmrl.ept.service.dto.ScheduledJobDTO;
import zw.org.nmrl.ept.service.mapper.ScheduledJobMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ScheduledJob}.
 */
@Service
@Transactional
public class ScheduledJobServiceImpl implements ScheduledJobService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledJobServiceImpl.class);

    private final ScheduledJobRepository scheduledJobRepository;

    private final ScheduledJobMapper scheduledJobMapper;

    public ScheduledJobServiceImpl(ScheduledJobRepository scheduledJobRepository, ScheduledJobMapper scheduledJobMapper) {
        this.scheduledJobRepository = scheduledJobRepository;
        this.scheduledJobMapper = scheduledJobMapper;
    }

    @Override
    public ScheduledJobDTO save(ScheduledJobDTO scheduledJobDTO) {
        LOG.debug("Request to save ScheduledJob : {}", scheduledJobDTO);
        ScheduledJob scheduledJob = scheduledJobMapper.toEntity(scheduledJobDTO);
        scheduledJob = scheduledJobRepository.save(scheduledJob);
        return scheduledJobMapper.toDto(scheduledJob);
    }

    @Override
    public ScheduledJobDTO update(ScheduledJobDTO scheduledJobDTO) {
        LOG.debug("Request to update ScheduledJob : {}", scheduledJobDTO);
        ScheduledJob scheduledJob = scheduledJobMapper.toEntity(scheduledJobDTO);
        scheduledJob = scheduledJobRepository.save(scheduledJob);
        return scheduledJobMapper.toDto(scheduledJob);
    }

    @Override
    public Optional<ScheduledJobDTO> partialUpdate(ScheduledJobDTO scheduledJobDTO) {
        LOG.debug("Request to partially update ScheduledJob : {}", scheduledJobDTO);

        return scheduledJobRepository
            .findById(scheduledJobDTO.getId())
            .map(existingScheduledJob -> {
                scheduledJobMapper.partialUpdate(existingScheduledJob, scheduledJobDTO);

                return existingScheduledJob;
            })
            .map(scheduledJobRepository::save)
            .map(scheduledJobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduledJobDTO> findOne(Long id) {
        LOG.debug("Request to get ScheduledJob : {}", id);
        return scheduledJobRepository.findById(id).map(scheduledJobMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ScheduledJob : {}", id);
        scheduledJobRepository.deleteById(id);
    }
}
