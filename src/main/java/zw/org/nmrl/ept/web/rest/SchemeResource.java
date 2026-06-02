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
import zw.org.nmrl.ept.repository.SchemeRepository;
import zw.org.nmrl.ept.service.SchemeService;
import zw.org.nmrl.ept.service.dto.SchemeDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.Scheme}.
 */
@RestController
@RequestMapping("/api/schemes")
public class SchemeResource {

    private static final Logger LOG = LoggerFactory.getLogger(SchemeResource.class);

    private static final String ENTITY_NAME = "scheme";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final SchemeService schemeService;

    private final SchemeRepository schemeRepository;

    public SchemeResource(SchemeService schemeService, SchemeRepository schemeRepository) {
        this.schemeService = schemeService;
        this.schemeRepository = schemeRepository;
    }

    /**
     * {@code POST  /schemes} : Create a new scheme.
     *
     * @param schemeDTO the schemeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new schemeDTO, or with status {@code 400 (Bad Request)} if the scheme has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SchemeDTO> createScheme(@Valid @RequestBody SchemeDTO schemeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Scheme : {}", schemeDTO);
        if (schemeDTO.getId() != null) {
            throw new BadRequestAlertException("A new scheme cannot already have an ID", ENTITY_NAME, "idexists");
        }
        schemeDTO = schemeService.save(schemeDTO);
        return ResponseEntity.created(new URI("/api/schemes/" + schemeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, schemeDTO.getId().toString()))
            .body(schemeDTO);
    }

    /**
     * {@code PUT  /schemes/:id} : Updates an existing scheme.
     *
     * @param id the id of the schemeDTO to save.
     * @param schemeDTO the schemeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated schemeDTO,
     * or with status {@code 400 (Bad Request)} if the schemeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the schemeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SchemeDTO> updateScheme(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SchemeDTO schemeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Scheme : {}, {}", id, schemeDTO);
        if (schemeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, schemeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!schemeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        schemeDTO = schemeService.update(schemeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, schemeDTO.getId().toString()))
            .body(schemeDTO);
    }

    /**
     * {@code PATCH  /schemes/:id} : Partial updates given fields of an existing scheme, field will ignore if it is null
     *
     * @param id the id of the schemeDTO to save.
     * @param schemeDTO the schemeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated schemeDTO,
     * or with status {@code 400 (Bad Request)} if the schemeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the schemeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the schemeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SchemeDTO> partialUpdateScheme(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SchemeDTO schemeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Scheme partially : {}, {}", id, schemeDTO);
        if (schemeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, schemeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!schemeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SchemeDTO> result = schemeService.partialUpdate(schemeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, schemeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /schemes} : get all the Schemes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Schemes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SchemeDTO>> getAllSchemes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Schemes");
        Page<SchemeDTO> page = schemeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /schemes/:id} : get the "id" scheme.
     *
     * @param id the id of the schemeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the schemeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SchemeDTO> getScheme(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Scheme : {}", id);
        Optional<SchemeDTO> schemeDTO = schemeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(schemeDTO);
    }

    /**
     * {@code DELETE  /schemes/:id} : delete the "id" scheme.
     *
     * @param id the id of the schemeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheme(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Scheme : {}", id);
        schemeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
