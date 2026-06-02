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
import zw.org.nmrl.ept.repository.ApiRequestLogRepository;
import zw.org.nmrl.ept.service.ApiRequestLogService;
import zw.org.nmrl.ept.service.dto.ApiRequestLogDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ApiRequestLog}.
 */
@RestController
@RequestMapping("/api/api-request-logs")
public class ApiRequestLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(ApiRequestLogResource.class);

    private static final String ENTITY_NAME = "apiRequestLog";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ApiRequestLogService apiRequestLogService;

    private final ApiRequestLogRepository apiRequestLogRepository;

    public ApiRequestLogResource(ApiRequestLogService apiRequestLogService, ApiRequestLogRepository apiRequestLogRepository) {
        this.apiRequestLogService = apiRequestLogService;
        this.apiRequestLogRepository = apiRequestLogRepository;
    }

    /**
     * {@code POST  /api-request-logs} : Create a new apiRequestLog.
     *
     * @param apiRequestLogDTO the apiRequestLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new apiRequestLogDTO, or with status {@code 400 (Bad Request)} if the apiRequestLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ApiRequestLogDTO> createApiRequestLog(@Valid @RequestBody ApiRequestLogDTO apiRequestLogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ApiRequestLog : {}", apiRequestLogDTO);
        if (apiRequestLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new apiRequestLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        apiRequestLogDTO = apiRequestLogService.save(apiRequestLogDTO);
        return ResponseEntity.created(new URI("/api/api-request-logs/" + apiRequestLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, apiRequestLogDTO.getId().toString()))
            .body(apiRequestLogDTO);
    }

    /**
     * {@code PUT  /api-request-logs/:id} : Updates an existing apiRequestLog.
     *
     * @param id the id of the apiRequestLogDTO to save.
     * @param apiRequestLogDTO the apiRequestLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apiRequestLogDTO,
     * or with status {@code 400 (Bad Request)} if the apiRequestLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the apiRequestLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiRequestLogDTO> updateApiRequestLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApiRequestLogDTO apiRequestLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ApiRequestLog : {}, {}", id, apiRequestLogDTO);
        if (apiRequestLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, apiRequestLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!apiRequestLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        apiRequestLogDTO = apiRequestLogService.update(apiRequestLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, apiRequestLogDTO.getId().toString()))
            .body(apiRequestLogDTO);
    }

    /**
     * {@code PATCH  /api-request-logs/:id} : Partial updates given fields of an existing apiRequestLog, field will ignore if it is null
     *
     * @param id the id of the apiRequestLogDTO to save.
     * @param apiRequestLogDTO the apiRequestLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apiRequestLogDTO,
     * or with status {@code 400 (Bad Request)} if the apiRequestLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the apiRequestLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the apiRequestLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApiRequestLogDTO> partialUpdateApiRequestLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApiRequestLogDTO apiRequestLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ApiRequestLog partially : {}, {}", id, apiRequestLogDTO);
        if (apiRequestLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, apiRequestLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!apiRequestLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApiRequestLogDTO> result = apiRequestLogService.partialUpdate(apiRequestLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, apiRequestLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /api-request-logs} : get all the Api Request Logs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Api Request Logs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ApiRequestLogDTO>> getAllApiRequestLogs(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ApiRequestLogs");
        Page<ApiRequestLogDTO> page = apiRequestLogService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /api-request-logs/:id} : get the "id" apiRequestLog.
     *
     * @param id the id of the apiRequestLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the apiRequestLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiRequestLogDTO> getApiRequestLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ApiRequestLog : {}", id);
        Optional<ApiRequestLogDTO> apiRequestLogDTO = apiRequestLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(apiRequestLogDTO);
    }

    /**
     * {@code DELETE  /api-request-logs/:id} : delete the "id" apiRequestLog.
     *
     * @param id the id of the apiRequestLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApiRequestLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ApiRequestLog : {}", id);
        apiRequestLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
