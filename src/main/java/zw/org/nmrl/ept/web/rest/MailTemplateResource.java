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
import zw.org.nmrl.ept.repository.MailTemplateRepository;
import zw.org.nmrl.ept.service.MailTemplateService;
import zw.org.nmrl.ept.service.dto.MailTemplateDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.MailTemplate}.
 */
@RestController
@RequestMapping("/api/mail-templates")
public class MailTemplateResource {

    private static final Logger LOG = LoggerFactory.getLogger(MailTemplateResource.class);

    private static final String ENTITY_NAME = "mailTemplate";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final MailTemplateService mailTemplateService;

    private final MailTemplateRepository mailTemplateRepository;

    public MailTemplateResource(MailTemplateService mailTemplateService, MailTemplateRepository mailTemplateRepository) {
        this.mailTemplateService = mailTemplateService;
        this.mailTemplateRepository = mailTemplateRepository;
    }

    /**
     * {@code POST  /mail-templates} : Create a new mailTemplate.
     *
     * @param mailTemplateDTO the mailTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mailTemplateDTO, or with status {@code 400 (Bad Request)} if the mailTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MailTemplateDTO> createMailTemplate(@Valid @RequestBody MailTemplateDTO mailTemplateDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MailTemplate : {}", mailTemplateDTO);
        if (mailTemplateDTO.getId() != null) {
            throw new BadRequestAlertException("A new mailTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        mailTemplateDTO = mailTemplateService.save(mailTemplateDTO);
        return ResponseEntity.created(new URI("/api/mail-templates/" + mailTemplateDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, mailTemplateDTO.getId().toString()))
            .body(mailTemplateDTO);
    }

    /**
     * {@code PUT  /mail-templates/:id} : Updates an existing mailTemplate.
     *
     * @param id the id of the mailTemplateDTO to save.
     * @param mailTemplateDTO the mailTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mailTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the mailTemplateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mailTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MailTemplateDTO> updateMailTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MailTemplateDTO mailTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MailTemplate : {}, {}", id, mailTemplateDTO);
        if (mailTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mailTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mailTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        mailTemplateDTO = mailTemplateService.update(mailTemplateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mailTemplateDTO.getId().toString()))
            .body(mailTemplateDTO);
    }

    /**
     * {@code PATCH  /mail-templates/:id} : Partial updates given fields of an existing mailTemplate, field will ignore if it is null
     *
     * @param id the id of the mailTemplateDTO to save.
     * @param mailTemplateDTO the mailTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mailTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the mailTemplateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mailTemplateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mailTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MailTemplateDTO> partialUpdateMailTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MailTemplateDTO mailTemplateDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MailTemplate partially : {}, {}", id, mailTemplateDTO);
        if (mailTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mailTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mailTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MailTemplateDTO> result = mailTemplateService.partialUpdate(mailTemplateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mailTemplateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mail-templates} : get all the Mail Templates.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mail Templates in body.
     */
    @GetMapping("")
    public List<MailTemplateDTO> getAllMailTemplates() {
        LOG.debug("REST request to get all MailTemplates");
        return mailTemplateService.findAll();
    }

    /**
     * {@code GET  /mail-templates/:id} : get the "id" mailTemplate.
     *
     * @param id the id of the mailTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mailTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MailTemplateDTO> getMailTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MailTemplate : {}", id);
        Optional<MailTemplateDTO> mailTemplateDTO = mailTemplateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mailTemplateDTO);
    }

    /**
     * {@code DELETE  /mail-templates/:id} : delete the "id" mailTemplate.
     *
     * @param id the id of the mailTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMailTemplate(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MailTemplate : {}", id);
        mailTemplateService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
