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
import zw.org.nmrl.ept.repository.SampleReferenceResultRepository;
import zw.org.nmrl.ept.service.SampleReferenceResultService;
import zw.org.nmrl.ept.service.dto.SampleReferenceResultDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.SampleReferenceResult}.
 */
@RestController
@RequestMapping("/api/sample-reference-results")
public class SampleReferenceResultResource {

    private static final Logger LOG = LoggerFactory.getLogger(SampleReferenceResultResource.class);

    private static final String ENTITY_NAME = "sampleReferenceResult";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final SampleReferenceResultService sampleReferenceResultService;

    private final SampleReferenceResultRepository sampleReferenceResultRepository;

    public SampleReferenceResultResource(
        SampleReferenceResultService sampleReferenceResultService,
        SampleReferenceResultRepository sampleReferenceResultRepository
    ) {
        this.sampleReferenceResultService = sampleReferenceResultService;
        this.sampleReferenceResultRepository = sampleReferenceResultRepository;
    }

    /**
     * {@code POST  /sample-reference-results} : Create a new sampleReferenceResult.
     *
     * @param sampleReferenceResultDTO the sampleReferenceResultDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sampleReferenceResultDTO, or with status {@code 400 (Bad Request)} if the sampleReferenceResult has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SampleReferenceResultDTO> createSampleReferenceResult(
        @Valid @RequestBody SampleReferenceResultDTO sampleReferenceResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save SampleReferenceResult : {}", sampleReferenceResultDTO);
        if (sampleReferenceResultDTO.getId() != null) {
            throw new BadRequestAlertException("A new sampleReferenceResult cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sampleReferenceResultDTO = sampleReferenceResultService.save(sampleReferenceResultDTO);
        return ResponseEntity.created(new URI("/api/sample-reference-results/" + sampleReferenceResultDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sampleReferenceResultDTO.getId().toString()))
            .body(sampleReferenceResultDTO);
    }

    /**
     * {@code PUT  /sample-reference-results/:id} : Updates an existing sampleReferenceResult.
     *
     * @param id the id of the sampleReferenceResultDTO to save.
     * @param sampleReferenceResultDTO the sampleReferenceResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sampleReferenceResultDTO,
     * or with status {@code 400 (Bad Request)} if the sampleReferenceResultDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sampleReferenceResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SampleReferenceResultDTO> updateSampleReferenceResult(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SampleReferenceResultDTO sampleReferenceResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SampleReferenceResult : {}, {}", id, sampleReferenceResultDTO);
        if (sampleReferenceResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sampleReferenceResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sampleReferenceResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sampleReferenceResultDTO = sampleReferenceResultService.update(sampleReferenceResultDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sampleReferenceResultDTO.getId().toString()))
            .body(sampleReferenceResultDTO);
    }

    /**
     * {@code PATCH  /sample-reference-results/:id} : Partial updates given fields of an existing sampleReferenceResult, field will ignore if it is null
     *
     * @param id the id of the sampleReferenceResultDTO to save.
     * @param sampleReferenceResultDTO the sampleReferenceResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sampleReferenceResultDTO,
     * or with status {@code 400 (Bad Request)} if the sampleReferenceResultDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sampleReferenceResultDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sampleReferenceResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SampleReferenceResultDTO> partialUpdateSampleReferenceResult(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SampleReferenceResultDTO sampleReferenceResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SampleReferenceResult partially : {}, {}", id, sampleReferenceResultDTO);
        if (sampleReferenceResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sampleReferenceResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sampleReferenceResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SampleReferenceResultDTO> result = sampleReferenceResultService.partialUpdate(sampleReferenceResultDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sampleReferenceResultDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sample-reference-results} : get all the Sample Reference Results.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Sample Reference Results in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SampleReferenceResultDTO>> getAllSampleReferenceResults(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of SampleReferenceResults");
        Page<SampleReferenceResultDTO> page = sampleReferenceResultService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sample-reference-results/:id} : get the "id" sampleReferenceResult.
     *
     * @param id the id of the sampleReferenceResultDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sampleReferenceResultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SampleReferenceResultDTO> getSampleReferenceResult(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SampleReferenceResult : {}", id);
        Optional<SampleReferenceResultDTO> sampleReferenceResultDTO = sampleReferenceResultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sampleReferenceResultDTO);
    }

    /**
     * {@code DELETE  /sample-reference-results/:id} : delete the "id" sampleReferenceResult.
     *
     * @param id the id of the sampleReferenceResultDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSampleReferenceResult(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SampleReferenceResult : {}", id);
        sampleReferenceResultService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
