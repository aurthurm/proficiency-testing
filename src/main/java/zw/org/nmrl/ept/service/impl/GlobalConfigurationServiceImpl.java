package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.GlobalConfiguration;
import zw.org.nmrl.ept.repository.GlobalConfigurationRepository;
import zw.org.nmrl.ept.service.GlobalConfigurationService;
import zw.org.nmrl.ept.service.dto.GlobalConfigurationDTO;
import zw.org.nmrl.ept.service.mapper.GlobalConfigurationMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.GlobalConfiguration}.
 */
@Service
@Transactional
public class GlobalConfigurationServiceImpl implements GlobalConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalConfigurationServiceImpl.class);

    private final GlobalConfigurationRepository globalConfigurationRepository;

    private final GlobalConfigurationMapper globalConfigurationMapper;

    public GlobalConfigurationServiceImpl(
        GlobalConfigurationRepository globalConfigurationRepository,
        GlobalConfigurationMapper globalConfigurationMapper
    ) {
        this.globalConfigurationRepository = globalConfigurationRepository;
        this.globalConfigurationMapper = globalConfigurationMapper;
    }

    @Override
    public GlobalConfigurationDTO save(GlobalConfigurationDTO globalConfigurationDTO) {
        LOG.debug("Request to save GlobalConfiguration : {}", globalConfigurationDTO);
        GlobalConfiguration globalConfiguration = globalConfigurationMapper.toEntity(globalConfigurationDTO);
        globalConfiguration = globalConfigurationRepository.save(globalConfiguration);
        return globalConfigurationMapper.toDto(globalConfiguration);
    }

    @Override
    public GlobalConfigurationDTO update(GlobalConfigurationDTO globalConfigurationDTO) {
        LOG.debug("Request to update GlobalConfiguration : {}", globalConfigurationDTO);
        GlobalConfiguration globalConfiguration = globalConfigurationMapper.toEntity(globalConfigurationDTO);
        globalConfiguration = globalConfigurationRepository.save(globalConfiguration);
        return globalConfigurationMapper.toDto(globalConfiguration);
    }

    @Override
    public Optional<GlobalConfigurationDTO> partialUpdate(GlobalConfigurationDTO globalConfigurationDTO) {
        LOG.debug("Request to partially update GlobalConfiguration : {}", globalConfigurationDTO);

        return globalConfigurationRepository
            .findById(globalConfigurationDTO.getId())
            .map(existingGlobalConfiguration -> {
                globalConfigurationMapper.partialUpdate(existingGlobalConfiguration, globalConfigurationDTO);

                return existingGlobalConfiguration;
            })
            .map(globalConfigurationRepository::save)
            .map(globalConfigurationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GlobalConfigurationDTO> findAll() {
        LOG.debug("Request to get all GlobalConfigurations");
        return globalConfigurationRepository
            .findAll()
            .stream()
            .map(globalConfigurationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GlobalConfigurationDTO> findOne(Long id) {
        LOG.debug("Request to get GlobalConfiguration : {}", id);
        return globalConfigurationRepository.findById(id).map(globalConfigurationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete GlobalConfiguration : {}", id);
        globalConfigurationRepository.deleteById(id);
    }
}
