package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ApiRequestLogAsserts.*;
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
import zw.org.nmrl.ept.domain.ApiRequestLog;
import zw.org.nmrl.ept.repository.ApiRequestLogRepository;
import zw.org.nmrl.ept.service.dto.ApiRequestLogDTO;
import zw.org.nmrl.ept.service.mapper.ApiRequestLogMapper;

/**
 * Integration tests for the {@link ApiRequestLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApiRequestLogResourceIT {

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_REQUESTED_BY = "AAAAAAAAAA";
    private static final String UPDATED_REQUESTED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_REQUESTED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUESTED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_NUMBER_OF_RECORDS = 1;
    private static final Integer UPDATED_NUMBER_OF_RECORDS = 2;

    private static final String DEFAULT_REQUEST_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TEST_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TEST_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_API_URL = "AAAAAAAAAA";
    private static final String UPDATED_API_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DATA_FORMAT = "AAAAAAAAAA";
    private static final String UPDATED_DATA_FORMAT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/api-request-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApiRequestLogRepository apiRequestLogRepository;

    @Autowired
    private ApiRequestLogMapper apiRequestLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApiRequestLogMockMvc;

    private ApiRequestLog apiRequestLog;

    private ApiRequestLog insertedApiRequestLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApiRequestLog createEntity() {
        return new ApiRequestLog()
            .transactionId(DEFAULT_TRANSACTION_ID)
            .requestedBy(DEFAULT_REQUESTED_BY)
            .requestedOn(DEFAULT_REQUESTED_ON)
            .numberOfRecords(DEFAULT_NUMBER_OF_RECORDS)
            .requestType(DEFAULT_REQUEST_TYPE)
            .testType(DEFAULT_TEST_TYPE)
            .apiUrl(DEFAULT_API_URL)
            .dataFormat(DEFAULT_DATA_FORMAT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApiRequestLog createUpdatedEntity() {
        return new ApiRequestLog()
            .transactionId(UPDATED_TRANSACTION_ID)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedOn(UPDATED_REQUESTED_ON)
            .numberOfRecords(UPDATED_NUMBER_OF_RECORDS)
            .requestType(UPDATED_REQUEST_TYPE)
            .testType(UPDATED_TEST_TYPE)
            .apiUrl(UPDATED_API_URL)
            .dataFormat(UPDATED_DATA_FORMAT);
    }

    @BeforeEach
    void initTest() {
        apiRequestLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedApiRequestLog != null) {
            apiRequestLogRepository.delete(insertedApiRequestLog);
            insertedApiRequestLog = null;
        }
    }

    @Test
    @Transactional
    void createApiRequestLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ApiRequestLog
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);
        var returnedApiRequestLogDTO = om.readValue(
            restApiRequestLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiRequestLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApiRequestLogDTO.class
        );

        // Validate the ApiRequestLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApiRequestLog = apiRequestLogMapper.toEntity(returnedApiRequestLogDTO);
        assertApiRequestLogUpdatableFieldsEquals(returnedApiRequestLog, getPersistedApiRequestLog(returnedApiRequestLog));

        insertedApiRequestLog = returnedApiRequestLog;
    }

    @Test
    @Transactional
    void createApiRequestLogWithExistingId() throws Exception {
        // Create the ApiRequestLog with an existing ID
        apiRequestLog.setId(1L);
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApiRequestLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiRequestLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ApiRequestLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTransactionIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        apiRequestLog.setTransactionId(null);

        // Create the ApiRequestLog, which fails.
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);

        restApiRequestLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiRequestLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllApiRequestLogs() throws Exception {
        // Initialize the database
        insertedApiRequestLog = apiRequestLogRepository.saveAndFlush(apiRequestLog);

        // Get all the apiRequestLogList
        restApiRequestLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apiRequestLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].requestedBy").value(hasItem(DEFAULT_REQUESTED_BY)))
            .andExpect(jsonPath("$.[*].requestedOn").value(hasItem(DEFAULT_REQUESTED_ON.toString())))
            .andExpect(jsonPath("$.[*].numberOfRecords").value(hasItem(DEFAULT_NUMBER_OF_RECORDS)))
            .andExpect(jsonPath("$.[*].requestType").value(hasItem(DEFAULT_REQUEST_TYPE)))
            .andExpect(jsonPath("$.[*].testType").value(hasItem(DEFAULT_TEST_TYPE)))
            .andExpect(jsonPath("$.[*].apiUrl").value(hasItem(DEFAULT_API_URL)))
            .andExpect(jsonPath("$.[*].dataFormat").value(hasItem(DEFAULT_DATA_FORMAT)));
    }

    @Test
    @Transactional
    void getApiRequestLog() throws Exception {
        // Initialize the database
        insertedApiRequestLog = apiRequestLogRepository.saveAndFlush(apiRequestLog);

        // Get the apiRequestLog
        restApiRequestLogMockMvc
            .perform(get(ENTITY_API_URL_ID, apiRequestLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(apiRequestLog.getId().intValue()))
            .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.requestedBy").value(DEFAULT_REQUESTED_BY))
            .andExpect(jsonPath("$.requestedOn").value(DEFAULT_REQUESTED_ON.toString()))
            .andExpect(jsonPath("$.numberOfRecords").value(DEFAULT_NUMBER_OF_RECORDS))
            .andExpect(jsonPath("$.requestType").value(DEFAULT_REQUEST_TYPE))
            .andExpect(jsonPath("$.testType").value(DEFAULT_TEST_TYPE))
            .andExpect(jsonPath("$.apiUrl").value(DEFAULT_API_URL))
            .andExpect(jsonPath("$.dataFormat").value(DEFAULT_DATA_FORMAT));
    }

    @Test
    @Transactional
    void getNonExistingApiRequestLog() throws Exception {
        // Get the apiRequestLog
        restApiRequestLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApiRequestLog() throws Exception {
        // Initialize the database
        insertedApiRequestLog = apiRequestLogRepository.saveAndFlush(apiRequestLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the apiRequestLog
        ApiRequestLog updatedApiRequestLog = apiRequestLogRepository.findById(apiRequestLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApiRequestLog are not directly saved in db
        em.detach(updatedApiRequestLog);
        updatedApiRequestLog
            .transactionId(UPDATED_TRANSACTION_ID)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedOn(UPDATED_REQUESTED_ON)
            .numberOfRecords(UPDATED_NUMBER_OF_RECORDS)
            .requestType(UPDATED_REQUEST_TYPE)
            .testType(UPDATED_TEST_TYPE)
            .apiUrl(UPDATED_API_URL)
            .dataFormat(UPDATED_DATA_FORMAT);
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(updatedApiRequestLog);

        restApiRequestLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, apiRequestLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(apiRequestLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApiRequestLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApiRequestLogToMatchAllProperties(updatedApiRequestLog);
    }

    @Test
    @Transactional
    void putNonExistingApiRequestLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiRequestLog.setId(longCount.incrementAndGet());

        // Create the ApiRequestLog
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApiRequestLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, apiRequestLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(apiRequestLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiRequestLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApiRequestLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiRequestLog.setId(longCount.incrementAndGet());

        // Create the ApiRequestLog
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiRequestLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(apiRequestLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiRequestLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApiRequestLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiRequestLog.setId(longCount.incrementAndGet());

        // Create the ApiRequestLog
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiRequestLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(apiRequestLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApiRequestLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApiRequestLogWithPatch() throws Exception {
        // Initialize the database
        insertedApiRequestLog = apiRequestLogRepository.saveAndFlush(apiRequestLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the apiRequestLog using partial update
        ApiRequestLog partialUpdatedApiRequestLog = new ApiRequestLog();
        partialUpdatedApiRequestLog.setId(apiRequestLog.getId());

        partialUpdatedApiRequestLog.requestedBy(UPDATED_REQUESTED_BY).apiUrl(UPDATED_API_URL).dataFormat(UPDATED_DATA_FORMAT);

        restApiRequestLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApiRequestLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApiRequestLog))
            )
            .andExpect(status().isOk());

        // Validate the ApiRequestLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApiRequestLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApiRequestLog, apiRequestLog),
            getPersistedApiRequestLog(apiRequestLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateApiRequestLogWithPatch() throws Exception {
        // Initialize the database
        insertedApiRequestLog = apiRequestLogRepository.saveAndFlush(apiRequestLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the apiRequestLog using partial update
        ApiRequestLog partialUpdatedApiRequestLog = new ApiRequestLog();
        partialUpdatedApiRequestLog.setId(apiRequestLog.getId());

        partialUpdatedApiRequestLog
            .transactionId(UPDATED_TRANSACTION_ID)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedOn(UPDATED_REQUESTED_ON)
            .numberOfRecords(UPDATED_NUMBER_OF_RECORDS)
            .requestType(UPDATED_REQUEST_TYPE)
            .testType(UPDATED_TEST_TYPE)
            .apiUrl(UPDATED_API_URL)
            .dataFormat(UPDATED_DATA_FORMAT);

        restApiRequestLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApiRequestLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApiRequestLog))
            )
            .andExpect(status().isOk());

        // Validate the ApiRequestLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApiRequestLogUpdatableFieldsEquals(partialUpdatedApiRequestLog, getPersistedApiRequestLog(partialUpdatedApiRequestLog));
    }

    @Test
    @Transactional
    void patchNonExistingApiRequestLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiRequestLog.setId(longCount.incrementAndGet());

        // Create the ApiRequestLog
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApiRequestLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, apiRequestLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(apiRequestLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiRequestLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApiRequestLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiRequestLog.setId(longCount.incrementAndGet());

        // Create the ApiRequestLog
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiRequestLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(apiRequestLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApiRequestLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApiRequestLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        apiRequestLog.setId(longCount.incrementAndGet());

        // Create the ApiRequestLog
        ApiRequestLogDTO apiRequestLogDTO = apiRequestLogMapper.toDto(apiRequestLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApiRequestLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(apiRequestLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApiRequestLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApiRequestLog() throws Exception {
        // Initialize the database
        insertedApiRequestLog = apiRequestLogRepository.saveAndFlush(apiRequestLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the apiRequestLog
        restApiRequestLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, apiRequestLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return apiRequestLogRepository.count();
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

    protected ApiRequestLog getPersistedApiRequestLog(ApiRequestLog apiRequestLog) {
        return apiRequestLogRepository.findById(apiRequestLog.getId()).orElseThrow();
    }

    protected void assertPersistedApiRequestLogToMatchAllProperties(ApiRequestLog expectedApiRequestLog) {
        assertApiRequestLogAllPropertiesEquals(expectedApiRequestLog, getPersistedApiRequestLog(expectedApiRequestLog));
    }

    protected void assertPersistedApiRequestLogToMatchUpdatableProperties(ApiRequestLog expectedApiRequestLog) {
        assertApiRequestLogAllUpdatablePropertiesEquals(expectedApiRequestLog, getPersistedApiRequestLog(expectedApiRequestLog));
    }
}
