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
import zw.org.nmrl.ept.repository.CountryRepository;
import zw.org.nmrl.ept.service.CountryService;
import zw.org.nmrl.ept.service.dto.CountryDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.Country}.
 */
@RestController
@RequestMapping("/api/countries")
public class CountryResource {

    private static final Logger LOG = LoggerFactory.getLogger(CountryResource.class);

    private static final String ENTITY_NAME = "country";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final CountryService countryService;

    private final CountryRepository countryRepository;

    public CountryResource(CountryService countryService, CountryRepository countryRepository) {
        this.countryService = countryService;
        this.countryRepository = countryRepository;
    }

    /**
     * {@code POST  /countries} : Create a new country.
     *
     * @param countryDTO the countryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new countryDTO, or with status {@code 400 (Bad Request)} if the country has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CountryDTO> createCountry(@Valid @RequestBody CountryDTO countryDTO) throws URISyntaxException {
        LOG.debug("REST request to save Country : {}", countryDTO);
        if (countryDTO.getId() != null) {
            throw new BadRequestAlertException("A new country cannot already have an ID", ENTITY_NAME, "idexists");
        }
        countryDTO = countryService.save(countryDTO);
        return ResponseEntity.created(new URI("/api/countries/" + countryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, countryDTO.getId().toString()))
            .body(countryDTO);
    }

    /**
     * {@code PUT  /countries/:id} : Updates an existing country.
     *
     * @param id the id of the countryDTO to save.
     * @param countryDTO the countryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated countryDTO,
     * or with status {@code 400 (Bad Request)} if the countryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the countryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CountryDTO> updateCountry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CountryDTO countryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Country : {}, {}", id, countryDTO);
        if (countryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, countryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!countryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        countryDTO = countryService.update(countryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, countryDTO.getId().toString()))
            .body(countryDTO);
    }

    /**
     * {@code PATCH  /countries/:id} : Partial updates given fields of an existing country, field will ignore if it is null
     *
     * @param id the id of the countryDTO to save.
     * @param countryDTO the countryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated countryDTO,
     * or with status {@code 400 (Bad Request)} if the countryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the countryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the countryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CountryDTO> partialUpdateCountry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CountryDTO countryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Country partially : {}, {}", id, countryDTO);
        if (countryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, countryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!countryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CountryDTO> result = countryService.partialUpdate(countryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, countryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /countries} : get all the Countries.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Countries in body.
     */
    @GetMapping("")
    public List<CountryDTO> getAllCountries() {
        LOG.debug("REST request to get all Countries");
        return countryService.findAll();
    }

    /**
     * {@code GET  /countries/:id} : get the "id" country.
     *
     * @param id the id of the countryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the countryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CountryDTO> getCountry(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Country : {}", id);
        Optional<CountryDTO> countryDTO = countryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(countryDTO);
    }

    /**
     * {@code DELETE  /countries/:id} : delete the "id" country.
     *
     * @param id the id of the countryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Country : {}", id);
        countryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
