package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.CertificateBatchAsserts.*;
import static zw.org.nmrl.ept.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.IntegrationTest;
import zw.org.nmrl.ept.domain.CertificateBatch;
import zw.org.nmrl.ept.domain.enumeration.CertificateBatchStatus;
import zw.org.nmrl.ept.repository.CertificateBatchRepository;
import zw.org.nmrl.ept.service.CertificateBatchService;
import zw.org.nmrl.ept.service.dto.CertificateBatchDTO;
import zw.org.nmrl.ept.service.mapper.CertificateBatchMapper;

/**
 * Integration tests for the {@link CertificateBatchResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CertificateBatchResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final CertificateBatchStatus DEFAULT_STATUS = CertificateBatchStatus.PENDING;
    private static final CertificateBatchStatus UPDATED_STATUS = CertificateBatchStatus.GENERATING;

    private static final Integer DEFAULT_EXCELLENCE_COUNT = 1;
    private static final Integer UPDATED_EXCELLENCE_COUNT = 2;

    private static final Integer DEFAULT_PARTICIPATION_COUNT = 1;
    private static final Integer UPDATED_PARTICIPATION_COUNT = 2;

    private static final Integer DEFAULT_SKIPPED_COUNT = 1;
    private static final Integer UPDATED_SKIPPED_COUNT = 2;

    private static final String DEFAULT_DOWNLOAD_URL = "AAAAAAAAAA";
    private static final String UPDATED_DOWNLOAD_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_APPROVED_BY = "AAAAAAAAAA";
    private static final String UPDATED_APPROVED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_APPROVED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_APPROVED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/certificate-batches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CertificateBatchRepository certificateBatchRepository;

    @Mock
    private CertificateBatchRepository certificateBatchRepositoryMock;

    @Autowired
    private CertificateBatchMapper certificateBatchMapper;

    @Mock
    private CertificateBatchService certificateBatchServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCertificateBatchMockMvc;

    private CertificateBatch certificateBatch;

    private CertificateBatch insertedCertificateBatch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CertificateBatch createEntity() {
        return new CertificateBatch()
            .name(DEFAULT_NAME)
            .status(DEFAULT_STATUS)
            .excellenceCount(DEFAULT_EXCELLENCE_COUNT)
            .participationCount(DEFAULT_PARTICIPATION_COUNT)
            .skippedCount(DEFAULT_SKIPPED_COUNT)
            .downloadUrl(DEFAULT_DOWNLOAD_URL)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .approvedBy(DEFAULT_APPROVED_BY)
            .approvedOn(DEFAULT_APPROVED_ON);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CertificateBatch createUpdatedEntity() {
        return new CertificateBatch()
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS)
            .excellenceCount(UPDATED_EXCELLENCE_COUNT)
            .participationCount(UPDATED_PARTICIPATION_COUNT)
            .skippedCount(UPDATED_SKIPPED_COUNT)
            .downloadUrl(UPDATED_DOWNLOAD_URL)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .approvedBy(UPDATED_APPROVED_BY)
            .approvedOn(UPDATED_APPROVED_ON);
    }

    @BeforeEach
    void initTest() {
        certificateBatch = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCertificateBatch != null) {
            certificateBatchRepository.delete(insertedCertificateBatch);
            insertedCertificateBatch = null;
        }
    }

    @Test
    @Transactional
    void createCertificateBatch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CertificateBatch
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);
        var returnedCertificateBatchDTO = om.readValue(
            restCertificateBatchMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateBatchDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CertificateBatchDTO.class
        );

        // Validate the CertificateBatch in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCertificateBatch = certificateBatchMapper.toEntity(returnedCertificateBatchDTO);
        assertCertificateBatchUpdatableFieldsEquals(returnedCertificateBatch, getPersistedCertificateBatch(returnedCertificateBatch));

        insertedCertificateBatch = returnedCertificateBatch;
    }

    @Test
    @Transactional
    void createCertificateBatchWithExistingId() throws Exception {
        // Create the CertificateBatch with an existing ID
        certificateBatch.setId(1L);
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCertificateBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateBatchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CertificateBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certificateBatch.setName(null);

        // Create the CertificateBatch, which fails.
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        restCertificateBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateBatchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certificateBatch.setStatus(null);

        // Create the CertificateBatch, which fails.
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        restCertificateBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateBatchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCertificateBatches() throws Exception {
        // Initialize the database
        insertedCertificateBatch = certificateBatchRepository.saveAndFlush(certificateBatch);

        // Get all the certificateBatchList
        restCertificateBatchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificateBatch.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].excellenceCount").value(hasItem(DEFAULT_EXCELLENCE_COUNT)))
            .andExpect(jsonPath("$.[*].participationCount").value(hasItem(DEFAULT_PARTICIPATION_COUNT)))
            .andExpect(jsonPath("$.[*].skippedCount").value(hasItem(DEFAULT_SKIPPED_COUNT)))
            .andExpect(jsonPath("$.[*].downloadUrl").value(hasItem(DEFAULT_DOWNLOAD_URL)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].approvedBy").value(hasItem(DEFAULT_APPROVED_BY)))
            .andExpect(jsonPath("$.[*].approvedOn").value(hasItem(DEFAULT_APPROVED_ON.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCertificateBatchesWithEagerRelationshipsIsEnabled() throws Exception {
        when(certificateBatchServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCertificateBatchMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(certificateBatchServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCertificateBatchesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(certificateBatchServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCertificateBatchMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(certificateBatchRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCertificateBatch() throws Exception {
        // Initialize the database
        insertedCertificateBatch = certificateBatchRepository.saveAndFlush(certificateBatch);

        // Get the certificateBatch
        restCertificateBatchMockMvc
            .perform(get(ENTITY_API_URL_ID, certificateBatch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(certificateBatch.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.excellenceCount").value(DEFAULT_EXCELLENCE_COUNT))
            .andExpect(jsonPath("$.participationCount").value(DEFAULT_PARTICIPATION_COUNT))
            .andExpect(jsonPath("$.skippedCount").value(DEFAULT_SKIPPED_COUNT))
            .andExpect(jsonPath("$.downloadUrl").value(DEFAULT_DOWNLOAD_URL))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.approvedBy").value(DEFAULT_APPROVED_BY))
            .andExpect(jsonPath("$.approvedOn").value(DEFAULT_APPROVED_ON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCertificateBatch() throws Exception {
        // Get the certificateBatch
        restCertificateBatchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCertificateBatch() throws Exception {
        // Initialize the database
        insertedCertificateBatch = certificateBatchRepository.saveAndFlush(certificateBatch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificateBatch
        CertificateBatch updatedCertificateBatch = certificateBatchRepository.findById(certificateBatch.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCertificateBatch are not directly saved in db
        em.detach(updatedCertificateBatch);
        updatedCertificateBatch
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS)
            .excellenceCount(UPDATED_EXCELLENCE_COUNT)
            .participationCount(UPDATED_PARTICIPATION_COUNT)
            .skippedCount(UPDATED_SKIPPED_COUNT)
            .downloadUrl(UPDATED_DOWNLOAD_URL)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .approvedBy(UPDATED_APPROVED_BY)
            .approvedOn(UPDATED_APPROVED_ON);
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(updatedCertificateBatch);

        restCertificateBatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificateBatchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateBatchDTO))
            )
            .andExpect(status().isOk());

        // Validate the CertificateBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCertificateBatchToMatchAllProperties(updatedCertificateBatch);
    }

    @Test
    @Transactional
    void putNonExistingCertificateBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateBatch.setId(longCount.incrementAndGet());

        // Create the CertificateBatch
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateBatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificateBatchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateBatchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificateBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCertificateBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateBatch.setId(longCount.incrementAndGet());

        // Create the CertificateBatch
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateBatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateBatchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificateBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCertificateBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateBatch.setId(longCount.incrementAndGet());

        // Create the CertificateBatch
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateBatchMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateBatchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CertificateBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCertificateBatchWithPatch() throws Exception {
        // Initialize the database
        insertedCertificateBatch = certificateBatchRepository.saveAndFlush(certificateBatch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificateBatch using partial update
        CertificateBatch partialUpdatedCertificateBatch = new CertificateBatch();
        partialUpdatedCertificateBatch.setId(certificateBatch.getId());

        partialUpdatedCertificateBatch
            .downloadUrl(UPDATED_DOWNLOAD_URL)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .approvedBy(UPDATED_APPROVED_BY)
            .approvedOn(UPDATED_APPROVED_ON);

        restCertificateBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificateBatch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificateBatch))
            )
            .andExpect(status().isOk());

        // Validate the CertificateBatch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificateBatchUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCertificateBatch, certificateBatch),
            getPersistedCertificateBatch(certificateBatch)
        );
    }

    @Test
    @Transactional
    void fullUpdateCertificateBatchWithPatch() throws Exception {
        // Initialize the database
        insertedCertificateBatch = certificateBatchRepository.saveAndFlush(certificateBatch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificateBatch using partial update
        CertificateBatch partialUpdatedCertificateBatch = new CertificateBatch();
        partialUpdatedCertificateBatch.setId(certificateBatch.getId());

        partialUpdatedCertificateBatch
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS)
            .excellenceCount(UPDATED_EXCELLENCE_COUNT)
            .participationCount(UPDATED_PARTICIPATION_COUNT)
            .skippedCount(UPDATED_SKIPPED_COUNT)
            .downloadUrl(UPDATED_DOWNLOAD_URL)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .approvedBy(UPDATED_APPROVED_BY)
            .approvedOn(UPDATED_APPROVED_ON);

        restCertificateBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificateBatch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificateBatch))
            )
            .andExpect(status().isOk());

        // Validate the CertificateBatch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificateBatchUpdatableFieldsEquals(
            partialUpdatedCertificateBatch,
            getPersistedCertificateBatch(partialUpdatedCertificateBatch)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCertificateBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateBatch.setId(longCount.incrementAndGet());

        // Create the CertificateBatch
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, certificateBatchDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificateBatchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificateBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCertificateBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateBatch.setId(longCount.incrementAndGet());

        // Create the CertificateBatch
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificateBatchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificateBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCertificateBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateBatch.setId(longCount.incrementAndGet());

        // Create the CertificateBatch
        CertificateBatchDTO certificateBatchDTO = certificateBatchMapper.toDto(certificateBatch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateBatchMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(certificateBatchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CertificateBatch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCertificateBatch() throws Exception {
        // Initialize the database
        insertedCertificateBatch = certificateBatchRepository.saveAndFlush(certificateBatch);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the certificateBatch
        restCertificateBatchMockMvc
            .perform(delete(ENTITY_API_URL_ID, certificateBatch.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return certificateBatchRepository.count();
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

    protected CertificateBatch getPersistedCertificateBatch(CertificateBatch certificateBatch) {
        return certificateBatchRepository.findById(certificateBatch.getId()).orElseThrow();
    }

    protected void assertPersistedCertificateBatchToMatchAllProperties(CertificateBatch expectedCertificateBatch) {
        assertCertificateBatchAllPropertiesEquals(expectedCertificateBatch, getPersistedCertificateBatch(expectedCertificateBatch));
    }

    protected void assertPersistedCertificateBatchToMatchUpdatableProperties(CertificateBatch expectedCertificateBatch) {
        assertCertificateBatchAllUpdatablePropertiesEquals(
            expectedCertificateBatch,
            getPersistedCertificateBatch(expectedCertificateBatch)
        );
    }
}
