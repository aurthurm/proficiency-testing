package zw.org.nmrl.ept.service;

import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import zw.org.nmrl.ept.domain.*; // for static metamodels
import zw.org.nmrl.ept.domain.EmailMessage;
import zw.org.nmrl.ept.repository.EmailMessageRepository;
import zw.org.nmrl.ept.service.criteria.EmailMessageCriteria;
import zw.org.nmrl.ept.service.dto.EmailMessageDTO;
import zw.org.nmrl.ept.service.mapper.EmailMessageMapper;

/**
 * Service for executing complex queries for {@link EmailMessage} entities in the database.
 * The main input is a {@link EmailMessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EmailMessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EmailMessageQueryService extends QueryService<EmailMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(EmailMessageQueryService.class);

    private final EmailMessageRepository emailMessageRepository;

    private final EmailMessageMapper emailMessageMapper;

    public EmailMessageQueryService(EmailMessageRepository emailMessageRepository, EmailMessageMapper emailMessageMapper) {
        this.emailMessageRepository = emailMessageRepository;
        this.emailMessageMapper = emailMessageMapper;
    }

    /**
     * Return a {@link Page} of {@link EmailMessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EmailMessageDTO> findByCriteria(EmailMessageCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<EmailMessage> specification = createSpecification(criteria);
        return emailMessageRepository.findAll(specification, page).map(emailMessageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EmailMessageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<EmailMessage> specification = createSpecification(criteria);
        return emailMessageRepository.count(specification);
    }

    /**
     * Function to convert {@link EmailMessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EmailMessage> createSpecification(EmailMessageCriteria criteria) {
        Specification<EmailMessage> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(EmailMessage_.template, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), EmailMessage_.id),
                    buildStringSpecification(criteria.getFromEmail(), EmailMessage_.fromEmail),
                    buildStringSpecification(criteria.getFromName(), EmailMessage_.fromName),
                    buildStringSpecification(criteria.getReplyTo(), EmailMessage_.replyTo),
                    buildStringSpecification(criteria.getToEmail(), EmailMessage_.toEmail),
                    buildStringSpecification(criteria.getCc(), EmailMessage_.cc),
                    buildStringSpecification(criteria.getBcc(), EmailMessage_.bcc),
                    buildStringSpecification(criteria.getSubject(), EmailMessage_.subject),
                    buildStringSpecification(criteria.getAttachmentRef(), EmailMessage_.attachmentRef),
                    buildSpecification(criteria.getStatus(), EmailMessage_.status),
                    buildStringSpecification(criteria.getFailureType(), EmailMessage_.failureType),
                    buildStringSpecification(criteria.getFailureReason(), EmailMessage_.failureReason),
                    buildRangeSpecification(criteria.getQueuedOn(), EmailMessage_.queuedOn),
                    buildRangeSpecification(criteria.getSentAt(), EmailMessage_.sentAt),
                    buildRangeSpecification(criteria.getRetryCount(), EmailMessage_.retryCount),
                    buildSpecification(criteria.getTemplateId(), root ->
                        root.join(EmailMessage_.template, JoinType.LEFT).get(MailTemplate_.id)
                    )
                )
            );
        }
        return specification;
    }
}
