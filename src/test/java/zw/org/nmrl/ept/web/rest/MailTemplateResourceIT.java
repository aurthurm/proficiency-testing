package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.MailTemplateAsserts.*;
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
import zw.org.nmrl.ept.domain.MailTemplate;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;
import zw.org.nmrl.ept.repository.MailTemplateRepository;
import zw.org.nmrl.ept.service.dto.MailTemplateDTO;
import zw.org.nmrl.ept.service.mapper.MailTemplateMapper;

/**
 * Integration tests for the {@link MailTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MailTemplateResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_HTML_BODY = "AAAAAAAAAA";
    private static final String UPDATED_HTML_BODY = "BBBBBBBBBB";

    private static final ContentStatus DEFAULT_STATUS = ContentStatus.PUBLISHED;
    private static final ContentStatus UPDATED_STATUS = ContentStatus.DRAFT;

    private static final String ENTITY_API_URL = "/api/mail-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MailTemplateRepository mailTemplateRepository;

    @Autowired
    private MailTemplateMapper mailTemplateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMailTemplateMockMvc;

    private MailTemplate mailTemplate;

    private MailTemplate insertedMailTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MailTemplate createEntity() {
        return new MailTemplate().code(DEFAULT_CODE).subject(DEFAULT_SUBJECT).htmlBody(DEFAULT_HTML_BODY).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MailTemplate createUpdatedEntity() {
        return new MailTemplate().code(UPDATED_CODE).subject(UPDATED_SUBJECT).htmlBody(UPDATED_HTML_BODY).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        mailTemplate = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMailTemplate != null) {
            mailTemplateRepository.delete(insertedMailTemplate);
            insertedMailTemplate = null;
        }
    }

    @Test
    @Transactional
    void createMailTemplate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MailTemplate
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);
        var returnedMailTemplateDTO = om.readValue(
            restMailTemplateMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mailTemplateDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MailTemplateDTO.class
        );

        // Validate the MailTemplate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMailTemplate = mailTemplateMapper.toEntity(returnedMailTemplateDTO);
        assertMailTemplateUpdatableFieldsEquals(returnedMailTemplate, getPersistedMailTemplate(returnedMailTemplate));

        insertedMailTemplate = returnedMailTemplate;
    }

    @Test
    @Transactional
    void createMailTemplateWithExistingId() throws Exception {
        // Create the MailTemplate with an existing ID
        mailTemplate.setId(1L);
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMailTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mailTemplateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MailTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mailTemplate.setCode(null);

        // Create the MailTemplate, which fails.
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);

        restMailTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mailTemplateDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMailTemplates() throws Exception {
        // Initialize the database
        insertedMailTemplate = mailTemplateRepository.saveAndFlush(mailTemplate);

        // Get all the mailTemplateList
        restMailTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mailTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].htmlBody").value(hasItem(DEFAULT_HTML_BODY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getMailTemplate() throws Exception {
        // Initialize the database
        insertedMailTemplate = mailTemplateRepository.saveAndFlush(mailTemplate);

        // Get the mailTemplate
        restMailTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, mailTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mailTemplate.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.htmlBody").value(DEFAULT_HTML_BODY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMailTemplate() throws Exception {
        // Get the mailTemplate
        restMailTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMailTemplate() throws Exception {
        // Initialize the database
        insertedMailTemplate = mailTemplateRepository.saveAndFlush(mailTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mailTemplate
        MailTemplate updatedMailTemplate = mailTemplateRepository.findById(mailTemplate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMailTemplate are not directly saved in db
        em.detach(updatedMailTemplate);
        updatedMailTemplate.code(UPDATED_CODE).subject(UPDATED_SUBJECT).htmlBody(UPDATED_HTML_BODY).status(UPDATED_STATUS);
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(updatedMailTemplate);

        restMailTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mailTemplateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mailTemplateDTO))
            )
            .andExpect(status().isOk());

        // Validate the MailTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMailTemplateToMatchAllProperties(updatedMailTemplate);
    }

    @Test
    @Transactional
    void putNonExistingMailTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mailTemplate.setId(longCount.incrementAndGet());

        // Create the MailTemplate
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMailTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mailTemplateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mailTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MailTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMailTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mailTemplate.setId(longCount.incrementAndGet());

        // Create the MailTemplate
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMailTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mailTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MailTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMailTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mailTemplate.setId(longCount.incrementAndGet());

        // Create the MailTemplate
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMailTemplateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mailTemplateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MailTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMailTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedMailTemplate = mailTemplateRepository.saveAndFlush(mailTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mailTemplate using partial update
        MailTemplate partialUpdatedMailTemplate = new MailTemplate();
        partialUpdatedMailTemplate.setId(mailTemplate.getId());

        partialUpdatedMailTemplate.subject(UPDATED_SUBJECT).status(UPDATED_STATUS);

        restMailTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMailTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMailTemplate))
            )
            .andExpect(status().isOk());

        // Validate the MailTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMailTemplateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMailTemplate, mailTemplate),
            getPersistedMailTemplate(mailTemplate)
        );
    }

    @Test
    @Transactional
    void fullUpdateMailTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedMailTemplate = mailTemplateRepository.saveAndFlush(mailTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mailTemplate using partial update
        MailTemplate partialUpdatedMailTemplate = new MailTemplate();
        partialUpdatedMailTemplate.setId(mailTemplate.getId());

        partialUpdatedMailTemplate.code(UPDATED_CODE).subject(UPDATED_SUBJECT).htmlBody(UPDATED_HTML_BODY).status(UPDATED_STATUS);

        restMailTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMailTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMailTemplate))
            )
            .andExpect(status().isOk());

        // Validate the MailTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMailTemplateUpdatableFieldsEquals(partialUpdatedMailTemplate, getPersistedMailTemplate(partialUpdatedMailTemplate));
    }

    @Test
    @Transactional
    void patchNonExistingMailTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mailTemplate.setId(longCount.incrementAndGet());

        // Create the MailTemplate
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMailTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mailTemplateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mailTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MailTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMailTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mailTemplate.setId(longCount.incrementAndGet());

        // Create the MailTemplate
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMailTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mailTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MailTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMailTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mailTemplate.setId(longCount.incrementAndGet());

        // Create the MailTemplate
        MailTemplateDTO mailTemplateDTO = mailTemplateMapper.toDto(mailTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMailTemplateMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mailTemplateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MailTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMailTemplate() throws Exception {
        // Initialize the database
        insertedMailTemplate = mailTemplateRepository.saveAndFlush(mailTemplate);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mailTemplate
        restMailTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, mailTemplate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return mailTemplateRepository.count();
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

    protected MailTemplate getPersistedMailTemplate(MailTemplate mailTemplate) {
        return mailTemplateRepository.findById(mailTemplate.getId()).orElseThrow();
    }

    protected void assertPersistedMailTemplateToMatchAllProperties(MailTemplate expectedMailTemplate) {
        assertMailTemplateAllPropertiesEquals(expectedMailTemplate, getPersistedMailTemplate(expectedMailTemplate));
    }

    protected void assertPersistedMailTemplateToMatchUpdatableProperties(MailTemplate expectedMailTemplate) {
        assertMailTemplateAllUpdatablePropertiesEquals(expectedMailTemplate, getPersistedMailTemplate(expectedMailTemplate));
    }
}
