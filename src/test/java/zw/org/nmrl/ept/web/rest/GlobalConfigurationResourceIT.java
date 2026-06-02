package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.GlobalConfigurationAsserts.*;
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
import zw.org.nmrl.ept.domain.GlobalConfiguration;
import zw.org.nmrl.ept.repository.GlobalConfigurationRepository;
import zw.org.nmrl.ept.service.dto.GlobalConfigurationDTO;
import zw.org.nmrl.ept.service.mapper.GlobalConfigurationMapper;

/**
 * Integration tests for the {@link GlobalConfigurationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GlobalConfigurationResourceIT {

    private static final String DEFAULT_CONFIG_KEY = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_CONFIG_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/global-configurations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GlobalConfigurationRepository globalConfigurationRepository;

    @Autowired
    private GlobalConfigurationMapper globalConfigurationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGlobalConfigurationMockMvc;

    private GlobalConfiguration globalConfiguration;

    private GlobalConfiguration insertedGlobalConfiguration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GlobalConfiguration createEntity() {
        return new GlobalConfiguration().configKey(DEFAULT_CONFIG_KEY).configValue(DEFAULT_CONFIG_VALUE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GlobalConfiguration createUpdatedEntity() {
        return new GlobalConfiguration().configKey(UPDATED_CONFIG_KEY).configValue(UPDATED_CONFIG_VALUE).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        globalConfiguration = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGlobalConfiguration != null) {
            globalConfigurationRepository.delete(insertedGlobalConfiguration);
            insertedGlobalConfiguration = null;
        }
    }

    @Test
    @Transactional
    void createGlobalConfiguration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the GlobalConfiguration
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);
        var returnedGlobalConfigurationDTO = om.readValue(
            restGlobalConfigurationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(globalConfigurationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GlobalConfigurationDTO.class
        );

        // Validate the GlobalConfiguration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGlobalConfiguration = globalConfigurationMapper.toEntity(returnedGlobalConfigurationDTO);
        assertGlobalConfigurationUpdatableFieldsEquals(
            returnedGlobalConfiguration,
            getPersistedGlobalConfiguration(returnedGlobalConfiguration)
        );

        insertedGlobalConfiguration = returnedGlobalConfiguration;
    }

    @Test
    @Transactional
    void createGlobalConfigurationWithExistingId() throws Exception {
        // Create the GlobalConfiguration with an existing ID
        globalConfiguration.setId(1L);
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGlobalConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(globalConfigurationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the GlobalConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkConfigKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        globalConfiguration.setConfigKey(null);

        // Create the GlobalConfiguration, which fails.
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);

        restGlobalConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(globalConfigurationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGlobalConfigurations() throws Exception {
        // Initialize the database
        insertedGlobalConfiguration = globalConfigurationRepository.saveAndFlush(globalConfiguration);

        // Get all the globalConfigurationList
        restGlobalConfigurationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(globalConfiguration.getId().intValue())))
            .andExpect(jsonPath("$.[*].configKey").value(hasItem(DEFAULT_CONFIG_KEY)))
            .andExpect(jsonPath("$.[*].configValue").value(hasItem(DEFAULT_CONFIG_VALUE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getGlobalConfiguration() throws Exception {
        // Initialize the database
        insertedGlobalConfiguration = globalConfigurationRepository.saveAndFlush(globalConfiguration);

        // Get the globalConfiguration
        restGlobalConfigurationMockMvc
            .perform(get(ENTITY_API_URL_ID, globalConfiguration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(globalConfiguration.getId().intValue()))
            .andExpect(jsonPath("$.configKey").value(DEFAULT_CONFIG_KEY))
            .andExpect(jsonPath("$.configValue").value(DEFAULT_CONFIG_VALUE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingGlobalConfiguration() throws Exception {
        // Get the globalConfiguration
        restGlobalConfigurationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGlobalConfiguration() throws Exception {
        // Initialize the database
        insertedGlobalConfiguration = globalConfigurationRepository.saveAndFlush(globalConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the globalConfiguration
        GlobalConfiguration updatedGlobalConfiguration = globalConfigurationRepository.findById(globalConfiguration.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGlobalConfiguration are not directly saved in db
        em.detach(updatedGlobalConfiguration);
        updatedGlobalConfiguration.configKey(UPDATED_CONFIG_KEY).configValue(UPDATED_CONFIG_VALUE).description(UPDATED_DESCRIPTION);
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(updatedGlobalConfiguration);

        restGlobalConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, globalConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(globalConfigurationDTO))
            )
            .andExpect(status().isOk());

        // Validate the GlobalConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGlobalConfigurationToMatchAllProperties(updatedGlobalConfiguration);
    }

    @Test
    @Transactional
    void putNonExistingGlobalConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        globalConfiguration.setId(longCount.incrementAndGet());

        // Create the GlobalConfiguration
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGlobalConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, globalConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(globalConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GlobalConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGlobalConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        globalConfiguration.setId(longCount.incrementAndGet());

        // Create the GlobalConfiguration
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGlobalConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(globalConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GlobalConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGlobalConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        globalConfiguration.setId(longCount.incrementAndGet());

        // Create the GlobalConfiguration
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGlobalConfigurationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(globalConfigurationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GlobalConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGlobalConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedGlobalConfiguration = globalConfigurationRepository.saveAndFlush(globalConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the globalConfiguration using partial update
        GlobalConfiguration partialUpdatedGlobalConfiguration = new GlobalConfiguration();
        partialUpdatedGlobalConfiguration.setId(globalConfiguration.getId());

        partialUpdatedGlobalConfiguration.configValue(UPDATED_CONFIG_VALUE);

        restGlobalConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGlobalConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGlobalConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the GlobalConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGlobalConfigurationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGlobalConfiguration, globalConfiguration),
            getPersistedGlobalConfiguration(globalConfiguration)
        );
    }

    @Test
    @Transactional
    void fullUpdateGlobalConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedGlobalConfiguration = globalConfigurationRepository.saveAndFlush(globalConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the globalConfiguration using partial update
        GlobalConfiguration partialUpdatedGlobalConfiguration = new GlobalConfiguration();
        partialUpdatedGlobalConfiguration.setId(globalConfiguration.getId());

        partialUpdatedGlobalConfiguration.configKey(UPDATED_CONFIG_KEY).configValue(UPDATED_CONFIG_VALUE).description(UPDATED_DESCRIPTION);

        restGlobalConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGlobalConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGlobalConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the GlobalConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGlobalConfigurationUpdatableFieldsEquals(
            partialUpdatedGlobalConfiguration,
            getPersistedGlobalConfiguration(partialUpdatedGlobalConfiguration)
        );
    }

    @Test
    @Transactional
    void patchNonExistingGlobalConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        globalConfiguration.setId(longCount.incrementAndGet());

        // Create the GlobalConfiguration
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGlobalConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, globalConfigurationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(globalConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GlobalConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGlobalConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        globalConfiguration.setId(longCount.incrementAndGet());

        // Create the GlobalConfiguration
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGlobalConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(globalConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GlobalConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGlobalConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        globalConfiguration.setId(longCount.incrementAndGet());

        // Create the GlobalConfiguration
        GlobalConfigurationDTO globalConfigurationDTO = globalConfigurationMapper.toDto(globalConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGlobalConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(globalConfigurationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GlobalConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGlobalConfiguration() throws Exception {
        // Initialize the database
        insertedGlobalConfiguration = globalConfigurationRepository.saveAndFlush(globalConfiguration);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the globalConfiguration
        restGlobalConfigurationMockMvc
            .perform(delete(ENTITY_API_URL_ID, globalConfiguration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return globalConfigurationRepository.count();
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

    protected GlobalConfiguration getPersistedGlobalConfiguration(GlobalConfiguration globalConfiguration) {
        return globalConfigurationRepository.findById(globalConfiguration.getId()).orElseThrow();
    }

    protected void assertPersistedGlobalConfigurationToMatchAllProperties(GlobalConfiguration expectedGlobalConfiguration) {
        assertGlobalConfigurationAllPropertiesEquals(
            expectedGlobalConfiguration,
            getPersistedGlobalConfiguration(expectedGlobalConfiguration)
        );
    }

    protected void assertPersistedGlobalConfigurationToMatchUpdatableProperties(GlobalConfiguration expectedGlobalConfiguration) {
        assertGlobalConfigurationAllUpdatablePropertiesEquals(
            expectedGlobalConfiguration,
            getPersistedGlobalConfiguration(expectedGlobalConfiguration)
        );
    }
}
