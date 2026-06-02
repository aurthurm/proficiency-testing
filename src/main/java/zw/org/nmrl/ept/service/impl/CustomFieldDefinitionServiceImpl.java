package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.CustomFieldDefinition;
import zw.org.nmrl.ept.repository.CustomFieldDefinitionRepository;
import zw.org.nmrl.ept.service.CustomFieldDefinitionService;
import zw.org.nmrl.ept.service.dto.CustomFieldDefinitionDTO;
import zw.org.nmrl.ept.service.mapper.CustomFieldDefinitionMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.CustomFieldDefinition}.
 */
@Service
@Transactional
public class CustomFieldDefinitionServiceImpl implements CustomFieldDefinitionService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFieldDefinitionServiceImpl.class);

    private final CustomFieldDefinitionRepository customFieldDefinitionRepository;

    private final CustomFieldDefinitionMapper customFieldDefinitionMapper;

    public CustomFieldDefinitionServiceImpl(
        CustomFieldDefinitionRepository customFieldDefinitionRepository,
        CustomFieldDefinitionMapper customFieldDefinitionMapper
    ) {
        this.customFieldDefinitionRepository = customFieldDefinitionRepository;
        this.customFieldDefinitionMapper = customFieldDefinitionMapper;
    }

    @Override
    public CustomFieldDefinitionDTO save(CustomFieldDefinitionDTO customFieldDefinitionDTO) {
        LOG.debug("Request to save CustomFieldDefinition : {}", customFieldDefinitionDTO);
        CustomFieldDefinition customFieldDefinition = customFieldDefinitionMapper.toEntity(customFieldDefinitionDTO);
        customFieldDefinition = customFieldDefinitionRepository.save(customFieldDefinition);
        return customFieldDefinitionMapper.toDto(customFieldDefinition);
    }

    @Override
    public CustomFieldDefinitionDTO update(CustomFieldDefinitionDTO customFieldDefinitionDTO) {
        LOG.debug("Request to update CustomFieldDefinition : {}", customFieldDefinitionDTO);
        CustomFieldDefinition customFieldDefinition = customFieldDefinitionMapper.toEntity(customFieldDefinitionDTO);
        customFieldDefinition = customFieldDefinitionRepository.save(customFieldDefinition);
        return customFieldDefinitionMapper.toDto(customFieldDefinition);
    }

    @Override
    public Optional<CustomFieldDefinitionDTO> partialUpdate(CustomFieldDefinitionDTO customFieldDefinitionDTO) {
        LOG.debug("Request to partially update CustomFieldDefinition : {}", customFieldDefinitionDTO);

        return customFieldDefinitionRepository
            .findById(customFieldDefinitionDTO.getId())
            .map(existingCustomFieldDefinition -> {
                customFieldDefinitionMapper.partialUpdate(existingCustomFieldDefinition, customFieldDefinitionDTO);

                return existingCustomFieldDefinition;
            })
            .map(customFieldDefinitionRepository::save)
            .map(customFieldDefinitionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomFieldDefinitionDTO> findAll() {
        LOG.debug("Request to get all CustomFieldDefinitions");
        return customFieldDefinitionRepository
            .findAll()
            .stream()
            .map(customFieldDefinitionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomFieldDefinitionDTO> findOne(Long id) {
        LOG.debug("Request to get CustomFieldDefinition : {}", id);
        return customFieldDefinitionRepository.findById(id).map(customFieldDefinitionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CustomFieldDefinition : {}", id);
        customFieldDefinitionRepository.deleteById(id);
    }
}
