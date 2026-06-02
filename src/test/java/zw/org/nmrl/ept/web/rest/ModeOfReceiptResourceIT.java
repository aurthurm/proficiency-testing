package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ModeOfReceiptAsserts.*;
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
import zw.org.nmrl.ept.domain.ModeOfReceipt;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.ModeOfReceiptRepository;
import zw.org.nmrl.ept.service.dto.ModeOfReceiptDTO;
import zw.org.nmrl.ept.service.mapper.ModeOfReceiptMapper;

/**
 * Integration tests for the {@link ModeOfReceiptResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ModeOfReceiptResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/mode-of-receipts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModeOfReceiptRepository modeOfReceiptRepository;

    @Autowired
    private ModeOfReceiptMapper modeOfReceiptMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModeOfReceiptMockMvc;

    private ModeOfReceipt modeOfReceipt;

    private ModeOfReceipt insertedModeOfReceipt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeOfReceipt createEntity() {
        return new ModeOfReceipt().name(DEFAULT_NAME).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeOfReceipt createUpdatedEntity() {
        return new ModeOfReceipt().name(UPDATED_NAME).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        modeOfReceipt = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedModeOfReceipt != null) {
            modeOfReceiptRepository.delete(insertedModeOfReceipt);
            insertedModeOfReceipt = null;
        }
    }

    @Test
    @Transactional
    void createModeOfReceipt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ModeOfReceipt
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);
        var returnedModeOfReceiptDTO = om.readValue(
            restModeOfReceiptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modeOfReceiptDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ModeOfReceiptDTO.class
        );

        // Validate the ModeOfReceipt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedModeOfReceipt = modeOfReceiptMapper.toEntity(returnedModeOfReceiptDTO);
        assertModeOfReceiptUpdatableFieldsEquals(returnedModeOfReceipt, getPersistedModeOfReceipt(returnedModeOfReceipt));

        insertedModeOfReceipt = returnedModeOfReceipt;
    }

    @Test
    @Transactional
    void createModeOfReceiptWithExistingId() throws Exception {
        // Create the ModeOfReceipt with an existing ID
        modeOfReceipt.setId(1L);
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restModeOfReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modeOfReceiptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ModeOfReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        modeOfReceipt.setName(null);

        // Create the ModeOfReceipt, which fails.
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        restModeOfReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modeOfReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        modeOfReceipt.setStatus(null);

        // Create the ModeOfReceipt, which fails.
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        restModeOfReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modeOfReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllModeOfReceipts() throws Exception {
        // Initialize the database
        insertedModeOfReceipt = modeOfReceiptRepository.saveAndFlush(modeOfReceipt);

        // Get all the modeOfReceiptList
        restModeOfReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modeOfReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getModeOfReceipt() throws Exception {
        // Initialize the database
        insertedModeOfReceipt = modeOfReceiptRepository.saveAndFlush(modeOfReceipt);

        // Get the modeOfReceipt
        restModeOfReceiptMockMvc
            .perform(get(ENTITY_API_URL_ID, modeOfReceipt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(modeOfReceipt.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingModeOfReceipt() throws Exception {
        // Get the modeOfReceipt
        restModeOfReceiptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingModeOfReceipt() throws Exception {
        // Initialize the database
        insertedModeOfReceipt = modeOfReceiptRepository.saveAndFlush(modeOfReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the modeOfReceipt
        ModeOfReceipt updatedModeOfReceipt = modeOfReceiptRepository.findById(modeOfReceipt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedModeOfReceipt are not directly saved in db
        em.detach(updatedModeOfReceipt);
        updatedModeOfReceipt.name(UPDATED_NAME).status(UPDATED_STATUS);
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(updatedModeOfReceipt);

        restModeOfReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modeOfReceiptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modeOfReceiptDTO))
            )
            .andExpect(status().isOk());

        // Validate the ModeOfReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedModeOfReceiptToMatchAllProperties(updatedModeOfReceipt);
    }

    @Test
    @Transactional
    void putNonExistingModeOfReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modeOfReceipt.setId(longCount.incrementAndGet());

        // Create the ModeOfReceipt
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModeOfReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modeOfReceiptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modeOfReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeOfReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchModeOfReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modeOfReceipt.setId(longCount.incrementAndGet());

        // Create the ModeOfReceipt
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeOfReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modeOfReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeOfReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModeOfReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modeOfReceipt.setId(longCount.incrementAndGet());

        // Create the ModeOfReceipt
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeOfReceiptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modeOfReceiptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModeOfReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateModeOfReceiptWithPatch() throws Exception {
        // Initialize the database
        insertedModeOfReceipt = modeOfReceiptRepository.saveAndFlush(modeOfReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the modeOfReceipt using partial update
        ModeOfReceipt partialUpdatedModeOfReceipt = new ModeOfReceipt();
        partialUpdatedModeOfReceipt.setId(modeOfReceipt.getId());

        partialUpdatedModeOfReceipt.status(UPDATED_STATUS);

        restModeOfReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModeOfReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModeOfReceipt))
            )
            .andExpect(status().isOk());

        // Validate the ModeOfReceipt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModeOfReceiptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedModeOfReceipt, modeOfReceipt),
            getPersistedModeOfReceipt(modeOfReceipt)
        );
    }

    @Test
    @Transactional
    void fullUpdateModeOfReceiptWithPatch() throws Exception {
        // Initialize the database
        insertedModeOfReceipt = modeOfReceiptRepository.saveAndFlush(modeOfReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the modeOfReceipt using partial update
        ModeOfReceipt partialUpdatedModeOfReceipt = new ModeOfReceipt();
        partialUpdatedModeOfReceipt.setId(modeOfReceipt.getId());

        partialUpdatedModeOfReceipt.name(UPDATED_NAME).status(UPDATED_STATUS);

        restModeOfReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModeOfReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModeOfReceipt))
            )
            .andExpect(status().isOk());

        // Validate the ModeOfReceipt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModeOfReceiptUpdatableFieldsEquals(partialUpdatedModeOfReceipt, getPersistedModeOfReceipt(partialUpdatedModeOfReceipt));
    }

    @Test
    @Transactional
    void patchNonExistingModeOfReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modeOfReceipt.setId(longCount.incrementAndGet());

        // Create the ModeOfReceipt
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModeOfReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, modeOfReceiptDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(modeOfReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeOfReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModeOfReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modeOfReceipt.setId(longCount.incrementAndGet());

        // Create the ModeOfReceipt
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeOfReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(modeOfReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModeOfReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModeOfReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modeOfReceipt.setId(longCount.incrementAndGet());

        // Create the ModeOfReceipt
        ModeOfReceiptDTO modeOfReceiptDTO = modeOfReceiptMapper.toDto(modeOfReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeOfReceiptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(modeOfReceiptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModeOfReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteModeOfReceipt() throws Exception {
        // Initialize the database
        insertedModeOfReceipt = modeOfReceiptRepository.saveAndFlush(modeOfReceipt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the modeOfReceipt
        restModeOfReceiptMockMvc
            .perform(delete(ENTITY_API_URL_ID, modeOfReceipt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return modeOfReceiptRepository.count();
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

    protected ModeOfReceipt getPersistedModeOfReceipt(ModeOfReceipt modeOfReceipt) {
        return modeOfReceiptRepository.findById(modeOfReceipt.getId()).orElseThrow();
    }

    protected void assertPersistedModeOfReceiptToMatchAllProperties(ModeOfReceipt expectedModeOfReceipt) {
        assertModeOfReceiptAllPropertiesEquals(expectedModeOfReceipt, getPersistedModeOfReceipt(expectedModeOfReceipt));
    }

    protected void assertPersistedModeOfReceiptToMatchUpdatableProperties(ModeOfReceipt expectedModeOfReceipt) {
        assertModeOfReceiptAllUpdatablePropertiesEquals(expectedModeOfReceipt, getPersistedModeOfReceipt(expectedModeOfReceipt));
    }
}
