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
import zw.org.nmrl.ept.repository.ScheduledJobRepository;
import zw.org.nmrl.ept.service.ScheduledJobQueryService;
import zw.org.nmrl.ept.service.ScheduledJobService;
import zw.org.nmrl.ept.service.criteria.ScheduledJobCriteria;
import zw.org.nmrl.ept.service.dto.ScheduledJobDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ScheduledJob}.
 */
@RestController
@RequestMapping("/api/scheduled-jobs")
public class ScheduledJobResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledJobResource.class);

    private static final String ENTITY_NAME = "scheduledJob";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ScheduledJobService scheduledJobService;

    private final ScheduledJobRepository scheduledJobRepository;

    private final ScheduledJobQueryService scheduledJobQueryService;

    public ScheduledJobResource(
        ScheduledJobService scheduledJobService,
        ScheduledJobRepository scheduledJobRepository,
        ScheduledJobQueryService scheduledJobQueryService
    ) {
        this.scheduledJobService = scheduledJobService;
        this.scheduledJobRepository = scheduledJobRepository;
        this.scheduledJobQueryService = scheduledJobQueryService;
    }

    /**
     * {@code POST  /scheduled-jobs} : Create a new scheduledJob.
     *
     * @param scheduledJobDTO the scheduledJobDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduledJobDTO, or with status {@code 400 (Bad Request)} if the scheduledJob has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ScheduledJobDTO> createScheduledJob(@Valid @RequestBody ScheduledJobDTO scheduledJobDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ScheduledJob : {}", scheduledJobDTO);
        if (scheduledJobDTO.getId() != null) {
            throw new BadRequestAlertException("A new scheduledJob cannot already have an ID", ENTITY_NAME, "idexists");
        }
        scheduledJobDTO = scheduledJobService.save(scheduledJobDTO);
        return ResponseEntity.created(new URI("/api/scheduled-jobs/" + scheduledJobDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, scheduledJobDTO.getId().toString()))
            .body(scheduledJobDTO);
    }

    /**
     * {@code PUT  /scheduled-jobs/:id} : Updates an existing scheduledJob.
     *
     * @param id the id of the scheduledJobDTO to save.
     * @param scheduledJobDTO the scheduledJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduledJobDTO,
     * or with status {@code 400 (Bad Request)} if the scheduledJobDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduledJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduledJobDTO> updateScheduledJob(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScheduledJobDTO scheduledJobDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ScheduledJob : {}, {}", id, scheduledJobDTO);
        if (scheduledJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduledJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduledJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        scheduledJobDTO = scheduledJobService.update(scheduledJobDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduledJobDTO.getId().toString()))
            .body(scheduledJobDTO);
    }

    /**
     * {@code PATCH  /scheduled-jobs/:id} : Partial updates given fields of an existing scheduledJob, field will ignore if it is null
     *
     * @param id the id of the scheduledJobDTO to save.
     * @param scheduledJobDTO the scheduledJobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduledJobDTO,
     * or with status {@code 400 (Bad Request)} if the scheduledJobDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scheduledJobDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scheduledJobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScheduledJobDTO> partialUpdateScheduledJob(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScheduledJobDTO scheduledJobDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ScheduledJob partially : {}, {}", id, scheduledJobDTO);
        if (scheduledJobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduledJobDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scheduledJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScheduledJobDTO> result = scheduledJobService.partialUpdate(scheduledJobDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduledJobDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /scheduled-jobs} : get all the Scheduled Jobs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Scheduled Jobs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ScheduledJobDTO>> getAllScheduledJobs(
        ScheduledJobCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ScheduledJobs by criteria: {}", criteria);

        Page<ScheduledJobDTO> page = scheduledJobQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scheduled-jobs/count} : count all the scheduledJobs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countScheduledJobs(ScheduledJobCriteria criteria) {
        LOG.debug("REST request to count ScheduledJobs by criteria: {}", criteria);
        return ResponseEntity.ok().body(scheduledJobQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /scheduled-jobs/:id} : get the "id" scheduledJob.
     *
     * @param id the id of the scheduledJobDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduledJobDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduledJobDTO> getScheduledJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ScheduledJob : {}", id);
        Optional<ScheduledJobDTO> scheduledJobDTO = scheduledJobService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scheduledJobDTO);
    }

    /**
     * {@code DELETE  /scheduled-jobs/:id} : delete the "id" scheduledJob.
     *
     * @param id the id of the scheduledJobDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduledJob(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ScheduledJob : {}", id);
        scheduledJobService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
