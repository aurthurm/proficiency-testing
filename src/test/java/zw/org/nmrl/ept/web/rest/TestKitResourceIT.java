package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.TestKitAsserts.*;
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
import zw.org.nmrl.ept.domain.TestKit;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.TestKitRepository;
import zw.org.nmrl.ept.service.dto.TestKitDTO;
import zw.org.nmrl.ept.service.mapper.TestKitMapper;

/**
 * Integration tests for the {@link TestKitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TestKitResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MANUFACTURER = "AAAAAAAAAA";
    private static final String UPDATED_MANUFACTURER = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/test-kits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestKitRepository testKitRepository;

    @Autowired
    private TestKitMapper testKitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTestKitMockMvc;

    private TestKit testKit;

    private TestKit insertedTestKit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestKit createEntity() {
        return new TestKit().name(DEFAULT_NAME).manufacturer(DEFAULT_MANUFACTURER).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestKit createUpdatedEntity() {
        return new TestKit().name(UPDATED_NAME).manufacturer(UPDATED_MANUFACTURER).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        testKit = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTestKit != null) {
            testKitRepository.delete(insertedTestKit);
            insertedTestKit = null;
        }
    }

    @Test
    @Transactional
    void createTestKit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TestKit
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);
        var returnedTestKitDTO = om.readValue(
            restTestKitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testKitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TestKitDTO.class
        );

        // Validate the TestKit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTestKit = testKitMapper.toEntity(returnedTestKitDTO);
        assertTestKitUpdatableFieldsEquals(returnedTestKit, getPersistedTestKit(returnedTestKit));

        insertedTestKit = returnedTestKit;
    }

    @Test
    @Transactional
    void createTestKitWithExistingId() throws Exception {
        // Create the TestKit with an existing ID
        testKit.setId(1L);
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTestKitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testKitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TestKit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        testKit.setName(null);

        // Create the TestKit, which fails.
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        restTestKitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testKitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        testKit.setStatus(null);

        // Create the TestKit, which fails.
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        restTestKitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testKitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTestKits() throws Exception {
        // Initialize the database
        insertedTestKit = testKitRepository.saveAndFlush(testKit);

        // Get all the testKitList
        restTestKitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testKit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].manufacturer").value(hasItem(DEFAULT_MANUFACTURER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getTestKit() throws Exception {
        // Initialize the database
        insertedTestKit = testKitRepository.saveAndFlush(testKit);

        // Get the testKit
        restTestKitMockMvc
            .perform(get(ENTITY_API_URL_ID, testKit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(testKit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.manufacturer").value(DEFAULT_MANUFACTURER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTestKit() throws Exception {
        // Get the testKit
        restTestKitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTestKit() throws Exception {
        // Initialize the database
        insertedTestKit = testKitRepository.saveAndFlush(testKit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testKit
        TestKit updatedTestKit = testKitRepository.findById(testKit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTestKit are not directly saved in db
        em.detach(updatedTestKit);
        updatedTestKit.name(UPDATED_NAME).manufacturer(UPDATED_MANUFACTURER).status(UPDATED_STATUS);
        TestKitDTO testKitDTO = testKitMapper.toDto(updatedTestKit);

        restTestKitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, testKitDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testKitDTO))
            )
            .andExpect(status().isOk());

        // Validate the TestKit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTestKitToMatchAllProperties(updatedTestKit);
    }

    @Test
    @Transactional
    void putNonExistingTestKit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testKit.setId(longCount.incrementAndGet());

        // Create the TestKit
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestKitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, testKitDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testKitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestKit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTestKit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testKit.setId(longCount.incrementAndGet());

        // Create the TestKit
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestKitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(testKitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestKit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTestKit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testKit.setId(longCount.incrementAndGet());

        // Create the TestKit
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestKitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testKitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestKit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTestKitWithPatch() throws Exception {
        // Initialize the database
        insertedTestKit = testKitRepository.saveAndFlush(testKit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testKit using partial update
        TestKit partialUpdatedTestKit = new TestKit();
        partialUpdatedTestKit.setId(testKit.getId());

        partialUpdatedTestKit.status(UPDATED_STATUS);

        restTestKitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestKit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTestKit))
            )
            .andExpect(status().isOk());

        // Validate the TestKit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestKitUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTestKit, testKit), getPersistedTestKit(testKit));
    }

    @Test
    @Transactional
    void fullUpdateTestKitWithPatch() throws Exception {
        // Initialize the database
        insertedTestKit = testKitRepository.saveAndFlush(testKit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testKit using partial update
        TestKit partialUpdatedTestKit = new TestKit();
        partialUpdatedTestKit.setId(testKit.getId());

        partialUpdatedTestKit.name(UPDATED_NAME).manufacturer(UPDATED_MANUFACTURER).status(UPDATED_STATUS);

        restTestKitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestKit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTestKit))
            )
            .andExpect(status().isOk());

        // Validate the TestKit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestKitUpdatableFieldsEquals(partialUpdatedTestKit, getPersistedTestKit(partialUpdatedTestKit));
    }

    @Test
    @Transactional
    void patchNonExistingTestKit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testKit.setId(longCount.incrementAndGet());

        // Create the TestKit
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestKitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, testKitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(testKitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestKit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTestKit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testKit.setId(longCount.incrementAndGet());

        // Create the TestKit
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestKitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(testKitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestKit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTestKit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        testKit.setId(longCount.incrementAndGet());

        // Create the TestKit
        TestKitDTO testKitDTO = testKitMapper.toDto(testKit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestKitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(testKitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestKit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTestKit() throws Exception {
        // Initialize the database
        insertedTestKit = testKitRepository.saveAndFlush(testKit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the testKit
        restTestKitMockMvc
            .perform(delete(ENTITY_API_URL_ID, testKit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return testKitRepository.count();
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

    protected TestKit getPersistedTestKit(TestKit testKit) {
        return testKitRepository.findById(testKit.getId()).orElseThrow();
    }

    protected void assertPersistedTestKitToMatchAllProperties(TestKit expectedTestKit) {
        assertTestKitAllPropertiesEquals(expectedTestKit, getPersistedTestKit(expectedTestKit));
    }

    protected void assertPersistedTestKitToMatchUpdatableProperties(TestKit expectedTestKit) {
        assertTestKitAllUpdatablePropertiesEquals(expectedTestKit, getPersistedTestKit(expectedTestKit));
    }
}
