package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.NotTestedReason;
import zw.org.nmrl.ept.repository.NotTestedReasonRepository;
import zw.org.nmrl.ept.service.NotTestedReasonService;
import zw.org.nmrl.ept.service.dto.NotTestedReasonDTO;
import zw.org.nmrl.ept.service.mapper.NotTestedReasonMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.NotTestedReason}.
 */
@Service
@Transactional
public class NotTestedReasonServiceImpl implements NotTestedReasonService {

    private static final Logger LOG = LoggerFactory.getLogger(NotTestedReasonServiceImpl.class);

    private final NotTestedReasonRepository notTestedReasonRepository;

    private final NotTestedReasonMapper notTestedReasonMapper;

    public NotTestedReasonServiceImpl(NotTestedReasonRepository notTestedReasonRepository, NotTestedReasonMapper notTestedReasonMapper) {
        this.notTestedReasonRepository = notTestedReasonRepository;
        this.notTestedReasonMapper = notTestedReasonMapper;
    }

    @Override
    public NotTestedReasonDTO save(NotTestedReasonDTO notTestedReasonDTO) {
        LOG.debug("Request to save NotTestedReason : {}", notTestedReasonDTO);
        NotTestedReason notTestedReason = notTestedReasonMapper.toEntity(notTestedReasonDTO);
        notTestedReason = notTestedReasonRepository.save(notTestedReason);
        return notTestedReasonMapper.toDto(notTestedReason);
    }

    @Override
    public NotTestedReasonDTO update(NotTestedReasonDTO notTestedReasonDTO) {
        LOG.debug("Request to update NotTestedReason : {}", notTestedReasonDTO);
        NotTestedReason notTestedReason = notTestedReasonMapper.toEntity(notTestedReasonDTO);
        notTestedReason = notTestedReasonRepository.save(notTestedReason);
        return notTestedReasonMapper.toDto(notTestedReason);
    }

    @Override
    public Optional<NotTestedReasonDTO> partialUpdate(NotTestedReasonDTO notTestedReasonDTO) {
        LOG.debug("Request to partially update NotTestedReason : {}", notTestedReasonDTO);

        return notTestedReasonRepository
            .findById(notTestedReasonDTO.getId())
            .map(existingNotTestedReason -> {
                notTestedReasonMapper.partialUpdate(existingNotTestedReason, notTestedReasonDTO);

                return existingNotTestedReason;
            })
            .map(notTestedReasonRepository::save)
            .map(notTestedReasonMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotTestedReasonDTO> findAll() {
        LOG.debug("Request to get all NotTestedReasons");
        return notTestedReasonRepository
            .findAll()
            .stream()
            .map(notTestedReasonMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotTestedReasonDTO> findOne(Long id) {
        LOG.debug("Request to get NotTestedReason : {}", id);
        return notTestedReasonRepository.findById(id).map(notTestedReasonMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete NotTestedReason : {}", id);
        notTestedReasonRepository.deleteById(id);
    }
}
