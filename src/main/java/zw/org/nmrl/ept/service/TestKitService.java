package zw.org.nmrl.ept.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.org.nmrl.ept.service.dto.TestKitDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.TestKit}.
 */
public interface TestKitService {
    /**
     * Save a testKit.
     *
     * @param testKitDTO the entity to save.
     * @return the persisted entity.
     */
    TestKitDTO save(TestKitDTO testKitDTO);

    /**
     * Updates a testKit.
     *
     * @param testKitDTO the entity to update.
     * @return the persisted entity.
     */
    TestKitDTO update(TestKitDTO testKitDTO);

    /**
     * Partially updates a testKit.
     *
     * @param testKitDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TestKitDTO> partialUpdate(TestKitDTO testKitDTO);

    /**
     * Get all the testKits.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TestKitDTO> findAll(Pageable pageable);

    /**
     * Get the "id" testKit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TestKitDTO> findOne(Long id);

    /**
     * Delete the "id" testKit.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
