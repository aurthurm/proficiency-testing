package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ParticipantCustomValue;
import zw.org.nmrl.ept.repository.ParticipantCustomValueRepository;
import zw.org.nmrl.ept.service.ParticipantCustomValueService;
import zw.org.nmrl.ept.service.dto.ParticipantCustomValueDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantCustomValueMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ParticipantCustomValue}.
 */
@Service
@Transactional
public class ParticipantCustomValueServiceImpl implements ParticipantCustomValueService {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantCustomValueServiceImpl.class);

    private final ParticipantCustomValueRepository participantCustomValueRepository;

    private final ParticipantCustomValueMapper participantCustomValueMapper;

    public ParticipantCustomValueServiceImpl(
        ParticipantCustomValueRepository participantCustomValueRepository,
        ParticipantCustomValueMapper participantCustomValueMapper
    ) {
        this.participantCustomValueRepository = participantCustomValueRepository;
        this.participantCustomValueMapper = participantCustomValueMapper;
    }

    @Override
    public ParticipantCustomValueDTO save(ParticipantCustomValueDTO participantCustomValueDTO) {
        LOG.debug("Request to save ParticipantCustomValue : {}", participantCustomValueDTO);
        ParticipantCustomValue participantCustomValue = participantCustomValueMapper.toEntity(participantCustomValueDTO);
        participantCustomValue = participantCustomValueRepository.save(participantCustomValue);
        return participantCustomValueMapper.toDto(participantCustomValue);
    }

    @Override
    public ParticipantCustomValueDTO update(ParticipantCustomValueDTO participantCustomValueDTO) {
        LOG.debug("Request to update ParticipantCustomValue : {}", participantCustomValueDTO);
        ParticipantCustomValue participantCustomValue = participantCustomValueMapper.toEntity(participantCustomValueDTO);
        participantCustomValue = participantCustomValueRepository.save(participantCustomValue);
        return participantCustomValueMapper.toDto(participantCustomValue);
    }

    @Override
    public Optional<ParticipantCustomValueDTO> partialUpdate(ParticipantCustomValueDTO participantCustomValueDTO) {
        LOG.debug("Request to partially update ParticipantCustomValue : {}", participantCustomValueDTO);

        return participantCustomValueRepository
            .findById(participantCustomValueDTO.getId())
            .map(existingParticipantCustomValue -> {
                participantCustomValueMapper.partialUpdate(existingParticipantCustomValue, participantCustomValueDTO);

                return existingParticipantCustomValue;
            })
            .map(participantCustomValueRepository::save)
            .map(participantCustomValueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantCustomValueDTO> findAll() {
        LOG.debug("Request to get all ParticipantCustomValues");
        return participantCustomValueRepository
            .findAll()
            .stream()
            .map(participantCustomValueMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParticipantCustomValueDTO> findOne(Long id) {
        LOG.debug("Request to get ParticipantCustomValue : {}", id);
        return participantCustomValueRepository.findById(id).map(participantCustomValueMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ParticipantCustomValue : {}", id);
        participantCustomValueRepository.deleteById(id);
    }
}
