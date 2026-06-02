package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ReportConfigurationAsserts.*;
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
import zw.org.nmrl.ept.domain.ReportConfiguration;
import zw.org.nmrl.ept.repository.ReportConfigurationRepository;
import zw.org.nmrl.ept.service.dto.ReportConfigurationDTO;
import zw.org.nmrl.ept.service.mapper.ReportConfigurationMapper;

/**
 * Integration tests for the {@link ReportConfigurationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReportConfigurationResourceIT {

    private static final String DEFAULT_REPORT_HEADER = "AAAAAAAAAA";
    private static final String UPDATED_REPORT_HEADER = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_LOGO = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_RIGHT = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_RIGHT = "BBBBBBBBBB";

    private static final String DEFAULT_LAYOUT = "AAAAAAAAAA";
    private static final String UPDATED_LAYOUT = "BBBBBBBBBB";

    private static final String DEFAULT_FORMAT = "AAAAAAAAAA";
    private static final String UPDATED_FORMAT = "BBBBBBBBBB";

    private static final Integer DEFAULT_TOP_MARGIN = 1;
    private static final Integer UPDATED_TOP_MARGIN = 2;

    private static final String DEFAULT_INSTITUTE_ADDRESS_POSITION = "AAAAAAAAAA";
    private static final String UPDATED_INSTITUTE_ADDRESS_POSITION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/report-configurations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReportConfigurationRepository reportConfigurationRepository;

    @Autowired
    private ReportConfigurationMapper reportConfigurationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReportConfigurationMockMvc;

    private ReportConfiguration reportConfiguration;

    private ReportConfiguration insertedReportConfiguration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReportConfiguration createEntity() {
        return new ReportConfiguration()
            .reportHeader(DEFAULT_REPORT_HEADER)
            .logo(DEFAULT_LOGO)
            .logoRight(DEFAULT_LOGO_RIGHT)
            .layout(DEFAULT_LAYOUT)
            .format(DEFAULT_FORMAT)
            .topMargin(DEFAULT_TOP_MARGIN)
            .instituteAddressPosition(DEFAULT_INSTITUTE_ADDRESS_POSITION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReportConfiguration createUpdatedEntity() {
        return new ReportConfiguration()
            .reportHeader(UPDATED_REPORT_HEADER)
            .logo(UPDATED_LOGO)
            .logoRight(UPDATED_LOGO_RIGHT)
            .layout(UPDATED_LAYOUT)
            .format(UPDATED_FORMAT)
            .topMargin(UPDATED_TOP_MARGIN)
            .instituteAddressPosition(UPDATED_INSTITUTE_ADDRESS_POSITION);
    }

    @BeforeEach
    void initTest() {
        reportConfiguration = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReportConfiguration != null) {
            reportConfigurationRepository.delete(insertedReportConfiguration);
            insertedReportConfiguration = null;
        }
    }

    @Test
    @Transactional
    void createReportConfiguration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReportConfiguration
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(reportConfiguration);
        var returnedReportConfigurationDTO = om.readValue(
            restReportConfigurationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportConfigurationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReportConfigurationDTO.class
        );

        // Validate the ReportConfiguration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReportConfiguration = reportConfigurationMapper.toEntity(returnedReportConfigurationDTO);
        assertReportConfigurationUpdatableFieldsEquals(
            returnedReportConfiguration,
            getPersistedReportConfiguration(returnedReportConfiguration)
        );

        insertedReportConfiguration = returnedReportConfiguration;
    }

    @Test
    @Transactional
    void createReportConfigurationWithExistingId() throws Exception {
        // Create the ReportConfiguration with an existing ID
        reportConfiguration.setId(1L);
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(reportConfiguration);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReportConfigurationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportConfigurationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReportConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReportConfigurations() throws Exception {
        // Initialize the database
        insertedReportConfiguration = reportConfigurationRepository.saveAndFlush(reportConfiguration);

        // Get all the reportConfigurationList
        restReportConfigurationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reportConfiguration.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportHeader").value(hasItem(DEFAULT_REPORT_HEADER)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.[*].logoRight").value(hasItem(DEFAULT_LOGO_RIGHT)))
            .andExpect(jsonPath("$.[*].layout").value(hasItem(DEFAULT_LAYOUT)))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT)))
            .andExpect(jsonPath("$.[*].topMargin").value(hasItem(DEFAULT_TOP_MARGIN)))
            .andExpect(jsonPath("$.[*].instituteAddressPosition").value(hasItem(DEFAULT_INSTITUTE_ADDRESS_POSITION)));
    }

    @Test
    @Transactional
    void getReportConfiguration() throws Exception {
        // Initialize the database
        insertedReportConfiguration = reportConfigurationRepository.saveAndFlush(reportConfiguration);

        // Get the reportConfiguration
        restReportConfigurationMockMvc
            .perform(get(ENTITY_API_URL_ID, reportConfiguration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reportConfiguration.getId().intValue()))
            .andExpect(jsonPath("$.reportHeader").value(DEFAULT_REPORT_HEADER))
            .andExpect(jsonPath("$.logo").value(DEFAULT_LOGO))
            .andExpect(jsonPath("$.logoRight").value(DEFAULT_LOGO_RIGHT))
            .andExpect(jsonPath("$.layout").value(DEFAULT_LAYOUT))
            .andExpect(jsonPath("$.format").value(DEFAULT_FORMAT))
            .andExpect(jsonPath("$.topMargin").value(DEFAULT_TOP_MARGIN))
            .andExpect(jsonPath("$.instituteAddressPosition").value(DEFAULT_INSTITUTE_ADDRESS_POSITION));
    }

    @Test
    @Transactional
    void getNonExistingReportConfiguration() throws Exception {
        // Get the reportConfiguration
        restReportConfigurationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReportConfiguration() throws Exception {
        // Initialize the database
        insertedReportConfiguration = reportConfigurationRepository.saveAndFlush(reportConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reportConfiguration
        ReportConfiguration updatedReportConfiguration = reportConfigurationRepository.findById(reportConfiguration.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReportConfiguration are not directly saved in db
        em.detach(updatedReportConfiguration);
        updatedReportConfiguration
            .reportHeader(UPDATED_REPORT_HEADER)
            .logo(UPDATED_LOGO)
            .logoRight(UPDATED_LOGO_RIGHT)
            .layout(UPDATED_LAYOUT)
            .format(UPDATED_FORMAT)
            .topMargin(UPDATED_TOP_MARGIN)
            .instituteAddressPosition(UPDATED_INSTITUTE_ADDRESS_POSITION);
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(updatedReportConfiguration);

        restReportConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reportConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reportConfigurationDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReportConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReportConfigurationToMatchAllProperties(updatedReportConfiguration);
    }

    @Test
    @Transactional
    void putNonExistingReportConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportConfiguration.setId(longCount.incrementAndGet());

        // Create the ReportConfiguration
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(reportConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reportConfigurationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reportConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReportConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReportConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportConfiguration.setId(longCount.incrementAndGet());

        // Create the ReportConfiguration
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(reportConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportConfigurationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reportConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReportConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReportConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportConfiguration.setId(longCount.incrementAndGet());

        // Create the ReportConfiguration
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(reportConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportConfigurationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportConfigurationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReportConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReportConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedReportConfiguration = reportConfigurationRepository.saveAndFlush(reportConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reportConfiguration using partial update
        ReportConfiguration partialUpdatedReportConfiguration = new ReportConfiguration();
        partialUpdatedReportConfiguration.setId(reportConfiguration.getId());

        partialUpdatedReportConfiguration
            .logoRight(UPDATED_LOGO_RIGHT)
            .layout(UPDATED_LAYOUT)
            .format(UPDATED_FORMAT)
            .topMargin(UPDATED_TOP_MARGIN);

        restReportConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReportConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReportConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the ReportConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportConfigurationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReportConfiguration, reportConfiguration),
            getPersistedReportConfiguration(reportConfiguration)
        );
    }

    @Test
    @Transactional
    void fullUpdateReportConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedReportConfiguration = reportConfigurationRepository.saveAndFlush(reportConfiguration);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the reportConfiguration using partial update
        ReportConfiguration partialUpdatedReportConfiguration = new ReportConfiguration();
        partialUpdatedReportConfiguration.setId(reportConfiguration.getId());

        partialUpdatedReportConfiguration
            .reportHeader(UPDATED_REPORT_HEADER)
            .logo(UPDATED_LOGO)
            .logoRight(UPDATED_LOGO_RIGHT)
            .layout(UPDATED_LAYOUT)
            .format(UPDATED_FORMAT)
            .topMargin(UPDATED_TOP_MARGIN)
            .instituteAddressPosition(UPDATED_INSTITUTE_ADDRESS_POSITION);

        restReportConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReportConfiguration.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReportConfiguration))
            )
            .andExpect(status().isOk());

        // Validate the ReportConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportConfigurationUpdatableFieldsEquals(
            partialUpdatedReportConfiguration,
            getPersistedReportConfiguration(partialUpdatedReportConfiguration)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReportConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportConfiguration.setId(longCount.incrementAndGet());

        // Create the ReportConfiguration
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(reportConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reportConfigurationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reportConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReportConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReportConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportConfiguration.setId(longCount.incrementAndGet());

        // Create the ReportConfiguration
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(reportConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reportConfigurationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReportConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReportConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportConfiguration.setId(longCount.incrementAndGet());

        // Create the ReportConfiguration
        ReportConfigurationDTO reportConfigurationDTO = reportConfigurationMapper.toDto(reportConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportConfigurationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reportConfigurationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReportConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReportConfiguration() throws Exception {
        // Initialize the database
        insertedReportConfiguration = reportConfigurationRepository.saveAndFlush(reportConfiguration);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the reportConfiguration
        restReportConfigurationMockMvc
            .perform(delete(ENTITY_API_URL_ID, reportConfiguration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reportConfigurationRepository.count();
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

    protected ReportConfiguration getPersistedReportConfiguration(ReportConfiguration reportConfiguration) {
        return reportConfigurationRepository.findById(reportConfiguration.getId()).orElseThrow();
    }

    protected void assertPersistedReportConfigurationToMatchAllProperties(ReportConfiguration expectedReportConfiguration) {
        assertReportConfigurationAllPropertiesEquals(
            expectedReportConfiguration,
            getPersistedReportConfiguration(expectedReportConfiguration)
        );
    }

    protected void assertPersistedReportConfigurationToMatchUpdatableProperties(ReportConfiguration expectedReportConfiguration) {
        assertReportConfigurationAllUpdatablePropertiesEquals(
            expectedReportConfiguration,
            getPersistedReportConfiguration(expectedReportConfiguration)
        );
    }
}
