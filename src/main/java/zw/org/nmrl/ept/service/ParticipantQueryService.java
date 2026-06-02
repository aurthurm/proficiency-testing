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
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.repository.ParticipantRepository;
import zw.org.nmrl.ept.service.criteria.ParticipantCriteria;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantMapper;

/**
 * Service for executing complex queries for {@link Participant} entities in the database.
 * The main input is a {@link ParticipantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ParticipantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ParticipantQueryService extends QueryService<Participant> {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantQueryService.class);

    private final ParticipantRepository participantRepository;

    private final ParticipantMapper participantMapper;

    public ParticipantQueryService(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    /**
     * Return a {@link Page} of {@link ParticipantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ParticipantDTO> findByCriteria(ParticipantCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Participant> specification = createSpecification(criteria);
        return participantRepository.findAll(specification, page).map(participantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ParticipantCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Participant> specification = createSpecification(criteria);
        return participantRepository.count(specification);
    }

    /**
     * Function to convert {@link ParticipantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Participant> createSpecification(ParticipantCriteria criteria) {
        Specification<Participant> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Participant_.country, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Participant_.id),
                    buildStringSpecification(criteria.getUniqueIdentifier(), Participant_.uniqueIdentifier),
                    buildStringSpecification(criteria.getInstituteName(), Participant_.instituteName),
                    buildStringSpecification(criteria.getDepartmentName(), Participant_.departmentName),
                    buildStringSpecification(criteria.getEmail(), Participant_.email),
                    buildStringSpecification(criteria.getAdditionalEmail(), Participant_.additionalEmail),
                    buildStringSpecification(criteria.getAddress(), Participant_.address),
                    buildStringSpecification(criteria.getShippingAddress(), Participant_.shippingAddress),
                    buildStringSpecification(criteria.getCity(), Participant_.city),
                    buildStringSpecification(criteria.getState(), Participant_.state),
                    buildStringSpecification(criteria.getDistrict(), Participant_.district),
                    buildStringSpecification(criteria.getZip(), Participant_.zip),
                    buildStringSpecification(criteria.getRegion(), Participant_.region),
                    buildStringSpecification(criteria.getPhone(), Participant_.phone),
                    buildStringSpecification(criteria.getMobile(), Participant_.mobile),
                    buildStringSpecification(criteria.getAffiliation(), Participant_.affiliation),
                    buildStringSpecification(criteria.getNetworkTier(), Participant_.networkTier),
                    buildStringSpecification(criteria.getSiteType(), Participant_.siteType),
                    buildStringSpecification(criteria.getFundingSource(), Participant_.fundingSource),
                    buildRangeSpecification(criteria.getTestingVolume(), Participant_.testingVolume),
                    buildStringSpecification(criteria.getPepfarId(), Participant_.pepfarId),
                    buildRangeSpecification(criteria.getLatitude(), Participant_.latitude),
                    buildRangeSpecification(criteria.getLongitude(), Participant_.longitude),
                    buildStringSpecification(criteria.getLabDirectorName(), Participant_.labDirectorName),
                    buildStringSpecification(criteria.getLabDirectorEmail(), Participant_.labDirectorEmail),
                    buildStringSpecification(criteria.getContactPersonName(), Participant_.contactPersonName),
                    buildStringSpecification(criteria.getContactPersonEmail(), Participant_.contactPersonEmail),
                    buildStringSpecification(criteria.getContactPersonPhone(), Participant_.contactPersonPhone),
                    buildSpecification(criteria.getStatus(), Participant_.status),
                    buildSpecification(criteria.getShipmentMapsId(), root ->
                        root.join(Participant_.shipmentMapses, JoinType.LEFT).get(ShipmentParticipantMap_.id)
                    ),
                    buildSpecification(criteria.getCountryId(), root -> root.join(Participant_.country, JoinType.LEFT).get(Country_.id)),
                    buildSpecification(criteria.getEnrollmentsId(), root ->
                        root.join(Participant_.enrollmentses, JoinType.LEFT).get(Enrollment_.id)
                    ),
                    buildSpecification(criteria.getCustomValuesId(), root ->
                        root.join(Participant_.customValueses, JoinType.LEFT).get(ParticipantCustomValue_.id)
                    ),
                    buildSpecification(criteria.getDataManagersId(), root ->
                        root.join(Participant_.dataManagerses, JoinType.LEFT).get(DataManager_.id)
                    )
                )
            );
        }
        return specification;
    }
}
