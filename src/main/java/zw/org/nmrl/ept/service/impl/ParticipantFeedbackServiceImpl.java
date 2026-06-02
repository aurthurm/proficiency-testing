package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ParticipantFeedback;
import zw.org.nmrl.ept.repository.ParticipantFeedbackRepository;
import zw.org.nmrl.ept.service.ParticipantFeedbackService;
import zw.org.nmrl.ept.service.dto.ParticipantFeedbackDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantFeedbackMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ParticipantFeedback}.
 */
@Service
@Transactional
public class ParticipantFeedbackServiceImpl implements ParticipantFeedbackService {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantFeedbackServiceImpl.class);

    private final ParticipantFeedbackRepository participantFeedbackRepository;

    private final ParticipantFeedbackMapper participantFeedbackMapper;

    public ParticipantFeedbackServiceImpl(
        ParticipantFeedbackRepository participantFeedbackRepository,
        ParticipantFeedbackMapper participantFeedbackMapper
    ) {
        this.participantFeedbackRepository = participantFeedbackRepository;
        this.participantFeedbackMapper = participantFeedbackMapper;
    }

    @Override
    public ParticipantFeedbackDTO save(ParticipantFeedbackDTO participantFeedbackDTO) {
        LOG.debug("Request to save ParticipantFeedback : {}", participantFeedbackDTO);
        ParticipantFeedback participantFeedback = participantFeedbackMapper.toEntity(participantFeedbackDTO);
        participantFeedback = participantFeedbackRepository.save(participantFeedback);
        return participantFeedbackMapper.toDto(participantFeedback);
    }

    @Override
    public ParticipantFeedbackDTO update(ParticipantFeedbackDTO participantFeedbackDTO) {
        LOG.debug("Request to update ParticipantFeedback : {}", participantFeedbackDTO);
        ParticipantFeedback participantFeedback = participantFeedbackMapper.toEntity(participantFeedbackDTO);
        participantFeedback = participantFeedbackRepository.save(participantFeedback);
        return participantFeedbackMapper.toDto(participantFeedback);
    }

    @Override
    public Optional<ParticipantFeedbackDTO> partialUpdate(ParticipantFeedbackDTO participantFeedbackDTO) {
        LOG.debug("Request to partially update ParticipantFeedback : {}", participantFeedbackDTO);

        return participantFeedbackRepository
            .findById(participantFeedbackDTO.getId())
            .map(existingParticipantFeedback -> {
                participantFeedbackMapper.partialUpdate(existingParticipantFeedback, participantFeedbackDTO);

                return existingParticipantFeedback;
            })
            .map(participantFeedbackRepository::save)
            .map(participantFeedbackMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantFeedbackDTO> findAll() {
        LOG.debug("Request to get all ParticipantFeedbacks");
        return participantFeedbackRepository
            .findAll()
            .stream()
            .map(participantFeedbackMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParticipantFeedbackDTO> findOne(Long id) {
        LOG.debug("Request to get ParticipantFeedback : {}", id);
        return participantFeedbackRepository.findById(id).map(participantFeedbackMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ParticipantFeedback : {}", id);
        participantFeedbackRepository.deleteById(id);
    }
}
