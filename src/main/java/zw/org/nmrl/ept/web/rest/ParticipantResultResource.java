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
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.service.ParticipantResultQueryService;
import zw.org.nmrl.ept.service.ParticipantResultService;
import zw.org.nmrl.ept.service.criteria.ParticipantResultCriteria;
import zw.org.nmrl.ept.service.dto.ParticipantResultDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ParticipantResult}.
 */
@RestController
@RequestMapping("/api/participant-results")
public class ParticipantResultResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantResultResource.class);

    private static final String ENTITY_NAME = "participantResult";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ParticipantResultService participantResultService;

    private final ParticipantResultRepository participantResultRepository;

    private final ParticipantResultQueryService participantResultQueryService;

    public ParticipantResultResource(
        ParticipantResultService participantResultService,
        ParticipantResultRepository participantResultRepository,
        ParticipantResultQueryService participantResultQueryService
    ) {
        this.participantResultService = participantResultService;
        this.participantResultRepository = participantResultRepository;
        this.participantResultQueryService = participantResultQueryService;
    }

    /**
     * {@code POST  /participant-results} : Create a new participantResult.
     *
     * @param participantResultDTO the participantResultDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participantResultDTO, or with status {@code 400 (Bad Request)} if the participantResult has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParticipantResultDTO> createParticipantResult(@Valid @RequestBody ParticipantResultDTO participantResultDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ParticipantResult : {}", participantResultDTO);
        if (participantResultDTO.getId() != null) {
            throw new BadRequestAlertException("A new participantResult cannot already have an ID", ENTITY_NAME, "idexists");
        }
        participantResultDTO = participantResultService.save(participantResultDTO);
        return ResponseEntity.created(new URI("/api/participant-results/" + participantResultDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, participantResultDTO.getId().toString()))
            .body(participantResultDTO);
    }

    /**
     * {@code PUT  /participant-results/:id} : Updates an existing participantResult.
     *
     * @param id the id of the participantResultDTO to save.
     * @param participantResultDTO the participantResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantResultDTO,
     * or with status {@code 400 (Bad Request)} if the participantResultDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participantResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantResultDTO> updateParticipantResult(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParticipantResultDTO participantResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ParticipantResult : {}, {}", id, participantResultDTO);
        if (participantResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        participantResultDTO = participantResultService.update(participantResultDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantResultDTO.getId().toString()))
            .body(participantResultDTO);
    }

    /**
     * {@code PATCH  /participant-results/:id} : Partial updates given fields of an existing participantResult, field will ignore if it is null
     *
     * @param id the id of the participantResultDTO to save.
     * @param participantResultDTO the participantResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantResultDTO,
     * or with status {@code 400 (Bad Request)} if the participantResultDTO is not valid,
     * or with status {@code 404 (Not Found)} if the participantResultDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the participantResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParticipantResultDTO> partialUpdateParticipantResult(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParticipantResultDTO participantResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ParticipantResult partially : {}, {}", id, participantResultDTO);
        if (participantResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParticipantResultDTO> result = participantResultService.partialUpdate(participantResultDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantResultDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /participant-results} : get all the Participant Results.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Participant Results in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ParticipantResultDTO>> getAllParticipantResults(
        ParticipantResultCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ParticipantResults by criteria: {}", criteria);

        Page<ParticipantResultDTO> page = participantResultQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /participant-results/count} : count all the participantResults.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countParticipantResults(ParticipantResultCriteria criteria) {
        LOG.debug("REST request to count ParticipantResults by criteria: {}", criteria);
        return ResponseEntity.ok().body(participantResultQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /participant-results/:id} : get the "id" participantResult.
     *
     * @param id the id of the participantResultDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the participantResultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResultDTO> getParticipantResult(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ParticipantResult : {}", id);
        Optional<ParticipantResultDTO> participantResultDTO = participantResultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(participantResultDTO);
    }

    /**
     * {@code DELETE  /participant-results/:id} : delete the "id" participantResult.
     *
     * @param id the id of the participantResultDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipantResult(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ParticipantResult : {}", id);
        participantResultService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
