package zw.org.nmrl.ept.service;

import java.util.List;
import java.util.Optional;
import zw.org.nmrl.ept.service.dto.HomePageSectionDTO;

/**
 * Service Interface for managing {@link zw.org.nmrl.ept.domain.HomePageSection}.
 */
public interface HomePageSectionService {
    /**
     * Save a homePageSection.
     *
     * @param homePageSectionDTO the entity to save.
     * @return the persisted entity.
     */
    HomePageSectionDTO save(HomePageSectionDTO homePageSectionDTO);

    /**
     * Updates a homePageSection.
     *
     * @param homePageSectionDTO the entity to update.
     * @return the persisted entity.
     */
    HomePageSectionDTO update(HomePageSectionDTO homePageSectionDTO);

    /**
     * Partially updates a homePageSection.
     *
     * @param homePageSectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<HomePageSectionDTO> partialUpdate(HomePageSectionDTO homePageSectionDTO);

    /**
     * Get all the homePageSections.
     *
     * @return the list of entities.
     */
    List<HomePageSectionDTO> findAll();

    /**
     * Get the "id" homePageSection.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<HomePageSectionDTO> findOne(Long id);

    /**
     * Delete the "id" homePageSection.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
