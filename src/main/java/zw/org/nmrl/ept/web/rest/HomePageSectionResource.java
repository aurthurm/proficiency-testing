package zw.org.nmrl.ept.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import zw.org.nmrl.ept.repository.HomePageSectionRepository;
import zw.org.nmrl.ept.service.HomePageSectionService;
import zw.org.nmrl.ept.service.dto.HomePageSectionDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.HomePageSection}.
 */
@RestController
@RequestMapping("/api/home-page-sections")
public class HomePageSectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(HomePageSectionResource.class);

    private static final String ENTITY_NAME = "homePageSection";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final HomePageSectionService homePageSectionService;

    private final HomePageSectionRepository homePageSectionRepository;

    public HomePageSectionResource(HomePageSectionService homePageSectionService, HomePageSectionRepository homePageSectionRepository) {
        this.homePageSectionService = homePageSectionService;
        this.homePageSectionRepository = homePageSectionRepository;
    }

    /**
     * {@code POST  /home-page-sections} : Create a new homePageSection.
     *
     * @param homePageSectionDTO the homePageSectionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new homePageSectionDTO, or with status {@code 400 (Bad Request)} if the homePageSection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HomePageSectionDTO> createHomePageSection(@Valid @RequestBody HomePageSectionDTO homePageSectionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save HomePageSection : {}", homePageSectionDTO);
        if (homePageSectionDTO.getId() != null) {
            throw new BadRequestAlertException("A new homePageSection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        homePageSectionDTO = homePageSectionService.save(homePageSectionDTO);
        return ResponseEntity.created(new URI("/api/home-page-sections/" + homePageSectionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, homePageSectionDTO.getId().toString()))
            .body(homePageSectionDTO);
    }

    /**
     * {@code PUT  /home-page-sections/:id} : Updates an existing homePageSection.
     *
     * @param id the id of the homePageSectionDTO to save.
     * @param homePageSectionDTO the homePageSectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated homePageSectionDTO,
     * or with status {@code 400 (Bad Request)} if the homePageSectionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the homePageSectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HomePageSectionDTO> updateHomePageSection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HomePageSectionDTO homePageSectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update HomePageSection : {}, {}", id, homePageSectionDTO);
        if (homePageSectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, homePageSectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!homePageSectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        homePageSectionDTO = homePageSectionService.update(homePageSectionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, homePageSectionDTO.getId().toString()))
            .body(homePageSectionDTO);
    }

    /**
     * {@code PATCH  /home-page-sections/:id} : Partial updates given fields of an existing homePageSection, field will ignore if it is null
     *
     * @param id the id of the homePageSectionDTO to save.
     * @param homePageSectionDTO the homePageSectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated homePageSectionDTO,
     * or with status {@code 400 (Bad Request)} if the homePageSectionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the homePageSectionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the homePageSectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HomePageSectionDTO> partialUpdateHomePageSection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HomePageSectionDTO homePageSectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update HomePageSection partially : {}, {}", id, homePageSectionDTO);
        if (homePageSectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, homePageSectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!homePageSectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HomePageSectionDTO> result = homePageSectionService.partialUpdate(homePageSectionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, homePageSectionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /home-page-sections} : get all the Home Page Sections.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Home Page Sections in body.
     */
    @GetMapping("")
    public List<HomePageSectionDTO> getAllHomePageSections() {
        LOG.debug("REST request to get all HomePageSections");
        return homePageSectionService.findAll();
    }

    /**
     * {@code GET  /home-page-sections/:id} : get the "id" homePageSection.
     *
     * @param id the id of the homePageSectionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the homePageSectionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HomePageSectionDTO> getHomePageSection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HomePageSection : {}", id);
        Optional<HomePageSectionDTO> homePageSectionDTO = homePageSectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(homePageSectionDTO);
    }

    /**
     * {@code DELETE  /home-page-sections/:id} : delete the "id" homePageSection.
     *
     * @param id the id of the homePageSectionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHomePageSection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HomePageSection : {}", id);
        homePageSectionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
