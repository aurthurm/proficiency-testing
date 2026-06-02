package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ContactMessageAsserts.*;
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
import zw.org.nmrl.ept.domain.ContactMessage;
import zw.org.nmrl.ept.repository.ContactMessageRepository;
import zw.org.nmrl.ept.service.dto.ContactMessageDTO;
import zw.org.nmrl.ept.service.mapper.ContactMessageMapper;

/**
 * Integration tests for the {@link ContactMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContactMessageResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "a{aq3;@;dz.{HpIg";
    private static final String UPDATED_EMAIL = "s!)Z@S.s;_";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_HANDLED = false;
    private static final Boolean UPDATED_IS_HANDLED = true;

    private static final String ENTITY_API_URL = "/api/contact-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private ContactMessageMapper contactMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContactMessageMockMvc;

    private ContactMessage contactMessage;

    private ContactMessage insertedContactMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContactMessage createEntity() {
        return new ContactMessage()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .subject(DEFAULT_SUBJECT)
            .message(DEFAULT_MESSAGE)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .submittedAt(DEFAULT_SUBMITTED_AT)
            .isHandled(DEFAULT_IS_HANDLED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContactMessage createUpdatedEntity() {
        return new ContactMessage()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .subject(UPDATED_SUBJECT)
            .message(UPDATED_MESSAGE)
            .ipAddress(UPDATED_IP_ADDRESS)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .isHandled(UPDATED_IS_HANDLED);
    }

    @BeforeEach
    void initTest() {
        contactMessage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedContactMessage != null) {
            contactMessageRepository.delete(insertedContactMessage);
            insertedContactMessage = null;
        }
    }

    @Test
    @Transactional
    void createContactMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);
        var returnedContactMessageDTO = om.readValue(
            restContactMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ContactMessageDTO.class
        );

        // Validate the ContactMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedContactMessage = contactMessageMapper.toEntity(returnedContactMessageDTO);
        assertContactMessageUpdatableFieldsEquals(returnedContactMessage, getPersistedContactMessage(returnedContactMessage));

        insertedContactMessage = returnedContactMessage;
    }

    @Test
    @Transactional
    void createContactMessageWithExistingId() throws Exception {
        // Create the ContactMessage with an existing ID
        contactMessage.setId(1L);
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contactMessage.setName(null);

        // Create the ContactMessage, which fails.
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        restContactMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contactMessage.setEmail(null);

        // Create the ContactMessage, which fails.
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        restContactMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContactMessages() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList
        restContactMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contactMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].isHandled").value(hasItem(DEFAULT_IS_HANDLED)));
    }

    @Test
    @Transactional
    void getContactMessage() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get the contactMessage
        restContactMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, contactMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contactMessage.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.submittedAt").value(DEFAULT_SUBMITTED_AT.toString()))
            .andExpect(jsonPath("$.isHandled").value(DEFAULT_IS_HANDLED));
    }

    @Test
    @Transactional
    void getNonExistingContactMessage() throws Exception {
        // Get the contactMessage
        restContactMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContactMessage() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contactMessage
        ContactMessage updatedContactMessage = contactMessageRepository.findById(contactMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContactMessage are not directly saved in db
        em.detach(updatedContactMessage);
        updatedContactMessage
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .subject(UPDATED_SUBJECT)
            .message(UPDATED_MESSAGE)
            .ipAddress(UPDATED_IP_ADDRESS)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .isHandled(UPDATED_IS_HANDLED);
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(updatedContactMessage);

        restContactMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contactMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContactMessageToMatchAllProperties(updatedContactMessage);
    }

    @Test
    @Transactional
    void putNonExistingContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contactMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContactMessageWithPatch() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contactMessage using partial update
        ContactMessage partialUpdatedContactMessage = new ContactMessage();
        partialUpdatedContactMessage.setId(contactMessage.getId());

        partialUpdatedContactMessage.name(UPDATED_NAME).subject(UPDATED_SUBJECT).ipAddress(UPDATED_IP_ADDRESS);

        restContactMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContactMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContactMessage))
            )
            .andExpect(status().isOk());

        // Validate the ContactMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContactMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedContactMessage, contactMessage),
            getPersistedContactMessage(contactMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateContactMessageWithPatch() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contactMessage using partial update
        ContactMessage partialUpdatedContactMessage = new ContactMessage();
        partialUpdatedContactMessage.setId(contactMessage.getId());

        partialUpdatedContactMessage
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .subject(UPDATED_SUBJECT)
            .message(UPDATED_MESSAGE)
            .ipAddress(UPDATED_IP_ADDRESS)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .isHandled(UPDATED_IS_HANDLED);

        restContactMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContactMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContactMessage))
            )
            .andExpect(status().isOk());

        // Validate the ContactMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContactMessageUpdatableFieldsEquals(partialUpdatedContactMessage, getPersistedContactMessage(partialUpdatedContactMessage));
    }

    @Test
    @Transactional
    void patchNonExistingContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contactMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContactMessage() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contactMessage
        restContactMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, contactMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contactMessageRepository.count();
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

    protected ContactMessage getPersistedContactMessage(ContactMessage contactMessage) {
        return contactMessageRepository.findById(contactMessage.getId()).orElseThrow();
    }

    protected void assertPersistedContactMessageToMatchAllProperties(ContactMessage expectedContactMessage) {
        assertContactMessageAllPropertiesEquals(expectedContactMessage, getPersistedContactMessage(expectedContactMessage));
    }

    protected void assertPersistedContactMessageToMatchUpdatableProperties(ContactMessage expectedContactMessage) {
        assertContactMessageAllUpdatablePropertiesEquals(expectedContactMessage, getPersistedContactMessage(expectedContactMessage));
    }
}
