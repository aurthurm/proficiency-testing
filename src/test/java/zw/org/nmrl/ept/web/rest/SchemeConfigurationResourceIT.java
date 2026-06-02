package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.SchemeConfigurationAsserts.*;
import static zw.org.nmrl.ept.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.domain.SchemeConfiguration;
import zw.org.nmrl.ept.repository.SchemeConfigurationRepository;
import zw.org.nmrl.ept.service.dto.SchemeConfigurationDTO;
import zw.org.nmrl.ept.service.mapper.SchemeConfigurationMapper;

/**
 * Integration tests for the {@link SchemeConfigurationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SchemeConfigurationResourceIT {

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final LocalDate DEFAULT_EFFECTIVE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EFFECTIVE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_PASSING_SCORE = 1D;
    private static final Double UPDATED_PASSING_SCORE = 2D;

    private static final Double DEFAULT_DOCUMENTATION_WEIGHT = 1D;
    private static final Double UPDATED_DOCUMENTATION_WEIGHT = 2D;

    private static final Boolean DEFAULT_ALLOW_LATE_RESPONSE = false;
    private static final Boolean UPDATED_ALLOW_LATE_RESPONSE = true;

    private static final String DEFAULT_OPTIONAL_FIELDS = "AAAAAAAAAA";
    private static final String UPDATED_OPTIONAL_FIELDS = "BBBBBBBBBB";

    private static final String DEFAULT_SCORING_RULES = "AAAAAAAAAA";
    private static final String UPDATED_SCORING_RULES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/scheme-configurations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SchemeConfigurationRepository schemeConfigurationRepository;

    @Autowired
    private SchemeConfigurationMapper schemeConfigurationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSchemeConfigurationMockMvc;

    private SchemeConfiguration schemeConfiguration;

    private SchemeConfiguration insertedSchemeConfiguration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SchemeConfiguration createEntity(EntityManager em) {
        SchemeConfiguration schemeConfiguration = new SchemeConfiguration()
            .version(DEFAULT_VERSION)
            .effectiveDate(DEFAULT_EFFECTIVE_DATE)
            .passingScore(DEFAULT_PASSING_SCORE)
            .documentationWeight(DEFAULT_DOCUMENTATION_WEIGHT)
            .allowLateResponse(DEFAULT_ALLOW_LATE_RESPONSE)
            .optionalFields(DEFAULT_OPTIONAL_FIELDS)
            .scoringRules(DEFAULT_SCORING_RULES)
            .isActive(DEFAULT_IS_ACTIVE);
        // Add required entity
        Scheme scheme;
        if (TestUtil.findAll(em, Scheme.class).isEmpty()) {
            scheme = SchemeResourceIT.createEntity();
            em.persist(scheme);
            em.flush();
        } else {
            scheme = TestUtil.findAll(em, Scheme.class).get(0);
        }
        schemeConfiguration.setScheme(scheme);
        return schemeConfiguration;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SchemeConfiguration createUpdatedEntity(EntityManager em) {
        SchemeConfiguration updatedSchemeConfiguration = new SchemeConfiguration()
            .version(UPDATED_VERSION)
            .effectiveDate(UPDATED_EFFECTIVE_DATE)
            .passingScore(UPDATED_PASSING_SCORE)
            .documentationWeight(UPDATED_DOCUMENTATION_WEIGHT)
            .allowLateResponse(UPDATED_ALLOW_LATE_RESPONSE)
            .optionalFields(UPDATED_OPTIONAL_FIELDS)
            .scoringRules(UPDATED_SCORING_RULES)
            .isActive(UPDATED_IS_ACTIVE);
        // Add required entity
        Scheme scheme;
        if (TestUtil.findAll(em, Scheme.class).isEmpty()) {
            scheme = SchemeResourceIT.createUpdatedEntity();
            em.persist(scheme);
            em.flush();
        } else {
            scheme = TestUtil.findAll(em, Scheme.class).get(0);
        }
        updatedSchemeConfiguration.setScheme(scheme);
        return updatedSchemeConfiguration;
    }

    @BeforeEach
    void initTest() {
        schemeConfiguration = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSchemeConfiguration != null) {
            schemeConfigurationRepository.delete(insertedSchemeConfiguration);
            insertedSchemeConfiguration = null;
        }
    }

    @Test
    @Transactional
    void createSchemeConfiguration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SchemeConfiguration
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);
        var returnedSchemeConfigurationDTO = om.readValue(
            restSchemeConfigurationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeConfigurationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SchemeConfigurationDTO.class
        );

        // Validate the SchemeConfiguration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSchemeConfiguration = schemeConfigurationMapper.toEntity(returnedSchemeConfigurationDTO);
        assertSchemeConfigurationUpdatableFieldsEquals(
            returnedSchemeConfiguration,
            getPersistedSchemeConfiguration(returnedSchemeConfiguration)
        );

        insertedSchemeConfiguration = returnedSchemeConfiguration;
    }

    @Test
    @Transactional
    void createSchemeConfigurationWithExistingId() throws Exception {
        // Create the SchemeConfiguration with an existing ID
        schemeConfiguration.setId(1L);
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSchemeConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeConfigurationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SchemeConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        schemeConfiguration.setVersion(null);

        // Create the SchemeConfiguration, which fails.
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        restSchemeConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeConfigurationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEffectiveDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        schemeConfiguration.setEffectiveDate(null);

        // Create the SchemeConfiguration, which fails.
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        restSchemeConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeConfigurationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPassingScoreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        schemeConfiguration.setPassingScore(null);

        // Create the SchemeConfiguration, which fails.
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        restSchemeConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeConfigurationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSchemeConfigurations() throws Exception {
        // Initialize the database
        insertedSchemeConfiguration = schemeConfigurationRepository.saveAndFlush(schemeConfiguration);

        // Get all the schemeConfigurationList
        restSchemeConfigurationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schemeConfiguration.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].effectiveDate").value(hasItem(DEFAULT_EFFECTIVE_DATE.toString())))
            .andExpect(jsonPath("$.[*].passingScore").value(hasItem(DEFAULT_PASSING_SCORE)))
            .andExpect(jsonPath("$.[*].documentationWeight").value(hasItem(DEFAULT_DOCUMENTATION_WEIGHT)))
            .andExpect(jsonPath("$.[*].allowLateResponse").value(hasItem(DEFAULT_ALLOW_LATE_RESPONSE)))
            .andExpect(jsonPath("$.[*].optionalFields").value(hasItem(DEFAULT_OPTIONAL_FIELDS)))
            .andExpect(jsonPath("$.[*].scoringRules").value(hasItem(DEFAULT_SCORING_RULES)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getSchemeConfiguration() throws Exception {
        // Initialize the database
        insertedSchemeConfiguration = schemeConfigurationRepository.saveAndFlush(schemeConfiguration);

        // Get the schemeConfiguration
        restSchemeConfigurationMockMvc
            .perform(get(ENTITY_API_URL_ID, schemeConfiguration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(schemeConfiguration.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.effectiveDate").value(DEFAULT_EFFECTIVE_DATE.toString()))
            .andExpect(jsonPath("$.passingScore").value(DEFAULT_PASSING_SCORE))
            .andExpect(jsonPath("$.documentationWeight").value(DEFAULT_DOCUMENTATION_WEIGHT))
            .andExpect(jsonPath("$.allowLateResponse").value(DEFAULT_ALLOW_LATE_RESPONSE))
            .andExpect(jsonPath("$.optionalFields").value(DEFAULT_OPTIONAL_FIELDS))
            .andExpect(jsonPath("$.scoringRules").value(DEFAULT_SCORING_RULES))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingSchemeConfiguration() throws Exception {
        // Get the schemeConfiguration
        restSchemeConfigurationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSchemeConfiguration() throws Exception {
        // Initialize the database
        insertedSchemeConfiguration = schemeConfigurationRepository.saveAndFlush(schemeConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schemeConfiguration
        SchemeConfiguration updatedSchemeConfiguration = schemeConfigurationRepository.findById(schemeConfiguration.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSchemeConfiguration are not directly saved in db
        em.detach(updatedSchemeConfiguration);
        updatedSchemeConfiguration
            .version(UPDATED_VERSION)
            .effectiveDate(UPDATED_EFFECTIVE_DATE)
            .passingScore(UPDATED_PASSING_SCORE)
            .documentationWeight(UPDATED_DOCUMENTATION_WEIGHT)
            .allowLateResponse(UPDATED_ALLOW_LATE_RESPONSE)
            .optionalFields(UPDATED_OPTIONAL_FIELDS)
            .scoringRules(UPDATED_SCORING_RULES)
            .isActive(UPDATED_IS_ACTIVE);
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(updatedSchemeConfiguration);

        restSchemeConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, schemeConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(schemeConfigurationDTO))
            )
            .andExpect(status().isOk());

        // Validate the SchemeConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSchemeConfigurationToMatchAllProperties(updatedSchemeConfiguration);
    }

    @Test
    @Transactional
    void putNonExistingSchemeConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schemeConfiguration.setId(longCount.incrementAndGet());

        // Create the SchemeConfiguration
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSchemeConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, schemeConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(schemeConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SchemeConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSchemeConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schemeConfiguration.setId(longCount.incrementAndGet());

        // Create the SchemeConfiguration
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSchemeConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(schemeConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SchemeConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSchemeConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schemeConfiguration.setId(longCount.incrementAndGet());

        // Create the SchemeConfiguration
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSchemeConfigurationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeConfigurationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SchemeConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSchemeConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedSchemeConfiguration = schemeConfigurationRepository.saveAndFlush(schemeConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schemeConfiguration using partial update
        SchemeConfiguration partialUpdatedSchemeConfiguration = new SchemeConfiguration();
        partialUpdatedSchemeConfiguration.setId(schemeConfiguration.getId());

        partialUpdatedSchemeConfiguration.optionalFields(UPDATED_OPTIONAL_FIELDS).scoringRules(UPDATED_SCORING_RULES);

        restSchemeConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchemeConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSchemeConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the SchemeConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSchemeConfigurationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSchemeConfiguration, schemeConfiguration),
            getPersistedSchemeConfiguration(schemeConfiguration)
        );
    }

    @Test
    @Transactional
    void fullUpdateSchemeConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedSchemeConfiguration = schemeConfigurationRepository.saveAndFlush(schemeConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the schemeConfiguration using partial update
        SchemeConfiguration partialUpdatedSchemeConfiguration = new SchemeConfiguration();
        partialUpdatedSchemeConfiguration.setId(schemeConfiguration.getId());

        partialUpdatedSchemeConfiguration
            .version(UPDATED_VERSION)
            .effectiveDate(UPDATED_EFFECTIVE_DATE)
            .passingScore(UPDATED_PASSING_SCORE)
            .documentationWeight(UPDATED_DOCUMENTATION_WEIGHT)
            .allowLateResponse(UPDATED_ALLOW_LATE_RESPONSE)
            .optionalFields(UPDATED_OPTIONAL_FIELDS)
            .scoringRules(UPDATED_SCORING_RULES)
            .isActive(UPDATED_IS_ACTIVE);

        restSchemeConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchemeConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSchemeConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the SchemeConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSchemeConfigurationUpdatableFieldsEquals(
            partialUpdatedSchemeConfiguration,
            getPersistedSchemeConfiguration(partialUpdatedSchemeConfiguration)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSchemeConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schemeConfiguration.setId(longCount.incrementAndGet());

        // Create the SchemeConfiguration
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSchemeConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, schemeConfigurationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(schemeConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SchemeConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSchemeConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schemeConfiguration.setId(longCount.incrementAndGet());

        // Create the SchemeConfiguration
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSchemeConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(schemeConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SchemeConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSchemeConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        schemeConfiguration.setId(longCount.incrementAndGet());

        // Create the SchemeConfiguration
        SchemeConfigurationDTO schemeConfigurationDTO = schemeConfigurationMapper.toDto(schemeConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSchemeConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(schemeConfigurationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SchemeConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSchemeConfiguration() throws Exception {
        // Initialize the database
        insertedSchemeConfiguration = schemeConfigurationRepository.saveAndFlush(schemeConfiguration);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the schemeConfiguration
        restSchemeConfigurationMockMvc
            .perform(delete(ENTITY_API_URL_ID, schemeConfiguration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return schemeConfigurationRepository.count();
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

    protected SchemeConfiguration getPersistedSchemeConfiguration(SchemeConfiguration schemeConfiguration) {
        return schemeConfigurationRepository.findById(schemeConfiguration.getId()).orElseThrow();
    }

    protected void assertPersistedSchemeConfigurationToMatchAllProperties(SchemeConfiguration expectedSchemeConfiguration) {
        assertSchemeConfigurationAllPropertiesEquals(
            expectedSchemeConfiguration,
            getPersistedSchemeConfiguration(expectedSchemeConfiguration)
        );
    }

    protected void assertPersistedSchemeConfigurationToMatchUpdatableProperties(SchemeConfiguration expectedSchemeConfiguration) {
        assertSchemeConfigurationAllUpdatablePropertiesEquals(
            expectedSchemeConfiguration,
            getPersistedSchemeConfiguration(expectedSchemeConfiguration)
        );
    }
}
