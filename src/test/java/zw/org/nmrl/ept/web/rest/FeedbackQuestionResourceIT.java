package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.FeedbackQuestionAsserts.*;
import static zw.org.nmrl.ept.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.IntegrationTest;
import zw.org.nmrl.ept.domain.FeedbackQuestion;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;
import zw.org.nmrl.ept.repository.FeedbackQuestionRepository;
import zw.org.nmrl.ept.service.dto.FeedbackQuestionDTO;
import zw.org.nmrl.ept.service.mapper.FeedbackQuestionMapper;

/**
 * Integration tests for the {@link FeedbackQuestionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FeedbackQuestionResourceIT {

    private static final String DEFAULT_QUESTION_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_TEXT = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final ContentStatus DEFAULT_STATUS = ContentStatus.PUBLISHED;
    private static final ContentStatus UPDATED_STATUS = ContentStatus.DRAFT;

    private static final String ENTITY_API_URL = "/api/feedback-questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FeedbackQuestionRepository feedbackQuestionRepository;

    @Autowired
    private FeedbackQuestionMapper feedbackQuestionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFeedbackQuestionMockMvc;

    private FeedbackQuestion feedbackQuestion;

    private FeedbackQuestion insertedFeedbackQuestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeedbackQuestion createEntity() {
        return new FeedbackQuestion().questionText(DEFAULT_QUESTION_TEXT).displayOrder(DEFAULT_DISPLAY_ORDER).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeedbackQuestion createUpdatedEntity() {
        return new FeedbackQuestion().questionText(UPDATED_QUESTION_TEXT).displayOrder(UPDATED_DISPLAY_ORDER).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        feedbackQuestion = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFeedbackQuestion != null) {
            feedbackQuestionRepository.delete(insertedFeedbackQuestion);
            insertedFeedbackQuestion = null;
        }
    }

    @Test
    @Transactional
    void createFeedbackQuestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FeedbackQuestion
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);
        var returnedFeedbackQuestionDTO = om.readValue(
            restFeedbackQuestionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feedbackQuestionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FeedbackQuestionDTO.class
        );

        // Validate the FeedbackQuestion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFeedbackQuestion = feedbackQuestionMapper.toEntity(returnedFeedbackQuestionDTO);
        assertFeedbackQuestionUpdatableFieldsEquals(returnedFeedbackQuestion, getPersistedFeedbackQuestion(returnedFeedbackQuestion));

        insertedFeedbackQuestion = returnedFeedbackQuestion;
    }

    @Test
    @Transactional
    void createFeedbackQuestionWithExistingId() throws Exception {
        // Create the FeedbackQuestion with an existing ID
        feedbackQuestion.setId(1L);
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedbackQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feedbackQuestionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FeedbackQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuestionTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feedbackQuestion.setQuestionText(null);

        // Create the FeedbackQuestion, which fails.
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        restFeedbackQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feedbackQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feedbackQuestion.setStatus(null);

        // Create the FeedbackQuestion, which fails.
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        restFeedbackQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feedbackQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFeedbackQuestions() throws Exception {
        // Initialize the database
        insertedFeedbackQuestion = feedbackQuestionRepository.saveAndFlush(feedbackQuestion);

        // Get all the feedbackQuestionList
        restFeedbackQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feedbackQuestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].questionText").value(hasItem(DEFAULT_QUESTION_TEXT)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getFeedbackQuestion() throws Exception {
        // Initialize the database
        insertedFeedbackQuestion = feedbackQuestionRepository.saveAndFlush(feedbackQuestion);

        // Get the feedbackQuestion
        restFeedbackQuestionMockMvc
            .perform(get(ENTITY_API_URL_ID, feedbackQuestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(feedbackQuestion.getId().intValue()))
            .andExpect(jsonPath("$.questionText").value(DEFAULT_QUESTION_TEXT))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFeedbackQuestion() throws Exception {
        // Get the feedbackQuestion
        restFeedbackQuestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFeedbackQuestion() throws Exception {
        // Initialize the database
        insertedFeedbackQuestion = feedbackQuestionRepository.saveAndFlush(feedbackQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feedbackQuestion
        FeedbackQuestion updatedFeedbackQuestion = feedbackQuestionRepository.findById(feedbackQuestion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFeedbackQuestion are not directly saved in db
        em.detach(updatedFeedbackQuestion);
        updatedFeedbackQuestion.questionText(UPDATED_QUESTION_TEXT).displayOrder(UPDATED_DISPLAY_ORDER).status(UPDATED_STATUS);
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(updatedFeedbackQuestion);

        restFeedbackQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, feedbackQuestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feedbackQuestionDTO))
            )
            .andExpect(status().isOk());

        // Validate the FeedbackQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFeedbackQuestionToMatchAllProperties(updatedFeedbackQuestion);
    }

    @Test
    @Transactional
    void putNonExistingFeedbackQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feedbackQuestion.setId(longCount.incrementAndGet());

        // Create the FeedbackQuestion
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeedbackQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, feedbackQuestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feedbackQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeedbackQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFeedbackQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feedbackQuestion.setId(longCount.incrementAndGet());

        // Create the FeedbackQuestion
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedbackQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feedbackQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeedbackQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFeedbackQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feedbackQuestion.setId(longCount.incrementAndGet());

        // Create the FeedbackQuestion
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedbackQuestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feedbackQuestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeedbackQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFeedbackQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedFeedbackQuestion = feedbackQuestionRepository.saveAndFlush(feedbackQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feedbackQuestion using partial update
        FeedbackQuestion partialUpdatedFeedbackQuestion = new FeedbackQuestion();
        partialUpdatedFeedbackQuestion.setId(feedbackQuestion.getId());

        partialUpdatedFeedbackQuestion.status(UPDATED_STATUS);

        restFeedbackQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeedbackQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeedbackQuestion))
            )
            .andExpect(status().isOk());

        // Validate the FeedbackQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeedbackQuestionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFeedbackQuestion, feedbackQuestion),
            getPersistedFeedbackQuestion(feedbackQuestion)
        );
    }

    @Test
    @Transactional
    void fullUpdateFeedbackQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedFeedbackQuestion = feedbackQuestionRepository.saveAndFlush(feedbackQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feedbackQuestion using partial update
        FeedbackQuestion partialUpdatedFeedbackQuestion = new FeedbackQuestion();
        partialUpdatedFeedbackQuestion.setId(feedbackQuestion.getId());

        partialUpdatedFeedbackQuestion.questionText(UPDATED_QUESTION_TEXT).displayOrder(UPDATED_DISPLAY_ORDER).status(UPDATED_STATUS);

        restFeedbackQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeedbackQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeedbackQuestion))
            )
            .andExpect(status().isOk());

        // Validate the FeedbackQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeedbackQuestionUpdatableFieldsEquals(
            partialUpdatedFeedbackQuestion,
            getPersistedFeedbackQuestion(partialUpdatedFeedbackQuestion)
        );
    }

    @Test
    @Transactional
    void patchNonExistingFeedbackQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feedbackQuestion.setId(longCount.incrementAndGet());

        // Create the FeedbackQuestion
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeedbackQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, feedbackQuestionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(feedbackQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeedbackQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFeedbackQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feedbackQuestion.setId(longCount.incrementAndGet());

        // Create the FeedbackQuestion
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedbackQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(feedbackQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeedbackQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFeedbackQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feedbackQuestion.setId(longCount.incrementAndGet());

        // Create the FeedbackQuestion
        FeedbackQuestionDTO feedbackQuestionDTO = feedbackQuestionMapper.toDto(feedbackQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedbackQuestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(feedbackQuestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeedbackQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFeedbackQuestion() throws Exception {
        // Initialize the database
        insertedFeedbackQuestion = feedbackQuestionRepository.saveAndFlush(feedbackQuestion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the feedbackQuestion
        restFeedbackQuestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, feedbackQuestion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return feedbackQuestionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected FeedbackQuestion getPersistedFeedbackQuestion(FeedbackQuestion feedbackQuestion) {
        return feedbackQuestionRepository.findById(feedbackQuestion.getId()).orElseThrow();
    }

    protected void assertPersistedFeedbackQuestionToMatchAllProperties(FeedbackQuestion expectedFeedbackQuestion) {
        assertFeedbackQuestionAllPropertiesEquals(expectedFeedbackQuestion, getPersistedFeedbackQuestion(expectedFeedbackQuestion));
    }

    protected void assertPersistedFeedbackQuestionToMatchUpdatableProperties(FeedbackQuestion expectedFeedbackQuestion) {
        assertFeedbackQuestionAllUpdatablePropertiesEquals(
            expectedFeedbackQuestion,
            getPersistedFeedbackQuestion(expectedFeedbackQuestion)
        );
    }
}
