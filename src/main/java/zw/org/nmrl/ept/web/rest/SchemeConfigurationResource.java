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
import zw.org.nmrl.ept.repository.SchemeConfigurationRepository;
import zw.org.nmrl.ept.service.SchemeConfigurationService;
import zw.org.nmrl.ept.service.dto.SchemeConfigurationDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.SchemeConfiguration}.
 */
@RestController
@RequestMapping("/api/scheme-configurations")
public class SchemeConfigurationResource {

    private static final Logger LOG = LoggerFactory.getLogger(SchemeConfigurationResource.class);

    private static final String ENTITY_NAME = "schemeConfiguration";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final SchemeConfigurationService schemeConfigurationService;

    private final SchemeConfigurationRepository schemeConfigurationRepository;

    public SchemeConfigurationResource(
        SchemeConfigurationService schemeConfigurationService,
        SchemeConfigurationRepository schemeConfigurationRepository
    ) {
        this.schemeConfigurationService = schemeConfigurationService;
        this.schemeConfigurationRepository = schemeConfigurationRepository;
    }

    /**
     * {@code POST  /scheme-configurations} : Create a new schemeConfiguration.
     *
     * @param schemeConfigurationDTO the schemeConfigurationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new schemeConfigurationDTO, or with status {@code 400 (Bad Request)} if the schemeConfiguration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SchemeConfigurationDTO> createSchemeConfiguration(
        @Valid @RequestBody SchemeConfigurationDTO schemeConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save SchemeConfiguration : {}", schemeConfigurationDTO);
        if (schemeConfigurationDTO.getId() != null) {
            throw new BadRequestAlertException("A new schemeConfiguration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        schemeConfigurationDTO = schemeConfigurationService.save(schemeConfigurationDTO);
        return ResponseEntity.created(new URI("/api/scheme-configurations/" + schemeConfigurationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, schemeConfigurationDTO.getId().toString()))
            .body(schemeConfigurationDTO);
    }

    /**
     * {@code PUT  /scheme-configurations/:id} : Updates an existing schemeConfiguration.
     *
     * @param id the id of the schemeConfigurationDTO to save.
     * @param schemeConfigurationDTO the schemeConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated schemeConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the schemeConfigurationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the schemeConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SchemeConfigurationDTO> updateSchemeConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SchemeConfigurationDTO schemeConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SchemeConfiguration : {}, {}", id, schemeConfigurationDTO);
        if (schemeConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, schemeConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!schemeConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        schemeConfigurationDTO = schemeConfigurationService.update(schemeConfigurationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, schemeConfigurationDTO.getId().toString()))
            .body(schemeConfigurationDTO);
    }

    /**
     * {@code PATCH  /scheme-configurations/:id} : Partial updates given fields of an existing schemeConfiguration, field will ignore if it is null
     *
     * @param id the id of the schemeConfigurationDTO to save.
     * @param schemeConfigurationDTO the schemeConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated schemeConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the schemeConfigurationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the schemeConfigurationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the schemeConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SchemeConfigurationDTO> partialUpdateSchemeConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SchemeConfigurationDTO schemeConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SchemeConfiguration partially : {}, {}", id, schemeConfigurationDTO);
        if (schemeConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, schemeConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!schemeConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SchemeConfigurationDTO> result = schemeConfigurationService.partialUpdate(schemeConfigurationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, schemeConfigurationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /scheme-configurations} : get all the Scheme Configurations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Scheme Configurations in body.
     */
    @GetMapping("")
    public List<SchemeConfigurationDTO> getAllSchemeConfigurations() {
        LOG.debug("REST request to get all SchemeConfigurations");
        return schemeConfigurationService.findAll();
    }

    /**
     * {@code GET  /scheme-configurations/:id} : get the "id" schemeConfiguration.
     *
     * @param id the id of the schemeConfigurationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the schemeConfigurationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SchemeConfigurationDTO> getSchemeConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SchemeConfiguration : {}", id);
        Optional<SchemeConfigurationDTO> schemeConfigurationDTO = schemeConfigurationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(schemeConfigurationDTO);
    }

    /**
     * {@code DELETE  /scheme-configurations/:id} : delete the "id" schemeConfiguration.
     *
     * @param id the id of the schemeConfigurationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchemeConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SchemeConfiguration : {}", id);
        schemeConfigurationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
