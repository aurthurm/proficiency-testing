package zw.org.nmrl.ept.service;

import java.util.Optional;
import zw.org.nmrl.ept.service.dto.EnrollmentDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.Enrollment}.
 */
public interface EnrollmentService {
    /**
     * Save a enrollment.
     *
     * @param enrollmentDTO the entity to save.
     * @return the persisted entity.
     */
    EnrollmentDTO save(EnrollmentDTO enrollmentDTO);

    /**
     * Updates a enrollment.
     *
     * @param enrollmentDTO the entity to update.
     * @return the persisted entity.
     */
    EnrollmentDTO update(EnrollmentDTO enrollmentDTO);

    /**
     * Partially updates a enrollment.
     *
     * @param enrollmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EnrollmentDTO> partialUpdate(EnrollmentDTO enrollmentDTO);

    /**
     * Get the "id" enrollment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EnrollmentDTO> findOne(Long id);

    /**
     * Delete the "id" enrollment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
