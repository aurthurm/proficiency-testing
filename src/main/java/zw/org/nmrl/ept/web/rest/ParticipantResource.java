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
import zw.org.nmrl.ept.repository.ParticipantRepository;
import zw.org.nmrl.ept.service.ParticipantQueryService;
import zw.org.nmrl.ept.service.ParticipantService;
import zw.org.nmrl.ept.service.criteria.ParticipantCriteria;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.Participant}.
 */
@RestController
@RequestMapping("/api/participants")
public class ParticipantResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantResource.class);

    private static final String ENTITY_NAME = "participant";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ParticipantService participantService;

    private final ParticipantRepository participantRepository;

    private final ParticipantQueryService participantQueryService;

    public ParticipantResource(
        ParticipantService participantService,
        ParticipantRepository participantRepository,
        ParticipantQueryService participantQueryService
    ) {
        this.participantService = participantService;
        this.participantRepository = participantRepository;
        this.participantQueryService = participantQueryService;
    }

    /**
     * {@code POST  /participants} : Create a new participant.
     *
     * @param participantDTO the participantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participantDTO, or with status {@code 400 (Bad Request)} if the participant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParticipantDTO> createParticipant(@Valid @RequestBody ParticipantDTO participantDTO) throws URISyntaxException {
        LOG.debug("REST request to save Participant : {}", participantDTO);
        if (participantDTO.getId() != null) {
            throw new BadRequestAlertException("A new participant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        participantDTO = participantService.save(participantDTO);
        return ResponseEntity.created(new URI("/api/participants/" + participantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, participantDTO.getId().toString()))
            .body(participantDTO);
    }

    /**
     * {@code PUT  /participants/:id} : Updates an existing participant.
     *
     * @param id the id of the participantDTO to save.
     * @param participantDTO the participantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantDTO,
     * or with status {@code 400 (Bad Request)} if the participantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantDTO> updateParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Participant : {}, {}", id, participantDTO);
        if (participantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        participantDTO = participantService.update(participantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantDTO.getId().toString()))
            .body(participantDTO);
    }

    /**
     * {@code PATCH  /participants/:id} : Partial updates given fields of an existing participant, field will ignore if it is null
     *
     * @param id the id of the participantDTO to save.
     * @param participantDTO the participantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantDTO,
     * or with status {@code 400 (Bad Request)} if the participantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the participantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the participantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParticipantDTO> partialUpdateParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Participant partially : {}, {}", id, participantDTO);
        if (participantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParticipantDTO> result = participantService.partialUpdate(participantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /participants} : get all the Participants.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Participants in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ParticipantDTO>> getAllParticipants(
        ParticipantCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Participants by criteria: {}", criteria);

        Page<ParticipantDTO> page = participantQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /participants/count} : count all the participants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countParticipants(ParticipantCriteria criteria) {
        LOG.debug("REST request to count Participants by criteria: {}", criteria);
        return ResponseEntity.ok().body(participantQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /participants/:id} : get the "id" participant.
     *
     * @param id the id of the participantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the participantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantDTO> getParticipant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Participant : {}", id);
        Optional<ParticipantDTO> participantDTO = participantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(participantDTO);
    }

    /**
     * {@code DELETE  /participants/:id} : delete the "id" participant.
     *
     * @param id the id of the participantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Participant : {}", id);
        participantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
