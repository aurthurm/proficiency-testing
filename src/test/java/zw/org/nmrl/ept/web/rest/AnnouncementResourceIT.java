package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.AnnouncementAsserts.*;
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
import zw.org.nmrl.ept.domain.Announcement;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;
import zw.org.nmrl.ept.repository.AnnouncementRepository;
import zw.org.nmrl.ept.service.dto.AnnouncementDTO;
import zw.org.nmrl.ept.service.mapper.AnnouncementMapper;

/**
 * Integration tests for the {@link AnnouncementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AnnouncementResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final ContentStatus DEFAULT_STATUS = ContentStatus.PUBLISHED;
    private static final ContentStatus UPDATED_STATUS = ContentStatus.DRAFT;

    private static final Instant DEFAULT_PUBLISHED_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_PUBLISHED_TO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_TO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/announcements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAnnouncementMockMvc;

    private Announcement announcement;

    private Announcement insertedAnnouncement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Announcement createEntity() {
        return new Announcement()
            .title(DEFAULT_TITLE)
            .body(DEFAULT_BODY)
            .status(DEFAULT_STATUS)
            .publishedFrom(DEFAULT_PUBLISHED_FROM)
            .publishedTo(DEFAULT_PUBLISHED_TO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Announcement createUpdatedEntity() {
        return new Announcement()
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .status(UPDATED_STATUS)
            .publishedFrom(UPDATED_PUBLISHED_FROM)
            .publishedTo(UPDATED_PUBLISHED_TO);
    }

    @BeforeEach
    void initTest() {
        announcement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAnnouncement != null) {
            announcementRepository.delete(insertedAnnouncement);
            insertedAnnouncement = null;
        }
    }

    @Test
    @Transactional
    void createAnnouncement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Announcement
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);
        var returnedAnnouncementDTO = om.readValue(
            restAnnouncementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(announcementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AnnouncementDTO.class
        );

        // Validate the Announcement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAnnouncement = announcementMapper.toEntity(returnedAnnouncementDTO);
        assertAnnouncementUpdatableFieldsEquals(returnedAnnouncement, getPersistedAnnouncement(returnedAnnouncement));

        insertedAnnouncement = returnedAnnouncement;
    }

    @Test
    @Transactional
    void createAnnouncementWithExistingId() throws Exception {
        // Create the Announcement with an existing ID
        announcement.setId(1L);
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnnouncementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(announcementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        announcement.setTitle(null);

        // Create the Announcement, which fails.
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        restAnnouncementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(announcementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        announcement.setStatus(null);

        // Create the Announcement, which fails.
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        restAnnouncementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(announcementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAnnouncements() throws Exception {
        // Initialize the database
        insertedAnnouncement = announcementRepository.saveAndFlush(announcement);

        // Get all the announcementList
        restAnnouncementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].publishedFrom").value(hasItem(DEFAULT_PUBLISHED_FROM.toString())))
            .andExpect(jsonPath("$.[*].publishedTo").value(hasItem(DEFAULT_PUBLISHED_TO.toString())));
    }

    @Test
    @Transactional
    void getAnnouncement() throws Exception {
        // Initialize the database
        insertedAnnouncement = announcementRepository.saveAndFlush(announcement);

        // Get the announcement
        restAnnouncementMockMvc
            .perform(get(ENTITY_API_URL_ID, announcement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(announcement.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.publishedFrom").value(DEFAULT_PUBLISHED_FROM.toString()))
            .andExpect(jsonPath("$.publishedTo").value(DEFAULT_PUBLISHED_TO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAnnouncement() throws Exception {
        // Get the announcement
        restAnnouncementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAnnouncement() throws Exception {
        // Initialize the database
        insertedAnnouncement = announcementRepository.saveAndFlush(announcement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the announcement
        Announcement updatedAnnouncement = announcementRepository.findById(announcement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAnnouncement are not directly saved in db
        em.detach(updatedAnnouncement);
        updatedAnnouncement
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .status(UPDATED_STATUS)
            .publishedFrom(UPDATED_PUBLISHED_FROM)
            .publishedTo(UPDATED_PUBLISHED_TO);
        AnnouncementDTO announcementDTO = announcementMapper.toDto(updatedAnnouncement);

        restAnnouncementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, announcementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(announcementDTO))
            )
            .andExpect(status().isOk());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAnnouncementToMatchAllProperties(updatedAnnouncement);
    }

    @Test
    @Transactional
    void putNonExistingAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // Create the Announcement
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, announcementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(announcementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // Create the Announcement
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(announcementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // Create the Announcement
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(announcementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAnnouncementWithPatch() throws Exception {
        // Initialize the database
        insertedAnnouncement = announcementRepository.saveAndFlush(announcement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the announcement using partial update
        Announcement partialUpdatedAnnouncement = new Announcement();
        partialUpdatedAnnouncement.setId(announcement.getId());

        partialUpdatedAnnouncement.title(UPDATED_TITLE).body(UPDATED_BODY).status(UPDATED_STATUS).publishedTo(UPDATED_PUBLISHED_TO);

        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnnouncement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAnnouncement))
            )
            .andExpect(status().isOk());

        // Validate the Announcement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnnouncementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAnnouncement, announcement),
            getPersistedAnnouncement(announcement)
        );
    }

    @Test
    @Transactional
    void fullUpdateAnnouncementWithPatch() throws Exception {
        // Initialize the database
        insertedAnnouncement = announcementRepository.saveAndFlush(announcement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the announcement using partial update
        Announcement partialUpdatedAnnouncement = new Announcement();
        partialUpdatedAnnouncement.setId(announcement.getId());

        partialUpdatedAnnouncement
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .status(UPDATED_STATUS)
            .publishedFrom(UPDATED_PUBLISHED_FROM)
            .publishedTo(UPDATED_PUBLISHED_TO);

        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnnouncement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAnnouncement))
            )
            .andExpect(status().isOk());

        // Validate the Announcement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnnouncementUpdatableFieldsEquals(partialUpdatedAnnouncement, getPersistedAnnouncement(partialUpdatedAnnouncement));
    }

    @Test
    @Transactional
    void patchNonExistingAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // Create the Announcement
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, announcementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(announcementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // Create the Announcement
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(announcementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAnnouncement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        announcement.setId(longCount.incrementAndGet());

        // Create the Announcement
        AnnouncementDTO announcementDTO = announcementMapper.toDto(announcement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnnouncementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(announcementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Announcement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAnnouncement() throws Exception {
        // Initialize the database
        insertedAnnouncement = announcementRepository.saveAndFlush(announcement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the announcement
        restAnnouncementMockMvc
            .perform(delete(ENTITY_API_URL_ID, announcement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return announcementRepository.count();
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

    protected Announcement getPersistedAnnouncement(Announcement announcement) {
        return announcementRepository.findById(announcement.getId()).orElseThrow();
    }

    protected void assertPersistedAnnouncementToMatchAllProperties(Announcement expectedAnnouncement) {
        assertAnnouncementAllPropertiesEquals(expectedAnnouncement, getPersistedAnnouncement(expectedAnnouncement));
    }

    protected void assertPersistedAnnouncementToMatchUpdatableProperties(Announcement expectedAnnouncement) {
        assertAnnouncementAllUpdatablePropertiesEquals(expectedAnnouncement, getPersistedAnnouncement(expectedAnnouncement));
    }
}
