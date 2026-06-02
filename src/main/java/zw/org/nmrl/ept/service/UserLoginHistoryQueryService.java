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
import zw.org.nmrl.ept.domain.UserLoginHistory;
import zw.org.nmrl.ept.repository.UserLoginHistoryRepository;
import zw.org.nmrl.ept.service.criteria.UserLoginHistoryCriteria;
import zw.org.nmrl.ept.service.dto.UserLoginHistoryDTO;
import zw.org.nmrl.ept.service.mapper.UserLoginHistoryMapper;

/**
 * Service for executing complex queries for {@link UserLoginHistory} entities in the database.
 * The main input is a {@link UserLoginHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link UserLoginHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserLoginHistoryQueryService extends QueryService<UserLoginHistory> {

    private static final Logger LOG = LoggerFactory.getLogger(UserLoginHistoryQueryService.class);

    private final UserLoginHistoryRepository userLoginHistoryRepository;

    private final UserLoginHistoryMapper userLoginHistoryMapper;

    public UserLoginHistoryQueryService(
        UserLoginHistoryRepository userLoginHistoryRepository,
        UserLoginHistoryMapper userLoginHistoryMapper
    ) {
        this.userLoginHistoryRepository = userLoginHistoryRepository;
        this.userLoginHistoryMapper = userLoginHistoryMapper;
    }

    /**
     * Return a {@link Page} of {@link UserLoginHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserLoginHistoryDTO> findByCriteria(UserLoginHistoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserLoginHistory> specification = createSpecification(criteria);
        return userLoginHistoryRepository.findAll(specification, page).map(userLoginHistoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserLoginHistoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<UserLoginHistory> specification = createSpecification(criteria);
        return userLoginHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link UserLoginHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserLoginHistory> createSpecification(UserLoginHistoryCriteria criteria) {
        Specification<UserLoginHistory> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), UserLoginHistory_.id),
                    buildStringSpecification(criteria.getLoginId(), UserLoginHistory_.loginId),
                    buildStringSpecification(criteria.getLoginContext(), UserLoginHistory_.loginContext),
                    buildSpecification(criteria.getLoginStatus(), UserLoginHistory_.loginStatus),
                    buildRangeSpecification(criteria.getAttemptedAt(), UserLoginHistory_.attemptedAt),
                    buildStringSpecification(criteria.getIpAddress(), UserLoginHistory_.ipAddress),
                    buildStringSpecification(criteria.getBrowser(), UserLoginHistory_.browser),
                    buildStringSpecification(criteria.getOperatingSystem(), UserLoginHistory_.operatingSystem),
                    buildStringSpecification(criteria.getSessionHash(), UserLoginHistory_.sessionHash)
                )
            );
        }
        return specification;
    }
}
