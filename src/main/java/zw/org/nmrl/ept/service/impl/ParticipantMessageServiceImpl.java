package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ParticipantMessage;
import zw.org.nmrl.ept.repository.ParticipantMessageRepository;
import zw.org.nmrl.ept.service.ParticipantMessageService;
import zw.org.nmrl.ept.service.dto.ParticipantMessageDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantMessageMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ParticipantMessage}.
 */
@Service
@Transactional
public class ParticipantMessageServiceImpl implements ParticipantMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantMessageServiceImpl.class);

    private final ParticipantMessageRepository participantMessageRepository;

    private final ParticipantMessageMapper participantMessageMapper;

    public ParticipantMessageServiceImpl(
        ParticipantMessageRepository participantMessageRepository,
        ParticipantMessageMapper participantMessageMapper
    ) {
        this.participantMessageRepository = participantMessageRepository;
        this.participantMessageMapper = participantMessageMapper;
    }

    @Override
    public ParticipantMessageDTO save(ParticipantMessageDTO participantMessageDTO) {
        LOG.debug("Request to save ParticipantMessage : {}", participantMessageDTO);
        ParticipantMessage participantMessage = participantMessageMapper.toEntity(participantMessageDTO);
        participantMessage = participantMessageRepository.save(participantMessage);
        return participantMessageMapper.toDto(participantMessage);
    }

    @Override
    public ParticipantMessageDTO update(ParticipantMessageDTO participantMessageDTO) {
        LOG.debug("Request to update ParticipantMessage : {}", participantMessageDTO);
        ParticipantMessage participantMessage = participantMessageMapper.toEntity(participantMessageDTO);
        participantMessage = participantMessageRepository.save(participantMessage);
        return participantMessageMapper.toDto(participantMessage);
    }

    @Override
    public Optional<ParticipantMessageDTO> partialUpdate(ParticipantMessageDTO participantMessageDTO) {
        LOG.debug("Request to partially update ParticipantMessage : {}", participantMessageDTO);

        return participantMessageRepository
            .findById(participantMessageDTO.getId())
            .map(existingParticipantMessage -> {
                participantMessageMapper.partialUpdate(existingParticipantMessage, participantMessageDTO);

                return existingParticipantMessage;
            })
            .map(participantMessageRepository::save)
            .map(participantMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParticipantMessageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ParticipantMessages");
        return participantMessageRepository.findAll(pageable).map(participantMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParticipantMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ParticipantMessage : {}", id);
        return participantMessageRepository.findById(id).map(participantMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ParticipantMessage : {}", id);
        participantMessageRepository.deleteById(id);
    }
}
