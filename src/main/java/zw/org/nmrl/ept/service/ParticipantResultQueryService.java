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
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.service.criteria.ParticipantResultCriteria;
import zw.org.nmrl.ept.service.dto.ParticipantResultDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantResultMapper;

/**
 * Service for executing complex queries for {@link ParticipantResult} entities in the database.
 * The main input is a {@link ParticipantResultCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ParticipantResultDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ParticipantResultQueryService extends QueryService<ParticipantResult> {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantResultQueryService.class);

    private final ParticipantResultRepository participantResultRepository;

    private final ParticipantResultMapper participantResultMapper;

    public ParticipantResultQueryService(
        ParticipantResultRepository participantResultRepository,
        ParticipantResultMapper participantResultMapper
    ) {
        this.participantResultRepository = participantResultRepository;
        this.participantResultMapper = participantResultMapper;
    }

    /**
     * Return a {@link Page} of {@link ParticipantResultDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ParticipantResultDTO> findByCriteria(ParticipantResultCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ParticipantResult> specification = createSpecification(criteria);
        return participantResultRepository.findAll(specification, page).map(participantResultMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ParticipantResultCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ParticipantResult> specification = createSpecification(criteria);
        return participantResultRepository.count(specification);
    }

    /**
     * Function to convert {@link ParticipantResultCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ParticipantResult> createSpecification(ParticipantResultCriteria criteria) {
        Specification<ParticipantResult> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ParticipantResult_.assay, JoinType.LEFT);
                root.fetch(ParticipantResult_.testKit, JoinType.LEFT);
                root.fetch(ParticipantResult_.sample, JoinType.LEFT);
                root.fetch(ParticipantResult_.shipmentParticipantMap, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ParticipantResult_.id),
                    buildStringSpecification(criteria.getReportedQualitativeResult(), ParticipantResult_.reportedQualitativeResult),
                    buildRangeSpecification(criteria.getReportedQuantitativeValue(), ParticipantResult_.reportedQuantitativeValue),
                    buildStringSpecification(criteria.getUnit(), ParticipantResult_.unit),
                    buildStringSpecification(criteria.getLotNumber(), ParticipantResult_.lotNumber),
                    buildRangeSpecification(criteria.getExpiryDate(), ParticipantResult_.expiryDate),
                    buildRangeSpecification(criteria.getzScore(), ParticipantResult_.zScore),
                    buildRangeSpecification(criteria.getCalculatedScore(), ParticipantResult_.calculatedScore),
                    buildStringSpecification(criteria.getComments(), ParticipantResult_.comments),
                    buildSpecification(criteria.getAssayId(), root -> root.join(ParticipantResult_.assay, JoinType.LEFT).get(Assay_.id)),
                    buildSpecification(criteria.getTestKitId(), root ->
                        root.join(ParticipantResult_.testKit, JoinType.LEFT).get(TestKit_.id)
                    ),
                    buildSpecification(criteria.getSampleId(), root ->
                        root.join(ParticipantResult_.sample, JoinType.LEFT).get(ShipmentSample_.id)
                    ),
                    buildSpecification(criteria.getShipmentParticipantMapId(), root ->
                        root.join(ParticipantResult_.shipmentParticipantMap, JoinType.LEFT).get(ShipmentParticipantMap_.id)
                    )
                )
            );
        }
        return specification;
    }
}
