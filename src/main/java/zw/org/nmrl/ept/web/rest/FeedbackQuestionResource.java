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
import zw.org.nmrl.ept.repository.FeedbackQuestionRepository;
import zw.org.nmrl.ept.service.FeedbackQuestionService;
import zw.org.nmrl.ept.service.dto.FeedbackQuestionDTO;
import zw.org.nmrl.ept.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link zw.org.nmrl.ept.domain.FeedbackQuestion}.
 */
@RestController
@RequestMapping("/api/feedback-questions")
public class FeedbackQuestionResource {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackQuestionResource.class);

    private static final String ENTITY_NAME = "feedbackQuestion";

    @Value("${jhipster.clientApp.name:proficiencyTesting}")
    private String applicationName;

    private final FeedbackQuestionService feedbackQuestionService;

    private final FeedbackQuestionRepository feedbackQuestionRepository;

    public FeedbackQuestionResource(
        FeedbackQuestionService feedbackQuestionService,
        FeedbackQuestionRepository feedbackQuestionRepository
    ) {
        this.feedbackQuestionService = feedbackQuestionService;
        this.feedbackQuestionRepository = feedbackQuestionRepository;
    }

    /**
     * {@code POST  /feedback-questions} : Create a new feedbackQuestion.
     *
     * @param feedbackQuestionDTO the feedbackQuestionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feedbackQuestionDTO, or with status {@code 400 (Bad Request)} if the feedbackQuestion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FeedbackQuestionDTO> createFeedbackQuestion(@Valid @RequestBody FeedbackQuestionDTO feedbackQuestionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FeedbackQuestion : {}", feedbackQuestionDTO);
        if (feedbackQuestionDTO.getId() != null) {
            throw new BadRequestAlertException("A new feedbackQuestion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        feedbackQuestionDTO = feedbackQuestionService.save(feedbackQuestionDTO);
        return ResponseEntity.created(new URI("/api/feedback-questions/" + feedbackQuestionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, feedbackQuestionDTO.getId().toString()))
            .body(feedbackQuestionDTO);
    }

    /**
     * {@code PUT  /feedback-questions/:id} : Updates an existing feedbackQuestion.
     *
     * @param id the id of the feedbackQuestionDTO to save.
     * @param feedbackQuestionDTO the feedbackQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feedbackQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the feedbackQuestionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the feedbackQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackQuestionDTO> updateFeedbackQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FeedbackQuestionDTO feedbackQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FeedbackQuestion : {}, {}", id, feedbackQuestionDTO);
        if (feedbackQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feedbackQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feedbackQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        feedbackQuestionDTO = feedbackQuestionService.update(feedbackQuestionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feedbackQuestionDTO.getId().toString()))
            .body(feedbackQuestionDTO);
    }

    /**
     * {@code PATCH  /feedback-questions/:id} : Partial updates given fields of an existing feedbackQuestion, field will ignore if it is null
     *
     * @param id the id of the feedbackQuestionDTO to save.
     * @param feedbackQuestionDTO the feedbackQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feedbackQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the feedbackQuestionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the feedbackQuestionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the feedbackQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FeedbackQuestionDTO> partialUpdateFeedbackQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FeedbackQuestionDTO feedbackQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FeedbackQuestion partially : {}, {}", id, feedbackQuestionDTO);
        if (feedbackQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feedbackQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feedbackQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FeedbackQuestionDTO> result = feedbackQuestionService.partialUpdate(feedbackQuestionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feedbackQuestionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /feedback-questions} : get all the Feedback Questions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Feedback Questions in body.
     */
    @GetMapping("")
    public List<FeedbackQuestionDTO> getAllFeedbackQuestions() {
        LOG.debug("REST request to get all FeedbackQuestions");
        return feedbackQuestionService.findAll();
    }

    /**
     * {@code GET  /feedback-questions/:id} : get the "id" feedbackQuestion.
     *
     * @param id the id of the feedbackQuestionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feedbackQuestionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackQuestionDTO> getFeedbackQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FeedbackQuestion : {}", id);
        Optional<FeedbackQuestionDTO> feedbackQuestionDTO = feedbackQuestionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(feedbackQuestionDTO);
    }

    /**
     * {@code DELETE  /feedback-questions/:id} : delete the "id" feedbackQuestion.
     *
     * @param id the id of the feedbackQuestionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedbackQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FeedbackQuestion : {}", id);
        feedbackQuestionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
