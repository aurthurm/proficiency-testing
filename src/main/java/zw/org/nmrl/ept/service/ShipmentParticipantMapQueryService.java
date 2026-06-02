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
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;
import zw.org.nmrl.ept.service.criteria.ShipmentParticipantMapCriteria;
import zw.org.nmrl.ept.service.dto.ShipmentParticipantMapDTO;
import zw.org.nmrl.ept.service.mapper.ShipmentParticipantMapMapper;

/**
 * Service for executing complex queries for {@link ShipmentParticipantMap} entities in the database.
 * The main input is a {@link ShipmentParticipantMapCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ShipmentParticipantMapDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShipmentParticipantMapQueryService extends QueryService<ShipmentParticipantMap> {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentParticipantMapQueryService.class);

    private final ShipmentParticipantMapRepository shipmentParticipantMapRepository;

    private final ShipmentParticipantMapMapper shipmentParticipantMapMapper;

    public ShipmentParticipantMapQueryService(
        ShipmentParticipantMapRepository shipmentParticipantMapRepository,
        ShipmentParticipantMapMapper shipmentParticipantMapMapper
    ) {
        this.shipmentParticipantMapRepository = shipmentParticipantMapRepository;
        this.shipmentParticipantMapMapper = shipmentParticipantMapMapper;
    }

    /**
     * Return a {@link Page} of {@link ShipmentParticipantMapDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShipmentParticipantMapDTO> findByCriteria(ShipmentParticipantMapCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ShipmentParticipantMap> specification = createSpecification(criteria);
        return shipmentParticipantMapRepository.findAll(specification, page).map(shipmentParticipantMapMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShipmentParticipantMapCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ShipmentParticipantMap> specification = createSpecification(criteria);
        return shipmentParticipantMapRepository.count(specification);
    }

    /**
     * Function to convert {@link ShipmentParticipantMapCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShipmentParticipantMap> createSpecification(ShipmentParticipantMapCriteria criteria) {
        Specification<ShipmentParticipantMap> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ShipmentParticipantMap_.modeOfReceipt, JoinType.LEFT);
                root.fetch(ShipmentParticipantMap_.notTestedReason, JoinType.LEFT);
                root.fetch(ShipmentParticipantMap_.shipment, JoinType.LEFT);
                root.fetch(ShipmentParticipantMap_.participant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ShipmentParticipantMap_.id),
                    buildSpecification(criteria.getResponseStatus(), ShipmentParticipantMap_.responseStatus),
                    buildRangeSpecification(criteria.getShipmentReceiptDate(), ShipmentParticipantMap_.shipmentReceiptDate),
                    buildRangeSpecification(criteria.getShipmentTestDate(), ShipmentParticipantMap_.shipmentTestDate),
                    buildRangeSpecification(criteria.getShipmentTestReportDate(), ShipmentParticipantMap_.shipmentTestReportDate),
                    buildRangeSpecification(criteria.getSubmittedAt(), ShipmentParticipantMap_.submittedAt),
                    buildRangeSpecification(criteria.getEvaluatedAt(), ShipmentParticipantMap_.evaluatedAt),
                    buildSpecification(criteria.getIsExcluded(), ShipmentParticipantMap_.isExcluded),
                    buildSpecification(criteria.getIsResponseLate(), ShipmentParticipantMap_.isResponseLate),
                    buildSpecification(criteria.getIsPtTestNotPerformed(), ShipmentParticipantMap_.isPtTestNotPerformed),
                    buildStringSpecification(criteria.getPtTestNotPerformedComments(), ShipmentParticipantMap_.ptTestNotPerformedComments),
                    buildSpecification(criteria.getSupervisorApproved(), ShipmentParticipantMap_.supervisorApproved),
                    buildStringSpecification(criteria.getParticipantSupervisor(), ShipmentParticipantMap_.participantSupervisor),
                    buildStringSpecification(criteria.getUserComment(), ShipmentParticipantMap_.userComment),
                    buildRangeSpecification(criteria.getShipmentScore(), ShipmentParticipantMap_.shipmentScore),
                    buildRangeSpecification(criteria.getDocumentationScore(), ShipmentParticipantMap_.documentationScore),
                    buildSpecification(criteria.getFinalResult(), ShipmentParticipantMap_.finalResult),
                    buildStringSpecification(criteria.getEvaluationComment(), ShipmentParticipantMap_.evaluationComment),
                    buildSpecification(criteria.getIsFollowup(), ShipmentParticipantMap_.isFollowup),
                    buildSpecification(criteria.getManualOverride(), ShipmentParticipantMap_.manualOverride),
                    buildSpecification(criteria.getQcStatus(), ShipmentParticipantMap_.qcStatus),
                    buildRangeSpecification(criteria.getQcDate(), ShipmentParticipantMap_.qcDate),
                    buildStringSpecification(criteria.getQcDoneBy(), ShipmentParticipantMap_.qcDoneBy),
                    buildSpecification(criteria.getSyncedToMobile(), ShipmentParticipantMap_.syncedToMobile),
                    buildRangeSpecification(criteria.getSyncedOn(), ShipmentParticipantMap_.syncedOn),
                    buildSpecification(criteria.getResultsId(), root ->
                        root.join(ShipmentParticipantMap_.resultses, JoinType.LEFT).get(ParticipantResult_.id)
                    ),
                    buildSpecification(criteria.getCapaRecordsId(), root ->
                        root.join(ShipmentParticipantMap_.capaRecordses, JoinType.LEFT).get(CapaRecord_.id)
                    ),
                    buildSpecification(criteria.getModeOfReceiptId(), root ->
                        root.join(ShipmentParticipantMap_.modeOfReceipt, JoinType.LEFT).get(ModeOfReceipt_.id)
                    ),
                    buildSpecification(criteria.getNotTestedReasonId(), root ->
                        root.join(ShipmentParticipantMap_.notTestedReason, JoinType.LEFT).get(NotTestedReason_.id)
                    ),
                    buildSpecification(criteria.getShipmentId(), root ->
                        root.join(ShipmentParticipantMap_.shipment, JoinType.LEFT).get(Shipment_.id)
                    ),
                    buildSpecification(criteria.getParticipantId(), root ->
                        root.join(ShipmentParticipantMap_.participant, JoinType.LEFT).get(Participant_.id)
                    )
                )
            );
        }
        return specification;
    }
}
