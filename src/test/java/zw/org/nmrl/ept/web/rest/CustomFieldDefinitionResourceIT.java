package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.CustomFieldDefinitionAsserts.*;
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
import zw.org.nmrl.ept.domain.enumeration.CustomFieldType;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.CustomFieldDefinitionRepository;
import zw.org.nmrl.ept.service.dto.CustomFieldDefinitionDTO;
import zw.org.nmrl.ept.service.mapper.CustomFieldDefinitionMapper;

/**
 * Integration tests for the {@link CustomFieldDefinitionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomFieldDefinitionResourceIT {

    private static final String DEFAULT_FIELD_KEY = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final CustomFieldType DEFAULT_FIELD_TYPE = CustomFieldType.TEXT;
    private static final CustomFieldType UPDATED_FIELD_TYPE = CustomFieldType.NUMBER;

    private static final String DEFAULT_OPTIONS = "AAAAAAAAAA";
    private static final String UPDATED_OPTIONS = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/custom-field-definitions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomFieldDefinitionRepository customFieldDefinitionRepository;

    @Autowired
    private CustomFieldDefinitionMapper customFieldDefinitionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomFieldDefinitionMockMvc;

    private CustomFieldDefinition customFieldDefinition;

    private CustomFieldDefinition insertedCustomFieldDefinition;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomFieldDefinition createEntity() {
        return new CustomFieldDefinition()
            .fieldKey(DEFAULT_FIELD_KEY)
            .label(DEFAULT_LABEL)
            .fieldType(DEFAULT_FIELD_TYPE)
            .options(DEFAULT_OPTIONS)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomFieldDefinition createUpdatedEntity() {
        return new CustomFieldDefinition()
            .fieldKey(UPDATED_FIELD_KEY)
            .label(UPDATED_LABEL)
            .fieldType(UPDATED_FIELD_TYPE)
            .options(UPDATED_OPTIONS)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        customFieldDefinition = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCustomFieldDefinition != null) {
            customFieldDefinitionRepository.delete(insertedCustomFieldDefinition);
            insertedCustomFieldDefinition = null;
        }
    }

    @Test
    @Transactional
    void createCustomFieldDefinition() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CustomFieldDefinition
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);
        var returnedCustomFieldDefinitionDTO = om.readValue(
            restCustomFieldDefinitionMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customFieldDefinitionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CustomFieldDefinitionDTO.class
        );

        // Validate the CustomFieldDefinition in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomFieldDefinition = customFieldDefinitionMapper.toEntity(returnedCustomFieldDefinitionDTO);
        assertCustomFieldDefinitionUpdatableFieldsEquals(
            returnedCustomFieldDefinition,
            getPersistedCustomFieldDefinition(returnedCustomFieldDefinition)
        );

        insertedCustomFieldDefinition = returnedCustomFieldDefinition;
    }

    @Test
    @Transactional
    void createCustomFieldDefinitionWithExistingId() throws Exception {
        // Create the CustomFieldDefinition with an existing ID
        customFieldDefinition.setId(1L);
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomFieldDefinitionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customFieldDefinitionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CustomFieldDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFieldKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customFieldDefinition.setFieldKey(null);

        // Create the CustomFieldDefinition, which fails.
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        restCustomFieldDefinitionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customFieldDefinitionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customFieldDefinition.setLabel(null);

        // Create the CustomFieldDefinition, which fails.
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        restCustomFieldDefinitionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customFieldDefinitionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFieldTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customFieldDefinition.setFieldType(null);

        // Create the CustomFieldDefinition, which fails.
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        restCustomFieldDefinitionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customFieldDefinitionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customFieldDefinition.setStatus(null);

        // Create the CustomFieldDefinition, which fails.
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        restCustomFieldDefinitionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customFieldDefinitionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCustomFieldDefinitions() throws Exception {
        // Initialize the database
        insertedCustomFieldDefinition = customFieldDefinitionRepository.saveAndFlush(customFieldDefinition);

        // Get all the customFieldDefinitionList
        restCustomFieldDefinitionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customFieldDefinition.getId().intValue())))
            .andExpect(jsonPath("$.[*].fieldKey").value(hasItem(DEFAULT_FIELD_KEY)))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].fieldType").value(hasItem(DEFAULT_FIELD_TYPE.toString())))
            .andExpect(jsonPath("$.[*].options").value(hasItem(DEFAULT_OPTIONS)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getCustomFieldDefinition() throws Exception {
        // Initialize the database
        insertedCustomFieldDefinition = customFieldDefinitionRepository.saveAndFlush(customFieldDefinition);

        // Get the customFieldDefinition
        restCustomFieldDefinitionMockMvc
            .perform(get(ENTITY_API_URL_ID, customFieldDefinition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customFieldDefinition.getId().intValue()))
            .andExpect(jsonPath("$.fieldKey").value(DEFAULT_FIELD_KEY))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.fieldType").value(DEFAULT_FIELD_TYPE.toString()))
            .andExpect(jsonPath("$.options").value(DEFAULT_OPTIONS))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCustomFieldDefinition() throws Exception {
        // Get the customFieldDefinition
        restCustomFieldDefinitionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomFieldDefinition() throws Exception {
        // Initialize the database
        insertedCustomFieldDefinition = customFieldDefinitionRepository.saveAndFlush(customFieldDefinition);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customFieldDefinition
        CustomFieldDefinition updatedCustomFieldDefinition = customFieldDefinitionRepository
            .findById(customFieldDefinition.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCustomFieldDefinition are not directly saved in db
        em.detach(updatedCustomFieldDefinition);
        updatedCustomFieldDefinition
            .fieldKey(UPDATED_FIELD_KEY)
            .label(UPDATED_LABEL)
            .fieldType(UPDATED_FIELD_TYPE)
            .options(UPDATED_OPTIONS)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .status(UPDATED_STATUS);
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(updatedCustomFieldDefinition);

        restCustomFieldDefinitionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customFieldDefinitionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customFieldDefinitionDTO))
            )
            .andExpect(status().isOk());

        // Validate the CustomFieldDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomFieldDefinitionToMatchAllProperties(updatedCustomFieldDefinition);
    }

    @Test
    @Transactional
    void putNonExistingCustomFieldDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customFieldDefinition.setId(longCount.incrementAndGet());

        // Create the CustomFieldDefinition
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomFieldDefinitionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customFieldDefinitionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customFieldDefinitionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomFieldDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomFieldDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customFieldDefinition.setId(longCount.incrementAndGet());

        // Create the CustomFieldDefinition
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomFieldDefinitionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customFieldDefinitionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomFieldDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomFieldDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customFieldDefinition.setId(longCount.incrementAndGet());

        // Create the CustomFieldDefinition
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomFieldDefinitionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customFieldDefinitionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomFieldDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomFieldDefinitionWithPatch() throws Exception {
        // Initialize the database
        insertedCustomFieldDefinition = customFieldDefinitionRepository.saveAndFlush(customFieldDefinition);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customFieldDefinition using partial update
        CustomFieldDefinition partialUpdatedCustomFieldDefinition = new CustomFieldDefinition();
        partialUpdatedCustomFieldDefinition.setId(customFieldDefinition.getId());

        partialUpdatedCustomFieldDefinition.options(UPDATED_OPTIONS);

        restCustomFieldDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomFieldDefinition.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomFieldDefinition))
            )
            .andExpect(status().isOk());

        // Validate the CustomFieldDefinition in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomFieldDefinitionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCustomFieldDefinition, customFieldDefinition),
            getPersistedCustomFieldDefinition(customFieldDefinition)
        );
    }

    @Test
    @Transactional
    void fullUpdateCustomFieldDefinitionWithPatch() throws Exception {
        // Initialize the database
        insertedCustomFieldDefinition = customFieldDefinitionRepository.saveAndFlush(customFieldDefinition);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customFieldDefinition using partial update
        CustomFieldDefinition partialUpdatedCustomFieldDefinition = new CustomFieldDefinition();
        partialUpdatedCustomFieldDefinition.setId(customFieldDefinition.getId());

        partialUpdatedCustomFieldDefinition
            .fieldKey(UPDATED_FIELD_KEY)
            .label(UPDATED_LABEL)
            .fieldType(UPDATED_FIELD_TYPE)
            .options(UPDATED_OPTIONS)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .status(UPDATED_STATUS);

        restCustomFieldDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomFieldDefinition.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomFieldDefinition))
            )
            .andExpect(status().isOk());

        // Validate the CustomFieldDefinition in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomFieldDefinitionUpdatableFieldsEquals(
            partialUpdatedCustomFieldDefinition,
            getPersistedCustomFieldDefinition(partialUpdatedCustomFieldDefinition)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCustomFieldDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customFieldDefinition.setId(longCount.incrementAndGet());

        // Create the CustomFieldDefinition
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomFieldDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customFieldDefinitionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customFieldDefinitionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomFieldDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomFieldDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customFieldDefinition.setId(longCount.incrementAndGet());

        // Create the CustomFieldDefinition
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomFieldDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customFieldDefinitionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomFieldDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomFieldDefinition() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customFieldDefinition.setId(longCount.incrementAndGet());

        // Create the CustomFieldDefinition
        CustomFieldDefinitionDTO customFieldDefinitionDTO = customFieldDefinitionMapper.toDto(customFieldDefinition);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomFieldDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(customFieldDefinitionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomFieldDefinition in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomFieldDefinition() throws Exception {
        // Initialize the database
        insertedCustomFieldDefinition = customFieldDefinitionRepository.saveAndFlush(customFieldDefinition);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customFieldDefinition
        restCustomFieldDefinitionMockMvc
            .perform(delete(ENTITY_API_URL_ID, customFieldDefinition.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customFieldDefinitionRepository.count();
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

    protected CustomFieldDefinition getPersistedCustomFieldDefinition(CustomFieldDefinition customFieldDefinition) {
        return customFieldDefinitionRepository.findById(customFieldDefinition.getId()).orElseThrow();
    }

    protected void assertPersistedCustomFieldDefinitionToMatchAllProperties(CustomFieldDefinition expectedCustomFieldDefinition) {
        assertCustomFieldDefinitionAllPropertiesEquals(
            expectedCustomFieldDefinition,
            getPersistedCustomFieldDefinition(expectedCustomFieldDefinition)
        );
    }

    protected void assertPersistedCustomFieldDefinitionToMatchUpdatableProperties(CustomFieldDefinition expectedCustomFieldDefinition) {
        assertCustomFieldDefinitionAllUpdatablePropertiesEquals(
            expectedCustomFieldDefinition,
            getPersistedCustomFieldDefinition(expectedCustomFieldDefinition)
        );
    }
}
