package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.ApiRequestLogDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.ApiRequestLog}.
 */
public interface ApiRequestLogService {
    /**
     * Save a apiRequestLog.
     *
     * @param apiRequestLogDTO the entity to save.
     * @return the persisted entity.
     */
    ApiRequestLogDTO save(ApiRequestLogDTO apiRequestLogDTO);

    /**
     * Updates a apiRequestLog.
     *
     * @param apiRequestLogDTO the entity to update.
     * @return the persisted entity.
     */
    ApiRequestLogDTO update(ApiRequestLogDTO apiRequestLogDTO);

    /**
     * Partially updates a apiRequestLog.
     *
     * @param apiRequestLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ApiRequestLogDTO> partialUpdate(ApiRequestLogDTO apiRequestLogDTO);

    /**
     * Get all the apiRequestLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ApiRequestLogDTO> findAll(Pageable pageable);

    /**
     * Get the "id" apiRequestLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ApiRequestLogDTO> findOne(Long id);

    /**
     * Delete the "id" apiRequestLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
