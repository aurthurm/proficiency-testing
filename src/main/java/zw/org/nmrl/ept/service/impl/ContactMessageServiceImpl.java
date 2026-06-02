package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ContactMessage;
import zw.org.nmrl.ept.repository.ContactMessageRepository;
import zw.org.nmrl.ept.service.ContactMessageService;
import zw.org.nmrl.ept.service.dto.ContactMessageDTO;
import zw.org.nmrl.ept.service.mapper.ContactMessageMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ContactMessage}.
 */
@Service
@Transactional
public class ContactMessageServiceImpl implements ContactMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ContactMessageServiceImpl.class);

    private final ContactMessageRepository contactMessageRepository;

    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository, ContactMessageMapper contactMessageMapper) {
        this.contactMessageRepository = contactMessageRepository;
        this.contactMessageMapper = contactMessageMapper;
    }

    @Override
    public ContactMessageDTO save(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to save ContactMessage : {}", contactMessageDTO);
        ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        contactMessage = contactMessageRepository.save(contactMessage);
        return contactMessageMapper.toDto(contactMessage);
    }

    @Override
    public ContactMessageDTO update(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to update ContactMessage : {}", contactMessageDTO);
        ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        contactMessage = contactMessageRepository.save(contactMessage);
        return contactMessageMapper.toDto(contactMessage);
    }

    @Override
    public Optional<ContactMessageDTO> partialUpdate(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to partially update ContactMessage : {}", contactMessageDTO);

        return contactMessageRepository
            .findById(contactMessageDTO.getId())
            .map(existingContactMessage -> {
                contactMessageMapper.partialUpdate(existingContactMessage, contactMessageDTO);

                return existingContactMessage;
            })
            .map(contactMessageRepository::save)
            .map(contactMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ContactMessages");
        return contactMessageRepository.findAll(pageable).map(contactMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ContactMessage : {}", id);
        return contactMessageRepository.findById(id).map(contactMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ContactMessage : {}", id);
        contactMessageRepository.deleteById(id);
    }
}
