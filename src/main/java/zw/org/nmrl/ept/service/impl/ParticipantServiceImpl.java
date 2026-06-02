package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.repository.ParticipantRepository;
import zw.org.nmrl.ept.service.ParticipantService;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.Participant}.
 */
@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    private final ParticipantRepository participantRepository;

    private final ParticipantMapper participantMapper;

    public ParticipantServiceImpl(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    @Override
    public ParticipantDTO save(ParticipantDTO participantDTO) {
        LOG.debug("Request to save Participant : {}", participantDTO);
        Participant participant = participantMapper.toEntity(participantDTO);
        participant = participantRepository.save(participant);
        return participantMapper.toDto(participant);
    }

    @Override
    public ParticipantDTO update(ParticipantDTO participantDTO) {
        LOG.debug("Request to update Participant : {}", participantDTO);
        Participant participant = participantMapper.toEntity(participantDTO);
        participant = participantRepository.save(participant);
        return participantMapper.toDto(participant);
    }

    @Override
    public Optional<ParticipantDTO> partialUpdate(ParticipantDTO participantDTO) {
        LOG.debug("Request to partially update Participant : {}", participantDTO);

        return participantRepository
            .findById(participantDTO.getId())
            .map(existingParticipant -> {
                participantMapper.partialUpdate(existingParticipant, participantDTO);

                return existingParticipant;
            })
            .map(participantRepository::save)
            .map(participantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParticipantDTO> findOne(Long id) {
        LOG.debug("Request to get Participant : {}", id);
        return participantRepository.findById(id).map(participantMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Participant : {}", id);
        participantRepository.deleteById(id);
    }
}
