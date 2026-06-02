package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.SchemeConfiguration;
import zw.org.nmrl.ept.repository.SchemeConfigurationRepository;
import zw.org.nmrl.ept.service.SchemeConfigurationService;
import zw.org.nmrl.ept.service.dto.SchemeConfigurationDTO;
import zw.org.nmrl.ept.service.mapper.SchemeConfigurationMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.SchemeConfiguration}.
 */
@Service
@Transactional
public class SchemeConfigurationServiceImpl implements SchemeConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(SchemeConfigurationServiceImpl.class);

    private final SchemeConfigurationRepository schemeConfigurationRepository;

    private final SchemeConfigurationMapper schemeConfigurationMapper;

    public SchemeConfigurationServiceImpl(
        SchemeConfigurationRepository schemeConfigurationRepository,
        SchemeConfigurationMapper schemeConfigurationMapper
    ) {
        this.schemeConfigurationRepository = schemeConfigurationRepository;
        this.schemeConfigurationMapper = schemeConfigurationMapper;
    }

    @Override
    public SchemeConfigurationDTO save(SchemeConfigurationDTO schemeConfigurationDTO) {
        LOG.debug("Request to save SchemeConfiguration : {}", schemeConfigurationDTO);
        SchemeConfiguration schemeConfiguration = schemeConfigurationMapper.toEntity(schemeConfigurationDTO);
        schemeConfiguration = schemeConfigurationRepository.save(schemeConfiguration);
        return schemeConfigurationMapper.toDto(schemeConfiguration);
    }

    @Override
    public SchemeConfigurationDTO update(SchemeConfigurationDTO schemeConfigurationDTO) {
        LOG.debug("Request to update SchemeConfiguration : {}", schemeConfigurationDTO);
        SchemeConfiguration schemeConfiguration = schemeConfigurationMapper.toEntity(schemeConfigurationDTO);
        schemeConfiguration = schemeConfigurationRepository.save(schemeConfiguration);
        return schemeConfigurationMapper.toDto(schemeConfiguration);
    }

    @Override
    public Optional<SchemeConfigurationDTO> partialUpdate(SchemeConfigurationDTO schemeConfigurationDTO) {
        LOG.debug("Request to partially update SchemeConfiguration : {}", schemeConfigurationDTO);

        return schemeConfigurationRepository
            .findById(schemeConfigurationDTO.getId())
            .map(existingSchemeConfiguration -> {
                schemeConfigurationMapper.partialUpdate(existingSchemeConfiguration, schemeConfigurationDTO);

                return existingSchemeConfiguration;
            })
            .map(schemeConfigurationRepository::save)
            .map(schemeConfigurationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchemeConfigurationDTO> findAll() {
        LOG.debug("Request to get all SchemeConfigurations");
        return schemeConfigurationRepository
            .findAll()
            .stream()
            .map(schemeConfigurationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SchemeConfigurationDTO> findOne(Long id) {
        LOG.debug("Request to get SchemeConfiguration : {}", id);
        return schemeConfigurationRepository.findById(id).map(schemeConfigurationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SchemeConfiguration : {}", id);
        schemeConfigurationRepository.deleteById(id);
    }
}
