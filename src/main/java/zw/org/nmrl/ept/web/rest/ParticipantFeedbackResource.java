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
import zw.org.nmrl.ept.repository.ParticipantFeedbackRepository;
import zw.org.nmrl.ept.service.ParticipantFeedbackService;
import zw.org.nmrl.ept.service.dto.ParticipantFeedbackDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ParticipantFeedback}.
 */
@RestController
@RequestMapping("/api/participant-feedbacks")
public class ParticipantFeedbackResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantFeedbackResource.class);

    private static final String ENTITY_NAME = "participantFeedback";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ParticipantFeedbackService participantFeedbackService;

    private final ParticipantFeedbackRepository participantFeedbackRepository;

    public ParticipantFeedbackResource(
        ParticipantFeedbackService participantFeedbackService,
        ParticipantFeedbackRepository participantFeedbackRepository
    ) {
        this.participantFeedbackService = participantFeedbackService;
        this.participantFeedbackRepository = participantFeedbackRepository;
    }

    /**
     * {@code POST  /participant-feedbacks} : Create a new participantFeedback.
     *
     * @param participantFeedbackDTO the participantFeedbackDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participantFeedbackDTO, or with status {@code 400 (Bad Request)} if the participantFeedback has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParticipantFeedbackDTO> createParticipantFeedback(
        @Valid @RequestBody ParticipantFeedbackDTO participantFeedbackDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ParticipantFeedback : {}", participantFeedbackDTO);
        if (participantFeedbackDTO.getId() != null) {
            throw new BadRequestAlertException("A new participantFeedback cannot already have an ID", ENTITY_NAME, "idexists");
        }
        participantFeedbackDTO = participantFeedbackService.save(participantFeedbackDTO);
        return ResponseEntity.created(new URI("/api/participant-feedbacks/" + participantFeedbackDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, participantFeedbackDTO.getId().toString()))
            .body(participantFeedbackDTO);
    }

    /**
     * {@code PUT  /participant-feedbacks/:id} : Updates an existing participantFeedback.
     *
     * @param id the id of the participantFeedbackDTO to save.
     * @param participantFeedbackDTO the participantFeedbackDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantFeedbackDTO,
     * or with status {@code 400 (Bad Request)} if the participantFeedbackDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participantFeedbackDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantFeedbackDTO> updateParticipantFeedback(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParticipantFeedbackDTO participantFeedbackDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ParticipantFeedback : {}, {}", id, participantFeedbackDTO);
        if (participantFeedbackDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantFeedbackDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantFeedbackRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        participantFeedbackDTO = participantFeedbackService.update(participantFeedbackDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantFeedbackDTO.getId().toString()))
            .body(participantFeedbackDTO);
    }

    /**
     * {@code PATCH  /participant-feedbacks/:id} : Partial updates given fields of an existing participantFeedback, field will ignore if it is null
     *
     * @param id the id of the participantFeedbackDTO to save.
     * @param participantFeedbackDTO the participantFeedbackDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantFeedbackDTO,
     * or with status {@code 400 (Bad Request)} if the participantFeedbackDTO is not valid,
     * or with status {@code 404 (Not Found)} if the participantFeedbackDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the participantFeedbackDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParticipantFeedbackDTO> partialUpdateParticipantFeedback(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParticipantFeedbackDTO participantFeedbackDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ParticipantFeedback partially : {}, {}", id, participantFeedbackDTO);
        if (participantFeedbackDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantFeedbackDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantFeedbackRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParticipantFeedbackDTO> result = participantFeedbackService.partialUpdate(participantFeedbackDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantFeedbackDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /participant-feedbacks} : get all the Participant Feedbacks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Participant Feedbacks in body.
     */
    @GetMapping("")
    public List<ParticipantFeedbackDTO> getAllParticipantFeedbacks() {
        LOG.debug("REST request to get all ParticipantFeedbacks");
        return participantFeedbackService.findAll();
    }

    /**
     * {@code GET  /participant-feedbacks/:id} : get the "id" participantFeedback.
     *
     * @param id the id of the participantFeedbackDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the participantFeedbackDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantFeedbackDTO> getParticipantFeedback(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ParticipantFeedback : {}", id);
        Optional<ParticipantFeedbackDTO> participantFeedbackDTO = participantFeedbackService.findOne(id);
        return ResponseUtil.wrapOrNotFound(participantFeedbackDTO);
    }

    /**
     * {@code DELETE  /participant-feedbacks/:id} : delete the "id" participantFeedback.
     *
     * @param id the id of the participantFeedbackDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipantFeedback(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ParticipantFeedback : {}", id);
        participantFeedbackService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
