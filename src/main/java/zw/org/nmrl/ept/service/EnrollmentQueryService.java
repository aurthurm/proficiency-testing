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
import zw.org.nmrl.ept.domain.Enrollment;
import zw.org.nmrl.ept.repository.EnrollmentRepository;
import zw.org.nmrl.ept.service.criteria.EnrollmentCriteria;
import zw.org.nmrl.ept.service.dto.EnrollmentDTO;
import zw.org.nmrl.ept.service.mapper.EnrollmentMapper;

/**
 * Service for executing complex queries for {@link Enrollment} entities in the database.
 * The main input is a {@link EnrollmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EnrollmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EnrollmentQueryService extends QueryService<Enrollment> {

    private static final Logger LOG = LoggerFactory.getLogger(EnrollmentQueryService.class);

    private final EnrollmentRepository enrollmentRepository;

    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentQueryService(EnrollmentRepository enrollmentRepository, EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    /**
     * Return a {@link Page} of {@link EnrollmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EnrollmentDTO> findByCriteria(EnrollmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Enrollment> specification = createSpecification(criteria);
        return enrollmentRepository.findAll(specification, page).map(enrollmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EnrollmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Enrollment> specification = createSpecification(criteria);
        return enrollmentRepository.count(specification);
    }

    /**
     * Function to convert {@link EnrollmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Enrollment> createSpecification(EnrollmentCriteria criteria) {
        Specification<Enrollment> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Enrollment_.participant, JoinType.LEFT);
                root.fetch(Enrollment_.scheme, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Enrollment_.id),
                    buildSpecification(criteria.getStatus(), Enrollment_.status),
                    buildRangeSpecification(criteria.getEnrolledOn(), Enrollment_.enrolledOn),
                    buildRangeSpecification(criteria.getWithdrawnOn(), Enrollment_.withdrawnOn),
                    buildSpecification(criteria.getParticipantId(), root ->
                        root.join(Enrollment_.participant, JoinType.LEFT).get(Participant_.id)
                    ),
                    buildSpecification(criteria.getSchemeId(), root -> root.join(Enrollment_.scheme, JoinType.LEFT).get(Scheme_.id))
                )
            );
        }
        return specification;
    }
}
