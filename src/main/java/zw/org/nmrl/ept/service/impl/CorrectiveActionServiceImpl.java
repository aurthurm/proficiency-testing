package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.CorrectiveAction;
import zw.org.nmrl.ept.repository.CorrectiveActionRepository;
import zw.org.nmrl.ept.service.CorrectiveActionService;
import zw.org.nmrl.ept.service.dto.CorrectiveActionDTO;
import zw.org.nmrl.ept.service.mapper.CorrectiveActionMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.CorrectiveAction}.
 */
@Service
@Transactional
public class CorrectiveActionServiceImpl implements CorrectiveActionService {

    private static final Logger LOG = LoggerFactory.getLogger(CorrectiveActionServiceImpl.class);

    private final CorrectiveActionRepository correctiveActionRepository;

    private final CorrectiveActionMapper correctiveActionMapper;

    public CorrectiveActionServiceImpl(
        CorrectiveActionRepository correctiveActionRepository,
        CorrectiveActionMapper correctiveActionMapper
    ) {
        this.correctiveActionRepository = correctiveActionRepository;
        this.correctiveActionMapper = correctiveActionMapper;
    }

    @Override
    public CorrectiveActionDTO save(CorrectiveActionDTO correctiveActionDTO) {
        LOG.debug("Request to save CorrectiveAction : {}", correctiveActionDTO);
        CorrectiveAction correctiveAction = correctiveActionMapper.toEntity(correctiveActionDTO);
        correctiveAction = correctiveActionRepository.save(correctiveAction);
        return correctiveActionMapper.toDto(correctiveAction);
    }

    @Override
    public CorrectiveActionDTO update(CorrectiveActionDTO correctiveActionDTO) {
        LOG.debug("Request to update CorrectiveAction : {}", correctiveActionDTO);
        CorrectiveAction correctiveAction = correctiveActionMapper.toEntity(correctiveActionDTO);
        correctiveAction = correctiveActionRepository.save(correctiveAction);
        return correctiveActionMapper.toDto(correctiveAction);
    }

    @Override
    public Optional<CorrectiveActionDTO> partialUpdate(CorrectiveActionDTO correctiveActionDTO) {
        LOG.debug("Request to partially update CorrectiveAction : {}", correctiveActionDTO);

        return correctiveActionRepository
            .findById(correctiveActionDTO.getId())
            .map(existingCorrectiveAction -> {
                correctiveActionMapper.partialUpdate(existingCorrectiveAction, correctiveActionDTO);

                return existingCorrectiveAction;
            })
            .map(correctiveActionRepository::save)
            .map(correctiveActionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrectiveActionDTO> findAll() {
        LOG.debug("Request to get all CorrectiveActions");
        return correctiveActionRepository
            .findAll()
            .stream()
            .map(correctiveActionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CorrectiveActionDTO> findOne(Long id) {
        LOG.debug("Request to get CorrectiveAction : {}", id);
        return correctiveActionRepository.findById(id).map(correctiveActionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CorrectiveAction : {}", id);
        correctiveActionRepository.deleteById(id);
    }
}
