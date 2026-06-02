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
import zw.org.nmrl.ept.domain.Distribution;
import zw.org.nmrl.ept.repository.DistributionRepository;
import zw.org.nmrl.ept.service.criteria.DistributionCriteria;
import zw.org.nmrl.ept.service.dto.DistributionDTO;
import zw.org.nmrl.ept.service.mapper.DistributionMapper;

/**
 * Service for executing complex queries for {@link Distribution} entities in the database.
 * The main input is a {@link DistributionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DistributionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DistributionQueryService extends QueryService<Distribution> {

    private static final Logger LOG = LoggerFactory.getLogger(DistributionQueryService.class);

    private final DistributionRepository distributionRepository;

    private final DistributionMapper distributionMapper;

    public DistributionQueryService(DistributionRepository distributionRepository, DistributionMapper distributionMapper) {
        this.distributionRepository = distributionRepository;
        this.distributionMapper = distributionMapper;
    }

    /**
     * Return a {@link Page} of {@link DistributionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DistributionDTO> findByCriteria(DistributionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Distribution> specification = createSpecification(criteria);
        return distributionRepository.findAll(specification, page).map(distributionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DistributionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Distribution> specification = createSpecification(criteria);
        return distributionRepository.count(specification);
    }

    /**
     * Function to convert {@link DistributionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Distribution> createSpecification(DistributionCriteria criteria) {
        Specification<Distribution> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Distribution_.id),
                    buildStringSpecification(criteria.getCode(), Distribution_.code),
                    buildRangeSpecification(criteria.getDistributionDate(), Distribution_.distributionDate),
                    buildSpecification(criteria.getStatus(), Distribution_.status),
                    buildSpecification(criteria.getShipmentsId(), root ->
                        root.join(Distribution_.shipmentses, JoinType.LEFT).get(Shipment_.id)
                    )
                )
            );
        }
        return specification;
    }
}
