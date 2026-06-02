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
import zw.org.nmrl.ept.repository.ParticipantMessageRepository;
import zw.org.nmrl.ept.service.ParticipantMessageService;
import zw.org.nmrl.ept.service.dto.ParticipantMessageDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ParticipantMessage}.
 */
@RestController
@RequestMapping("/api/participant-messages")
public class ParticipantMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantMessageResource.class);

    private static final String ENTITY_NAME = "participantMessage";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ParticipantMessageService participantMessageService;

    private final ParticipantMessageRepository participantMessageRepository;

    public ParticipantMessageResource(
        ParticipantMessageService participantMessageService,
        ParticipantMessageRepository participantMessageRepository
    ) {
        this.participantMessageService = participantMessageService;
        this.participantMessageRepository = participantMessageRepository;
    }

    /**
     * {@code POST  /participant-messages} : Create a new participantMessage.
     *
     * @param participantMessageDTO the participantMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participantMessageDTO, or with status {@code 400 (Bad Request)} if the participantMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParticipantMessageDTO> createParticipantMessage(@Valid @RequestBody ParticipantMessageDTO participantMessageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ParticipantMessage : {}", participantMessageDTO);
        if (participantMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new participantMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        participantMessageDTO = participantMessageService.save(participantMessageDTO);
        return ResponseEntity.created(new URI("/api/participant-messages/" + participantMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, participantMessageDTO.getId().toString()))
            .body(participantMessageDTO);
    }

    /**
     * {@code PUT  /participant-messages/:id} : Updates an existing participantMessage.
     *
     * @param id the id of the participantMessageDTO to save.
     * @param participantMessageDTO the participantMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantMessageDTO,
     * or with status {@code 400 (Bad Request)} if the participantMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participantMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantMessageDTO> updateParticipantMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParticipantMessageDTO participantMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ParticipantMessage : {}, {}", id, participantMessageDTO);
        if (participantMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        participantMessageDTO = participantMessageService.update(participantMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantMessageDTO.getId().toString()))
            .body(participantMessageDTO);
    }

    /**
     * {@code PATCH  /participant-messages/:id} : Partial updates given fields of an existing participantMessage, field will ignore if it is null
     *
     * @param id the id of the participantMessageDTO to save.
     * @param participantMessageDTO the participantMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantMessageDTO,
     * or with status {@code 400 (Bad Request)} if the participantMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the participantMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the participantMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParticipantMessageDTO> partialUpdateParticipantMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParticipantMessageDTO participantMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ParticipantMessage partially : {}, {}", id, participantMessageDTO);
        if (participantMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParticipantMessageDTO> result = participantMessageService.partialUpdate(participantMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /participant-messages} : get all the Participant Messages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Participant Messages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ParticipantMessageDTO>> getAllParticipantMessages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ParticipantMessages");
        Page<ParticipantMessageDTO> page = participantMessageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /participant-messages/:id} : get the "id" participantMessage.
     *
     * @param id the id of the participantMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the participantMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantMessageDTO> getParticipantMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ParticipantMessage : {}", id);
        Optional<ParticipantMessageDTO> participantMessageDTO = participantMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(participantMessageDTO);
    }

    /**
     * {@code DELETE  /participant-messages/:id} : delete the "id" participantMessage.
     *
     * @param id the id of the participantMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipantMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ParticipantMessage : {}", id);
        participantMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
