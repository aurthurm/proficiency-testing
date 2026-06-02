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
import zw.org.nmrl.ept.repository.ModeOfReceiptRepository;
import zw.org.nmrl.ept.service.ModeOfReceiptService;
import zw.org.nmrl.ept.service.dto.ModeOfReceiptDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ModeOfReceipt}.
 */
@RestController
@RequestMapping("/api/mode-of-receipts")
public class ModeOfReceiptResource {

    private static final Logger LOG = LoggerFactory.getLogger(ModeOfReceiptResource.class);

    private static final String ENTITY_NAME = "modeOfReceipt";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ModeOfReceiptService modeOfReceiptService;

    private final ModeOfReceiptRepository modeOfReceiptRepository;

    public ModeOfReceiptResource(ModeOfReceiptService modeOfReceiptService, ModeOfReceiptRepository modeOfReceiptRepository) {
        this.modeOfReceiptService = modeOfReceiptService;
        this.modeOfReceiptRepository = modeOfReceiptRepository;
    }

    /**
     * {@code POST  /mode-of-receipts} : Create a new modeOfReceipt.
     *
     * @param modeOfReceiptDTO the modeOfReceiptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new modeOfReceiptDTO, or with status {@code 400 (Bad Request)} if the modeOfReceipt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ModeOfReceiptDTO> createModeOfReceipt(@Valid @RequestBody ModeOfReceiptDTO modeOfReceiptDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ModeOfReceipt : {}", modeOfReceiptDTO);
        if (modeOfReceiptDTO.getId() != null) {
            throw new BadRequestAlertException("A new modeOfReceipt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        modeOfReceiptDTO = modeOfReceiptService.save(modeOfReceiptDTO);
        return ResponseEntity.created(new URI("/api/mode-of-receipts/" + modeOfReceiptDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, modeOfReceiptDTO.getId().toString()))
            .body(modeOfReceiptDTO);
    }

    /**
     * {@code PUT  /mode-of-receipts/:id} : Updates an existing modeOfReceipt.
     *
     * @param id the id of the modeOfReceiptDTO to save.
     * @param modeOfReceiptDTO the modeOfReceiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modeOfReceiptDTO,
     * or with status {@code 400 (Bad Request)} if the modeOfReceiptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the modeOfReceiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ModeOfReceiptDTO> updateModeOfReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ModeOfReceiptDTO modeOfReceiptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ModeOfReceipt : {}, {}", id, modeOfReceiptDTO);
        if (modeOfReceiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modeOfReceiptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!modeOfReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        modeOfReceiptDTO = modeOfReceiptService.update(modeOfReceiptDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, modeOfReceiptDTO.getId().toString()))
            .body(modeOfReceiptDTO);
    }

    /**
     * {@code PATCH  /mode-of-receipts/:id} : Partial updates given fields of an existing modeOfReceipt, field will ignore if it is null
     *
     * @param id the id of the modeOfReceiptDTO to save.
     * @param modeOfReceiptDTO the modeOfReceiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modeOfReceiptDTO,
     * or with status {@code 400 (Bad Request)} if the modeOfReceiptDTO is not valid,
     * or with status {@code 404 (Not Found)} if the modeOfReceiptDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the modeOfReceiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ModeOfReceiptDTO> partialUpdateModeOfReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ModeOfReceiptDTO modeOfReceiptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ModeOfReceipt partially : {}, {}", id, modeOfReceiptDTO);
        if (modeOfReceiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modeOfReceiptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!modeOfReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ModeOfReceiptDTO> result = modeOfReceiptService.partialUpdate(modeOfReceiptDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, modeOfReceiptDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mode-of-receipts} : get all the Mode Of Receipts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mode Of Receipts in body.
     */
    @GetMapping("")
    public List<ModeOfReceiptDTO> getAllModeOfReceipts() {
        LOG.debug("REST request to get all ModeOfReceipts");
        return modeOfReceiptService.findAll();
    }

    /**
     * {@code GET  /mode-of-receipts/:id} : get the "id" modeOfReceipt.
     *
     * @param id the id of the modeOfReceiptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the modeOfReceiptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModeOfReceiptDTO> getModeOfReceipt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ModeOfReceipt : {}", id);
        Optional<ModeOfReceiptDTO> modeOfReceiptDTO = modeOfReceiptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(modeOfReceiptDTO);
    }

    /**
     * {@code DELETE  /mode-of-receipts/:id} : delete the "id" modeOfReceipt.
     *
     * @param id the id of the modeOfReceiptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModeOfReceipt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ModeOfReceipt : {}", id);
        modeOfReceiptService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
