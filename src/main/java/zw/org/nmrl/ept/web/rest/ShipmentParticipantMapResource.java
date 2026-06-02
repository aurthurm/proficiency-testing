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
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;
import zw.org.nmrl.ept.service.ShipmentParticipantMapQueryService;
import zw.org.nmrl.ept.service.ShipmentParticipantMapService;
import zw.org.nmrl.ept.service.criteria.ShipmentParticipantMapCriteria;
import zw.org.nmrl.ept.service.dto.ShipmentParticipantMapDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.ShipmentParticipantMap}.
 */
@RestController
@RequestMapping("/api/shipment-participant-maps")
public class ShipmentParticipantMapResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentParticipantMapResource.class);

    private static final String ENTITY_NAME = "shipmentParticipantMap";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final ShipmentParticipantMapService shipmentParticipantMapService;

    private final ShipmentParticipantMapRepository shipmentParticipantMapRepository;

    private final ShipmentParticipantMapQueryService shipmentParticipantMapQueryService;

    public ShipmentParticipantMapResource(
        ShipmentParticipantMapService shipmentParticipantMapService,
        ShipmentParticipantMapRepository shipmentParticipantMapRepository,
        ShipmentParticipantMapQueryService shipmentParticipantMapQueryService
    ) {
        this.shipmentParticipantMapService = shipmentParticipantMapService;
        this.shipmentParticipantMapRepository = shipmentParticipantMapRepository;
        this.shipmentParticipantMapQueryService = shipmentParticipantMapQueryService;
    }

    /**
     * {@code POST  /shipment-participant-maps} : Create a new shipmentParticipantMap.
     *
     * @param shipmentParticipantMapDTO the shipmentParticipantMapDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shipmentParticipantMapDTO, or with status {@code 400 (Bad Request)} if the shipmentParticipantMap has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShipmentParticipantMapDTO> createShipmentParticipantMap(
        @Valid @RequestBody ShipmentParticipantMapDTO shipmentParticipantMapDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ShipmentParticipantMap : {}", shipmentParticipantMapDTO);
        if (shipmentParticipantMapDTO.getId() != null) {
            throw new BadRequestAlertException("A new shipmentParticipantMap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shipmentParticipantMapDTO = shipmentParticipantMapService.save(shipmentParticipantMapDTO);
        return ResponseEntity.created(new URI("/api/shipment-participant-maps/" + shipmentParticipantMapDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shipmentParticipantMapDTO.getId().toString()))
            .body(shipmentParticipantMapDTO);
    }

    /**
     * {@code PUT  /shipment-participant-maps/:id} : Updates an existing shipmentParticipantMap.
     *
     * @param id the id of the shipmentParticipantMapDTO to save.
     * @param shipmentParticipantMapDTO the shipmentParticipantMapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentParticipantMapDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentParticipantMapDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shipmentParticipantMapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentParticipantMapDTO> updateShipmentParticipantMap(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShipmentParticipantMapDTO shipmentParticipantMapDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ShipmentParticipantMap : {}, {}", id, shipmentParticipantMapDTO);
        if (shipmentParticipantMapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentParticipantMapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentParticipantMapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shipmentParticipantMapDTO = shipmentParticipantMapService.update(shipmentParticipantMapDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shipmentParticipantMapDTO.getId().toString()))
            .body(shipmentParticipantMapDTO);
    }

    /**
     * {@code PATCH  /shipment-participant-maps/:id} : Partial updates given fields of an existing shipmentParticipantMap, field will ignore if it is null
     *
     * @param id the id of the shipmentParticipantMapDTO to save.
     * @param shipmentParticipantMapDTO the shipmentParticipantMapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shipmentParticipantMapDTO,
     * or with status {@code 400 (Bad Request)} if the shipmentParticipantMapDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shipmentParticipantMapDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shipmentParticipantMapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShipmentParticipantMapDTO> partialUpdateShipmentParticipantMap(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShipmentParticipantMapDTO shipmentParticipantMapDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ShipmentParticipantMap partially : {}, {}", id, shipmentParticipantMapDTO);
        if (shipmentParticipantMapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shipmentParticipantMapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shipmentParticipantMapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShipmentParticipantMapDTO> result = shipmentParticipantMapService.partialUpdate(shipmentParticipantMapDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shipmentParticipantMapDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shipment-participant-maps} : get all the Shipment Participant Maps.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Shipment Participant Maps in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ShipmentParticipantMapDTO>> getAllShipmentParticipantMaps(
        ShipmentParticipantMapCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ShipmentParticipantMaps by criteria: {}", criteria);

        Page<ShipmentParticipantMapDTO> page = shipmentParticipantMapQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shipment-participant-maps/count} : count all the shipmentParticipantMaps.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countShipmentParticipantMaps(ShipmentParticipantMapCriteria criteria) {
        LOG.debug("REST request to count ShipmentParticipantMaps by criteria: {}", criteria);
        return ResponseEntity.ok().body(shipmentParticipantMapQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shipment-participant-maps/:id} : get the "id" shipmentParticipantMap.
     *
     * @param id the id of the shipmentParticipantMapDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shipmentParticipantMapDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentParticipantMapDTO> getShipmentParticipantMap(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ShipmentParticipantMap : {}", id);
        Optional<ShipmentParticipantMapDTO> shipmentParticipantMapDTO = shipmentParticipantMapService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shipmentParticipantMapDTO);
    }

    /**
     * {@code DELETE  /shipment-participant-maps/:id} : delete the "id" shipmentParticipantMap.
     *
     * @param id the id of the shipmentParticipantMapDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipmentParticipantMap(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ShipmentParticipantMap : {}", id);
        shipmentParticipantMapService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
