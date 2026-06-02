package zw.org.nmrl.ept.web.rest;

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
import zw.org.nmrl.ept.repository.ReportConfigurationRepository;
import zw.org.nmrl.ept.service.ReportConfigurationService;
import zw.org.nmrl.ept.service.dto.ReportConfigurationDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ReportConfiguration}.
 */
@RestController
@RequestMapping("/api/report-configurations")
public class ReportConfigurationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReportConfigurationResource.class);

    private static final String ENTITY_NAME = "reportConfiguration";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ReportConfigurationService reportConfigurationService;

    private final ReportConfigurationRepository reportConfigurationRepository;

    public ReportConfigurationResource(
        ReportConfigurationService reportConfigurationService,
        ReportConfigurationRepository reportConfigurationRepository
    ) {
        this.reportConfigurationService = reportConfigurationService;
        this.reportConfigurationRepository = reportConfigurationRepository;
    }

    /**
     * {@code POST  /report-configurations} : Create a new reportConfiguration.
     *
     * @param reportConfigurationDTO the reportConfigurationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reportConfigurationDTO, or with status {@code 400 (Bad Request)} if the reportConfiguration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReportConfigurationDTO> createReportConfiguration(@RequestBody ReportConfigurationDTO reportConfigurationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ReportConfiguration : {}", reportConfigurationDTO);
        if (reportConfigurationDTO.getId() != null) {
            throw new BadRequestAlertException("A new reportConfiguration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        reportConfigurationDTO = reportConfigurationService.save(reportConfigurationDTO);
        return ResponseEntity.created(new URI("/api/report-configurations/" + reportConfigurationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, reportConfigurationDTO.getId().toString()))
            .body(reportConfigurationDTO);
    }

    /**
     * {@code PUT  /report-configurations/:id} : Updates an existing reportConfiguration.
     *
     * @param id the id of the reportConfigurationDTO to save.
     * @param reportConfigurationDTO the reportConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reportConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the reportConfigurationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reportConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReportConfigurationDTO> updateReportConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReportConfigurationDTO reportConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReportConfiguration : {}, {}", id, reportConfigurationDTO);
        if (reportConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reportConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reportConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        reportConfigurationDTO = reportConfigurationService.update(reportConfigurationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reportConfigurationDTO.getId().toString()))
            .body(reportConfigurationDTO);
    }

    /**
     * {@code PATCH  /report-configurations/:id} : Partial updates given fields of an existing reportConfiguration, field will ignore if it is null
     *
     * @param id the id of the reportConfigurationDTO to save.
     * @param reportConfigurationDTO the reportConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reportConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the reportConfigurationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reportConfigurationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reportConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReportConfigurationDTO> partialUpdateReportConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReportConfigurationDTO reportConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReportConfiguration partially : {}, {}", id, reportConfigurationDTO);
        if (reportConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reportConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reportConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReportConfigurationDTO> result = reportConfigurationService.partialUpdate(reportConfigurationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reportConfigurationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /report-configurations} : get all the Report Configurations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Report Configurations in body.
     */
    @GetMapping("")
    public List<ReportConfigurationDTO> getAllReportConfigurations() {
        LOG.debug("REST request to get all ReportConfigurations");
        return reportConfigurationService.findAll();
    }

    /**
     * {@code GET  /report-configurations/:id} : get the "id" reportConfiguration.
     *
     * @param id the id of the reportConfigurationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reportConfigurationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportConfigurationDTO> getReportConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReportConfiguration : {}", id);
        Optional<ReportConfigurationDTO> reportConfigurationDTO = reportConfigurationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reportConfigurationDTO);
    }

    /**
     * {@code DELETE  /report-configurations/:id} : delete the "id" reportConfiguration.
     *
     * @param id the id of the reportConfigurationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReportConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReportConfiguration : {}", id);
        reportConfigurationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
