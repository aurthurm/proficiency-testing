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
import zw.org.nmrl.ept.repository.ShipmentSampleRepository;
import zw.org.nmrl.ept.service.ShipmentSampleService;
import zw.org.nmrl.ept.service.dto.ShipmentSampleDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ShipmentSample}.
 */
@RestController
@RequestMapping("/api/shipment-samples")
public class ShipmentSampleResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentSampleResource.class);

    private static final String ENTITY_NAME = "shipmentSample";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ShipmentSampleService shipmentSampleService;

    private final ShipmentSampleRepository shipmentSampleRepository;

    public ShipmentSampleResource(ShipmentSampleService shipmentSampleService, ShipmentSampleRepository shipmentSampleRepository) {
        this.shipmentSampleService = shipmentSampleService;
        this.shipmentSampleRepository = shipmentSampleRepository;
    }

    /**
     * {@code POST  /shipment-samples} : Create a new shipmentSample.
     *
     * @param shipmentSampleDTO the shipmentSampleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shipmentSampleDTO, or with status {@code 400 (Bad Request)} if the shipmentSample has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShipmentSampleDTO> createShipmentSample(@Valid @RequestBody ShipmentSampleDTO shipmentSampleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ShipmentSample : {}", shipmentSampleDTO);
        if (shipmentSampleDTO.getId() != null) {
            throw new BadRequestAlertException("A new shipmentSample cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shipmentSampleDTO = shipmentSampleService.save(shipmentSampleDTO);
        return ResponseEntity.created(new URI("/api/shipment-samples/" + shipmentSampleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shipmentSampleDTO.getId().toString()))
            .body(shipmentSampleDTO);
    }

    /**
     * {@code PUT  /shipment-samples/:id} : Updates an existing shipmentSample.
     *
     * @param id the id of the shipmentSampleDTO to save.
     * @param shipmentSampleDTO the shipmentSampleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentSampleDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentSampleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shipmentSampleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentSampleDTO> updateShipmentSample(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShipmentSampleDTO shipmentSampleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ShipmentSample : {}, {}", id, shipmentSampleDTO);
        if (shipmentSampleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentSampleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentSampleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shipmentSampleDTO = shipmentSampleService.update(shipmentSampleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shipmentSampleDTO.getId().toString()))
            .body(shipmentSampleDTO);
    }

    /**
     * {@code PATCH  /shipment-samples/:id} : Partial updates given fields of an existing shipmentSample, field will ignore if it is null
     *
     * @param id the id of the shipmentSampleDTO to save.
     * @param shipmentSampleDTO the shipmentSampleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentSampleDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentSampleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shipmentSampleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shipmentSampleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShipmentSampleDTO> partialUpdateShipmentSample(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShipmentSampleDTO shipmentSampleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ShipmentSample partially : {}, {}", id, shipmentSampleDTO);
        if (shipmentSampleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentSampleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentSampleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShipmentSampleDTO> result = shipmentSampleService.partialUpdate(shipmentSampleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shipmentSampleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shipment-samples} : get all the Shipment Samples.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Shipment Samples in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ShipmentSampleDTO>> getAllShipmentSamples(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ShipmentSamples");
        Page<ShipmentSampleDTO> page = shipmentSampleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shipment-samples/:id} : get the "id" shipmentSample.
     *
     * @param id the id of the shipmentSampleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shipmentSampleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentSampleDTO> getShipmentSample(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ShipmentSample : {}", id);
        Optional<ShipmentSampleDTO> shipmentSampleDTO = shipmentSampleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shipmentSampleDTO);
    }

    /**
     * {@code DELETE  /shipment-samples/:id} : delete the "id" shipmentSample.
     *
     * @param id the id of the shipmentSampleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipmentSample(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ShipmentSample : {}", id);
        shipmentSampleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
