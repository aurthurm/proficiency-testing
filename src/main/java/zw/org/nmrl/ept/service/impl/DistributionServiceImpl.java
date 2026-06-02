package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.Distribution;
import zw.org.nmrl.ept.repository.DistributionRepository;
import zw.org.nmrl.ept.service.DistributionService;
import zw.org.nmrl.ept.service.dto.DistributionDTO;
import zw.org.nmrl.ept.service.mapper.DistributionMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.Distribution}.
 */
@Service
@Transactional
public class DistributionServiceImpl implements DistributionService {

    private static final Logger LOG = LoggerFactory.getLogger(DistributionServiceImpl.class);

    private final DistributionRepository distributionRepository;

    private final DistributionMapper distributionMapper;

    public DistributionServiceImpl(DistributionRepository distributionRepository, DistributionMapper distributionMapper) {
        this.distributionRepository = distributionRepository;
        this.distributionMapper = distributionMapper;
    }

    @Override
    public DistributionDTO save(DistributionDTO distributionDTO) {
        LOG.debug("Request to save Distribution : {}", distributionDTO);
        Distribution distribution = distributionMapper.toEntity(distributionDTO);
        distribution = distributionRepository.save(distribution);
        return distributionMapper.toDto(distribution);
    }

    @Override
    public DistributionDTO update(DistributionDTO distributionDTO) {
        LOG.debug("Request to update Distribution : {}", distributionDTO);
        Distribution distribution = distributionMapper.toEntity(distributionDTO);
        distribution = distributionRepository.save(distribution);
        return distributionMapper.toDto(distribution);
    }

    @Override
    public Optional<DistributionDTO> partialUpdate(DistributionDTO distributionDTO) {
        LOG.debug("Request to partially update Distribution : {}", distributionDTO);

        return distributionRepository
            .findById(distributionDTO.getId())
            .map(existingDistribution -> {
                distributionMapper.partialUpdate(existingDistribution, distributionDTO);

                return existingDistribution;
            })
            .map(distributionRepository::save)
            .map(distributionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DistributionDTO> findOne(Long id) {
        LOG.debug("Request to get Distribution : {}", id);
        return distributionRepository.findById(id).map(distributionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Distribution : {}", id);
        distributionRepository.deleteById(id);
    }
}
