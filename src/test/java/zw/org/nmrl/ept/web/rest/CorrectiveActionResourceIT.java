package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.CorrectiveActionAsserts.*;
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
import zw.org.nmrl.ept.domain.CorrectiveAction;
import zw.org.nmrl.ept.repository.CorrectiveActionRepository;
import zw.org.nmrl.ept.service.dto.CorrectiveActionDTO;
import zw.org.nmrl.ept.service.mapper.CorrectiveActionMapper;

/**
 * Integration tests for the {@link CorrectiveActionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CorrectiveActionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/corrective-actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CorrectiveActionRepository correctiveActionRepository;

    @Autowired
    private CorrectiveActionMapper correctiveActionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCorrectiveActionMockMvc;

    private CorrectiveAction correctiveAction;

    private CorrectiveAction insertedCorrectiveAction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CorrectiveAction createEntity() {
        return new CorrectiveAction().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CorrectiveAction createUpdatedEntity() {
        return new CorrectiveAction().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        correctiveAction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCorrectiveAction != null) {
            correctiveActionRepository.delete(insertedCorrectiveAction);
            insertedCorrectiveAction = null;
        }
    }

    @Test
    @Transactional
    void createCorrectiveAction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CorrectiveAction
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);
        var returnedCorrectiveActionDTO = om.readValue(
            restCorrectiveActionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correctiveActionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CorrectiveActionDTO.class
        );

        // Validate the CorrectiveAction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCorrectiveAction = correctiveActionMapper.toEntity(returnedCorrectiveActionDTO);
        assertCorrectiveActionUpdatableFieldsEquals(returnedCorrectiveAction, getPersistedCorrectiveAction(returnedCorrectiveAction));

        insertedCorrectiveAction = returnedCorrectiveAction;
    }

    @Test
    @Transactional
    void createCorrectiveActionWithExistingId() throws Exception {
        // Create the CorrectiveAction with an existing ID
        correctiveAction.setId(1L);
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCorrectiveActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correctiveActionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CorrectiveAction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        correctiveAction.setTitle(null);

        // Create the CorrectiveAction, which fails.
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);

        restCorrectiveActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correctiveActionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCorrectiveActions() throws Exception {
        // Initialize the database
        insertedCorrectiveAction = correctiveActionRepository.saveAndFlush(correctiveAction);

        // Get all the correctiveActionList
        restCorrectiveActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(correctiveAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getCorrectiveAction() throws Exception {
        // Initialize the database
        insertedCorrectiveAction = correctiveActionRepository.saveAndFlush(correctiveAction);

        // Get the correctiveAction
        restCorrectiveActionMockMvc
            .perform(get(ENTITY_API_URL_ID, correctiveAction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(correctiveAction.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingCorrectiveAction() throws Exception {
        // Get the correctiveAction
        restCorrectiveActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCorrectiveAction() throws Exception {
        // Initialize the database
        insertedCorrectiveAction = correctiveActionRepository.saveAndFlush(correctiveAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the correctiveAction
        CorrectiveAction updatedCorrectiveAction = correctiveActionRepository.findById(correctiveAction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCorrectiveAction are not directly saved in db
        em.detach(updatedCorrectiveAction);
        updatedCorrectiveAction.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(updatedCorrectiveAction);

        restCorrectiveActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, correctiveActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(correctiveActionDTO))
            )
            .andExpect(status().isOk());

        // Validate the CorrectiveAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCorrectiveActionToMatchAllProperties(updatedCorrectiveAction);
    }

    @Test
    @Transactional
    void putNonExistingCorrectiveAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correctiveAction.setId(longCount.incrementAndGet());

        // Create the CorrectiveAction
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorrectiveActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, correctiveActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(correctiveActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrectiveAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCorrectiveAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correctiveAction.setId(longCount.incrementAndGet());

        // Create the CorrectiveAction
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrectiveActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(correctiveActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrectiveAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCorrectiveAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correctiveAction.setId(longCount.incrementAndGet());

        // Create the CorrectiveAction
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrectiveActionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correctiveActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CorrectiveAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCorrectiveActionWithPatch() throws Exception {
        // Initialize the database
        insertedCorrectiveAction = correctiveActionRepository.saveAndFlush(correctiveAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the correctiveAction using partial update
        CorrectiveAction partialUpdatedCorrectiveAction = new CorrectiveAction();
        partialUpdatedCorrectiveAction.setId(correctiveAction.getId());

        partialUpdatedCorrectiveAction.description(UPDATED_DESCRIPTION);

        restCorrectiveActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCorrectiveAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCorrectiveAction))
            )
            .andExpect(status().isOk());

        // Validate the CorrectiveAction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCorrectiveActionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCorrectiveAction, correctiveAction),
            getPersistedCorrectiveAction(correctiveAction)
        );
    }

    @Test
    @Transactional
    void fullUpdateCorrectiveActionWithPatch() throws Exception {
        // Initialize the database
        insertedCorrectiveAction = correctiveActionRepository.saveAndFlush(correctiveAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the correctiveAction using partial update
        CorrectiveAction partialUpdatedCorrectiveAction = new CorrectiveAction();
        partialUpdatedCorrectiveAction.setId(correctiveAction.getId());

        partialUpdatedCorrectiveAction.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restCorrectiveActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCorrectiveAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCorrectiveAction))
            )
            .andExpect(status().isOk());

        // Validate the CorrectiveAction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCorrectiveActionUpdatableFieldsEquals(
            partialUpdatedCorrectiveAction,
            getPersistedCorrectiveAction(partialUpdatedCorrectiveAction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCorrectiveAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correctiveAction.setId(longCount.incrementAndGet());

        // Create the CorrectiveAction
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorrectiveActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, correctiveActionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(correctiveActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrectiveAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCorrectiveAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correctiveAction.setId(longCount.incrementAndGet());

        // Create the CorrectiveAction
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrectiveActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(correctiveActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrectiveAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCorrectiveAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correctiveAction.setId(longCount.incrementAndGet());

        // Create the CorrectiveAction
        CorrectiveActionDTO correctiveActionDTO = correctiveActionMapper.toDto(correctiveAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrectiveActionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(correctiveActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CorrectiveAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCorrectiveAction() throws Exception {
        // Initialize the database
        insertedCorrectiveAction = correctiveActionRepository.saveAndFlush(correctiveAction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the correctiveAction
        restCorrectiveActionMockMvc
            .perform(delete(ENTITY_API_URL_ID, correctiveAction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return correctiveActionRepository.count();
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

    protected CorrectiveAction getPersistedCorrectiveAction(CorrectiveAction correctiveAction) {
        return correctiveActionRepository.findById(correctiveAction.getId()).orElseThrow();
    }

    protected void assertPersistedCorrectiveActionToMatchAllProperties(CorrectiveAction expectedCorrectiveAction) {
        assertCorrectiveActionAllPropertiesEquals(expectedCorrectiveAction, getPersistedCorrectiveAction(expectedCorrectiveAction));
    }

    protected void assertPersistedCorrectiveActionToMatchUpdatableProperties(CorrectiveAction expectedCorrectiveAction) {
        assertCorrectiveActionAllUpdatablePropertiesEquals(
            expectedCorrectiveAction,
            getPersistedCorrectiveAction(expectedCorrectiveAction)
        );
    }
}
