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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import zw.org.nmrl.ept.repository.AnnouncementRepository;
import zw.org.nmrl.ept.service.AnnouncementService;
import zw.org.nmrl.ept.service.dto.AnnouncementDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.Announcement}.
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementResource {

    private static final Logger LOG = LoggerFactory.getLogger(AnnouncementResource.class);

    private static final String ENTITY_NAME = "announcement";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final AnnouncementService announcementService;

    private final AnnouncementRepository announcementRepository;

    public AnnouncementResource(AnnouncementService announcementService, AnnouncementRepository announcementRepository) {
        this.announcementService = announcementService;
        this.announcementRepository = announcementRepository;
    }

    /**
     * {@code POST  /announcements} : Create a new announcement.
     *
     * @param announcementDTO the announcementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new announcementDTO, or with status {@code 400 (Bad Request)} if the announcement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AnnouncementDTO> createAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Announcement : {}", announcementDTO);
        if (announcementDTO.getId() != null) {
            throw new BadRequestAlertException("A new announcement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        announcementDTO = announcementService.save(announcementDTO);
        return ResponseEntity.created(new URI("/api/announcements/" + announcementDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, announcementDTO.getId().toString()))
            .body(announcementDTO);
    }

    /**
     * {@code PUT  /announcements/:id} : Updates an existing announcement.
     *
     * @param id the id of the announcementDTO to save.
     * @param announcementDTO the announcementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated announcementDTO,
     * or with status {@code 400 (Bad Request)} if the announcementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the announcementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> updateAnnouncement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AnnouncementDTO announcementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Announcement : {}, {}", id, announcementDTO);
        if (announcementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, announcementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!announcementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        announcementDTO = announcementService.update(announcementDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, announcementDTO.getId().toString()))
            .body(announcementDTO);
    }

    /**
     * {@code PATCH  /announcements/:id} : Partial updates given fields of an existing announcement, field will ignore if it is null
     *
     * @param id the id of the announcementDTO to save.
     * @param announcementDTO the announcementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated announcementDTO,
     * or with status {@code 400 (Bad Request)} if the announcementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the announcementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the announcementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AnnouncementDTO> partialUpdateAnnouncement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AnnouncementDTO announcementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Announcement partially : {}, {}", id, announcementDTO);
        if (announcementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, announcementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!announcementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AnnouncementDTO> result = announcementService.partialUpdate(announcementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, announcementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /announcements} : get all the Announcements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Announcements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AnnouncementDTO>> getAllAnnouncements(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Announcements");
        Page<AnnouncementDTO> page = announcementService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /announcements/:id} : get the "id" announcement.
     *
     * @param id the id of the announcementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the announcementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> getAnnouncement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Announcement : {}", id);
        Optional<AnnouncementDTO> announcementDTO = announcementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(announcementDTO);
    }

    /**
     * {@code DELETE  /announcements/:id} : delete the "id" announcement.
     *
     * @param id the id of the announcementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Announcement : {}", id);
        announcementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
