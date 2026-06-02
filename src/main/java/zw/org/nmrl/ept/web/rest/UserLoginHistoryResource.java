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
import zw.org.nmrl.ept.repository.UserLoginHistoryRepository;
import zw.org.nmrl.ept.service.UserLoginHistoryQueryService;
import zw.org.nmrl.ept.service.UserLoginHistoryService;
import zw.org.nmrl.ept.service.criteria.UserLoginHistoryCriteria;
import zw.org.nmrl.ept.service.dto.UserLoginHistoryDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.UserLoginHistory}.
 */
@RestController
@RequestMapping("/api/user-login-histories")
public class UserLoginHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserLoginHistoryResource.class);

    private static final String ENTITY_NAME = "userLoginHistory";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final UserLoginHistoryService userLoginHistoryService;

    private final UserLoginHistoryRepository userLoginHistoryRepository;

    private final UserLoginHistoryQueryService userLoginHistoryQueryService;

    public UserLoginHistoryResource(
        UserLoginHistoryService userLoginHistoryService,
        UserLoginHistoryRepository userLoginHistoryRepository,
        UserLoginHistoryQueryService userLoginHistoryQueryService
    ) {
        this.userLoginHistoryService = userLoginHistoryService;
        this.userLoginHistoryRepository = userLoginHistoryRepository;
        this.userLoginHistoryQueryService = userLoginHistoryQueryService;
    }

    /**
     * {@code POST  /user-login-histories} : Create a new userLoginHistory.
     *
     * @param userLoginHistoryDTO the userLoginHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userLoginHistoryDTO, or with status {@code 400 (Bad Request)} if the userLoginHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserLoginHistoryDTO> createUserLoginHistory(@Valid @RequestBody UserLoginHistoryDTO userLoginHistoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UserLoginHistory : {}", userLoginHistoryDTO);
        if (userLoginHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new userLoginHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userLoginHistoryDTO = userLoginHistoryService.save(userLoginHistoryDTO);
        return ResponseEntity.created(new URI("/api/user-login-histories/" + userLoginHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userLoginHistoryDTO.getId().toString()))
            .body(userLoginHistoryDTO);
    }

    /**
     * {@code PUT  /user-login-histories/:id} : Updates an existing userLoginHistory.
     *
     * @param id the id of the userLoginHistoryDTO to save.
     * @param userLoginHistoryDTO the userLoginHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userLoginHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the userLoginHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userLoginHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserLoginHistoryDTO> updateUserLoginHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserLoginHistoryDTO userLoginHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserLoginHistory : {}, {}", id, userLoginHistoryDTO);
        if (userLoginHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userLoginHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userLoginHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userLoginHistoryDTO = userLoginHistoryService.update(userLoginHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userLoginHistoryDTO.getId().toString()))
            .body(userLoginHistoryDTO);
    }

    /**
     * {@code PATCH  /user-login-histories/:id} : Partial updates given fields of an existing userLoginHistory, field will ignore if it is null
     *
     * @param id the id of the userLoginHistoryDTO to save.
     * @param userLoginHistoryDTO the userLoginHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userLoginHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the userLoginHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userLoginHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userLoginHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserLoginHistoryDTO> partialUpdateUserLoginHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserLoginHistoryDTO userLoginHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserLoginHistory partially : {}, {}", id, userLoginHistoryDTO);
        if (userLoginHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userLoginHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userLoginHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserLoginHistoryDTO> result = userLoginHistoryService.partialUpdate(userLoginHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userLoginHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-login-histories} : get all the User Login Histories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of User Login Histories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserLoginHistoryDTO>> getAllUserLoginHistories(
        UserLoginHistoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get UserLoginHistories by criteria: {}", criteria);

        Page<UserLoginHistoryDTO> page = userLoginHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-login-histories/count} : count all the userLoginHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countUserLoginHistories(UserLoginHistoryCriteria criteria) {
        LOG.debug("REST request to count UserLoginHistories by criteria: {}", criteria);
        return ResponseEntity.ok().body(userLoginHistoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-login-histories/:id} : get the "id" userLoginHistory.
     *
     * @param id the id of the userLoginHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userLoginHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserLoginHistoryDTO> getUserLoginHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserLoginHistory : {}", id);
        Optional<UserLoginHistoryDTO> userLoginHistoryDTO = userLoginHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userLoginHistoryDTO);
    }

    /**
     * {@code DELETE  /user-login-histories/:id} : delete the "id" userLoginHistory.
     *
     * @param id the id of the userLoginHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserLoginHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserLoginHistory : {}", id);
        userLoginHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
