package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.UserLoginHistoryDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.UserLoginHistory}.
 */
public interface UserLoginHistoryService {
    /**
     * Save a userLoginHistory.
     *
     * @param userLoginHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    UserLoginHistoryDTO save(UserLoginHistoryDTO userLoginHistoryDTO);

    /**
     * Updates a userLoginHistory.
     *
     * @param userLoginHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    UserLoginHistoryDTO update(UserLoginHistoryDTO userLoginHistoryDTO);

    /**
     * Partially updates a userLoginHistory.
     *
     * @param userLoginHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserLoginHistoryDTO> partialUpdate(UserLoginHistoryDTO userLoginHistoryDTO);

    /**
     * Get the "id" userLoginHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserLoginHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" userLoginHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
