package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.MailTemplate;
import zw.org.nmrl.ept.repository.MailTemplateRepository;
import zw.org.nmrl.ept.service.MailTemplateService;
import zw.org.nmrl.ept.service.dto.MailTemplateDTO;
import zw.org.nmrl.ept.service.mapper.MailTemplateMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.MailTemplate}.
 */
@Service
@Transactional
public class MailTemplateServiceImpl implements MailTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(MailTemplateServiceImpl.class);

    private final MailTemplateRepository mailTemplateRepository;

    private final MailTemplateMapper mailTemplateMapper;

    public MailTemplateServiceImpl(MailTemplateRepository mailTemplateRepository, MailTemplateMapper mailTemplateMapper) {
        this.mailTemplateRepository = mailTemplateRepository;
        this.mailTemplateMapper = mailTemplateMapper;
    }

    @Override
    public MailTemplateDTO save(MailTemplateDTO mailTemplateDTO) {
        LOG.debug("Request to save MailTemplate : {}", mailTemplateDTO);
        MailTemplate mailTemplate = mailTemplateMapper.toEntity(mailTemplateDTO);
        mailTemplate = mailTemplateRepository.save(mailTemplate);
        return mailTemplateMapper.toDto(mailTemplate);
    }

    @Override
    public MailTemplateDTO update(MailTemplateDTO mailTemplateDTO) {
        LOG.debug("Request to update MailTemplate : {}", mailTemplateDTO);
        MailTemplate mailTemplate = mailTemplateMapper.toEntity(mailTemplateDTO);
        mailTemplate = mailTemplateRepository.save(mailTemplate);
        return mailTemplateMapper.toDto(mailTemplate);
    }

    @Override
    public Optional<MailTemplateDTO> partialUpdate(MailTemplateDTO mailTemplateDTO) {
        LOG.debug("Request to partially update MailTemplate : {}", mailTemplateDTO);

        return mailTemplateRepository
            .findById(mailTemplateDTO.getId())
            .map(existingMailTemplate -> {
                mailTemplateMapper.partialUpdate(existingMailTemplate, mailTemplateDTO);

                return existingMailTemplate;
            })
            .map(mailTemplateRepository::save)
            .map(mailTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MailTemplateDTO> findAll() {
        LOG.debug("Request to get all MailTemplates");
        return mailTemplateRepository.findAll().stream().map(mailTemplateMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MailTemplateDTO> findOne(Long id) {
        LOG.debug("Request to get MailTemplate : {}", id);
        return mailTemplateRepository.findById(id).map(mailTemplateMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete MailTemplate : {}", id);
        mailTemplateRepository.deleteById(id);
    }
}
