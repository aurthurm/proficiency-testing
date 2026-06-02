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
import zw.org.nmrl.ept.repository.AssayRepository;
import zw.org.nmrl.ept.service.AssayService;
import zw.org.nmrl.ept.service.dto.AssayDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.Assay}.
 */
@RestController
@RequestMapping("/api/assays")
public class AssayResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssayResource.class);

    private static final String ENTITY_NAME = "assay";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final AssayService assayService;

    private final AssayRepository assayRepository;

    public AssayResource(AssayService assayService, AssayRepository assayRepository) {
        this.assayService = assayService;
        this.assayRepository = assayRepository;
    }

    /**
     * {@code POST  /assays} : Create a new assay.
     *
     * @param assayDTO the assayDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assayDTO, or with status {@code 400 (Bad Request)} if the assay has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssayDTO> createAssay(@Valid @RequestBody AssayDTO assayDTO) throws URISyntaxException {
        LOG.debug("REST request to save Assay : {}", assayDTO);
        if (assayDTO.getId() != null) {
            throw new BadRequestAlertException("A new assay cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assayDTO = assayService.save(assayDTO);
        return ResponseEntity.created(new URI("/api/assays/" + assayDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, assayDTO.getId().toString()))
            .body(assayDTO);
    }

    /**
     * {@code PUT  /assays/:id} : Updates an existing assay.
     *
     * @param id the id of the assayDTO to save.
     * @param assayDTO the assayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assayDTO,
     * or with status {@code 400 (Bad Request)} if the assayDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssayDTO> updateAssay(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssayDTO assayDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Assay : {}, {}", id, assayDTO);
        if (assayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assayDTO = assayService.update(assayDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assayDTO.getId().toString()))
            .body(assayDTO);
    }

    /**
     * {@code PATCH  /assays/:id} : Partial updates given fields of an existing assay, field will ignore if it is null
     *
     * @param id the id of the assayDTO to save.
     * @param assayDTO the assayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assayDTO,
     * or with status {@code 400 (Bad Request)} if the assayDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assayDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssayDTO> partialUpdateAssay(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssayDTO assayDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Assay partially : {}, {}", id, assayDTO);
        if (assayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssayDTO> result = assayService.partialUpdate(assayDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assayDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /assays} : get all the Assays.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Assays in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AssayDTO>> getAllAssays(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Assays");
        Page<AssayDTO> page = assayService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /assays/:id} : get the "id" assay.
     *
     * @param id the id of the assayDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assayDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssayDTO> getAssay(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Assay : {}", id);
        Optional<AssayDTO> assayDTO = assayService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assayDTO);
    }

    /**
     * {@code DELETE  /assays/:id} : delete the "id" assay.
     *
     * @param id the id of the assayDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssay(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Assay : {}", id);
        assayService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
