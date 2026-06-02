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
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.service.DataManagerService;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.DataManager}.
 */
@RestController
@RequestMapping("/api/data-managers")
public class DataManagerResource {

    private static final Logger LOG = LoggerFactory.getLogger(DataManagerResource.class);

    private static final String ENTITY_NAME = "dataManager";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final DataManagerService dataManagerService;

    private final DataManagerRepository dataManagerRepository;

    public DataManagerResource(DataManagerService dataManagerService, DataManagerRepository dataManagerRepository) {
        this.dataManagerService = dataManagerService;
        this.dataManagerRepository = dataManagerRepository;
    }

    /**
     * {@code POST  /data-managers} : Create a new dataManager.
     *
     * @param dataManagerDTO the dataManagerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dataManagerDTO, or with status {@code 400 (Bad Request)} if the dataManager has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DataManagerDTO> createDataManager(@Valid @RequestBody DataManagerDTO dataManagerDTO) throws URISyntaxException {
        LOG.debug("REST request to save DataManager : {}", dataManagerDTO);
        if (dataManagerDTO.getId() != null) {
            throw new BadRequestAlertException("A new dataManager cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dataManagerDTO = dataManagerService.save(dataManagerDTO);
        return ResponseEntity.created(new URI("/api/data-managers/" + dataManagerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dataManagerDTO.getId().toString()))
            .body(dataManagerDTO);
    }

    /**
     * {@code PUT  /data-managers/:id} : Updates an existing dataManager.
     *
     * @param id the id of the dataManagerDTO to save.
     * @param dataManagerDTO the dataManagerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataManagerDTO,
     * or with status {@code 400 (Bad Request)} if the dataManagerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dataManagerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DataManagerDTO> updateDataManager(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DataManagerDTO dataManagerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DataManager : {}, {}", id, dataManagerDTO);
        if (dataManagerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dataManagerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dataManagerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dataManagerDTO = dataManagerService.update(dataManagerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dataManagerDTO.getId().toString()))
            .body(dataManagerDTO);
    }

    /**
     * {@code PATCH  /data-managers/:id} : Partial updates given fields of an existing dataManager, field will ignore if it is null
     *
     * @param id the id of the dataManagerDTO to save.
     * @param dataManagerDTO the dataManagerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dataManagerDTO,
     * or with status {@code 400 (Bad Request)} if the dataManagerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dataManagerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dataManagerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DataManagerDTO> partialUpdateDataManager(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DataManagerDTO dataManagerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DataManager partially : {}, {}", id, dataManagerDTO);
        if (dataManagerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dataManagerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dataManagerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DataManagerDTO> result = dataManagerService.partialUpdate(dataManagerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dataManagerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /data-managers} : get all the Data Managers.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Data Managers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DataManagerDTO>> getAllDataManagers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of DataManagers");
        Page<DataManagerDTO> page;
        if (eagerload) {
            page = dataManagerService.findAllWithEagerRelationships(pageable);
        } else {
            page = dataManagerService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /data-managers/:id} : get the "id" dataManager.
     *
     * @param id the id of the dataManagerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dataManagerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DataManagerDTO> getDataManager(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DataManager : {}", id);
        Optional<DataManagerDTO> dataManagerDTO = dataManagerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dataManagerDTO);
    }

    /**
     * {@code DELETE  /data-managers/:id} : delete the "id" dataManager.
     *
     * @param id the id of the dataManagerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDataManager(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DataManager : {}", id);
        dataManagerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
