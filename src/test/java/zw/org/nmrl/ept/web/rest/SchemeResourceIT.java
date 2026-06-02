package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.SchemeAsserts.*;
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
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.domain.enumeration.ResultModality;
import zw.org.nmrl.ept.domain.enumeration.SchemeType;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.SchemeRepository;
import zw.org.nmrl.ept.service.dto.SchemeDTO;
import zw.org.nmrl.ept.service.mapper.SchemeMapper;

/**
 * Integration tests for the {@link SchemeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SchemeResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final SchemeType DEFAULT_SCHEME_TYPE = SchemeType.DTS;
    private static final SchemeType UPDATED_SCHEME_TYPE = SchemeType.VL;

    private static final ResultModality DEFAULT_MODALITY = ResultModality.QUALITATIVE;
    private static final ResultModality UPDATED_MODALITY = ResultModality.QUANTITATIVE;

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/schemes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private SchemeMapper schemeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSchemeMockMvc;

    private Scheme scheme;

    private Scheme insertedScheme;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scheme createEntity() {
        return new Scheme()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .schemeType(DEFAULT_SCHEME_TYPE)
            .modality(DEFAULT_MODALITY)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scheme createUpdatedEntity() {
        return new Scheme()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .schemeType(UPDATED_SCHEME_TYPE)
            .modality(UPDATED_MODALITY)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        scheme = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedScheme != null) {
            schemeRepository.delete(insertedScheme);
            insertedScheme = null;
        }
    }

    @Test
    @Transactional
    void createScheme() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Scheme
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);
        var returnedSchemeDTO = om.readValue(
            restSchemeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SchemeDTO.class
        );

        // Validate the Scheme in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScheme = schemeMapper.toEntity(returnedSchemeDTO);
        assertSchemeUpdatableFieldsEquals(returnedScheme, getPersistedScheme(returnedScheme));

        insertedScheme = returnedScheme;
    }

    @Test
    @Transactional
    void createSchemeWithExistingId() throws Exception {
        // Create the Scheme with an existing ID
        scheme.setId(1L);
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSchemeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Scheme in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheme.setCode(null);

        // Create the Scheme, which fails.
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        restSchemeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheme.setName(null);

        // Create the Scheme, which fails.
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        restSchemeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSchemeTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheme.setSchemeType(null);

        // Create the Scheme, which fails.
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        restSchemeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModalityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheme.setModality(null);

        // Create the Scheme, which fails.
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        restSchemeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheme.setStatus(null);

        // Create the Scheme, which fails.
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        restSchemeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSchemes() throws Exception {
        // Initialize the database
        insertedScheme = schemeRepository.saveAndFlush(scheme);

        // Get all the schemeList
        restSchemeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheme.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].schemeType").value(hasItem(DEFAULT_SCHEME_TYPE.toString())))
            .andExpect(jsonPath("$.[*].modality").value(hasItem(DEFAULT_MODALITY.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getScheme() throws Exception {
        // Initialize the database
        insertedScheme = schemeRepository.saveAndFlush(scheme);

        // Get the scheme
        restSchemeMockMvc
            .perform(get(ENTITY_API_URL_ID, scheme.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scheme.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.schemeType").value(DEFAULT_SCHEME_TYPE.toString()))
            .andExpect(jsonPath("$.modality").value(DEFAULT_MODALITY.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingScheme() throws Exception {
        // Get the scheme
        restSchemeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScheme() throws Exception {
        // Initialize the database
        insertedScheme = schemeRepository.saveAndFlush(scheme);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheme
        Scheme updatedScheme = schemeRepository.findById(scheme.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScheme are not directly saved in db
        em.detach(updatedScheme);
        updatedScheme
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .schemeType(UPDATED_SCHEME_TYPE)
            .modality(UPDATED_MODALITY)
            .status(UPDATED_STATUS);
        SchemeDTO schemeDTO = schemeMapper.toDto(updatedScheme);

        restSchemeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, schemeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Scheme in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSchemeToMatchAllProperties(updatedScheme);
    }

    @Test
    @Transactional
    void putNonExistingScheme() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheme.setId(longCount.incrementAndGet());

        // Create the Scheme
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSchemeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, schemeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scheme in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScheme() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheme.setId(longCount.incrementAndGet());

        // Create the Scheme
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSchemeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(schemeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scheme in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScheme() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheme.setId(longCount.incrementAndGet());

        // Create the Scheme
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSchemeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(schemeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Scheme in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSchemeWithPatch() throws Exception {
        // Initialize the database
        insertedScheme = schemeRepository.saveAndFlush(scheme);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheme using partial update
        Scheme partialUpdatedScheme = new Scheme();
        partialUpdatedScheme.setId(scheme.getId());

        partialUpdatedScheme.name(UPDATED_NAME).schemeType(UPDATED_SCHEME_TYPE).status(UPDATED_STATUS);

        restSchemeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheme.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheme))
            )
            .andExpect(status().isOk());

        // Validate the Scheme in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSchemeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedScheme, scheme), getPersistedScheme(scheme));
    }

    @Test
    @Transactional
    void fullUpdateSchemeWithPatch() throws Exception {
        // Initialize the database
        insertedScheme = schemeRepository.saveAndFlush(scheme);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheme using partial update
        Scheme partialUpdatedScheme = new Scheme();
        partialUpdatedScheme.setId(scheme.getId());

        partialUpdatedScheme
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .schemeType(UPDATED_SCHEME_TYPE)
            .modality(UPDATED_MODALITY)
            .status(UPDATED_STATUS);

        restSchemeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheme.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheme))
            )
            .andExpect(status().isOk());

        // Validate the Scheme in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSchemeUpdatableFieldsEquals(partialUpdatedScheme, getPersistedScheme(partialUpdatedScheme));
    }

    @Test
    @Transactional
    void patchNonExistingScheme() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheme.setId(longCount.incrementAndGet());

        // Create the Scheme
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSchemeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, schemeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(schemeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scheme in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScheme() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheme.setId(longCount.incrementAndGet());

        // Create the Scheme
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSchemeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(schemeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scheme in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScheme() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheme.setId(longCount.incrementAndGet());

        // Create the Scheme
        SchemeDTO schemeDTO = schemeMapper.toDto(scheme);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSchemeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(schemeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Scheme in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScheme() throws Exception {
        // Initialize the database
        insertedScheme = schemeRepository.saveAndFlush(scheme);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scheme
        restSchemeMockMvc
            .perform(delete(ENTITY_API_URL_ID, scheme.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return schemeRepository.count();
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

    protected Scheme getPersistedScheme(Scheme scheme) {
        return schemeRepository.findById(scheme.getId()).orElseThrow();
    }

    protected void assertPersistedSchemeToMatchAllProperties(Scheme expectedScheme) {
        assertSchemeAllPropertiesEquals(expectedScheme, getPersistedScheme(expectedScheme));
    }

    protected void assertPersistedSchemeToMatchUpdatableProperties(Scheme expectedScheme) {
        assertSchemeAllUpdatablePropertiesEquals(expectedScheme, getPersistedScheme(expectedScheme));
    }
}
