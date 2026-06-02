package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.SampleReferenceResultAsserts.*;
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
import zw.org.nmrl.ept.domain.SampleReferenceResult;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.repository.SampleReferenceResultRepository;
import zw.org.nmrl.ept.service.dto.SampleReferenceResultDTO;
import zw.org.nmrl.ept.service.mapper.SampleReferenceResultMapper;

/**
 * Integration tests for the {@link SampleReferenceResultResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SampleReferenceResultResourceIT {

    private static final String DEFAULT_QUALITATIVE_RESULT = "AAAAAAAAAA";
    private static final String UPDATED_QUALITATIVE_RESULT = "BBBBBBBBBB";

    private static final Double DEFAULT_QUANTITATIVE_VALUE = 1D;
    private static final Double UPDATED_QUANTITATIVE_VALUE = 2D;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Double DEFAULT_LOWER_LIMIT = 1D;
    private static final Double UPDATED_LOWER_LIMIT = 2D;

    private static final Double DEFAULT_UPPER_LIMIT = 1D;
    private static final Double UPDATED_UPPER_LIMIT = 2D;

    private static final Boolean DEFAULT_IS_CONTROL_EXPECTED = false;
    private static final Boolean UPDATED_IS_CONTROL_EXPECTED = true;

    private static final String ENTITY_API_URL = "/api/sample-reference-results";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SampleReferenceResultRepository sampleReferenceResultRepository;

    @Autowired
    private SampleReferenceResultMapper sampleReferenceResultMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSampleReferenceResultMockMvc;

    private SampleReferenceResult sampleReferenceResult;

    private SampleReferenceResult insertedSampleReferenceResult;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SampleReferenceResult createEntity(EntityManager em) {
        SampleReferenceResult sampleReferenceResult = new SampleReferenceResult()
            .qualitativeResult(DEFAULT_QUALITATIVE_RESULT)
            .quantitativeValue(DEFAULT_QUANTITATIVE_VALUE)
            .unit(DEFAULT_UNIT)
            .lowerLimit(DEFAULT_LOWER_LIMIT)
            .upperLimit(DEFAULT_UPPER_LIMIT)
            .isControlExpected(DEFAULT_IS_CONTROL_EXPECTED);
        // Add required entity
        ShipmentSample shipmentSample;
        if (TestUtil.findAll(em, ShipmentSample.class).isEmpty()) {
            shipmentSample = ShipmentSampleResourceIT.createEntity(em);
            em.persist(shipmentSample);
            em.flush();
        } else {
            shipmentSample = TestUtil.findAll(em, ShipmentSample.class).get(0);
        }
        sampleReferenceResult.setSample(shipmentSample);
        return sampleReferenceResult;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SampleReferenceResult createUpdatedEntity(EntityManager em) {
        SampleReferenceResult updatedSampleReferenceResult = new SampleReferenceResult()
            .qualitativeResult(UPDATED_QUALITATIVE_RESULT)
            .quantitativeValue(UPDATED_QUANTITATIVE_VALUE)
            .unit(UPDATED_UNIT)
            .lowerLimit(UPDATED_LOWER_LIMIT)
            .upperLimit(UPDATED_UPPER_LIMIT)
            .isControlExpected(UPDATED_IS_CONTROL_EXPECTED);
        // Add required entity
        ShipmentSample shipmentSample;
        if (TestUtil.findAll(em, ShipmentSample.class).isEmpty()) {
            shipmentSample = ShipmentSampleResourceIT.createUpdatedEntity(em);
            em.persist(shipmentSample);
            em.flush();
        } else {
            shipmentSample = TestUtil.findAll(em, ShipmentSample.class).get(0);
        }
        updatedSampleReferenceResult.setSample(shipmentSample);
        return updatedSampleReferenceResult;
    }

    @BeforeEach
    void initTest() {
        sampleReferenceResult = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSampleReferenceResult != null) {
            sampleReferenceResultRepository.delete(insertedSampleReferenceResult);
            insertedSampleReferenceResult = null;
        }
    }

    @Test
    @Transactional
    void createSampleReferenceResult() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SampleReferenceResult
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(sampleReferenceResult);
        var returnedSampleReferenceResultDTO = om.readValue(
            restSampleReferenceResultMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sampleReferenceResultDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SampleReferenceResultDTO.class
        );

        // Validate the SampleReferenceResult in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSampleReferenceResult = sampleReferenceResultMapper.toEntity(returnedSampleReferenceResultDTO);
        assertSampleReferenceResultUpdatableFieldsEquals(
            returnedSampleReferenceResult,
            getPersistedSampleReferenceResult(returnedSampleReferenceResult)
        );

        insertedSampleReferenceResult = returnedSampleReferenceResult;
    }

    @Test
    @Transactional
    void createSampleReferenceResultWithExistingId() throws Exception {
        // Create the SampleReferenceResult with an existing ID
        sampleReferenceResult.setId(1L);
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(sampleReferenceResult);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSampleReferenceResultMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sampleReferenceResultDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SampleReferenceResult in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSampleReferenceResults() throws Exception {
        // Initialize the database
        insertedSampleReferenceResult = sampleReferenceResultRepository.saveAndFlush(sampleReferenceResult);

        // Get all the sampleReferenceResultList
        restSampleReferenceResultMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sampleReferenceResult.getId().intValue())))
            .andExpect(jsonPath("$.[*].qualitativeResult").value(hasItem(DEFAULT_QUALITATIVE_RESULT)))
            .andExpect(jsonPath("$.[*].quantitativeValue").value(hasItem(DEFAULT_QUANTITATIVE_VALUE)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].lowerLimit").value(hasItem(DEFAULT_LOWER_LIMIT)))
            .andExpect(jsonPath("$.[*].upperLimit").value(hasItem(DEFAULT_UPPER_LIMIT)))
            .andExpect(jsonPath("$.[*].isControlExpected").value(hasItem(DEFAULT_IS_CONTROL_EXPECTED)));
    }

    @Test
    @Transactional
    void getSampleReferenceResult() throws Exception {
        // Initialize the database
        insertedSampleReferenceResult = sampleReferenceResultRepository.saveAndFlush(sampleReferenceResult);

        // Get the sampleReferenceResult
        restSampleReferenceResultMockMvc
            .perform(get(ENTITY_API_URL_ID, sampleReferenceResult.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sampleReferenceResult.getId().intValue()))
            .andExpect(jsonPath("$.qualitativeResult").value(DEFAULT_QUALITATIVE_RESULT))
            .andExpect(jsonPath("$.quantitativeValue").value(DEFAULT_QUANTITATIVE_VALUE))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.lowerLimit").value(DEFAULT_LOWER_LIMIT))
            .andExpect(jsonPath("$.upperLimit").value(DEFAULT_UPPER_LIMIT))
            .andExpect(jsonPath("$.isControlExpected").value(DEFAULT_IS_CONTROL_EXPECTED));
    }

    @Test
    @Transactional
    void getNonExistingSampleReferenceResult() throws Exception {
        // Get the sampleReferenceResult
        restSampleReferenceResultMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSampleReferenceResult() throws Exception {
        // Initialize the database
        insertedSampleReferenceResult = sampleReferenceResultRepository.saveAndFlush(sampleReferenceResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sampleReferenceResult
        SampleReferenceResult updatedSampleReferenceResult = sampleReferenceResultRepository
            .findById(sampleReferenceResult.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedSampleReferenceResult are not directly saved in db
        em.detach(updatedSampleReferenceResult);
        updatedSampleReferenceResult
            .qualitativeResult(UPDATED_QUALITATIVE_RESULT)
            .quantitativeValue(UPDATED_QUANTITATIVE_VALUE)
            .unit(UPDATED_UNIT)
            .lowerLimit(UPDATED_LOWER_LIMIT)
            .upperLimit(UPDATED_UPPER_LIMIT)
            .isControlExpected(UPDATED_IS_CONTROL_EXPECTED);
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(updatedSampleReferenceResult);

        restSampleReferenceResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sampleReferenceResultDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sampleReferenceResultDTO))
            )
            .andExpect(status().isOk());

        // Validate the SampleReferenceResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSampleReferenceResultToMatchAllProperties(updatedSampleReferenceResult);
    }

    @Test
    @Transactional
    void putNonExistingSampleReferenceResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sampleReferenceResult.setId(longCount.incrementAndGet());

        // Create the SampleReferenceResult
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(sampleReferenceResult);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSampleReferenceResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sampleReferenceResultDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sampleReferenceResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SampleReferenceResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSampleReferenceResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sampleReferenceResult.setId(longCount.incrementAndGet());

        // Create the SampleReferenceResult
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(sampleReferenceResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSampleReferenceResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sampleReferenceResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SampleReferenceResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSampleReferenceResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sampleReferenceResult.setId(longCount.incrementAndGet());

        // Create the SampleReferenceResult
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(sampleReferenceResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSampleReferenceResultMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sampleReferenceResultDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SampleReferenceResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSampleReferenceResultWithPatch() throws Exception {
        // Initialize the database
        insertedSampleReferenceResult = sampleReferenceResultRepository.saveAndFlush(sampleReferenceResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sampleReferenceResult using partial update
        SampleReferenceResult partialUpdatedSampleReferenceResult = new SampleReferenceResult();
        partialUpdatedSampleReferenceResult.setId(sampleReferenceResult.getId());

        partialUpdatedSampleReferenceResult
            .qualitativeResult(UPDATED_QUALITATIVE_RESULT)
            .unit(UPDATED_UNIT)
            .upperLimit(UPDATED_UPPER_LIMIT);

        restSampleReferenceResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSampleReferenceResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSampleReferenceResult))
            )
            .andExpect(status().isOk());

        // Validate the SampleReferenceResult in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSampleReferenceResultUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSampleReferenceResult, sampleReferenceResult),
            getPersistedSampleReferenceResult(sampleReferenceResult)
        );
    }

    @Test
    @Transactional
    void fullUpdateSampleReferenceResultWithPatch() throws Exception {
        // Initialize the database
        insertedSampleReferenceResult = sampleReferenceResultRepository.saveAndFlush(sampleReferenceResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sampleReferenceResult using partial update
        SampleReferenceResult partialUpdatedSampleReferenceResult = new SampleReferenceResult();
        partialUpdatedSampleReferenceResult.setId(sampleReferenceResult.getId());

        partialUpdatedSampleReferenceResult
            .qualitativeResult(UPDATED_QUALITATIVE_RESULT)
            .quantitativeValue(UPDATED_QUANTITATIVE_VALUE)
            .unit(UPDATED_UNIT)
            .lowerLimit(UPDATED_LOWER_LIMIT)
            .upperLimit(UPDATED_UPPER_LIMIT)
            .isControlExpected(UPDATED_IS_CONTROL_EXPECTED);

        restSampleReferenceResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSampleReferenceResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSampleReferenceResult))
            )
            .andExpect(status().isOk());

        // Validate the SampleReferenceResult in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSampleReferenceResultUpdatableFieldsEquals(
            partialUpdatedSampleReferenceResult,
            getPersistedSampleReferenceResult(partialUpdatedSampleReferenceResult)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSampleReferenceResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sampleReferenceResult.setId(longCount.incrementAndGet());

        // Create the SampleReferenceResult
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(sampleReferenceResult);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSampleReferenceResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sampleReferenceResultDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sampleReferenceResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SampleReferenceResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSampleReferenceResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sampleReferenceResult.setId(longCount.incrementAndGet());

        // Create the SampleReferenceResult
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(sampleReferenceResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSampleReferenceResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sampleReferenceResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SampleReferenceResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSampleReferenceResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sampleReferenceResult.setId(longCount.incrementAndGet());

        // Create the SampleReferenceResult
        SampleReferenceResultDTO sampleReferenceResultDTO = sampleReferenceResultMapper.toDto(sampleReferenceResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSampleReferenceResultMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sampleReferenceResultDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SampleReferenceResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSampleReferenceResult() throws Exception {
        // Initialize the database
        insertedSampleReferenceResult = sampleReferenceResultRepository.saveAndFlush(sampleReferenceResult);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sampleReferenceResult
        restSampleReferenceResultMockMvc
            .perform(delete(ENTITY_API_URL_ID, sampleReferenceResult.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sampleReferenceResultRepository.count();
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

    protected SampleReferenceResult getPersistedSampleReferenceResult(SampleReferenceResult sampleReferenceResult) {
        return sampleReferenceResultRepository.findById(sampleReferenceResult.getId()).orElseThrow();
    }

    protected void assertPersistedSampleReferenceResultToMatchAllProperties(SampleReferenceResult expectedSampleReferenceResult) {
        assertSampleReferenceResultAllPropertiesEquals(
            expectedSampleReferenceResult,
            getPersistedSampleReferenceResult(expectedSampleReferenceResult)
        );
    }

    protected void assertPersistedSampleReferenceResultToMatchUpdatableProperties(SampleReferenceResult expectedSampleReferenceResult) {
        assertSampleReferenceResultAllUpdatablePropertiesEquals(
            expectedSampleReferenceResult,
            getPersistedSampleReferenceResult(expectedSampleReferenceResult)
        );
    }
}
