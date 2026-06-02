package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.repository.SchemeRepository;
import zw.org.nmrl.ept.service.SchemeService;
import zw.org.nmrl.ept.service.dto.SchemeDTO;
import zw.org.nmrl.ept.service.mapper.SchemeMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.Scheme}.
 */
@Service
@Transactional
public class SchemeServiceImpl implements SchemeService {

    private static final Logger LOG = LoggerFactory.getLogger(SchemeServiceImpl.class);

    private final SchemeRepository schemeRepository;

    private final SchemeMapper schemeMapper;

    public SchemeServiceImpl(SchemeRepository schemeRepository, SchemeMapper schemeMapper) {
        this.schemeRepository = schemeRepository;
        this.schemeMapper = schemeMapper;
    }

    @Override
    public SchemeDTO save(SchemeDTO schemeDTO) {
        LOG.debug("Request to save Scheme : {}", schemeDTO);
        Scheme scheme = schemeMapper.toEntity(schemeDTO);
        scheme = schemeRepository.save(scheme);
        return schemeMapper.toDto(scheme);
    }

    @Override
    public SchemeDTO update(SchemeDTO schemeDTO) {
        LOG.debug("Request to update Scheme : {}", schemeDTO);
        Scheme scheme = schemeMapper.toEntity(schemeDTO);
        scheme = schemeRepository.save(scheme);
        return schemeMapper.toDto(scheme);
    }

    @Override
    public Optional<SchemeDTO> partialUpdate(SchemeDTO schemeDTO) {
        LOG.debug("Request to partially update Scheme : {}", schemeDTO);

        return schemeRepository
            .findById(schemeDTO.getId())
            .map(existingScheme -> {
                schemeMapper.partialUpdate(existingScheme, schemeDTO);

                return existingScheme;
            })
            .map(schemeRepository::save)
            .map(schemeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SchemeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Schemes");
        return schemeRepository.findAll(pageable).map(schemeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SchemeDTO> findOne(Long id) {
        LOG.debug("Request to get Scheme : {}", id);
        return schemeRepository.findById(id).map(schemeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Scheme : {}", id);
        schemeRepository.deleteById(id);
    }
}
