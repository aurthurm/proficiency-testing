package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.NotTestedReasonAsserts.*;
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
import zw.org.nmrl.ept.domain.NotTestedReason;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.NotTestedReasonRepository;
import zw.org.nmrl.ept.service.dto.NotTestedReasonDTO;
import zw.org.nmrl.ept.service.mapper.NotTestedReasonMapper;

/**
 * Integration tests for the {@link NotTestedReasonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotTestedReasonResourceIT {

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/not-tested-reasons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotTestedReasonRepository notTestedReasonRepository;

    @Autowired
    private NotTestedReasonMapper notTestedReasonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotTestedReasonMockMvc;

    private NotTestedReason notTestedReason;

    private NotTestedReason insertedNotTestedReason;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotTestedReason createEntity() {
        return new NotTestedReason().reason(DEFAULT_REASON).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotTestedReason createUpdatedEntity() {
        return new NotTestedReason().reason(UPDATED_REASON).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        notTestedReason = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotTestedReason != null) {
            notTestedReasonRepository.delete(insertedNotTestedReason);
            insertedNotTestedReason = null;
        }
    }

    @Test
    @Transactional
    void createNotTestedReason() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NotTestedReason
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);
        var returnedNotTestedReasonDTO = om.readValue(
            restNotTestedReasonMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notTestedReasonDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotTestedReasonDTO.class
        );

        // Validate the NotTestedReason in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotTestedReason = notTestedReasonMapper.toEntity(returnedNotTestedReasonDTO);
        assertNotTestedReasonUpdatableFieldsEquals(returnedNotTestedReason, getPersistedNotTestedReason(returnedNotTestedReason));

        insertedNotTestedReason = returnedNotTestedReason;
    }

    @Test
    @Transactional
    void createNotTestedReasonWithExistingId() throws Exception {
        // Create the NotTestedReason with an existing ID
        notTestedReason.setId(1L);
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotTestedReasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notTestedReasonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the NotTestedReason in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notTestedReason.setReason(null);

        // Create the NotTestedReason, which fails.
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        restNotTestedReasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notTestedReasonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notTestedReason.setStatus(null);

        // Create the NotTestedReason, which fails.
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        restNotTestedReasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notTestedReasonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotTestedReasons() throws Exception {
        // Initialize the database
        insertedNotTestedReason = notTestedReasonRepository.saveAndFlush(notTestedReason);

        // Get all the notTestedReasonList
        restNotTestedReasonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notTestedReason.getId().intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getNotTestedReason() throws Exception {
        // Initialize the database
        insertedNotTestedReason = notTestedReasonRepository.saveAndFlush(notTestedReason);

        // Get the notTestedReason
        restNotTestedReasonMockMvc
            .perform(get(ENTITY_API_URL_ID, notTestedReason.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notTestedReason.getId().intValue()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNotTestedReason() throws Exception {
        // Get the notTestedReason
        restNotTestedReasonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotTestedReason() throws Exception {
        // Initialize the database
        insertedNotTestedReason = notTestedReasonRepository.saveAndFlush(notTestedReason);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notTestedReason
        NotTestedReason updatedNotTestedReason = notTestedReasonRepository.findById(notTestedReason.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotTestedReason are not directly saved in db
        em.detach(updatedNotTestedReason);
        updatedNotTestedReason.reason(UPDATED_REASON).status(UPDATED_STATUS);
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(updatedNotTestedReason);

        restNotTestedReasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notTestedReasonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notTestedReasonDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotTestedReason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotTestedReasonToMatchAllProperties(updatedNotTestedReason);
    }

    @Test
    @Transactional
    void putNonExistingNotTestedReason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notTestedReason.setId(longCount.incrementAndGet());

        // Create the NotTestedReason
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotTestedReasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notTestedReasonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notTestedReasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotTestedReason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotTestedReason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notTestedReason.setId(longCount.incrementAndGet());

        // Create the NotTestedReason
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotTestedReasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notTestedReasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotTestedReason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotTestedReason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notTestedReason.setId(longCount.incrementAndGet());

        // Create the NotTestedReason
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotTestedReasonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notTestedReasonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotTestedReason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotTestedReasonWithPatch() throws Exception {
        // Initialize the database
        insertedNotTestedReason = notTestedReasonRepository.saveAndFlush(notTestedReason);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notTestedReason using partial update
        NotTestedReason partialUpdatedNotTestedReason = new NotTestedReason();
        partialUpdatedNotTestedReason.setId(notTestedReason.getId());

        restNotTestedReasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotTestedReason.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotTestedReason))
            )
            .andExpect(status().isOk());

        // Validate the NotTestedReason in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotTestedReasonUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotTestedReason, notTestedReason),
            getPersistedNotTestedReason(notTestedReason)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotTestedReasonWithPatch() throws Exception {
        // Initialize the database
        insertedNotTestedReason = notTestedReasonRepository.saveAndFlush(notTestedReason);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notTestedReason using partial update
        NotTestedReason partialUpdatedNotTestedReason = new NotTestedReason();
        partialUpdatedNotTestedReason.setId(notTestedReason.getId());

        partialUpdatedNotTestedReason.reason(UPDATED_REASON).status(UPDATED_STATUS);

        restNotTestedReasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotTestedReason.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotTestedReason))
            )
            .andExpect(status().isOk());

        // Validate the NotTestedReason in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotTestedReasonUpdatableFieldsEquals(
            partialUpdatedNotTestedReason,
            getPersistedNotTestedReason(partialUpdatedNotTestedReason)
        );
    }

    @Test
    @Transactional
    void patchNonExistingNotTestedReason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notTestedReason.setId(longCount.incrementAndGet());

        // Create the NotTestedReason
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotTestedReasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notTestedReasonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notTestedReasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotTestedReason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotTestedReason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notTestedReason.setId(longCount.incrementAndGet());

        // Create the NotTestedReason
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotTestedReasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notTestedReasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotTestedReason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotTestedReason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notTestedReason.setId(longCount.incrementAndGet());

        // Create the NotTestedReason
        NotTestedReasonDTO notTestedReasonDTO = notTestedReasonMapper.toDto(notTestedReason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotTestedReasonMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(notTestedReasonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotTestedReason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotTestedReason() throws Exception {
        // Initialize the database
        insertedNotTestedReason = notTestedReasonRepository.saveAndFlush(notTestedReason);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notTestedReason
        restNotTestedReasonMockMvc
            .perform(delete(ENTITY_API_URL_ID, notTestedReason.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notTestedReasonRepository.count();
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

    protected NotTestedReason getPersistedNotTestedReason(NotTestedReason notTestedReason) {
        return notTestedReasonRepository.findById(notTestedReason.getId()).orElseThrow();
    }

    protected void assertPersistedNotTestedReasonToMatchAllProperties(NotTestedReason expectedNotTestedReason) {
        assertNotTestedReasonAllPropertiesEquals(expectedNotTestedReason, getPersistedNotTestedReason(expectedNotTestedReason));
    }

    protected void assertPersistedNotTestedReasonToMatchUpdatableProperties(NotTestedReason expectedNotTestedReason) {
        assertNotTestedReasonAllUpdatablePropertiesEquals(expectedNotTestedReason, getPersistedNotTestedReason(expectedNotTestedReason));
    }
}
