package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.EmailMessage;
import zw.org.nmrl.ept.repository.EmailMessageRepository;
import zw.org.nmrl.ept.service.EmailMessageService;
import zw.org.nmrl.ept.service.dto.EmailMessageDTO;
import zw.org.nmrl.ept.service.mapper.EmailMessageMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.EmailMessage}.
 */
@Service
@Transactional
public class EmailMessageServiceImpl implements EmailMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailMessageServiceImpl.class);

    private final EmailMessageRepository emailMessageRepository;

    private final EmailMessageMapper emailMessageMapper;

    public EmailMessageServiceImpl(EmailMessageRepository emailMessageRepository, EmailMessageMapper emailMessageMapper) {
        this.emailMessageRepository = emailMessageRepository;
        this.emailMessageMapper = emailMessageMapper;
    }

    @Override
    public EmailMessageDTO save(EmailMessageDTO emailMessageDTO) {
        LOG.debug("Request to save EmailMessage : {}", emailMessageDTO);
        EmailMessage emailMessage = emailMessageMapper.toEntity(emailMessageDTO);
        emailMessage = emailMessageRepository.save(emailMessage);
        return emailMessageMapper.toDto(emailMessage);
    }

    @Override
    public EmailMessageDTO update(EmailMessageDTO emailMessageDTO) {
        LOG.debug("Request to update EmailMessage : {}", emailMessageDTO);
        EmailMessage emailMessage = emailMessageMapper.toEntity(emailMessageDTO);
        emailMessage = emailMessageRepository.save(emailMessage);
        return emailMessageMapper.toDto(emailMessage);
    }

    @Override
    public Optional<EmailMessageDTO> partialUpdate(EmailMessageDTO emailMessageDTO) {
        LOG.debug("Request to partially update EmailMessage : {}", emailMessageDTO);

        return emailMessageRepository
            .findById(emailMessageDTO.getId())
            .map(existingEmailMessage -> {
                emailMessageMapper.partialUpdate(existingEmailMessage, emailMessageDTO);

                return existingEmailMessage;
            })
            .map(emailMessageRepository::save)
            .map(emailMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailMessageDTO> findOne(Long id) {
        LOG.debug("Request to get EmailMessage : {}", id);
        return emailMessageRepository.findById(id).map(emailMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete EmailMessage : {}", id);
        emailMessageRepository.deleteById(id);
    }
}
