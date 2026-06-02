package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ParticipantMessageAsserts.*;
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
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.ParticipantMessage;
import zw.org.nmrl.ept.repository.ParticipantMessageRepository;
import zw.org.nmrl.ept.service.dto.ParticipantMessageDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantMessageMapper;

/**
 * Integration tests for the {@link ParticipantMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParticipantMessageResourceIT {

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_READ = false;
    private static final Boolean UPDATED_IS_READ = true;

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/participant-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParticipantMessageRepository participantMessageRepository;

    @Autowired
    private ParticipantMessageMapper participantMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParticipantMessageMockMvc;

    private ParticipantMessage participantMessage;

    private ParticipantMessage insertedParticipantMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParticipantMessage createEntity(EntityManager em) {
        ParticipantMessage participantMessage = new ParticipantMessage()
            .subject(DEFAULT_SUBJECT)
            .body(DEFAULT_BODY)
            .isRead(DEFAULT_IS_READ)
            .sentAt(DEFAULT_SENT_AT);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        participantMessage.setParticipant(participant);
        return participantMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParticipantMessage createUpdatedEntity(EntityManager em) {
        ParticipantMessage updatedParticipantMessage = new ParticipantMessage()
            .subject(UPDATED_SUBJECT)
            .body(UPDATED_BODY)
            .isRead(UPDATED_IS_READ)
            .sentAt(UPDATED_SENT_AT);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createUpdatedEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        updatedParticipantMessage.setParticipant(participant);
        return updatedParticipantMessage;
    }

    @BeforeEach
    void initTest() {
        participantMessage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedParticipantMessage != null) {
            participantMessageRepository.delete(insertedParticipantMessage);
            insertedParticipantMessage = null;
        }
    }

    @Test
    @Transactional
    void createParticipantMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ParticipantMessage
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(participantMessage);
        var returnedParticipantMessageDTO = om.readValue(
            restParticipantMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParticipantMessageDTO.class
        );

        // Validate the ParticipantMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParticipantMessage = participantMessageMapper.toEntity(returnedParticipantMessageDTO);
        assertParticipantMessageUpdatableFieldsEquals(
            returnedParticipantMessage,
            getPersistedParticipantMessage(returnedParticipantMessage)
        );

        insertedParticipantMessage = returnedParticipantMessage;
    }

    @Test
    @Transactional
    void createParticipantMessageWithExistingId() throws Exception {
        // Create the ParticipantMessage with an existing ID
        participantMessage.setId(1L);
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(participantMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParticipantMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllParticipantMessages() throws Exception {
        // Initialize the database
        insertedParticipantMessage = participantMessageRepository.saveAndFlush(participantMessage);

        // Get all the participantMessageList
        restParticipantMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participantMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].isRead").value(hasItem(DEFAULT_IS_READ)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())));
    }

    @Test
    @Transactional
    void getParticipantMessage() throws Exception {
        // Initialize the database
        insertedParticipantMessage = participantMessageRepository.saveAndFlush(participantMessage);

        // Get the participantMessage
        restParticipantMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, participantMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participantMessage.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.isRead").value(DEFAULT_IS_READ))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingParticipantMessage() throws Exception {
        // Get the participantMessage
        restParticipantMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParticipantMessage() throws Exception {
        // Initialize the database
        insertedParticipantMessage = participantMessageRepository.saveAndFlush(participantMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantMessage
        ParticipantMessage updatedParticipantMessage = participantMessageRepository.findById(participantMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParticipantMessage are not directly saved in db
        em.detach(updatedParticipantMessage);
        updatedParticipantMessage.subject(UPDATED_SUBJECT).body(UPDATED_BODY).isRead(UPDATED_IS_READ).sentAt(UPDATED_SENT_AT);
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(updatedParticipantMessage);

        restParticipantMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParticipantMessageToMatchAllProperties(updatedParticipantMessage);
    }

    @Test
    @Transactional
    void putNonExistingParticipantMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantMessage.setId(longCount.incrementAndGet());

        // Create the ParticipantMessage
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(participantMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParticipantMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantMessage.setId(longCount.incrementAndGet());

        // Create the ParticipantMessage
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(participantMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParticipantMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantMessage.setId(longCount.incrementAndGet());

        // Create the ParticipantMessage
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(participantMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParticipantMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParticipantMessageWithPatch() throws Exception {
        // Initialize the database
        insertedParticipantMessage = participantMessageRepository.saveAndFlush(participantMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantMessage using partial update
        ParticipantMessage partialUpdatedParticipantMessage = new ParticipantMessage();
        partialUpdatedParticipantMessage.setId(participantMessage.getId());

        partialUpdatedParticipantMessage.isRead(UPDATED_IS_READ).sentAt(UPDATED_SENT_AT);

        restParticipantMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipantMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipantMessage))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParticipantMessage, participantMessage),
            getPersistedParticipantMessage(participantMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateParticipantMessageWithPatch() throws Exception {
        // Initialize the database
        insertedParticipantMessage = participantMessageRepository.saveAndFlush(participantMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantMessage using partial update
        ParticipantMessage partialUpdatedParticipantMessage = new ParticipantMessage();
        partialUpdatedParticipantMessage.setId(participantMessage.getId());

        partialUpdatedParticipantMessage.subject(UPDATED_SUBJECT).body(UPDATED_BODY).isRead(UPDATED_IS_READ).sentAt(UPDATED_SENT_AT);

        restParticipantMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipantMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipantMessage))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantMessageUpdatableFieldsEquals(
            partialUpdatedParticipantMessage,
            getPersistedParticipantMessage(partialUpdatedParticipantMessage)
        );
    }

    @Test
    @Transactional
    void patchNonExistingParticipantMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantMessage.setId(longCount.incrementAndGet());

        // Create the ParticipantMessage
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(participantMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, participantMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParticipantMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantMessage.setId(longCount.incrementAndGet());

        // Create the ParticipantMessage
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(participantMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParticipantMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantMessage.setId(longCount.incrementAndGet());

        // Create the ParticipantMessage
        ParticipantMessageDTO participantMessageDTO = participantMessageMapper.toDto(participantMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(participantMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParticipantMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParticipantMessage() throws Exception {
        // Initialize the database
        insertedParticipantMessage = participantMessageRepository.saveAndFlush(participantMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the participantMessage
        restParticipantMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, participantMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return participantMessageRepository.count();
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

    protected ParticipantMessage getPersistedParticipantMessage(ParticipantMessage participantMessage) {
        return participantMessageRepository.findById(participantMessage.getId()).orElseThrow();
    }

    protected void assertPersistedParticipantMessageToMatchAllProperties(ParticipantMessage expectedParticipantMessage) {
        assertParticipantMessageAllPropertiesEquals(expectedParticipantMessage, getPersistedParticipantMessage(expectedParticipantMessage));
    }

    protected void assertPersistedParticipantMessageToMatchUpdatableProperties(ParticipantMessage expectedParticipantMessage) {
        assertParticipantMessageAllUpdatablePropertiesEquals(
            expectedParticipantMessage,
            getPersistedParticipantMessage(expectedParticipantMessage)
        );
    }
}
