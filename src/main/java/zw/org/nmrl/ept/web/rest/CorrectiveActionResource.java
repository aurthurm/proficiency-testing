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
import zw.org.nmrl.ept.repository.CorrectiveActionRepository;
import zw.org.nmrl.ept.service.CorrectiveActionService;
import zw.org.nmrl.ept.service.dto.CorrectiveActionDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.CorrectiveAction}.
 */
@RestController
@RequestMapping("/api/corrective-actions")
public class CorrectiveActionResource {

    private static final Logger LOG = LoggerFactory.getLogger(CorrectiveActionResource.class);

    private static final String ENTITY_NAME = "correctiveAction";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final CorrectiveActionService correctiveActionService;

    private final CorrectiveActionRepository correctiveActionRepository;

    public CorrectiveActionResource(
        CorrectiveActionService correctiveActionService,
        CorrectiveActionRepository correctiveActionRepository
    ) {
        this.correctiveActionService = correctiveActionService;
        this.correctiveActionRepository = correctiveActionRepository;
    }

    /**
     * {@code POST  /corrective-actions} : Create a new correctiveAction.
     *
     * @param correctiveActionDTO the correctiveActionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new correctiveActionDTO, or with status {@code 400 (Bad Request)} if the correctiveAction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CorrectiveActionDTO> createCorrectiveAction(@Valid @RequestBody CorrectiveActionDTO correctiveActionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CorrectiveAction : {}", correctiveActionDTO);
        if (correctiveActionDTO.getId() != null) {
            throw new BadRequestAlertException("A new correctiveAction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        correctiveActionDTO = correctiveActionService.save(correctiveActionDTO);
        return ResponseEntity.created(new URI("/api/corrective-actions/" + correctiveActionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, correctiveActionDTO.getId().toString()))
            .body(correctiveActionDTO);
    }

    /**
     * {@code PUT  /corrective-actions/:id} : Updates an existing correctiveAction.
     *
     * @param id the id of the correctiveActionDTO to save.
     * @param correctiveActionDTO the correctiveActionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated correctiveActionDTO,
     * or with status {@code 400 (Bad Request)} if the correctiveActionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the correctiveActionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CorrectiveActionDTO> updateCorrectiveAction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CorrectiveActionDTO correctiveActionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CorrectiveAction : {}, {}", id, correctiveActionDTO);
        if (correctiveActionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, correctiveActionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!correctiveActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        correctiveActionDTO = correctiveActionService.update(correctiveActionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, correctiveActionDTO.getId().toString()))
            .body(correctiveActionDTO);
    }

    /**
     * {@code PATCH  /corrective-actions/:id} : Partial updates given fields of an existing correctiveAction, field will ignore if it is null
     *
     * @param id the id of the correctiveActionDTO to save.
     * @param correctiveActionDTO the correctiveActionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated correctiveActionDTO,
     * or with status {@code 400 (Bad Request)} if the correctiveActionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the correctiveActionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the correctiveActionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CorrectiveActionDTO> partialUpdateCorrectiveAction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CorrectiveActionDTO correctiveActionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CorrectiveAction partially : {}, {}", id, correctiveActionDTO);
        if (correctiveActionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, correctiveActionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!correctiveActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CorrectiveActionDTO> result = correctiveActionService.partialUpdate(correctiveActionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, correctiveActionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /corrective-actions} : get all the Corrective Actions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Corrective Actions in body.
     */
    @GetMapping("")
    public List<CorrectiveActionDTO> getAllCorrectiveActions() {
        LOG.debug("REST request to get all CorrectiveActions");
        return correctiveActionService.findAll();
    }

    /**
     * {@code GET  /corrective-actions/:id} : get the "id" correctiveAction.
     *
     * @param id the id of the correctiveActionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the correctiveActionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CorrectiveActionDTO> getCorrectiveAction(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CorrectiveAction : {}", id);
        Optional<CorrectiveActionDTO> correctiveActionDTO = correctiveActionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(correctiveActionDTO);
    }

    /**
     * {@code DELETE  /corrective-actions/:id} : delete the "id" correctiveAction.
     *
     * @param id the id of the correctiveActionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCorrectiveAction(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CorrectiveAction : {}", id);
        correctiveActionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
