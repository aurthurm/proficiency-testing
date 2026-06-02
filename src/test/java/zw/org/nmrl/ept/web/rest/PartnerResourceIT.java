package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.PartnerAsserts.*;
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
import zw.org.nmrl.ept.domain.Partner;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;
import zw.org.nmrl.ept.repository.PartnerRepository;
import zw.org.nmrl.ept.service.dto.PartnerDTO;
import zw.org.nmrl.ept.service.mapper.PartnerMapper;

/**
 * Integration tests for the {@link PartnerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartnerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_REF = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_REF = "BBBBBBBBBB";

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final ContentStatus DEFAULT_STATUS = ContentStatus.PUBLISHED;
    private static final ContentStatus UPDATED_STATUS = ContentStatus.DRAFT;

    private static final String ENTITY_API_URL = "/api/partners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerMapper partnerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartnerMockMvc;

    private Partner partner;

    private Partner insertedPartner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Partner createEntity() {
        return new Partner()
            .name(DEFAULT_NAME)
            .link(DEFAULT_LINK)
            .logoRef(DEFAULT_LOGO_REF)
            .sortOrder(DEFAULT_SORT_ORDER)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Partner createUpdatedEntity() {
        return new Partner()
            .name(UPDATED_NAME)
            .link(UPDATED_LINK)
            .logoRef(UPDATED_LOGO_REF)
            .sortOrder(UPDATED_SORT_ORDER)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        partner = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPartner != null) {
            partnerRepository.delete(insertedPartner);
            insertedPartner = null;
        }
    }

    @Test
    @Transactional
    void createPartner() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Partner
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);
        var returnedPartnerDTO = om.readValue(
            restPartnerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartnerDTO.class
        );

        // Validate the Partner in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPartner = partnerMapper.toEntity(returnedPartnerDTO);
        assertPartnerUpdatableFieldsEquals(returnedPartner, getPersistedPartner(returnedPartner));

        insertedPartner = returnedPartner;
    }

    @Test
    @Transactional
    void createPartnerWithExistingId() throws Exception {
        // Create the Partner with an existing ID
        partner.setId(1L);
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partner.setName(null);

        // Create the Partner, which fails.
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        restPartnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partner.setStatus(null);

        // Create the Partner, which fails.
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        restPartnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartners() throws Exception {
        // Initialize the database
        insertedPartner = partnerRepository.saveAndFlush(partner);

        // Get all the partnerList
        restPartnerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partner.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].logoRef").value(hasItem(DEFAULT_LOGO_REF)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getPartner() throws Exception {
        // Initialize the database
        insertedPartner = partnerRepository.saveAndFlush(partner);

        // Get the partner
        restPartnerMockMvc
            .perform(get(ENTITY_API_URL_ID, partner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partner.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK))
            .andExpect(jsonPath("$.logoRef").value(DEFAULT_LOGO_REF))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPartner() throws Exception {
        // Get the partner
        restPartnerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartner() throws Exception {
        // Initialize the database
        insertedPartner = partnerRepository.saveAndFlush(partner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partner
        Partner updatedPartner = partnerRepository.findById(partner.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartner are not directly saved in db
        em.detach(updatedPartner);
        updatedPartner.name(UPDATED_NAME).link(UPDATED_LINK).logoRef(UPDATED_LOGO_REF).sortOrder(UPDATED_SORT_ORDER).status(UPDATED_STATUS);
        PartnerDTO partnerDTO = partnerMapper.toDto(updatedPartner);

        restPartnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Partner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartnerToMatchAllProperties(updatedPartner);
    }

    @Test
    @Transactional
    void putNonExistingPartner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partner.setId(longCount.incrementAndGet());

        // Create the Partner
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partnerDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partner.setId(longCount.incrementAndGet());

        // Create the Partner
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partnerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partner.setId(longCount.incrementAndGet());

        // Create the Partner
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partnerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Partner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartnerWithPatch() throws Exception {
        // Initialize the database
        insertedPartner = partnerRepository.saveAndFlush(partner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partner using partial update
        Partner partialUpdatedPartner = new Partner();
        partialUpdatedPartner.setId(partner.getId());

        partialUpdatedPartner.link(UPDATED_LINK).logoRef(UPDATED_LOGO_REF).sortOrder(UPDATED_SORT_ORDER);

        restPartnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartner))
            )
            .andExpect(status().isOk());

        // Validate the Partner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPartner, partner), getPersistedPartner(partner));
    }

    @Test
    @Transactional
    void fullUpdatePartnerWithPatch() throws Exception {
        // Initialize the database
        insertedPartner = partnerRepository.saveAndFlush(partner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partner using partial update
        Partner partialUpdatedPartner = new Partner();
        partialUpdatedPartner.setId(partner.getId());

        partialUpdatedPartner
            .name(UPDATED_NAME)
            .link(UPDATED_LINK)
            .logoRef(UPDATED_LOGO_REF)
            .sortOrder(UPDATED_SORT_ORDER)
            .status(UPDATED_STATUS);

        restPartnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartner))
            )
            .andExpect(status().isOk());

        // Validate the Partner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartnerUpdatableFieldsEquals(partialUpdatedPartner, getPersistedPartner(partialUpdatedPartner));
    }

    @Test
    @Transactional
    void patchNonExistingPartner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partner.setId(longCount.incrementAndGet());

        // Create the Partner
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partnerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partner.setId(longCount.incrementAndGet());

        // Create the Partner
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partnerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Partner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partner.setId(longCount.incrementAndGet());

        // Create the Partner
        PartnerDTO partnerDTO = partnerMapper.toDto(partner);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartnerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partnerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Partner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartner() throws Exception {
        // Initialize the database
        insertedPartner = partnerRepository.saveAndFlush(partner);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partner
        restPartnerMockMvc
            .perform(delete(ENTITY_API_URL_ID, partner.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partnerRepository.count();
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

    protected Partner getPersistedPartner(Partner partner) {
        return partnerRepository.findById(partner.getId()).orElseThrow();
    }

    protected void assertPersistedPartnerToMatchAllProperties(Partner expectedPartner) {
        assertPartnerAllPropertiesEquals(expectedPartner, getPersistedPartner(expectedPartner));
    }

    protected void assertPersistedPartnerToMatchUpdatableProperties(Partner expectedPartner) {
        assertPartnerAllUpdatablePropertiesEquals(expectedPartner, getPersistedPartner(expectedPartner));
    }
}
