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
import zw.org.nmrl.ept.repository.CertificateBatchRepository;
import zw.org.nmrl.ept.service.CertificateBatchService;
import zw.org.nmrl.ept.service.dto.CertificateBatchDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.CertificateBatch}.
 */
@RestController
@RequestMapping("/api/certificate-batches")
public class CertificateBatchResource {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateBatchResource.class);

    private static final String ENTITY_NAME = "certificateBatch";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final CertificateBatchService certificateBatchService;

    private final CertificateBatchRepository certificateBatchRepository;

    public CertificateBatchResource(
        CertificateBatchService certificateBatchService,
        CertificateBatchRepository certificateBatchRepository
    ) {
        this.certificateBatchService = certificateBatchService;
        this.certificateBatchRepository = certificateBatchRepository;
    }

    /**
     * {@code POST  /certificate-batches} : Create a new certificateBatch.
     *
     * @param certificateBatchDTO the certificateBatchDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certificateBatchDTO, or with status {@code 400 (Bad Request)} if the certificateBatch has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CertificateBatchDTO> createCertificateBatch(@Valid @RequestBody CertificateBatchDTO certificateBatchDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CertificateBatch : {}", certificateBatchDTO);
        if (certificateBatchDTO.getId() != null) {
            throw new BadRequestAlertException("A new certificateBatch cannot already have an ID", ENTITY_NAME, "idexists");
        }
        certificateBatchDTO = certificateBatchService.save(certificateBatchDTO);
        return ResponseEntity.created(new URI("/api/certificate-batches/" + certificateBatchDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, certificateBatchDTO.getId().toString()))
            .body(certificateBatchDTO);
    }

    /**
     * {@code PUT  /certificate-batches/:id} : Updates an existing certificateBatch.
     *
     * @param id the id of the certificateBatchDTO to save.
     * @param certificateBatchDTO the certificateBatchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificateBatchDTO,
     * or with status {@code 400 (Bad Request)} if the certificateBatchDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certificateBatchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CertificateBatchDTO> updateCertificateBatch(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CertificateBatchDTO certificateBatchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CertificateBatch : {}, {}", id, certificateBatchDTO);
        if (certificateBatchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificateBatchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificateBatchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        certificateBatchDTO = certificateBatchService.update(certificateBatchDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificateBatchDTO.getId().toString()))
            .body(certificateBatchDTO);
    }

    /**
     * {@code PATCH  /certificate-batches/:id} : Partial updates given fields of an existing certificateBatch, field will ignore if it is null
     *
     * @param id the id of the certificateBatchDTO to save.
     * @param certificateBatchDTO the certificateBatchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificateBatchDTO,
     * or with status {@code 400 (Bad Request)} if the certificateBatchDTO is not valid,
     * or with status {@code 404 (Not Found)} if the certificateBatchDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the certificateBatchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CertificateBatchDTO> partialUpdateCertificateBatch(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CertificateBatchDTO certificateBatchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CertificateBatch partially : {}, {}", id, certificateBatchDTO);
        if (certificateBatchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificateBatchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificateBatchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CertificateBatchDTO> result = certificateBatchService.partialUpdate(certificateBatchDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificateBatchDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /certificate-batches} : get all the Certificate Batches.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Certificate Batches in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CertificateBatchDTO>> getAllCertificateBatches(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of CertificateBatches");
        Page<CertificateBatchDTO> page;
        if (eagerload) {
            page = certificateBatchService.findAllWithEagerRelationships(pageable);
        } else {
            page = certificateBatchService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /certificate-batches/:id} : get the "id" certificateBatch.
     *
     * @param id the id of the certificateBatchDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certificateBatchDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CertificateBatchDTO> getCertificateBatch(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CertificateBatch : {}", id);
        Optional<CertificateBatchDTO> certificateBatchDTO = certificateBatchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(certificateBatchDTO);
    }

    /**
     * {@code DELETE  /certificate-batches/:id} : delete the "id" certificateBatch.
     *
     * @param id the id of the certificateBatchDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificateBatch(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CertificateBatch : {}", id);
        certificateBatchService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
