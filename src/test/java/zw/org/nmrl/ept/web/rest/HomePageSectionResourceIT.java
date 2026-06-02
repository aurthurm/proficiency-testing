package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.HomePageSectionAsserts.*;
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
import zw.org.nmrl.ept.domain.HomePageSection;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;
import zw.org.nmrl.ept.repository.HomePageSectionRepository;
import zw.org.nmrl.ept.service.dto.HomePageSectionDTO;
import zw.org.nmrl.ept.service.mapper.HomePageSectionMapper;

/**
 * Integration tests for the {@link HomePageSectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HomePageSectionResourceIT {

    private static final String DEFAULT_SECTION = "AAAAAAAAAA";
    private static final String UPDATED_SECTION = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_REF = "AAAAAAAAAA";
    private static final String UPDATED_FILE_REF = "BBBBBBBBBB";

    private static final String DEFAULT_ICON = "AAAAAAAAAA";
    private static final String UPDATED_ICON = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final ContentStatus DEFAULT_STATUS = ContentStatus.PUBLISHED;
    private static final ContentStatus UPDATED_STATUS = ContentStatus.DRAFT;

    private static final String ENTITY_API_URL = "/api/home-page-sections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HomePageSectionRepository homePageSectionRepository;

    @Autowired
    private HomePageSectionMapper homePageSectionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHomePageSectionMockMvc;

    private HomePageSection homePageSection;

    private HomePageSection insertedHomePageSection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HomePageSection createEntity() {
        return new HomePageSection()
            .section(DEFAULT_SECTION)
            .type(DEFAULT_TYPE)
            .title(DEFAULT_TITLE)
            .text(DEFAULT_TEXT)
            .link(DEFAULT_LINK)
            .fileRef(DEFAULT_FILE_REF)
            .icon(DEFAULT_ICON)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HomePageSection createUpdatedEntity() {
        return new HomePageSection()
            .section(UPDATED_SECTION)
            .type(UPDATED_TYPE)
            .title(UPDATED_TITLE)
            .text(UPDATED_TEXT)
            .link(UPDATED_LINK)
            .fileRef(UPDATED_FILE_REF)
            .icon(UPDATED_ICON)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        homePageSection = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHomePageSection != null) {
            homePageSectionRepository.delete(insertedHomePageSection);
            insertedHomePageSection = null;
        }
    }

    @Test
    @Transactional
    void createHomePageSection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HomePageSection
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);
        var returnedHomePageSectionDTO = om.readValue(
            restHomePageSectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(homePageSectionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HomePageSectionDTO.class
        );

        // Validate the HomePageSection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHomePageSection = homePageSectionMapper.toEntity(returnedHomePageSectionDTO);
        assertHomePageSectionUpdatableFieldsEquals(returnedHomePageSection, getPersistedHomePageSection(returnedHomePageSection));

        insertedHomePageSection = returnedHomePageSection;
    }

    @Test
    @Transactional
    void createHomePageSectionWithExistingId() throws Exception {
        // Create the HomePageSection with an existing ID
        homePageSection.setId(1L);
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHomePageSectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(homePageSectionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the HomePageSection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSectionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        homePageSection.setSection(null);

        // Create the HomePageSection, which fails.
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        restHomePageSectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(homePageSectionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        homePageSection.setStatus(null);

        // Create the HomePageSection, which fails.
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        restHomePageSectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(homePageSectionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHomePageSections() throws Exception {
        // Initialize the database
        insertedHomePageSection = homePageSectionRepository.saveAndFlush(homePageSection);

        // Get all the homePageSectionList
        restHomePageSectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(homePageSection.getId().intValue())))
            .andExpect(jsonPath("$.[*].section").value(hasItem(DEFAULT_SECTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].fileRef").value(hasItem(DEFAULT_FILE_REF)))
            .andExpect(jsonPath("$.[*].icon").value(hasItem(DEFAULT_ICON)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getHomePageSection() throws Exception {
        // Initialize the database
        insertedHomePageSection = homePageSectionRepository.saveAndFlush(homePageSection);

        // Get the homePageSection
        restHomePageSectionMockMvc
            .perform(get(ENTITY_API_URL_ID, homePageSection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(homePageSection.getId().intValue()))
            .andExpect(jsonPath("$.section").value(DEFAULT_SECTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK))
            .andExpect(jsonPath("$.fileRef").value(DEFAULT_FILE_REF))
            .andExpect(jsonPath("$.icon").value(DEFAULT_ICON))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingHomePageSection() throws Exception {
        // Get the homePageSection
        restHomePageSectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHomePageSection() throws Exception {
        // Initialize the database
        insertedHomePageSection = homePageSectionRepository.saveAndFlush(homePageSection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the homePageSection
        HomePageSection updatedHomePageSection = homePageSectionRepository.findById(homePageSection.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHomePageSection are not directly saved in db
        em.detach(updatedHomePageSection);
        updatedHomePageSection
            .section(UPDATED_SECTION)
            .type(UPDATED_TYPE)
            .title(UPDATED_TITLE)
            .text(UPDATED_TEXT)
            .link(UPDATED_LINK)
            .fileRef(UPDATED_FILE_REF)
            .icon(UPDATED_ICON)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .status(UPDATED_STATUS);
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(updatedHomePageSection);

        restHomePageSectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, homePageSectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(homePageSectionDTO))
            )
            .andExpect(status().isOk());

        // Validate the HomePageSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHomePageSectionToMatchAllProperties(updatedHomePageSection);
    }

    @Test
    @Transactional
    void putNonExistingHomePageSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        homePageSection.setId(longCount.incrementAndGet());

        // Create the HomePageSection
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHomePageSectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, homePageSectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(homePageSectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HomePageSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHomePageSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        homePageSection.setId(longCount.incrementAndGet());

        // Create the HomePageSection
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHomePageSectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(homePageSectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HomePageSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHomePageSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        homePageSection.setId(longCount.incrementAndGet());

        // Create the HomePageSection
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHomePageSectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(homePageSectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HomePageSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHomePageSectionWithPatch() throws Exception {
        // Initialize the database
        insertedHomePageSection = homePageSectionRepository.saveAndFlush(homePageSection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the homePageSection using partial update
        HomePageSection partialUpdatedHomePageSection = new HomePageSection();
        partialUpdatedHomePageSection.setId(homePageSection.getId());

        partialUpdatedHomePageSection
            .section(UPDATED_SECTION)
            .title(UPDATED_TITLE)
            .text(UPDATED_TEXT)
            .link(UPDATED_LINK)
            .icon(UPDATED_ICON)
            .status(UPDATED_STATUS);

        restHomePageSectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHomePageSection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHomePageSection))
            )
            .andExpect(status().isOk());

        // Validate the HomePageSection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHomePageSectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHomePageSection, homePageSection),
            getPersistedHomePageSection(homePageSection)
        );
    }

    @Test
    @Transactional
    void fullUpdateHomePageSectionWithPatch() throws Exception {
        // Initialize the database
        insertedHomePageSection = homePageSectionRepository.saveAndFlush(homePageSection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the homePageSection using partial update
        HomePageSection partialUpdatedHomePageSection = new HomePageSection();
        partialUpdatedHomePageSection.setId(homePageSection.getId());

        partialUpdatedHomePageSection
            .section(UPDATED_SECTION)
            .type(UPDATED_TYPE)
            .title(UPDATED_TITLE)
            .text(UPDATED_TEXT)
            .link(UPDATED_LINK)
            .fileRef(UPDATED_FILE_REF)
            .icon(UPDATED_ICON)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .status(UPDATED_STATUS);

        restHomePageSectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHomePageSection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHomePageSection))
            )
            .andExpect(status().isOk());

        // Validate the HomePageSection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHomePageSectionUpdatableFieldsEquals(
            partialUpdatedHomePageSection,
            getPersistedHomePageSection(partialUpdatedHomePageSection)
        );
    }

    @Test
    @Transactional
    void patchNonExistingHomePageSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        homePageSection.setId(longCount.incrementAndGet());

        // Create the HomePageSection
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHomePageSectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, homePageSectionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(homePageSectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HomePageSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHomePageSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        homePageSection.setId(longCount.incrementAndGet());

        // Create the HomePageSection
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHomePageSectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(homePageSectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HomePageSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHomePageSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        homePageSection.setId(longCount.incrementAndGet());

        // Create the HomePageSection
        HomePageSectionDTO homePageSectionDTO = homePageSectionMapper.toDto(homePageSection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHomePageSectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(homePageSectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HomePageSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHomePageSection() throws Exception {
        // Initialize the database
        insertedHomePageSection = homePageSectionRepository.saveAndFlush(homePageSection);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the homePageSection
        restHomePageSectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, homePageSection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return homePageSectionRepository.count();
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

    protected HomePageSection getPersistedHomePageSection(HomePageSection homePageSection) {
        return homePageSectionRepository.findById(homePageSection.getId()).orElseThrow();
    }

    protected void assertPersistedHomePageSectionToMatchAllProperties(HomePageSection expectedHomePageSection) {
        assertHomePageSectionAllPropertiesEquals(expectedHomePageSection, getPersistedHomePageSection(expectedHomePageSection));
    }

    protected void assertPersistedHomePageSectionToMatchUpdatableProperties(HomePageSection expectedHomePageSection) {
        assertHomePageSectionAllUpdatablePropertiesEquals(expectedHomePageSection, getPersistedHomePageSection(expectedHomePageSection));
    }
}
