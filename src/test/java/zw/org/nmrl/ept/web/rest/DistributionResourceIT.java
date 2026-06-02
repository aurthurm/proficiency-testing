package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.DistributionAsserts.*;
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
import zw.org.nmrl.ept.domain.Distribution;
import zw.org.nmrl.ept.domain.enumeration.DistributionStatus;
import zw.org.nmrl.ept.repository.DistributionRepository;
import zw.org.nmrl.ept.service.dto.DistributionDTO;
import zw.org.nmrl.ept.service.mapper.DistributionMapper;

/**
 * Integration tests for the {@link DistributionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DistributionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DISTRIBUTION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DISTRIBUTION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DISTRIBUTION_DATE = LocalDate.ofEpochDay(-1L);

    private static final DistributionStatus DEFAULT_STATUS = DistributionStatus.DRAFT;
    private static final DistributionStatus UPDATED_STATUS = DistributionStatus.OPEN;

    private static final String ENTITY_API_URL = "/api/distributions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DistributionRepository distributionRepository;

    @Autowired
    private DistributionMapper distributionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDistributionMockMvc;

    private Distribution distribution;

    private Distribution insertedDistribution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Distribution createEntity() {
        return new Distribution().code(DEFAULT_CODE).distributionDate(DEFAULT_DISTRIBUTION_DATE).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Distribution createUpdatedEntity() {
        return new Distribution().code(UPDATED_CODE).distributionDate(UPDATED_DISTRIBUTION_DATE).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        distribution = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDistribution != null) {
            distributionRepository.delete(insertedDistribution);
            insertedDistribution = null;
        }
    }

    @Test
    @Transactional
    void createDistribution() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Distribution
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);
        var returnedDistributionDTO = om.readValue(
            restDistributionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(distributionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DistributionDTO.class
        );

        // Validate the Distribution in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDistribution = distributionMapper.toEntity(returnedDistributionDTO);
        assertDistributionUpdatableFieldsEquals(returnedDistribution, getPersistedDistribution(returnedDistribution));

        insertedDistribution = returnedDistribution;
    }

    @Test
    @Transactional
    void createDistributionWithExistingId() throws Exception {
        // Create the Distribution with an existing ID
        distribution.setId(1L);
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDistributionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(distributionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Distribution in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        distribution.setCode(null);

        // Create the Distribution, which fails.
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        restDistributionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(distributionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDistributionDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        distribution.setDistributionDate(null);

        // Create the Distribution, which fails.
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        restDistributionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(distributionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        distribution.setStatus(null);

        // Create the Distribution, which fails.
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        restDistributionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(distributionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDistributions() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList
        restDistributionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(distribution.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].distributionDate").value(hasItem(DEFAULT_DISTRIBUTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getDistribution() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get the distribution
        restDistributionMockMvc
            .perform(get(ENTITY_API_URL_ID, distribution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(distribution.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.distributionDate").value(DEFAULT_DISTRIBUTION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getDistributionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        Long id = distribution.getId();

        defaultDistributionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDistributionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDistributionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDistributionsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where code equals to
        defaultDistributionFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDistributionsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where code in
        defaultDistributionFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDistributionsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where code is not null
        defaultDistributionFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllDistributionsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where code contains
        defaultDistributionFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDistributionsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where code does not contain
        defaultDistributionFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllDistributionsByDistributionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where distributionDate equals to
        defaultDistributionFiltering(
            "distributionDate.equals=" + DEFAULT_DISTRIBUTION_DATE,
            "distributionDate.equals=" + UPDATED_DISTRIBUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllDistributionsByDistributionDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where distributionDate in
        defaultDistributionFiltering(
            "distributionDate.in=" + DEFAULT_DISTRIBUTION_DATE + "," + UPDATED_DISTRIBUTION_DATE,
            "distributionDate.in=" + UPDATED_DISTRIBUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllDistributionsByDistributionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where distributionDate is not null
        defaultDistributionFiltering("distributionDate.specified=true", "distributionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDistributionsByDistributionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where distributionDate is greater than or equal to
        defaultDistributionFiltering(
            "distributionDate.greaterThanOrEqual=" + DEFAULT_DISTRIBUTION_DATE,
            "distributionDate.greaterThanOrEqual=" + UPDATED_DISTRIBUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllDistributionsByDistributionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where distributionDate is less than or equal to
        defaultDistributionFiltering(
            "distributionDate.lessThanOrEqual=" + DEFAULT_DISTRIBUTION_DATE,
            "distributionDate.lessThanOrEqual=" + SMALLER_DISTRIBUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllDistributionsByDistributionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where distributionDate is less than
        defaultDistributionFiltering(
            "distributionDate.lessThan=" + UPDATED_DISTRIBUTION_DATE,
            "distributionDate.lessThan=" + DEFAULT_DISTRIBUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllDistributionsByDistributionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where distributionDate is greater than
        defaultDistributionFiltering(
            "distributionDate.greaterThan=" + SMALLER_DISTRIBUTION_DATE,
            "distributionDate.greaterThan=" + DEFAULT_DISTRIBUTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllDistributionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where status equals to
        defaultDistributionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDistributionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where status in
        defaultDistributionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllDistributionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        // Get all the distributionList where status is not null
        defaultDistributionFiltering("status.specified=true", "status.specified=false");
    }

    private void defaultDistributionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDistributionShouldBeFound(shouldBeFound);
        defaultDistributionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDistributionShouldBeFound(String filter) throws Exception {
        restDistributionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(distribution.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].distributionDate").value(hasItem(DEFAULT_DISTRIBUTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restDistributionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDistributionShouldNotBeFound(String filter) throws Exception {
        restDistributionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDistributionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDistribution() throws Exception {
        // Get the distribution
        restDistributionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDistribution() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the distribution
        Distribution updatedDistribution = distributionRepository.findById(distribution.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDistribution are not directly saved in db
        em.detach(updatedDistribution);
        updatedDistribution.code(UPDATED_CODE).distributionDate(UPDATED_DISTRIBUTION_DATE).status(UPDATED_STATUS);
        DistributionDTO distributionDTO = distributionMapper.toDto(updatedDistribution);

        restDistributionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, distributionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(distributionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Distribution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDistributionToMatchAllProperties(updatedDistribution);
    }

    @Test
    @Transactional
    void putNonExistingDistribution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        distribution.setId(longCount.incrementAndGet());

        // Create the Distribution
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDistributionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, distributionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(distributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Distribution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDistribution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        distribution.setId(longCount.incrementAndGet());

        // Create the Distribution
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistributionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(distributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Distribution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDistribution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        distribution.setId(longCount.incrementAndGet());

        // Create the Distribution
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistributionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(distributionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Distribution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDistributionWithPatch() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the distribution using partial update
        Distribution partialUpdatedDistribution = new Distribution();
        partialUpdatedDistribution.setId(distribution.getId());

        partialUpdatedDistribution.distributionDate(UPDATED_DISTRIBUTION_DATE);

        restDistributionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDistribution.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDistribution))
            )
            .andExpect(status().isOk());

        // Validate the Distribution in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDistributionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDistribution, distribution),
            getPersistedDistribution(distribution)
        );
    }

    @Test
    @Transactional
    void fullUpdateDistributionWithPatch() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the distribution using partial update
        Distribution partialUpdatedDistribution = new Distribution();
        partialUpdatedDistribution.setId(distribution.getId());

        partialUpdatedDistribution.code(UPDATED_CODE).distributionDate(UPDATED_DISTRIBUTION_DATE).status(UPDATED_STATUS);

        restDistributionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDistribution.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDistribution))
            )
            .andExpect(status().isOk());

        // Validate the Distribution in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDistributionUpdatableFieldsEquals(partialUpdatedDistribution, getPersistedDistribution(partialUpdatedDistribution));
    }

    @Test
    @Transactional
    void patchNonExistingDistribution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        distribution.setId(longCount.incrementAndGet());

        // Create the Distribution
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDistributionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, distributionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(distributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Distribution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDistribution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        distribution.setId(longCount.incrementAndGet());

        // Create the Distribution
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistributionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(distributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Distribution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDistribution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        distribution.setId(longCount.incrementAndGet());

        // Create the Distribution
        DistributionDTO distributionDTO = distributionMapper.toDto(distribution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDistributionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(distributionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Distribution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDistribution() throws Exception {
        // Initialize the database
        insertedDistribution = distributionRepository.saveAndFlush(distribution);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the distribution
        restDistributionMockMvc
            .perform(delete(ENTITY_API_URL_ID, distribution.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return distributionRepository.count();
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

    protected Distribution getPersistedDistribution(Distribution distribution) {
        return distributionRepository.findById(distribution.getId()).orElseThrow();
    }

    protected void assertPersistedDistributionToMatchAllProperties(Distribution expectedDistribution) {
        assertDistributionAllPropertiesEquals(expectedDistribution, getPersistedDistribution(expectedDistribution));
    }

    protected void assertPersistedDistributionToMatchUpdatableProperties(Distribution expectedDistribution) {
        assertDistributionAllUpdatablePropertiesEquals(expectedDistribution, getPersistedDistribution(expectedDistribution));
    }
}
