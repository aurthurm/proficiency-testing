package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.AuditLogDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.AuditLog}.
 */
public interface AuditLogService {
    /**
     * Save a auditLog.
     *
     * @param auditLogDTO the entity to save.
     * @return the persisted entity.
     */
    AuditLogDTO save(AuditLogDTO auditLogDTO);

    /**
     * Updates a auditLog.
     *
     * @param auditLogDTO the entity to update.
     * @return the persisted entity.
     */
    AuditLogDTO update(AuditLogDTO auditLogDTO);

    /**
     * Partially updates a auditLog.
     *
     * @param auditLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AuditLogDTO> partialUpdate(AuditLogDTO auditLogDTO);

    /**
     * Get the "id" auditLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AuditLogDTO> findOne(Long id);

    /**
     * Delete the "id" auditLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
