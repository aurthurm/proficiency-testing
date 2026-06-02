package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.CertificateTemplateAsserts.*;
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
import zw.org.nmrl.ept.domain.CertificateTemplate;
import zw.org.nmrl.ept.domain.enumeration.CertificateType;
import zw.org.nmrl.ept.repository.CertificateTemplateRepository;
import zw.org.nmrl.ept.service.dto.CertificateTemplateDTO;
import zw.org.nmrl.ept.service.mapper.CertificateTemplateMapper;

/**
 * Integration tests for the {@link CertificateTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CertificateTemplateResourceIT {

    private static final CertificateType DEFAULT_CERTIFICATE_TYPE = CertificateType.PARTICIPATION;
    private static final CertificateType UPDATED_CERTIFICATE_TYPE = CertificateType.EXCELLENCE;

    private static final String DEFAULT_FILE_REF = "AAAAAAAAAA";
    private static final String UPDATED_FILE_REF = "BBBBBBBBBB";

    private static final String DEFAULT_DETECTED_FIELDS = "AAAAAAAAAA";
    private static final String UPDATED_DETECTED_FIELDS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/certificate-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CertificateTemplateRepository certificateTemplateRepository;

    @Autowired
    private CertificateTemplateMapper certificateTemplateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCertificateTemplateMockMvc;

    private CertificateTemplate certificateTemplate;

    private CertificateTemplate insertedCertificateTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CertificateTemplate createEntity() {
        return new CertificateTemplate()
            .certificateType(DEFAULT_CERTIFICATE_TYPE)
            .fileRef(DEFAULT_FILE_REF)
            .detectedFields(DEFAULT_DETECTED_FIELDS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CertificateTemplate createUpdatedEntity() {
        return new CertificateTemplate()
            .certificateType(UPDATED_CERTIFICATE_TYPE)
            .fileRef(UPDATED_FILE_REF)
            .detectedFields(UPDATED_DETECTED_FIELDS);
    }

    @BeforeEach
    void initTest() {
        certificateTemplate = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCertificateTemplate != null) {
            certificateTemplateRepository.delete(insertedCertificateTemplate);
            insertedCertificateTemplate = null;
        }
    }

    @Test
    @Transactional
    void createCertificateTemplate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CertificateTemplate
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);
        var returnedCertificateTemplateDTO = om.readValue(
            restCertificateTemplateMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateTemplateDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CertificateTemplateDTO.class
        );

        // Validate the CertificateTemplate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCertificateTemplate = certificateTemplateMapper.toEntity(returnedCertificateTemplateDTO);
        assertCertificateTemplateUpdatableFieldsEquals(
            returnedCertificateTemplate,
            getPersistedCertificateTemplate(returnedCertificateTemplate)
        );

        insertedCertificateTemplate = returnedCertificateTemplate;
    }

    @Test
    @Transactional
    void createCertificateTemplateWithExistingId() throws Exception {
        // Create the CertificateTemplate with an existing ID
        certificateTemplate.setId(1L);
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCertificateTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateTemplateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CertificateTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCertificateTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certificateTemplate.setCertificateType(null);

        // Create the CertificateTemplate, which fails.
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);

        restCertificateTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCertificateTemplates() throws Exception {
        // Initialize the database
        insertedCertificateTemplate = certificateTemplateRepository.saveAndFlush(certificateTemplate);

        // Get all the certificateTemplateList
        restCertificateTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificateTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].certificateType").value(hasItem(DEFAULT_CERTIFICATE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileRef").value(hasItem(DEFAULT_FILE_REF)))
            .andExpect(jsonPath("$.[*].detectedFields").value(hasItem(DEFAULT_DETECTED_FIELDS)));
    }

    @Test
    @Transactional
    void getCertificateTemplate() throws Exception {
        // Initialize the database
        insertedCertificateTemplate = certificateTemplateRepository.saveAndFlush(certificateTemplate);

        // Get the certificateTemplate
        restCertificateTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, certificateTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(certificateTemplate.getId().intValue()))
            .andExpect(jsonPath("$.certificateType").value(DEFAULT_CERTIFICATE_TYPE.toString()))
            .andExpect(jsonPath("$.fileRef").value(DEFAULT_FILE_REF))
            .andExpect(jsonPath("$.detectedFields").value(DEFAULT_DETECTED_FIELDS));
    }

    @Test
    @Transactional
    void getNonExistingCertificateTemplate() throws Exception {
        // Get the certificateTemplate
        restCertificateTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCertificateTemplate() throws Exception {
        // Initialize the database
        insertedCertificateTemplate = certificateTemplateRepository.saveAndFlush(certificateTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificateTemplate
        CertificateTemplate updatedCertificateTemplate = certificateTemplateRepository.findById(certificateTemplate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCertificateTemplate are not directly saved in db
        em.detach(updatedCertificateTemplate);
        updatedCertificateTemplate
            .certificateType(UPDATED_CERTIFICATE_TYPE)
            .fileRef(UPDATED_FILE_REF)
            .detectedFields(UPDATED_DETECTED_FIELDS);
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(updatedCertificateTemplate);

        restCertificateTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificateTemplateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateTemplateDTO))
            )
            .andExpect(status().isOk());

        // Validate the CertificateTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCertificateTemplateToMatchAllProperties(updatedCertificateTemplate);
    }

    @Test
    @Transactional
    void putNonExistingCertificateTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateTemplate.setId(longCount.incrementAndGet());

        // Create the CertificateTemplate
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificateTemplateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificateTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCertificateTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateTemplate.setId(longCount.incrementAndGet());

        // Create the CertificateTemplate
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificateTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificateTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCertificateTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateTemplate.setId(longCount.incrementAndGet());

        // Create the CertificateTemplate
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateTemplateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificateTemplateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CertificateTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCertificateTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedCertificateTemplate = certificateTemplateRepository.saveAndFlush(certificateTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificateTemplate using partial update
        CertificateTemplate partialUpdatedCertificateTemplate = new CertificateTemplate();
        partialUpdatedCertificateTemplate.setId(certificateTemplate.getId());

        partialUpdatedCertificateTemplate.certificateType(UPDATED_CERTIFICATE_TYPE);

        restCertificateTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificateTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificateTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CertificateTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificateTemplateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCertificateTemplate, certificateTemplate),
            getPersistedCertificateTemplate(certificateTemplate)
        );
    }

    @Test
    @Transactional
    void fullUpdateCertificateTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedCertificateTemplate = certificateTemplateRepository.saveAndFlush(certificateTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificateTemplate using partial update
        CertificateTemplate partialUpdatedCertificateTemplate = new CertificateTemplate();
        partialUpdatedCertificateTemplate.setId(certificateTemplate.getId());

        partialUpdatedCertificateTemplate
            .certificateType(UPDATED_CERTIFICATE_TYPE)
            .fileRef(UPDATED_FILE_REF)
            .detectedFields(UPDATED_DETECTED_FIELDS);

        restCertificateTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificateTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificateTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CertificateTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificateTemplateUpdatableFieldsEquals(
            partialUpdatedCertificateTemplate,
            getPersistedCertificateTemplate(partialUpdatedCertificateTemplate)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCertificateTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateTemplate.setId(longCount.incrementAndGet());

        // Create the CertificateTemplate
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, certificateTemplateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificateTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificateTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCertificateTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateTemplate.setId(longCount.incrementAndGet());

        // Create the CertificateTemplate
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificateTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificateTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCertificateTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificateTemplate.setId(longCount.incrementAndGet());

        // Create the CertificateTemplate
        CertificateTemplateDTO certificateTemplateDTO = certificateTemplateMapper.toDto(certificateTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(certificateTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CertificateTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCertificateTemplate() throws Exception {
        // Initialize the database
        insertedCertificateTemplate = certificateTemplateRepository.saveAndFlush(certificateTemplate);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the certificateTemplate
        restCertificateTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, certificateTemplate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return certificateTemplateRepository.count();
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

    protected CertificateTemplate getPersistedCertificateTemplate(CertificateTemplate certificateTemplate) {
        return certificateTemplateRepository.findById(certificateTemplate.getId()).orElseThrow();
    }

    protected void assertPersistedCertificateTemplateToMatchAllProperties(CertificateTemplate expectedCertificateTemplate) {
        assertCertificateTemplateAllPropertiesEquals(
            expectedCertificateTemplate,
            getPersistedCertificateTemplate(expectedCertificateTemplate)
        );
    }

    protected void assertPersistedCertificateTemplateToMatchUpdatableProperties(CertificateTemplate expectedCertificateTemplate) {
        assertCertificateTemplateAllUpdatablePropertiesEquals(
            expectedCertificateTemplate,
            getPersistedCertificateTemplate(expectedCertificateTemplate)
        );
    }
}
