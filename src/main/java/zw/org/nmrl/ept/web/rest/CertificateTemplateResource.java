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
import zw.org.nmrl.ept.repository.CertificateTemplateRepository;
import zw.org.nmrl.ept.service.CertificateTemplateService;
import zw.org.nmrl.ept.service.dto.CertificateTemplateDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.CertificateTemplate}.
 */
@RestController
@RequestMapping("/api/certificate-templates")
public class CertificateTemplateResource {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateTemplateResource.class);

    private static final String ENTITY_NAME = "certificateTemplate";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final CertificateTemplateService certificateTemplateService;

    private final CertificateTemplateRepository certificateTemplateRepository;

    public CertificateTemplateResource(
        CertificateTemplateService certificateTemplateService,
        CertificateTemplateRepository certificateTemplateRepository
    ) {
        this.certificateTemplateService = certificateTemplateService;
        this.certificateTemplateRepository = certificateTemplateRepository;
    }

    /**
     * {@code POST  /certificate-templates} : Create a new certificateTemplate.
     *
     * @param certificateTemplateDTO the certificateTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certificateTemplateDTO, or with status {@code 400 (Bad Request)} if the certificateTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CertificateTemplateDTO> createCertificateTemplate(
        @Valid @RequestBody CertificateTemplateDTO certificateTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save CertificateTemplate : {}", certificateTemplateDTO);
        if (certificateTemplateDTO.getId() != null) {
            throw new BadRequestAlertException("A new certificateTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        certificateTemplateDTO = certificateTemplateService.save(certificateTemplateDTO);
        return ResponseEntity.created(new URI("/api/certificate-templates/" + certificateTemplateDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, certificateTemplateDTO.getId().toString()))
            .body(certificateTemplateDTO);
    }

    /**
     * {@code PUT  /certificate-templates/:id} : Updates an existing certificateTemplate.
     *
     * @param id the id of the certificateTemplateDTO to save.
     * @param certificateTemplateDTO the certificateTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificateTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the certificateTemplateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certificateTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CertificateTemplateDTO> updateCertificateTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CertificateTemplateDTO certificateTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CertificateTemplate : {}, {}", id, certificateTemplateDTO);
        if (certificateTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificateTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificateTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        certificateTemplateDTO = certificateTemplateService.update(certificateTemplateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificateTemplateDTO.getId().toString()))
            .body(certificateTemplateDTO);
    }

    /**
     * {@code PATCH  /certificate-templates/:id} : Partial updates given fields of an existing certificateTemplate, field will ignore if it is null
     *
     * @param id the id of the certificateTemplateDTO to save.
     * @param certificateTemplateDTO the certificateTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificateTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the certificateTemplateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the certificateTemplateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the certificateTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CertificateTemplateDTO> partialUpdateCertificateTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CertificateTemplateDTO certificateTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CertificateTemplate partially : {}, {}", id, certificateTemplateDTO);
        if (certificateTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificateTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificateTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CertificateTemplateDTO> result = certificateTemplateService.partialUpdate(certificateTemplateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificateTemplateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /certificate-templates} : get all the Certificate Templates.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Certificate Templates in body.
     */
    @GetMapping("")
    public List<CertificateTemplateDTO> getAllCertificateTemplates(@RequestParam(name = "filter", required = false) String filter) {
        if ("scheme-is-null".equals(filter)) {
            LOG.debug("REST request to get all CertificateTemplates where scheme is null");
            return certificateTemplateService.findAllWhereSchemeIsNull();
        }
        LOG.debug("REST request to get all CertificateTemplates");
        return certificateTemplateService.findAll();
    }

    /**
     * {@code GET  /certificate-templates/:id} : get the "id" certificateTemplate.
     *
     * @param id the id of the certificateTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certificateTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CertificateTemplateDTO> getCertificateTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CertificateTemplate : {}", id);
        Optional<CertificateTemplateDTO> certificateTemplateDTO = certificateTemplateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(certificateTemplateDTO);
    }

    /**
     * {@code DELETE  /certificate-templates/:id} : delete the "id" certificateTemplate.
     *
     * @param id the id of the certificateTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificateTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CertificateTemplate : {}", id);
        certificateTemplateService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
