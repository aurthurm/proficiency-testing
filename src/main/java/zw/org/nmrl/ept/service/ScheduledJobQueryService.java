package zw.org.nmrl.ept.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import zw.org.nmrl.ept.domain.*; // for static metamodels
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.repository.ScheduledJobRepository;
import zw.org.nmrl.ept.service.criteria.ScheduledJobCriteria;
import zw.org.nmrl.ept.service.dto.ScheduledJobDTO;
import zw.org.nmrl.ept.service.mapper.ScheduledJobMapper;

/**
 * Service for executing complex queries for {@link ScheduledJob} entities in the database.
 * The main input is a {@link ScheduledJobCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ScheduledJobDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScheduledJobQueryService extends QueryService<ScheduledJob> {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledJobQueryService.class);

    private final ScheduledJobRepository scheduledJobRepository;

    private final ScheduledJobMapper scheduledJobMapper;

    public ScheduledJobQueryService(ScheduledJobRepository scheduledJobRepository, ScheduledJobMapper scheduledJobMapper) {
        this.scheduledJobRepository = scheduledJobRepository;
        this.scheduledJobMapper = scheduledJobMapper;
    }

    /**
     * Return a {@link Page} of {@link ScheduledJobDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScheduledJobDTO> findByCriteria(ScheduledJobCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ScheduledJob> specification = createSpecification(criteria);
        return scheduledJobRepository.findAll(specification, page).map(scheduledJobMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScheduledJobCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ScheduledJob> specification = createSpecification(criteria);
        return scheduledJobRepository.count(specification);
    }

    /**
     * Function to convert {@link ScheduledJobCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScheduledJob> createSpecification(ScheduledJobCriteria criteria) {
        Specification<ScheduledJob> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ScheduledJob_.id),
                    buildSpecification(criteria.getJobType(), ScheduledJob_.jobType),
                    buildSpecification(criteria.getStatus(), ScheduledJob_.status),
                    buildStringSpecification(criteria.getRequestedBy(), ScheduledJob_.requestedBy),
                    buildRangeSpecification(criteria.getRequestedOn(), ScheduledJob_.requestedOn),
                    buildRangeSpecification(criteria.getStartedAt(), ScheduledJob_.startedAt),
                    buildRangeSpecification(criteria.getLastHeartbeat(), ScheduledJob_.lastHeartbeat),
                    buildRangeSpecification(criteria.getCompletedAt(), ScheduledJob_.completedAt),
                    buildRangeSpecification(criteria.getProgressCompleted(), ScheduledJob_.progressCompleted),
                    buildRangeSpecification(criteria.getProgressTotal(), ScheduledJob_.progressTotal)
                )
            );
        }
        return specification;
    }
}
