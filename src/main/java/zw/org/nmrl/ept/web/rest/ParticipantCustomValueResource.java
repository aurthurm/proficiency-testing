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
import zw.org.nmrl.ept.repository.ParticipantCustomValueRepository;
import zw.org.nmrl.ept.service.ParticipantCustomValueService;
import zw.org.nmrl.ept.service.dto.ParticipantCustomValueDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ParticipantCustomValue}.
 */
@RestController
@RequestMapping("/api/participant-custom-values")
public class ParticipantCustomValueResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantCustomValueResource.class);

    private static final String ENTITY_NAME = "participantCustomValue";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ParticipantCustomValueService participantCustomValueService;

    private final ParticipantCustomValueRepository participantCustomValueRepository;

    public ParticipantCustomValueResource(
        ParticipantCustomValueService participantCustomValueService,
        ParticipantCustomValueRepository participantCustomValueRepository
    ) {
        this.participantCustomValueService = participantCustomValueService;
        this.participantCustomValueRepository = participantCustomValueRepository;
    }

    /**
     * {@code POST  /participant-custom-values} : Create a new participantCustomValue.
     *
     * @param participantCustomValueDTO the participantCustomValueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participantCustomValueDTO, or with status {@code 400 (Bad Request)} if the participantCustomValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParticipantCustomValueDTO> createParticipantCustomValue(
        @Valid @RequestBody ParticipantCustomValueDTO participantCustomValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ParticipantCustomValue : {}", participantCustomValueDTO);
        if (participantCustomValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new participantCustomValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        participantCustomValueDTO = participantCustomValueService.save(participantCustomValueDTO);
        return ResponseEntity.created(new URI("/api/participant-custom-values/" + participantCustomValueDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, participantCustomValueDTO.getId().toString()))
            .body(participantCustomValueDTO);
    }

    /**
     * {@code PUT  /participant-custom-values/:id} : Updates an existing participantCustomValue.
     *
     * @param id the id of the participantCustomValueDTO to save.
     * @param participantCustomValueDTO the participantCustomValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantCustomValueDTO,
     * or with status {@code 400 (Bad Request)} if the participantCustomValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participantCustomValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantCustomValueDTO> updateParticipantCustomValue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParticipantCustomValueDTO participantCustomValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ParticipantCustomValue : {}, {}", id, participantCustomValueDTO);
        if (participantCustomValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantCustomValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantCustomValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        participantCustomValueDTO = participantCustomValueService.update(participantCustomValueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantCustomValueDTO.getId().toString()))
            .body(participantCustomValueDTO);
    }

    /**
     * {@code PATCH  /participant-custom-values/:id} : Partial updates given fields of an existing participantCustomValue, field will ignore if it is null
     *
     * @param id the id of the participantCustomValueDTO to save.
     * @param participantCustomValueDTO the participantCustomValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantCustomValueDTO,
     * or with status {@code 400 (Bad Request)} if the participantCustomValueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the participantCustomValueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the participantCustomValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParticipantCustomValueDTO> partialUpdateParticipantCustomValue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParticipantCustomValueDTO participantCustomValueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ParticipantCustomValue partially : {}, {}", id, participantCustomValueDTO);
        if (participantCustomValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantCustomValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantCustomValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParticipantCustomValueDTO> result = participantCustomValueService.partialUpdate(participantCustomValueDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantCustomValueDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /participant-custom-values} : get all the Participant Custom Values.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Participant Custom Values in body.
     */
    @GetMapping("")
    public List<ParticipantCustomValueDTO> getAllParticipantCustomValues() {
        LOG.debug("REST request to get all ParticipantCustomValues");
        return participantCustomValueService.findAll();
    }

    /**
     * {@code GET  /participant-custom-values/:id} : get the "id" participantCustomValue.
     *
     * @param id the id of the participantCustomValueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the participantCustomValueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantCustomValueDTO> getParticipantCustomValue(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ParticipantCustomValue : {}", id);
        Optional<ParticipantCustomValueDTO> participantCustomValueDTO = participantCustomValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(participantCustomValueDTO);
    }

    /**
     * {@code DELETE  /participant-custom-values/:id} : delete the "id" participantCustomValue.
     *
     * @param id the id of the participantCustomValueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipantCustomValue(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ParticipantCustomValue : {}", id);
        participantCustomValueService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
