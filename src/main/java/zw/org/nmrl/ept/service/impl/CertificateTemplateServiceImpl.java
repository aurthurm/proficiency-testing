package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.CertificateTemplate;
import zw.org.nmrl.ept.repository.CertificateTemplateRepository;
import zw.org.nmrl.ept.service.CertificateTemplateService;
import zw.org.nmrl.ept.service.dto.CertificateTemplateDTO;
import zw.org.nmrl.ept.service.mapper.CertificateTemplateMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.CertificateTemplate}.
 */
@Service
@Transactional
public class CertificateTemplateServiceImpl implements CertificateTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateTemplateServiceImpl.class);

    private final CertificateTemplateRepository certificateTemplateRepository;

    private final CertificateTemplateMapper certificateTemplateMapper;

    public CertificateTemplateServiceImpl(
        CertificateTemplateRepository certificateTemplateRepository,
        CertificateTemplateMapper certificateTemplateMapper
    ) {
        this.certificateTemplateRepository = certificateTemplateRepository;
        this.certificateTemplateMapper = certificateTemplateMapper;
    }

    @Override
    public CertificateTemplateDTO save(CertificateTemplateDTO certificateTemplateDTO) {
        LOG.debug("Request to save CertificateTemplate : {}", certificateTemplateDTO);
        CertificateTemplate certificateTemplate = certificateTemplateMapper.toEntity(certificateTemplateDTO);
        certificateTemplate = certificateTemplateRepository.save(certificateTemplate);
        return certificateTemplateMapper.toDto(certificateTemplate);
    }

    @Override
    public CertificateTemplateDTO update(CertificateTemplateDTO certificateTemplateDTO) {
        LOG.debug("Request to update CertificateTemplate : {}", certificateTemplateDTO);
        CertificateTemplate certificateTemplate = certificateTemplateMapper.toEntity(certificateTemplateDTO);
        certificateTemplate = certificateTemplateRepository.save(certificateTemplate);
        return certificateTemplateMapper.toDto(certificateTemplate);
    }

    @Override
    public Optional<CertificateTemplateDTO> partialUpdate(CertificateTemplateDTO certificateTemplateDTO) {
        LOG.debug("Request to partially update CertificateTemplate : {}", certificateTemplateDTO);

        return certificateTemplateRepository
            .findById(certificateTemplateDTO.getId())
            .map(existingCertificateTemplate -> {
                certificateTemplateMapper.partialUpdate(existingCertificateTemplate, certificateTemplateDTO);

                return existingCertificateTemplate;
            })
            .map(certificateTemplateRepository::save)
            .map(certificateTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateTemplateDTO> findAll() {
        LOG.debug("Request to get all CertificateTemplates");
        return certificateTemplateRepository
            .findAll()
            .stream()
            .map(certificateTemplateMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the certificateTemplates where Scheme is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CertificateTemplateDTO> findAllWhereSchemeIsNull() {
        LOG.debug("Request to get all certificateTemplates where Scheme is null");
        return StreamSupport.stream(certificateTemplateRepository.findAll().spliterator(), false)
            .filter(certificateTemplate -> certificateTemplate.getScheme() == null)
            .map(certificateTemplateMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CertificateTemplateDTO> findOne(Long id) {
        LOG.debug("Request to get CertificateTemplate : {}", id);
        return certificateTemplateRepository.findById(id).map(certificateTemplateMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CertificateTemplate : {}", id);
        certificateTemplateRepository.deleteById(id);
    }
}
