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
import zw.org.nmrl.ept.repository.CapaRecordRepository;
import zw.org.nmrl.ept.service.CapaRecordQueryService;
import zw.org.nmrl.ept.service.CapaRecordService;
import zw.org.nmrl.ept.service.criteria.CapaRecordCriteria;
import zw.org.nmrl.ept.service.dto.CapaRecordDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.CapaRecord}.
 */
@RestController
@RequestMapping("/api/capa-records")
public class CapaRecordResource {

    private static final Logger LOG = LoggerFactory.getLogger(CapaRecordResource.class);

    private static final String ENTITY_NAME = "capaRecord";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final CapaRecordService capaRecordService;

    private final CapaRecordRepository capaRecordRepository;

    private final CapaRecordQueryService capaRecordQueryService;

    public CapaRecordResource(
        CapaRecordService capaRecordService,
        CapaRecordRepository capaRecordRepository,
        CapaRecordQueryService capaRecordQueryService
    ) {
        this.capaRecordService = capaRecordService;
        this.capaRecordRepository = capaRecordRepository;
        this.capaRecordQueryService = capaRecordQueryService;
    }

    /**
     * {@code POST  /capa-records} : Create a new capaRecord.
     *
     * @param capaRecordDTO the capaRecordDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new capaRecordDTO, or with status {@code 400 (Bad Request)} if the capaRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CapaRecordDTO> createCapaRecord(@Valid @RequestBody CapaRecordDTO capaRecordDTO) throws URISyntaxException {
        LOG.debug("REST request to save CapaRecord : {}", capaRecordDTO);
        if (capaRecordDTO.getId() != null) {
            throw new BadRequestAlertException("A new capaRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        capaRecordDTO = capaRecordService.save(capaRecordDTO);
        return ResponseEntity.created(new URI("/api/capa-records/" + capaRecordDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, capaRecordDTO.getId().toString()))
            .body(capaRecordDTO);
    }

    /**
     * {@code PUT  /capa-records/:id} : Updates an existing capaRecord.
     *
     * @param id the id of the capaRecordDTO to save.
     * @param capaRecordDTO the capaRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated capaRecordDTO,
     * or with status {@code 400 (Bad Request)} if the capaRecordDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the capaRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CapaRecordDTO> updateCapaRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CapaRecordDTO capaRecordDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CapaRecord : {}, {}", id, capaRecordDTO);
        if (capaRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, capaRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!capaRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        capaRecordDTO = capaRecordService.update(capaRecordDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, capaRecordDTO.getId().toString()))
            .body(capaRecordDTO);
    }

    /**
     * {@code PATCH  /capa-records/:id} : Partial updates given fields of an existing capaRecord, field will ignore if it is null
     *
     * @param id the id of the capaRecordDTO to save.
     * @param capaRecordDTO the capaRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated capaRecordDTO,
     * or with status {@code 400 (Bad Request)} if the capaRecordDTO is not valid,
     * or with status {@code 404 (Not Found)} if the capaRecordDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the capaRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CapaRecordDTO> partialUpdateCapaRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CapaRecordDTO capaRecordDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CapaRecord partially : {}, {}", id, capaRecordDTO);
        if (capaRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, capaRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!capaRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CapaRecordDTO> result = capaRecordService.partialUpdate(capaRecordDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, capaRecordDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /capa-records} : get all the Capa Records.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Capa Records in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CapaRecordDTO>> getAllCapaRecords(
        CapaRecordCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CapaRecords by criteria: {}", criteria);

        Page<CapaRecordDTO> page = capaRecordQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /capa-records/count} : count all the capaRecords.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCapaRecords(CapaRecordCriteria criteria) {
        LOG.debug("REST request to count CapaRecords by criteria: {}", criteria);
        return ResponseEntity.ok().body(capaRecordQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /capa-records/:id} : get the "id" capaRecord.
     *
     * @param id the id of the capaRecordDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the capaRecordDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CapaRecordDTO> getCapaRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CapaRecord : {}", id);
        Optional<CapaRecordDTO> capaRecordDTO = capaRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(capaRecordDTO);
    }

    /**
     * {@code DELETE  /capa-records/:id} : delete the "id" capaRecord.
     *
     * @param id the id of the capaRecordDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCapaRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CapaRecord : {}", id);
        capaRecordService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
