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
import zw.org.nmrl.ept.repository.CustomFieldDefinitionRepository;
import zw.org.nmrl.ept.service.CustomFieldDefinitionService;
import zw.org.nmrl.ept.service.dto.CustomFieldDefinitionDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.CustomFieldDefinition}.
 */
@RestController
@RequestMapping("/api/custom-field-definitions")
public class CustomFieldDefinitionResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFieldDefinitionResource.class);

    private static final String ENTITY_NAME = "customFieldDefinition";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final CustomFieldDefinitionService customFieldDefinitionService;

    private final CustomFieldDefinitionRepository customFieldDefinitionRepository;

    public CustomFieldDefinitionResource(
        CustomFieldDefinitionService customFieldDefinitionService,
        CustomFieldDefinitionRepository customFieldDefinitionRepository
    ) {
        this.customFieldDefinitionService = customFieldDefinitionService;
        this.customFieldDefinitionRepository = customFieldDefinitionRepository;
    }

    /**
     * {@code POST  /custom-field-definitions} : Create a new customFieldDefinition.
     *
     * @param customFieldDefinitionDTO the customFieldDefinitionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customFieldDefinitionDTO, or with status {@code 400 (Bad Request)} if the customFieldDefinition has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomFieldDefinitionDTO> createCustomFieldDefinition(
        @Valid @RequestBody CustomFieldDefinitionDTO customFieldDefinitionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save CustomFieldDefinition : {}", customFieldDefinitionDTO);
        if (customFieldDefinitionDTO.getId() != null) {
            throw new BadRequestAlertException("A new customFieldDefinition cannot already have an ID", ENTITY_NAME, "idexists");
        }
        customFieldDefinitionDTO = customFieldDefinitionService.save(customFieldDefinitionDTO);
        return ResponseEntity.created(new URI("/api/custom-field-definitions/" + customFieldDefinitionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, customFieldDefinitionDTO.getId().toString()))
            .body(customFieldDefinitionDTO);
    }

    /**
     * {@code PUT  /custom-field-definitions/:id} : Updates an existing customFieldDefinition.
     *
     * @param id the id of the customFieldDefinitionDTO to save.
     * @param customFieldDefinitionDTO the customFieldDefinitionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customFieldDefinitionDTO,
     * or with status {@code 400 (Bad Request)} if the customFieldDefinitionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customFieldDefinitionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomFieldDefinitionDTO> updateCustomFieldDefinition(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomFieldDefinitionDTO customFieldDefinitionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CustomFieldDefinition : {}, {}", id, customFieldDefinitionDTO);
        if (customFieldDefinitionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customFieldDefinitionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customFieldDefinitionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        customFieldDefinitionDTO = customFieldDefinitionService.update(customFieldDefinitionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customFieldDefinitionDTO.getId().toString()))
            .body(customFieldDefinitionDTO);
    }

    /**
     * {@code PATCH  /custom-field-definitions/:id} : Partial updates given fields of an existing customFieldDefinition, field will ignore if it is null
     *
     * @param id the id of the customFieldDefinitionDTO to save.
     * @param customFieldDefinitionDTO the customFieldDefinitionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customFieldDefinitionDTO,
     * or with status {@code 400 (Bad Request)} if the customFieldDefinitionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customFieldDefinitionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customFieldDefinitionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CustomFieldDefinitionDTO> partialUpdateCustomFieldDefinition(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CustomFieldDefinitionDTO customFieldDefinitionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CustomFieldDefinition partially : {}, {}", id, customFieldDefinitionDTO);
        if (customFieldDefinitionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customFieldDefinitionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customFieldDefinitionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CustomFieldDefinitionDTO> result = customFieldDefinitionService.partialUpdate(customFieldDefinitionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customFieldDefinitionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /custom-field-definitions} : get all the Custom Field Definitions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Custom Field Definitions in body.
     */
    @GetMapping("")
    public List<CustomFieldDefinitionDTO> getAllCustomFieldDefinitions() {
        LOG.debug("REST request to get all CustomFieldDefinitions");
        return customFieldDefinitionService.findAll();
    }

    /**
     * {@code GET  /custom-field-definitions/:id} : get the "id" customFieldDefinition.
     *
     * @param id the id of the customFieldDefinitionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customFieldDefinitionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomFieldDefinitionDTO> getCustomFieldDefinition(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CustomFieldDefinition : {}", id);
        Optional<CustomFieldDefinitionDTO> customFieldDefinitionDTO = customFieldDefinitionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customFieldDefinitionDTO);
    }

    /**
     * {@code DELETE  /custom-field-definitions/:id} : delete the "id" customFieldDefinition.
     *
     * @param id the id of the customFieldDefinitionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomFieldDefinition(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CustomFieldDefinition : {}", id);
        customFieldDefinitionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
