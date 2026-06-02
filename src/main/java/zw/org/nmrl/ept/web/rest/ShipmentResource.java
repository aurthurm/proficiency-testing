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
import zw.org.nmrl.ept.repository.ShipmentRepository;
import zw.org.nmrl.ept.service.ShipmentQueryService;
import zw.org.nmrl.ept.service.ShipmentService;
import zw.org.nmrl.ept.service.criteria.ShipmentCriteria;
import zw.org.nmrl.ept.service.dto.ShipmentDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.Shipment}.
 */
@RestController
@RequestMapping("/api/shipments")
public class ShipmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentResource.class);

    private static final String ENTITY_NAME = "shipment";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ShipmentService shipmentService;

    private final ShipmentRepository shipmentRepository;

    private final ShipmentQueryService shipmentQueryService;

    public ShipmentResource(
        ShipmentService shipmentService,
        ShipmentRepository shipmentRepository,
        ShipmentQueryService shipmentQueryService
    ) {
        this.shipmentService = shipmentService;
        this.shipmentRepository = shipmentRepository;
        this.shipmentQueryService = shipmentQueryService;
    }

    /**
     * {@code POST  /shipments} : Create a new shipment.
     *
     * @param shipmentDTO the shipmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shipmentDTO, or with status {@code 400 (Bad Request)} if the shipment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShipmentDTO> createShipment(@Valid @RequestBody ShipmentDTO shipmentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Shipment : {}", shipmentDTO);
        if (shipmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new shipment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shipmentDTO = shipmentService.save(shipmentDTO);
        return ResponseEntity.created(new URI("/api/shipments/" + shipmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shipmentDTO.getId().toString()))
            .body(shipmentDTO);
    }

    /**
     * {@code PUT  /shipments/:id} : Updates an existing shipment.
     *
     * @param id the id of the shipmentDTO to save.
     * @param shipmentDTO the shipmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shipmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentDTO> updateShipment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShipmentDTO shipmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Shipment : {}, {}", id, shipmentDTO);
        if (shipmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shipmentDTO = shipmentService.update(shipmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shipmentDTO.getId().toString()))
            .body(shipmentDTO);
    }

    /**
     * {@code PATCH  /shipments/:id} : Partial updates given fields of an existing shipment, field will ignore if it is null
     *
     * @param id the id of the shipmentDTO to save.
     * @param shipmentDTO the shipmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shipmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shipmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShipmentDTO> partialUpdateShipment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShipmentDTO shipmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Shipment partially : {}, {}", id, shipmentDTO);
        if (shipmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShipmentDTO> result = shipmentService.partialUpdate(shipmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shipmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shipments} : get all the Shipments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Shipments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ShipmentDTO>> getAllShipments(
        ShipmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Shipments by criteria: {}", criteria);

        Page<ShipmentDTO> page = shipmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shipments/count} : count all the shipments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countShipments(ShipmentCriteria criteria) {
        LOG.debug("REST request to count Shipments by criteria: {}", criteria);
        return ResponseEntity.ok().body(shipmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shipments/:id} : get the "id" shipment.
     *
     * @param id the id of the shipmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shipmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDTO> getShipment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Shipment : {}", id);
        Optional<ShipmentDTO> shipmentDTO = shipmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shipmentDTO);
    }

    /**
     * {@code DELETE  /shipments/:id} : delete the "id" shipment.
     *
     * @param id the id of the shipmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Shipment : {}", id);
        shipmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
