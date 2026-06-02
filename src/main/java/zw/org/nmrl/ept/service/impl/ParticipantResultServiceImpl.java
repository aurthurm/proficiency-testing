package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.service.ParticipantResultService;
import zw.org.nmrl.ept.service.dto.ParticipantResultDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantResultMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ParticipantResult}.
 */
@Service
@Transactional
public class ParticipantResultServiceImpl implements ParticipantResultService {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantResultServiceImpl.class);

    private final ParticipantResultRepository participantResultRepository;

    private final ParticipantResultMapper participantResultMapper;

    public ParticipantResultServiceImpl(
        ParticipantResultRepository participantResultRepository,
        ParticipantResultMapper participantResultMapper
    ) {
        this.participantResultRepository = participantResultRepository;
        this.participantResultMapper = participantResultMapper;
    }

    @Override
    public ParticipantResultDTO save(ParticipantResultDTO participantResultDTO) {
        LOG.debug("Request to save ParticipantResult : {}", participantResultDTO);
        ParticipantResult participantResult = participantResultMapper.toEntity(participantResultDTO);
        participantResult = participantResultRepository.save(participantResult);
        return participantResultMapper.toDto(participantResult);
    }

    @Override
    public ParticipantResultDTO update(ParticipantResultDTO participantResultDTO) {
        LOG.debug("Request to update ParticipantResult : {}", participantResultDTO);
        ParticipantResult participantResult = participantResultMapper.toEntity(participantResultDTO);
        participantResult = participantResultRepository.save(participantResult);
        return participantResultMapper.toDto(participantResult);
    }

    @Override
    public Optional<ParticipantResultDTO> partialUpdate(ParticipantResultDTO participantResultDTO) {
        LOG.debug("Request to partially update ParticipantResult : {}", participantResultDTO);

        return participantResultRepository
            .findById(participantResultDTO.getId())
            .map(existingParticipantResult -> {
                participantResultMapper.partialUpdate(existingParticipantResult, participantResultDTO);

                return existingParticipantResult;
            })
            .map(participantResultRepository::save)
            .map(participantResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParticipantResultDTO> findOne(Long id) {
        LOG.debug("Request to get ParticipantResult : {}", id);
        return participantResultRepository.findById(id).map(participantResultMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ParticipantResult : {}", id);
        participantResultRepository.deleteById(id);
    }
}
