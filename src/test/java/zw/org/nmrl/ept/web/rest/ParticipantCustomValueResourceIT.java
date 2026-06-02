package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ParticipantCustomValueAsserts.*;
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
import zw.org.nmrl.ept.domain.CustomFieldDefinition;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.ParticipantCustomValue;
import zw.org.nmrl.ept.repository.ParticipantCustomValueRepository;
import zw.org.nmrl.ept.service.dto.ParticipantCustomValueDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantCustomValueMapper;

/**
 * Integration tests for the {@link ParticipantCustomValueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParticipantCustomValueResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/participant-custom-values";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParticipantCustomValueRepository participantCustomValueRepository;

    @Autowired
    private ParticipantCustomValueMapper participantCustomValueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParticipantCustomValueMockMvc;

    private ParticipantCustomValue participantCustomValue;

    private ParticipantCustomValue insertedParticipantCustomValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParticipantCustomValue createEntity(EntityManager em) {
        ParticipantCustomValue participantCustomValue = new ParticipantCustomValue().value(DEFAULT_VALUE);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        participantCustomValue.setParticipant(participant);
        // Add required entity
        CustomFieldDefinition customFieldDefinition;
        if (TestUtil.findAll(em, CustomFieldDefinition.class).isEmpty()) {
            customFieldDefinition = CustomFieldDefinitionResourceIT.createEntity();
            em.persist(customFieldDefinition);
            em.flush();
        } else {
            customFieldDefinition = TestUtil.findAll(em, CustomFieldDefinition.class).get(0);
        }
        participantCustomValue.setDefinition(customFieldDefinition);
        return participantCustomValue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParticipantCustomValue createUpdatedEntity(EntityManager em) {
        ParticipantCustomValue updatedParticipantCustomValue = new ParticipantCustomValue().value(UPDATED_VALUE);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createUpdatedEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        updatedParticipantCustomValue.setParticipant(participant);
        // Add required entity
        CustomFieldDefinition customFieldDefinition;
        if (TestUtil.findAll(em, CustomFieldDefinition.class).isEmpty()) {
            customFieldDefinition = CustomFieldDefinitionResourceIT.createUpdatedEntity();
            em.persist(customFieldDefinition);
            em.flush();
        } else {
            customFieldDefinition = TestUtil.findAll(em, CustomFieldDefinition.class).get(0);
        }
        updatedParticipantCustomValue.setDefinition(customFieldDefinition);
        return updatedParticipantCustomValue;
    }

    @BeforeEach
    void initTest() {
        participantCustomValue = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedParticipantCustomValue != null) {
            participantCustomValueRepository.delete(insertedParticipantCustomValue);
            insertedParticipantCustomValue = null;
        }
    }

    @Test
    @Transactional
    void createParticipantCustomValue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ParticipantCustomValue
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(participantCustomValue);
        var returnedParticipantCustomValueDTO = om.readValue(
            restParticipantCustomValueMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantCustomValueDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParticipantCustomValueDTO.class
        );

        // Validate the ParticipantCustomValue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParticipantCustomValue = participantCustomValueMapper.toEntity(returnedParticipantCustomValueDTO);
        assertParticipantCustomValueUpdatableFieldsEquals(
            returnedParticipantCustomValue,
            getPersistedParticipantCustomValue(returnedParticipantCustomValue)
        );

        insertedParticipantCustomValue = returnedParticipantCustomValue;
    }

    @Test
    @Transactional
    void createParticipantCustomValueWithExistingId() throws Exception {
        // Create the ParticipantCustomValue with an existing ID
        participantCustomValue.setId(1L);
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(participantCustomValue);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantCustomValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantCustomValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParticipantCustomValue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllParticipantCustomValues() throws Exception {
        // Initialize the database
        insertedParticipantCustomValue = participantCustomValueRepository.saveAndFlush(participantCustomValue);

        // Get all the participantCustomValueList
        restParticipantCustomValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participantCustomValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getParticipantCustomValue() throws Exception {
        // Initialize the database
        insertedParticipantCustomValue = participantCustomValueRepository.saveAndFlush(participantCustomValue);

        // Get the participantCustomValue
        restParticipantCustomValueMockMvc
            .perform(get(ENTITY_API_URL_ID, participantCustomValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participantCustomValue.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingParticipantCustomValue() throws Exception {
        // Get the participantCustomValue
        restParticipantCustomValueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParticipantCustomValue() throws Exception {
        // Initialize the database
        insertedParticipantCustomValue = participantCustomValueRepository.saveAndFlush(participantCustomValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantCustomValue
        ParticipantCustomValue updatedParticipantCustomValue = participantCustomValueRepository
            .findById(participantCustomValue.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedParticipantCustomValue are not directly saved in db
        em.detach(updatedParticipantCustomValue);
        updatedParticipantCustomValue.value(UPDATED_VALUE);
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(updatedParticipantCustomValue);

        restParticipantCustomValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantCustomValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantCustomValueDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantCustomValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParticipantCustomValueToMatchAllProperties(updatedParticipantCustomValue);
    }

    @Test
    @Transactional
    void putNonExistingParticipantCustomValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantCustomValue.setId(longCount.incrementAndGet());

        // Create the ParticipantCustomValue
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(participantCustomValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantCustomValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantCustomValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantCustomValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantCustomValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParticipantCustomValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantCustomValue.setId(longCount.incrementAndGet());

        // Create the ParticipantCustomValue
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(participantCustomValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantCustomValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantCustomValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantCustomValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParticipantCustomValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantCustomValue.setId(longCount.incrementAndGet());

        // Create the ParticipantCustomValue
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(participantCustomValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantCustomValueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantCustomValueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParticipantCustomValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParticipantCustomValueWithPatch() throws Exception {
        // Initialize the database
        insertedParticipantCustomValue = participantCustomValueRepository.saveAndFlush(participantCustomValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantCustomValue using partial update
        ParticipantCustomValue partialUpdatedParticipantCustomValue = new ParticipantCustomValue();
        partialUpdatedParticipantCustomValue.setId(participantCustomValue.getId());

        restParticipantCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipantCustomValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipantCustomValue))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantCustomValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantCustomValueUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParticipantCustomValue, participantCustomValue),
            getPersistedParticipantCustomValue(participantCustomValue)
        );
    }

    @Test
    @Transactional
    void fullUpdateParticipantCustomValueWithPatch() throws Exception {
        // Initialize the database
        insertedParticipantCustomValue = participantCustomValueRepository.saveAndFlush(participantCustomValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantCustomValue using partial update
        ParticipantCustomValue partialUpdatedParticipantCustomValue = new ParticipantCustomValue();
        partialUpdatedParticipantCustomValue.setId(participantCustomValue.getId());

        partialUpdatedParticipantCustomValue.value(UPDATED_VALUE);

        restParticipantCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipantCustomValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipantCustomValue))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantCustomValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantCustomValueUpdatableFieldsEquals(
            partialUpdatedParticipantCustomValue,
            getPersistedParticipantCustomValue(partialUpdatedParticipantCustomValue)
        );
    }

    @Test
    @Transactional
    void patchNonExistingParticipantCustomValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantCustomValue.setId(longCount.incrementAndGet());

        // Create the ParticipantCustomValue
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(participantCustomValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, participantCustomValueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantCustomValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantCustomValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParticipantCustomValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantCustomValue.setId(longCount.incrementAndGet());

        // Create the ParticipantCustomValue
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(participantCustomValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantCustomValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantCustomValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParticipantCustomValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantCustomValue.setId(longCount.incrementAndGet());

        // Create the ParticipantCustomValue
        ParticipantCustomValueDTO participantCustomValueDTO = participantCustomValueMapper.toDto(participantCustomValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantCustomValueMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(participantCustomValueDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParticipantCustomValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParticipantCustomValue() throws Exception {
        // Initialize the database
        insertedParticipantCustomValue = participantCustomValueRepository.saveAndFlush(participantCustomValue);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the participantCustomValue
        restParticipantCustomValueMockMvc
            .perform(delete(ENTITY_API_URL_ID, participantCustomValue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return participantCustomValueRepository.count();
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

    protected ParticipantCustomValue getPersistedParticipantCustomValue(ParticipantCustomValue participantCustomValue) {
        return participantCustomValueRepository.findById(participantCustomValue.getId()).orElseThrow();
    }

    protected void assertPersistedParticipantCustomValueToMatchAllProperties(ParticipantCustomValue expectedParticipantCustomValue) {
        assertParticipantCustomValueAllPropertiesEquals(
            expectedParticipantCustomValue,
            getPersistedParticipantCustomValue(expectedParticipantCustomValue)
        );
    }

    protected void assertPersistedParticipantCustomValueToMatchUpdatableProperties(ParticipantCustomValue expectedParticipantCustomValue) {
        assertParticipantCustomValueAllUpdatablePropertiesEquals(
            expectedParticipantCustomValue,
            getPersistedParticipantCustomValue(expectedParticipantCustomValue)
        );
    }
}
