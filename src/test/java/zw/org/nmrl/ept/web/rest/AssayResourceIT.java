package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.AssayAsserts.*;
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
import zw.org.nmrl.ept.domain.Assay;
import zw.org.nmrl.ept.domain.enumeration.AssayType;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.AssayRepository;
import zw.org.nmrl.ept.service.dto.AssayDTO;
import zw.org.nmrl.ept.service.mapper.AssayMapper;

/**
 * Integration tests for the {@link AssayResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AssayResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final AssayType DEFAULT_ASSAY_TYPE = AssayType.DTS_ALGORITHM;
    private static final AssayType UPDATED_ASSAY_TYPE = AssayType.VL_ASSAY;

    private static final String DEFAULT_MANUFACTURER = "AAAAAAAAAA";
    private static final String UPDATED_MANUFACTURER = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/assays";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssayRepository assayRepository;

    @Autowired
    private AssayMapper assayMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssayMockMvc;

    private Assay assay;

    private Assay insertedAssay;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assay createEntity() {
        return new Assay().name(DEFAULT_NAME).assayType(DEFAULT_ASSAY_TYPE).manufacturer(DEFAULT_MANUFACTURER).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assay createUpdatedEntity() {
        return new Assay().name(UPDATED_NAME).assayType(UPDATED_ASSAY_TYPE).manufacturer(UPDATED_MANUFACTURER).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        assay = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAssay != null) {
            assayRepository.delete(insertedAssay);
            insertedAssay = null;
        }
    }

    @Test
    @Transactional
    void createAssay() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Assay
        AssayDTO assayDTO = assayMapper.toDto(assay);
        var returnedAssayDTO = om.readValue(
            restAssayMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assayDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssayDTO.class
        );

        // Validate the Assay in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssay = assayMapper.toEntity(returnedAssayDTO);
        assertAssayUpdatableFieldsEquals(returnedAssay, getPersistedAssay(returnedAssay));

        insertedAssay = returnedAssay;
    }

    @Test
    @Transactional
    void createAssayWithExistingId() throws Exception {
        // Create the Assay with an existing ID
        assay.setId(1L);
        AssayDTO assayDTO = assayMapper.toDto(assay);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assayDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Assay in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assay.setName(null);

        // Create the Assay, which fails.
        AssayDTO assayDTO = assayMapper.toDto(assay);

        restAssayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assayDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAssayTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assay.setAssayType(null);

        // Create the Assay, which fails.
        AssayDTO assayDTO = assayMapper.toDto(assay);

        restAssayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assayDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assay.setStatus(null);

        // Create the Assay, which fails.
        AssayDTO assayDTO = assayMapper.toDto(assay);

        restAssayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assayDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssays() throws Exception {
        // Initialize the database
        insertedAssay = assayRepository.saveAndFlush(assay);

        // Get all the assayList
        restAssayMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assay.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].assayType").value(hasItem(DEFAULT_ASSAY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].manufacturer").value(hasItem(DEFAULT_MANUFACTURER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getAssay() throws Exception {
        // Initialize the database
        insertedAssay = assayRepository.saveAndFlush(assay);

        // Get the assay
        restAssayMockMvc
            .perform(get(ENTITY_API_URL_ID, assay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assay.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.assayType").value(DEFAULT_ASSAY_TYPE.toString()))
            .andExpect(jsonPath("$.manufacturer").value(DEFAULT_MANUFACTURER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAssay() throws Exception {
        // Get the assay
        restAssayMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssay() throws Exception {
        // Initialize the database
        insertedAssay = assayRepository.saveAndFlush(assay);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assay
        Assay updatedAssay = assayRepository.findById(assay.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssay are not directly saved in db
        em.detach(updatedAssay);
        updatedAssay.name(UPDATED_NAME).assayType(UPDATED_ASSAY_TYPE).manufacturer(UPDATED_MANUFACTURER).status(UPDATED_STATUS);
        AssayDTO assayDTO = assayMapper.toDto(updatedAssay);

        restAssayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assayDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assayDTO))
            )
            .andExpect(status().isOk());

        // Validate the Assay in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssayToMatchAllProperties(updatedAssay);
    }

    @Test
    @Transactional
    void putNonExistingAssay() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assay.setId(longCount.incrementAndGet());

        // Create the Assay
        AssayDTO assayDTO = assayMapper.toDto(assay);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assayDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assay in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssay() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assay.setId(longCount.incrementAndGet());

        // Create the Assay
        AssayDTO assayDTO = assayMapper.toDto(assay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assay in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssay() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assay.setId(longCount.incrementAndGet());

        // Create the Assay
        AssayDTO assayDTO = assayMapper.toDto(assay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssayMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assayDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assay in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssayWithPatch() throws Exception {
        // Initialize the database
        insertedAssay = assayRepository.saveAndFlush(assay);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assay using partial update
        Assay partialUpdatedAssay = new Assay();
        partialUpdatedAssay.setId(assay.getId());

        partialUpdatedAssay.name(UPDATED_NAME).assayType(UPDATED_ASSAY_TYPE).status(UPDATED_STATUS);

        restAssayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssay.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssay))
            )
            .andExpect(status().isOk());

        // Validate the Assay in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssayUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAssay, assay), getPersistedAssay(assay));
    }

    @Test
    @Transactional
    void fullUpdateAssayWithPatch() throws Exception {
        // Initialize the database
        insertedAssay = assayRepository.saveAndFlush(assay);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assay using partial update
        Assay partialUpdatedAssay = new Assay();
        partialUpdatedAssay.setId(assay.getId());

        partialUpdatedAssay.name(UPDATED_NAME).assayType(UPDATED_ASSAY_TYPE).manufacturer(UPDATED_MANUFACTURER).status(UPDATED_STATUS);

        restAssayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssay.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssay))
            )
            .andExpect(status().isOk());

        // Validate the Assay in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssayUpdatableFieldsEquals(partialUpdatedAssay, getPersistedAssay(partialUpdatedAssay));
    }

    @Test
    @Transactional
    void patchNonExistingAssay() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assay.setId(longCount.incrementAndGet());

        // Create the Assay
        AssayDTO assayDTO = assayMapper.toDto(assay);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assayDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assay in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssay() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assay.setId(longCount.incrementAndGet());

        // Create the Assay
        AssayDTO assayDTO = assayMapper.toDto(assay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assay in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssay() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assay.setId(longCount.incrementAndGet());

        // Create the Assay
        AssayDTO assayDTO = assayMapper.toDto(assay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssayMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assayDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assay in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssay() throws Exception {
        // Initialize the database
        insertedAssay = assayRepository.saveAndFlush(assay);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assay
        restAssayMockMvc
            .perform(delete(ENTITY_API_URL_ID, assay.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assayRepository.count();
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

    protected Assay getPersistedAssay(Assay assay) {
        return assayRepository.findById(assay.getId()).orElseThrow();
    }

    protected void assertPersistedAssayToMatchAllProperties(Assay expectedAssay) {
        assertAssayAllPropertiesEquals(expectedAssay, getPersistedAssay(expectedAssay));
    }

    protected void assertPersistedAssayToMatchUpdatableProperties(Assay expectedAssay) {
        assertAssayAllUpdatablePropertiesEquals(expectedAssay, getPersistedAssay(expectedAssay));
    }
}
