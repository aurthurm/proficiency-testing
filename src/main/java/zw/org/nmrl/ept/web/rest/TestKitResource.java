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
import zw.org.nmrl.ept.repository.TestKitRepository;
import zw.org.nmrl.ept.service.TestKitService;
import zw.org.nmrl.ept.service.dto.TestKitDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.TestKit}.
 */
@RestController
@RequestMapping("/api/test-kits")
public class TestKitResource {

    private static final Logger LOG = LoggerFactory.getLogger(TestKitResource.class);

    private static final String ENTITY_NAME = "testKit";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final TestKitService testKitService;

    private final TestKitRepository testKitRepository;

    public TestKitResource(TestKitService testKitService, TestKitRepository testKitRepository) {
        this.testKitService = testKitService;
        this.testKitRepository = testKitRepository;
    }

    /**
     * {@code POST  /test-kits} : Create a new testKit.
     *
     * @param testKitDTO the testKitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new testKitDTO, or with status {@code 400 (Bad Request)} if the testKit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TestKitDTO> createTestKit(@Valid @RequestBody TestKitDTO testKitDTO) throws URISyntaxException {
        LOG.debug("REST request to save TestKit : {}", testKitDTO);
        if (testKitDTO.getId() != null) {
            throw new BadRequestAlertException("A new testKit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        testKitDTO = testKitService.save(testKitDTO);
        return ResponseEntity.created(new URI("/api/test-kits/" + testKitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, testKitDTO.getId().toString()))
            .body(testKitDTO);
    }

    /**
     * {@code PUT  /test-kits/:id} : Updates an existing testKit.
     *
     * @param id the id of the testKitDTO to save.
     * @param testKitDTO the testKitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testKitDTO,
     * or with status {@code 400 (Bad Request)} if the testKitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the testKitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TestKitDTO> updateTestKit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TestKitDTO testKitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TestKit : {}, {}", id, testKitDTO);
        if (testKitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testKitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!testKitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        testKitDTO = testKitService.update(testKitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, testKitDTO.getId().toString()))
            .body(testKitDTO);
    }

    /**
     * {@code PATCH  /test-kits/:id} : Partial updates given fields of an existing testKit, field will ignore if it is null
     *
     * @param id the id of the testKitDTO to save.
     * @param testKitDTO the testKitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testKitDTO,
     * or with status {@code 400 (Bad Request)} if the testKitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the testKitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the testKitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TestKitDTO> partialUpdateTestKit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TestKitDTO testKitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TestKit partially : {}, {}", id, testKitDTO);
        if (testKitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testKitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!testKitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TestKitDTO> result = testKitService.partialUpdate(testKitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, testKitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /test-kits} : get all the Test Kits.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Test Kits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TestKitDTO>> getAllTestKits(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of TestKits");
        Page<TestKitDTO> page = testKitService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /test-kits/:id} : get the "id" testKit.
     *
     * @param id the id of the testKitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the testKitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TestKitDTO> getTestKit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TestKit : {}", id);
        Optional<TestKitDTO> testKitDTO = testKitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(testKitDTO);
    }

    /**
     * {@code DELETE  /test-kits/:id} : delete the "id" testKit.
     *
     * @param id the id of the testKitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestKit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TestKit : {}", id);
        testKitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
