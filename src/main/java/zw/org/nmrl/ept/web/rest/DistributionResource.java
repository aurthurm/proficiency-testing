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
import zw.org.nmrl.ept.repository.DistributionRepository;
import zw.org.nmrl.ept.service.DistributionQueryService;
import zw.org.nmrl.ept.service.DistributionService;
import zw.org.nmrl.ept.service.criteria.DistributionCriteria;
import zw.org.nmrl.ept.service.dto.DistributionDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.Distribution}.
 */
@RestController
@RequestMapping("/api/distributions")
public class DistributionResource {

    private static final Logger LOG = LoggerFactory.getLogger(DistributionResource.class);

    private static final String ENTITY_NAME = "distribution";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final DistributionService distributionService;

    private final DistributionRepository distributionRepository;

    private final DistributionQueryService distributionQueryService;

    public DistributionResource(
        DistributionService distributionService,
        DistributionRepository distributionRepository,
        DistributionQueryService distributionQueryService
    ) {
        this.distributionService = distributionService;
        this.distributionRepository = distributionRepository;
        this.distributionQueryService = distributionQueryService;
    }

    /**
     * {@code POST  /distributions} : Create a new distribution.
     *
     * @param distributionDTO the distributionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new distributionDTO, or with status {@code 400 (Bad Request)} if the distribution has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DistributionDTO> createDistribution(@Valid @RequestBody DistributionDTO distributionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Distribution : {}", distributionDTO);
        if (distributionDTO.getId() != null) {
            throw new BadRequestAlertException("A new distribution cannot already have an ID", ENTITY_NAME, "idexists");
        }
        distributionDTO = distributionService.save(distributionDTO);
        return ResponseEntity.created(new URI("/api/distributions/" + distributionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, distributionDTO.getId().toString()))
            .body(distributionDTO);
    }

    /**
     * {@code PUT  /distributions/:id} : Updates an existing distribution.
     *
     * @param id the id of the distributionDTO to save.
     * @param distributionDTO the distributionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated distributionDTO,
     * or with status {@code 400 (Bad Request)} if the distributionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the distributionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DistributionDTO> updateDistribution(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DistributionDTO distributionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Distribution : {}, {}", id, distributionDTO);
        if (distributionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, distributionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!distributionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        distributionDTO = distributionService.update(distributionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, distributionDTO.getId().toString()))
            .body(distributionDTO);
    }

    /**
     * {@code PATCH  /distributions/:id} : Partial updates given fields of an existing distribution, field will ignore if it is null
     *
     * @param id the id of the distributionDTO to save.
     * @param distributionDTO the distributionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated distributionDTO,
     * or with status {@code 400 (Bad Request)} if the distributionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the distributionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the distributionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DistributionDTO> partialUpdateDistribution(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DistributionDTO distributionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Distribution partially : {}, {}", id, distributionDTO);
        if (distributionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, distributionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!distributionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DistributionDTO> result = distributionService.partialUpdate(distributionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, distributionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /distributions} : get all the Distributions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Distributions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DistributionDTO>> getAllDistributions(
        DistributionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Distributions by criteria: {}", criteria);

        Page<DistributionDTO> page = distributionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /distributions/count} : count all the distributions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDistributions(DistributionCriteria criteria) {
        LOG.debug("REST request to count Distributions by criteria: {}", criteria);
        return ResponseEntity.ok().body(distributionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /distributions/:id} : get the "id" distribution.
     *
     * @param id the id of the distributionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the distributionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DistributionDTO> getDistribution(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Distribution : {}", id);
        Optional<DistributionDTO> distributionDTO = distributionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(distributionDTO);
    }

    /**
     * {@code DELETE  /distributions/:id} : delete the "id" distribution.
     *
     * @param id the id of the distributionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDistribution(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Distribution : {}", id);
        distributionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
