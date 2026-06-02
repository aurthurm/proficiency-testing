package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ParticipantFeedbackAsserts.*;
import static zw.org.nmrl.ept.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.ParticipantFeedback;
import zw.org.nmrl.ept.repository.ParticipantFeedbackRepository;
import zw.org.nmrl.ept.service.dto.ParticipantFeedbackDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantFeedbackMapper;

/**
 * Integration tests for the {@link ParticipantFeedbackResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParticipantFeedbackResourceIT {

    private static final String DEFAULT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER = "BBBBBBBBBB";

    private static final Instant DEFAULT_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/participant-feedbacks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParticipantFeedbackRepository participantFeedbackRepository;

    @Autowired
    private ParticipantFeedbackMapper participantFeedbackMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParticipantFeedbackMockMvc;

    private ParticipantFeedback participantFeedback;

    private ParticipantFeedback insertedParticipantFeedback;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParticipantFeedback createEntity(EntityManager em) {
        ParticipantFeedback participantFeedback = new ParticipantFeedback().answer(DEFAULT_ANSWER).submittedAt(DEFAULT_SUBMITTED_AT);
        // Add required entity
        FeedbackQuestion feedbackQuestion;
        if (TestUtil.findAll(em, FeedbackQuestion.class).isEmpty()) {
            feedbackQuestion = FeedbackQuestionResourceIT.createEntity();
            em.persist(feedbackQuestion);
            em.flush();
        } else {
            feedbackQuestion = TestUtil.findAll(em, FeedbackQuestion.class).get(0);
        }
        participantFeedback.setQuestion(feedbackQuestion);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        participantFeedback.setParticipant(participant);
        return participantFeedback;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParticipantFeedback createUpdatedEntity(EntityManager em) {
        ParticipantFeedback updatedParticipantFeedback = new ParticipantFeedback().answer(UPDATED_ANSWER).submittedAt(UPDATED_SUBMITTED_AT);
        // Add required entity
        FeedbackQuestion feedbackQuestion;
        if (TestUtil.findAll(em, FeedbackQuestion.class).isEmpty()) {
            feedbackQuestion = FeedbackQuestionResourceIT.createUpdatedEntity();
            em.persist(feedbackQuestion);
            em.flush();
        } else {
            feedbackQuestion = TestUtil.findAll(em, FeedbackQuestion.class).get(0);
        }
        updatedParticipantFeedback.setQuestion(feedbackQuestion);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createUpdatedEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        updatedParticipantFeedback.setParticipant(participant);
        return updatedParticipantFeedback;
    }

    @BeforeEach
    void initTest() {
        participantFeedback = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedParticipantFeedback != null) {
            participantFeedbackRepository.delete(insertedParticipantFeedback);
            insertedParticipantFeedback = null;
        }
    }

    @Test
    @Transactional
    void createParticipantFeedback() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ParticipantFeedback
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(participantFeedback);
        var returnedParticipantFeedbackDTO = om.readValue(
            restParticipantFeedbackMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantFeedbackDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParticipantFeedbackDTO.class
        );

        // Validate the ParticipantFeedback in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParticipantFeedback = participantFeedbackMapper.toEntity(returnedParticipantFeedbackDTO);
        assertParticipantFeedbackUpdatableFieldsEquals(
            returnedParticipantFeedback,
            getPersistedParticipantFeedback(returnedParticipantFeedback)
        );

        insertedParticipantFeedback = returnedParticipantFeedback;
    }

    @Test
    @Transactional
    void createParticipantFeedbackWithExistingId() throws Exception {
        // Create the ParticipantFeedback with an existing ID
        participantFeedback.setId(1L);
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(participantFeedback);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantFeedbackMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantFeedbackDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParticipantFeedback in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllParticipantFeedbacks() throws Exception {
        // Initialize the database
        insertedParticipantFeedback = participantFeedbackRepository.saveAndFlush(participantFeedback);

        // Get all the participantFeedbackList
        restParticipantFeedbackMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participantFeedback.getId().intValue())))
            .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER)))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())));
    }

    @Test
    @Transactional
    void getParticipantFeedback() throws Exception {
        // Initialize the database
        insertedParticipantFeedback = participantFeedbackRepository.saveAndFlush(participantFeedback);

        // Get the participantFeedback
        restParticipantFeedbackMockMvc
            .perform(get(ENTITY_API_URL_ID, participantFeedback.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participantFeedback.getId().intValue()))
            .andExpect(jsonPath("$.answer").value(DEFAULT_ANSWER))
            .andExpect(jsonPath("$.submittedAt").value(DEFAULT_SUBMITTED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingParticipantFeedback() throws Exception {
        // Get the participantFeedback
        restParticipantFeedbackMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParticipantFeedback() throws Exception {
        // Initialize the database
        insertedParticipantFeedback = participantFeedbackRepository.saveAndFlush(participantFeedback);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantFeedback
        ParticipantFeedback updatedParticipantFeedback = participantFeedbackRepository.findById(participantFeedback.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParticipantFeedback are not directly saved in db
        em.detach(updatedParticipantFeedback);
        updatedParticipantFeedback.answer(UPDATED_ANSWER).submittedAt(UPDATED_SUBMITTED_AT);
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(updatedParticipantFeedback);

        restParticipantFeedbackMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantFeedbackDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantFeedbackDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantFeedback in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParticipantFeedbackToMatchAllProperties(updatedParticipantFeedback);
    }

    @Test
    @Transactional
    void putNonExistingParticipantFeedback() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantFeedback.setId(longCount.incrementAndGet());

        // Create the ParticipantFeedback
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(participantFeedback);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantFeedbackMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantFeedbackDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantFeedbackDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantFeedback in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParticipantFeedback() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantFeedback.setId(longCount.incrementAndGet());

        // Create the ParticipantFeedback
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(participantFeedback);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantFeedbackMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantFeedbackDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantFeedback in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParticipantFeedback() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantFeedback.setId(longCount.incrementAndGet());

        // Create the ParticipantFeedback
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(participantFeedback);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantFeedbackMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantFeedbackDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParticipantFeedback in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParticipantFeedbackWithPatch() throws Exception {
        // Initialize the database
        insertedParticipantFeedback = participantFeedbackRepository.saveAndFlush(participantFeedback);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantFeedback using partial update
        ParticipantFeedback partialUpdatedParticipantFeedback = new ParticipantFeedback();
        partialUpdatedParticipantFeedback.setId(participantFeedback.getId());

        partialUpdatedParticipantFeedback.submittedAt(UPDATED_SUBMITTED_AT);

        restParticipantFeedbackMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipantFeedback.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipantFeedback))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantFeedback in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantFeedbackUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParticipantFeedback, participantFeedback),
            getPersistedParticipantFeedback(participantFeedback)
        );
    }

    @Test
    @Transactional
    void fullUpdateParticipantFeedbackWithPatch() throws Exception {
        // Initialize the database
        insertedParticipantFeedback = participantFeedbackRepository.saveAndFlush(participantFeedback);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantFeedback using partial update
        ParticipantFeedback partialUpdatedParticipantFeedback = new ParticipantFeedback();
        partialUpdatedParticipantFeedback.setId(participantFeedback.getId());

        partialUpdatedParticipantFeedback.answer(UPDATED_ANSWER).submittedAt(UPDATED_SUBMITTED_AT);

        restParticipantFeedbackMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipantFeedback.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipantFeedback))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantFeedback in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantFeedbackUpdatableFieldsEquals(
            partialUpdatedParticipantFeedback,
            getPersistedParticipantFeedback(partialUpdatedParticipantFeedback)
        );
    }

    @Test
    @Transactional
    void patchNonExistingParticipantFeedback() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantFeedback.setId(longCount.incrementAndGet());

        // Create the ParticipantFeedback
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(participantFeedback);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantFeedbackMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, participantFeedbackDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantFeedbackDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantFeedback in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParticipantFeedback() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantFeedback.setId(longCount.incrementAndGet());

        // Create the ParticipantFeedback
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(participantFeedback);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantFeedbackMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantFeedbackDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantFeedback in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParticipantFeedback() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantFeedback.setId(longCount.incrementAndGet());

        // Create the ParticipantFeedback
        ParticipantFeedbackDTO participantFeedbackDTO = participantFeedbackMapper.toDto(participantFeedback);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantFeedbackMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(participantFeedbackDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParticipantFeedback in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParticipantFeedback() throws Exception {
        // Initialize the database
        insertedParticipantFeedback = participantFeedbackRepository.saveAndFlush(participantFeedback);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the participantFeedback
        restParticipantFeedbackMockMvc
            .perform(delete(ENTITY_API_URL_ID, participantFeedback.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return participantFeedbackRepository.count();
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

    protected ParticipantFeedback getPersistedParticipantFeedback(ParticipantFeedback participantFeedback) {
        return participantFeedbackRepository.findById(participantFeedback.getId()).orElseThrow();
    }

    protected void assertPersistedParticipantFeedbackToMatchAllProperties(ParticipantFeedback expectedParticipantFeedback) {
        assertParticipantFeedbackAllPropertiesEquals(
            expectedParticipantFeedback,
            getPersistedParticipantFeedback(expectedParticipantFeedback)
        );
    }

    protected void assertPersistedParticipantFeedbackToMatchUpdatableProperties(ParticipantFeedback expectedParticipantFeedback) {
        assertParticipantFeedbackAllUpdatablePropertiesEquals(
            expectedParticipantFeedback,
            getPersistedParticipantFeedback(expectedParticipantFeedback)
        );
    }
}
