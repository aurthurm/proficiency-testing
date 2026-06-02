package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.Assay;
import zw.org.nmrl.ept.repository.AssayRepository;
import zw.org.nmrl.ept.service.AssayService;
import zw.org.nmrl.ept.service.dto.AssayDTO;
import zw.org.nmrl.ept.service.mapper.AssayMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.Assay}.
 */
@Service
@Transactional
public class AssayServiceImpl implements AssayService {

    private static final Logger LOG = LoggerFactory.getLogger(AssayServiceImpl.class);

    private final AssayRepository assayRepository;

    private final AssayMapper assayMapper;

    public AssayServiceImpl(AssayRepository assayRepository, AssayMapper assayMapper) {
        this.assayRepository = assayRepository;
        this.assayMapper = assayMapper;
    }

    @Override
    public AssayDTO save(AssayDTO assayDTO) {
        LOG.debug("Request to save Assay : {}", assayDTO);
        Assay assay = assayMapper.toEntity(assayDTO);
        assay = assayRepository.save(assay);
        return assayMapper.toDto(assay);
    }

    @Override
    public AssayDTO update(AssayDTO assayDTO) {
        LOG.debug("Request to update Assay : {}", assayDTO);
        Assay assay = assayMapper.toEntity(assayDTO);
        assay = assayRepository.save(assay);
        return assayMapper.toDto(assay);
    }

    @Override
    public Optional<AssayDTO> partialUpdate(AssayDTO assayDTO) {
        LOG.debug("Request to partially update Assay : {}", assayDTO);

        return assayRepository
            .findById(assayDTO.getId())
            .map(existingAssay -> {
                assayMapper.partialUpdate(existingAssay, assayDTO);

                return existingAssay;
            })
            .map(assayRepository::save)
            .map(assayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssayDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Assays");
        return assayRepository.findAll(pageable).map(assayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssayDTO> findOne(Long id) {
        LOG.debug("Request to get Assay : {}", id);
        return assayRepository.findById(id).map(assayMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Assay : {}", id);
        assayRepository.deleteById(id);
    }
}
