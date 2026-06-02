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
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.repository.ShipmentRepository;
import zw.org.nmrl.ept.service.criteria.ShipmentCriteria;
import zw.org.nmrl.ept.service.dto.ShipmentDTO;
import zw.org.nmrl.ept.service.mapper.ShipmentMapper;

/**
 * Service for executing complex queries for {@link Shipment} entities in the database.
 * The main input is a {@link ShipmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ShipmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShipmentQueryService extends QueryService<Shipment> {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentQueryService.class);

    private final ShipmentRepository shipmentRepository;

    private final ShipmentMapper shipmentMapper;

    public ShipmentQueryService(ShipmentRepository shipmentRepository, ShipmentMapper shipmentMapper) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
    }

    /**
     * Return a {@link Page} of {@link ShipmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> findByCriteria(ShipmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Shipment> specification = createSpecification(criteria);
        return shipmentRepository.findAll(specification, page).map(shipmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShipmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Shipment> specification = createSpecification(criteria);
        return shipmentRepository.count(specification);
    }

    /**
     * Function to convert {@link ShipmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Shipment> createSpecification(ShipmentCriteria criteria) {
        Specification<Shipment> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Shipment_.distribution, JoinType.LEFT);
                root.fetch(Shipment_.scheme, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Shipment_.id),
                    buildStringSpecification(criteria.getCode(), Shipment_.code),
                    buildRangeSpecification(criteria.getShipmentDate(), Shipment_.shipmentDate),
                    buildRangeSpecification(criteria.getResponseDeadline(), Shipment_.responseDeadline),
                    buildSpecification(criteria.getResponsesOpen(), Shipment_.responsesOpen),
                    buildSpecification(criteria.getAutoCloseAtDeadline(), Shipment_.autoCloseAtDeadline),
                    buildSpecification(criteria.getAllowEditingResponse(), Shipment_.allowEditingResponse),
                    buildStringSpecification(criteria.getIssuingAuthority(), Shipment_.issuingAuthority),
                    buildStringSpecification(criteria.getCoordinatorName(), Shipment_.coordinatorName),
                    buildStringSpecification(criteria.getCoordinatorEmail(), Shipment_.coordinatorEmail),
                    buildStringSpecification(criteria.getCoordinatorPhone(), Shipment_.coordinatorPhone),
                    buildRangeSpecification(criteria.getNumberOfSamples(), Shipment_.numberOfSamples),
                    buildRangeSpecification(criteria.getMaxScore(), Shipment_.maxScore),
                    buildSpecification(criteria.getStatus(), Shipment_.status),
                    buildRangeSpecification(criteria.getReportsGeneratedAt(), Shipment_.reportsGeneratedAt),
                    buildRangeSpecification(criteria.getFinalizedAt(), Shipment_.finalizedAt),
                    buildSpecification(criteria.getSamplesId(), root ->
                        root.join(Shipment_.sampleses, JoinType.LEFT).get(ShipmentSample_.id)
                    ),
                    buildSpecification(criteria.getParticipantMapsId(), root ->
                        root.join(Shipment_.participantMapses, JoinType.LEFT).get(ShipmentParticipantMap_.id)
                    ),
                    buildSpecification(criteria.getDistributionId(), root ->
                        root.join(Shipment_.distribution, JoinType.LEFT).get(Distribution_.id)
                    ),
                    buildSpecification(criteria.getSchemeId(), root -> root.join(Shipment_.scheme, JoinType.LEFT).get(Scheme_.id)),
                    buildSpecification(criteria.getCertificateBatchesId(), root ->
                        root.join(Shipment_.certificateBatcheses, JoinType.LEFT).get(CertificateBatch_.id)
                    )
                )
            );
        }
        return specification;
    }
}
