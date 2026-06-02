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
import zw.org.nmrl.ept.repository.NotTestedReasonRepository;
import zw.org.nmrl.ept.service.NotTestedReasonService;
import zw.org.nmrl.ept.service.dto.NotTestedReasonDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.NotTestedReason}.
 */
@RestController
@RequestMapping("/api/not-tested-reasons")
public class NotTestedReasonResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotTestedReasonResource.class);

    private static final String ENTITY_NAME = "notTestedReason";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final NotTestedReasonService notTestedReasonService;

    private final NotTestedReasonRepository notTestedReasonRepository;

    public NotTestedReasonResource(NotTestedReasonService notTestedReasonService, NotTestedReasonRepository notTestedReasonRepository) {
        this.notTestedReasonService = notTestedReasonService;
        this.notTestedReasonRepository = notTestedReasonRepository;
    }

    /**
     * {@code POST  /not-tested-reasons} : Create a new notTestedReason.
     *
     * @param notTestedReasonDTO the notTestedReasonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notTestedReasonDTO, or with status {@code 400 (Bad Request)} if the notTestedReason has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NotTestedReasonDTO> createNotTestedReason(@Valid @RequestBody NotTestedReasonDTO notTestedReasonDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save NotTestedReason : {}", notTestedReasonDTO);
        if (notTestedReasonDTO.getId() != null) {
            throw new BadRequestAlertException("A new notTestedReason cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notTestedReasonDTO = notTestedReasonService.save(notTestedReasonDTO);
        return ResponseEntity.created(new URI("/api/not-tested-reasons/" + notTestedReasonDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notTestedReasonDTO.getId().toString()))
            .body(notTestedReasonDTO);
    }

    /**
     * {@code PUT  /not-tested-reasons/:id} : Updates an existing notTestedReason.
     *
     * @param id the id of the notTestedReasonDTO to save.
     * @param notTestedReasonDTO the notTestedReasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notTestedReasonDTO,
     * or with status {@code 400 (Bad Request)} if the notTestedReasonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notTestedReasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotTestedReasonDTO> updateNotTestedReason(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotTestedReasonDTO notTestedReasonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NotTestedReason : {}, {}", id, notTestedReasonDTO);
        if (notTestedReasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notTestedReasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notTestedReasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notTestedReasonDTO = notTestedReasonService.update(notTestedReasonDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notTestedReasonDTO.getId().toString()))
            .body(notTestedReasonDTO);
    }

    /**
     * {@code PATCH  /not-tested-reasons/:id} : Partial updates given fields of an existing notTestedReason, field will ignore if it is null
     *
     * @param id the id of the notTestedReasonDTO to save.
     * @param notTestedReasonDTO the notTestedReasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notTestedReasonDTO,
     * or with status {@code 400 (Bad Request)} if the notTestedReasonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notTestedReasonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notTestedReasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotTestedReasonDTO> partialUpdateNotTestedReason(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotTestedReasonDTO notTestedReasonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NotTestedReason partially : {}, {}", id, notTestedReasonDTO);
        if (notTestedReasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notTestedReasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notTestedReasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotTestedReasonDTO> result = notTestedReasonService.partialUpdate(notTestedReasonDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notTestedReasonDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /not-tested-reasons} : get all the Not Tested Reasons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Not Tested Reasons in body.
     */
    @GetMapping("")
    public List<NotTestedReasonDTO> getAllNotTestedReasons() {
        LOG.debug("REST request to get all NotTestedReasons");
        return notTestedReasonService.findAll();
    }

    /**
     * {@code GET  /not-tested-reasons/:id} : get the "id" notTestedReason.
     *
     * @param id the id of the notTestedReasonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notTestedReasonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotTestedReasonDTO> getNotTestedReason(@PathVariable("id") Long id) {
        LOG.debug("REST request to get NotTestedReason : {}", id);
        Optional<NotTestedReasonDTO> notTestedReasonDTO = notTestedReasonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notTestedReasonDTO);
    }

    /**
     * {@code DELETE  /not-tested-reasons/:id} : delete the "id" notTestedReason.
     *
     * @param id the id of the notTestedReasonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotTestedReason(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete NotTestedReason : {}", id);
        notTestedReasonService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
