package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.DataManagerAsserts.*;
import static zw.org.nmrl.ept.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.enumeration.DataManagerRole;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.repository.UserRepository;
import zw.org.nmrl.ept.service.DataManagerService;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;
import zw.org.nmrl.ept.service.mapper.DataManagerMapper;

/**
 * Integration tests for the {@link DataManagerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DataManagerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INSTITUTE = "AAAAAAAAAA";
    private static final String UPDATED_INSTITUTE = "BBBBBBBBBB";

    private static final String DEFAULT_PRIMARY_EMAIL = "k>@2r,O>t.n,3vU4";
    private static final String UPDATED_PRIMARY_EMAIL = "p@4~.-B|";

    private static final String DEFAULT_SECONDARY_EMAIL = "h'k,@M.fmxY1";
    private static final String UPDATED_SECONDARY_EMAIL = "g@lopu.+";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final DataManagerRole DEFAULT_ROLE = DataManagerRole.MANAGER;
    private static final DataManagerRole UPDATED_ROLE = DataManagerRole.PTCC;

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final Boolean DEFAULT_QC_ACCESS = false;
    private static final Boolean UPDATED_QC_ACCESS = true;

    private static final Boolean DEFAULT_VIEW_ONLY_ACCESS = false;
    private static final Boolean UPDATED_VIEW_ONLY_ACCESS = true;

    private static final Boolean DEFAULT_ENABLE_TEST_RESPONSE_DATE = false;
    private static final Boolean UPDATED_ENABLE_TEST_RESPONSE_DATE = true;

    private static final Boolean DEFAULT_ENABLE_MODE_OF_RECEIPT = false;
    private static final Boolean UPDATED_ENABLE_MODE_OF_RECEIPT = true;

    private static final Boolean DEFAULT_FORCE_PROFILE_CHECK = false;
    private static final Boolean UPDATED_FORCE_PROFILE_CHECK = true;

    private static final String ENTITY_API_URL = "/api/data-managers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DataManagerRepository dataManagerRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DataManagerRepository dataManagerRepositoryMock;

    @Autowired
    private DataManagerMapper dataManagerMapper;

    @Mock
    private DataManagerService dataManagerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDataManagerMockMvc;

    private DataManager dataManager;

    private DataManager insertedDataManager;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataManager createEntity() {
        return new DataManager()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .institute(DEFAULT_INSTITUTE)
            .primaryEmail(DEFAULT_PRIMARY_EMAIL)
            .secondaryEmail(DEFAULT_SECONDARY_EMAIL)
            .phone(DEFAULT_PHONE)
            .mobile(DEFAULT_MOBILE)
            .language(DEFAULT_LANGUAGE)
            .role(DEFAULT_ROLE)
            .status(DEFAULT_STATUS)
            .qcAccess(DEFAULT_QC_ACCESS)
            .viewOnlyAccess(DEFAULT_VIEW_ONLY_ACCESS)
            .enableTestResponseDate(DEFAULT_ENABLE_TEST_RESPONSE_DATE)
            .enableModeOfReceipt(DEFAULT_ENABLE_MODE_OF_RECEIPT)
            .forceProfileCheck(DEFAULT_FORCE_PROFILE_CHECK);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DataManager createUpdatedEntity() {
        return new DataManager()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .institute(UPDATED_INSTITUTE)
            .primaryEmail(UPDATED_PRIMARY_EMAIL)
            .secondaryEmail(UPDATED_SECONDARY_EMAIL)
            .phone(UPDATED_PHONE)
            .mobile(UPDATED_MOBILE)
            .language(UPDATED_LANGUAGE)
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .qcAccess(UPDATED_QC_ACCESS)
            .viewOnlyAccess(UPDATED_VIEW_ONLY_ACCESS)
            .enableTestResponseDate(UPDATED_ENABLE_TEST_RESPONSE_DATE)
            .enableModeOfReceipt(UPDATED_ENABLE_MODE_OF_RECEIPT)
            .forceProfileCheck(UPDATED_FORCE_PROFILE_CHECK);
    }

    @BeforeEach
    void initTest() {
        dataManager = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDataManager != null) {
            dataManagerRepository.delete(insertedDataManager);
            insertedDataManager = null;
        }
    }

    @Test
    @Transactional
    void createDataManager() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DataManager
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);
        var returnedDataManagerDTO = om.readValue(
            restDataManagerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dataManagerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DataManagerDTO.class
        );

        // Validate the DataManager in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDataManager = dataManagerMapper.toEntity(returnedDataManagerDTO);
        assertDataManagerUpdatableFieldsEquals(returnedDataManager, getPersistedDataManager(returnedDataManager));

        insertedDataManager = returnedDataManager;
    }

    @Test
    @Transactional
    void createDataManagerWithExistingId() throws Exception {
        // Create the DataManager with an existing ID
        dataManager.setId(1L);
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDataManagerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dataManagerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DataManager in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPrimaryEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dataManager.setPrimaryEmail(null);

        // Create the DataManager, which fails.
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        restDataManagerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dataManagerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dataManager.setRole(null);

        // Create the DataManager, which fails.
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        restDataManagerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dataManagerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dataManager.setStatus(null);

        // Create the DataManager, which fails.
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        restDataManagerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dataManagerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDataManagers() throws Exception {
        // Initialize the database
        insertedDataManager = dataManagerRepository.saveAndFlush(dataManager);

        // Get all the dataManagerList
        restDataManagerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataManager.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].institute").value(hasItem(DEFAULT_INSTITUTE)))
            .andExpect(jsonPath("$.[*].primaryEmail").value(hasItem(DEFAULT_PRIMARY_EMAIL)))
            .andExpect(jsonPath("$.[*].secondaryEmail").value(hasItem(DEFAULT_SECONDARY_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].qcAccess").value(hasItem(DEFAULT_QC_ACCESS)))
            .andExpect(jsonPath("$.[*].viewOnlyAccess").value(hasItem(DEFAULT_VIEW_ONLY_ACCESS)))
            .andExpect(jsonPath("$.[*].enableTestResponseDate").value(hasItem(DEFAULT_ENABLE_TEST_RESPONSE_DATE)))
            .andExpect(jsonPath("$.[*].enableModeOfReceipt").value(hasItem(DEFAULT_ENABLE_MODE_OF_RECEIPT)))
            .andExpect(jsonPath("$.[*].forceProfileCheck").value(hasItem(DEFAULT_FORCE_PROFILE_CHECK)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDataManagersWithEagerRelationshipsIsEnabled() throws Exception {
        when(dataManagerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDataManagerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(dataManagerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDataManagersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(dataManagerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDataManagerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(dataManagerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDataManager() throws Exception {
        // Initialize the database
        insertedDataManager = dataManagerRepository.saveAndFlush(dataManager);

        // Get the dataManager
        restDataManagerMockMvc
            .perform(get(ENTITY_API_URL_ID, dataManager.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dataManager.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.institute").value(DEFAULT_INSTITUTE))
            .andExpect(jsonPath("$.primaryEmail").value(DEFAULT_PRIMARY_EMAIL))
            .andExpect(jsonPath("$.secondaryEmail").value(DEFAULT_SECONDARY_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.qcAccess").value(DEFAULT_QC_ACCESS))
            .andExpect(jsonPath("$.viewOnlyAccess").value(DEFAULT_VIEW_ONLY_ACCESS))
            .andExpect(jsonPath("$.enableTestResponseDate").value(DEFAULT_ENABLE_TEST_RESPONSE_DATE))
            .andExpect(jsonPath("$.enableModeOfReceipt").value(DEFAULT_ENABLE_MODE_OF_RECEIPT))
            .andExpect(jsonPath("$.forceProfileCheck").value(DEFAULT_FORCE_PROFILE_CHECK));
    }

    @Test
    @Transactional
    void getNonExistingDataManager() throws Exception {
        // Get the dataManager
        restDataManagerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDataManager() throws Exception {
        // Initialize the database
        insertedDataManager = dataManagerRepository.saveAndFlush(dataManager);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dataManager
        DataManager updatedDataManager = dataManagerRepository.findById(dataManager.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDataManager are not directly saved in db
        em.detach(updatedDataManager);
        updatedDataManager
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .institute(UPDATED_INSTITUTE)
            .primaryEmail(UPDATED_PRIMARY_EMAIL)
            .secondaryEmail(UPDATED_SECONDARY_EMAIL)
            .phone(UPDATED_PHONE)
            .mobile(UPDATED_MOBILE)
            .language(UPDATED_LANGUAGE)
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .qcAccess(UPDATED_QC_ACCESS)
            .viewOnlyAccess(UPDATED_VIEW_ONLY_ACCESS)
            .enableTestResponseDate(UPDATED_ENABLE_TEST_RESPONSE_DATE)
            .enableModeOfReceipt(UPDATED_ENABLE_MODE_OF_RECEIPT)
            .forceProfileCheck(UPDATED_FORCE_PROFILE_CHECK);
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(updatedDataManager);

        restDataManagerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dataManagerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dataManagerDTO))
            )
            .andExpect(status().isOk());

        // Validate the DataManager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDataManagerToMatchAllProperties(updatedDataManager);
    }

    @Test
    @Transactional
    void putNonExistingDataManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataManager.setId(longCount.incrementAndGet());

        // Create the DataManager
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataManagerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dataManagerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dataManagerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DataManager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDataManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataManager.setId(longCount.incrementAndGet());

        // Create the DataManager
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDataManagerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dataManagerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DataManager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDataManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataManager.setId(longCount.incrementAndGet());

        // Create the DataManager
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDataManagerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dataManagerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DataManager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDataManagerWithPatch() throws Exception {
        // Initialize the database
        insertedDataManager = dataManagerRepository.saveAndFlush(dataManager);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dataManager using partial update
        DataManager partialUpdatedDataManager = new DataManager();
        partialUpdatedDataManager.setId(dataManager.getId());

        partialUpdatedDataManager
            .firstName(UPDATED_FIRST_NAME)
            .institute(UPDATED_INSTITUTE)
            .status(UPDATED_STATUS)
            .viewOnlyAccess(UPDATED_VIEW_ONLY_ACCESS)
            .enableTestResponseDate(UPDATED_ENABLE_TEST_RESPONSE_DATE)
            .enableModeOfReceipt(UPDATED_ENABLE_MODE_OF_RECEIPT)
            .forceProfileCheck(UPDATED_FORCE_PROFILE_CHECK);

        restDataManagerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDataManager.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDataManager))
            )
            .andExpect(status().isOk());

        // Validate the DataManager in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDataManagerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDataManager, dataManager),
            getPersistedDataManager(dataManager)
        );
    }

    @Test
    @Transactional
    void fullUpdateDataManagerWithPatch() throws Exception {
        // Initialize the database
        insertedDataManager = dataManagerRepository.saveAndFlush(dataManager);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dataManager using partial update
        DataManager partialUpdatedDataManager = new DataManager();
        partialUpdatedDataManager.setId(dataManager.getId());

        partialUpdatedDataManager
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .institute(UPDATED_INSTITUTE)
            .primaryEmail(UPDATED_PRIMARY_EMAIL)
            .secondaryEmail(UPDATED_SECONDARY_EMAIL)
            .phone(UPDATED_PHONE)
            .mobile(UPDATED_MOBILE)
            .language(UPDATED_LANGUAGE)
            .role(UPDATED_ROLE)
            .status(UPDATED_STATUS)
            .qcAccess(UPDATED_QC_ACCESS)
            .viewOnlyAccess(UPDATED_VIEW_ONLY_ACCESS)
            .enableTestResponseDate(UPDATED_ENABLE_TEST_RESPONSE_DATE)
            .enableModeOfReceipt(UPDATED_ENABLE_MODE_OF_RECEIPT)
            .forceProfileCheck(UPDATED_FORCE_PROFILE_CHECK);

        restDataManagerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDataManager.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDataManager))
            )
            .andExpect(status().isOk());

        // Validate the DataManager in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDataManagerUpdatableFieldsEquals(partialUpdatedDataManager, getPersistedDataManager(partialUpdatedDataManager));
    }

    @Test
    @Transactional
    void patchNonExistingDataManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataManager.setId(longCount.incrementAndGet());

        // Create the DataManager
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDataManagerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dataManagerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dataManagerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DataManager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDataManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataManager.setId(longCount.incrementAndGet());

        // Create the DataManager
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDataManagerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dataManagerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DataManager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDataManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dataManager.setId(longCount.incrementAndGet());

        // Create the DataManager
        DataManagerDTO dataManagerDTO = dataManagerMapper.toDto(dataManager);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDataManagerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dataManagerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DataManager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDataManager() throws Exception {
        // Initialize the database
        insertedDataManager = dataManagerRepository.saveAndFlush(dataManager);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dataManager
        restDataManagerMockMvc
            .perform(delete(ENTITY_API_URL_ID, dataManager.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dataManagerRepository.count();
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

    protected DataManager getPersistedDataManager(DataManager dataManager) {
        return dataManagerRepository.findById(dataManager.getId()).orElseThrow();
    }

    protected void assertPersistedDataManagerToMatchAllProperties(DataManager expectedDataManager) {
        assertDataManagerAllPropertiesEquals(expectedDataManager, getPersistedDataManager(expectedDataManager));
    }

    protected void assertPersistedDataManagerToMatchUpdatableProperties(DataManager expectedDataManager) {
        assertDataManagerAllUpdatablePropertiesEquals(expectedDataManager, getPersistedDataManager(expectedDataManager));
    }
}
