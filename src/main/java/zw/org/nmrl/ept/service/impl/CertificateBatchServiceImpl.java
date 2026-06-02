package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.CertificateBatch;
import zw.org.nmrl.ept.repository.CertificateBatchRepository;
import zw.org.nmrl.ept.service.CertificateBatchService;
import zw.org.nmrl.ept.service.dto.CertificateBatchDTO;
import zw.org.nmrl.ept.service.mapper.CertificateBatchMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.CertificateBatch}.
 */
@Service
@Transactional
public class CertificateBatchServiceImpl implements CertificateBatchService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateBatchServiceImpl.class);

    private final CertificateBatchRepository certificateBatchRepository;

    private final CertificateBatchMapper certificateBatchMapper;

    public CertificateBatchServiceImpl(
        CertificateBatchRepository certificateBatchRepository,
        CertificateBatchMapper certificateBatchMapper
    ) {
        this.certificateBatchRepository = certificateBatchRepository;
        this.certificateBatchMapper = certificateBatchMapper;
    }

    @Override
    public CertificateBatchDTO save(CertificateBatchDTO certificateBatchDTO) {
        LOG.debug("Request to save CertificateBatch : {}", certificateBatchDTO);
        CertificateBatch certificateBatch = certificateBatchMapper.toEntity(certificateBatchDTO);
        certificateBatch = certificateBatchRepository.save(certificateBatch);
        return certificateBatchMapper.toDto(certificateBatch);
    }

    @Override
    public CertificateBatchDTO update(CertificateBatchDTO certificateBatchDTO) {
        LOG.debug("Request to update CertificateBatch : {}", certificateBatchDTO);
        CertificateBatch certificateBatch = certificateBatchMapper.toEntity(certificateBatchDTO);
        certificateBatch = certificateBatchRepository.save(certificateBatch);
        return certificateBatchMapper.toDto(certificateBatch);
    }

    @Override
    public Optional<CertificateBatchDTO> partialUpdate(CertificateBatchDTO certificateBatchDTO) {
        LOG.debug("Request to partially update CertificateBatch : {}", certificateBatchDTO);

        return certificateBatchRepository
            .findById(certificateBatchDTO.getId())
            .map(existingCertificateBatch -> {
                certificateBatchMapper.partialUpdate(existingCertificateBatch, certificateBatchDTO);

                return existingCertificateBatch;
            })
            .map(certificateBatchRepository::save)
            .map(certificateBatchMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CertificateBatchDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CertificateBatches");
        return certificateBatchRepository.findAll(pageable).map(certificateBatchMapper::toDto);
    }

    public Page<CertificateBatchDTO> findAllWithEagerRelationships(Pageable pageable) {
        return certificateBatchRepository.findAllWithEagerRelationships(pageable).map(certificateBatchMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CertificateBatchDTO> findOne(Long id) {
        LOG.debug("Request to get CertificateBatch : {}", id);
        return certificateBatchRepository.findOneWithEagerRelationships(id).map(certificateBatchMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CertificateBatch : {}", id);
        certificateBatchRepository.deleteById(id);
    }
}
