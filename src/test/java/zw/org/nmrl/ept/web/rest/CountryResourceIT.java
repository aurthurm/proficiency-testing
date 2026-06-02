package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.CountryAsserts.*;
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
import zw.org.nmrl.ept.domain.Country;
import zw.org.nmrl.ept.repository.CountryRepository;
import zw.org.nmrl.ept.service.dto.CountryDTO;
import zw.org.nmrl.ept.service.mapper.CountryMapper;

/**
 * Integration tests for the {@link CountryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CountryResourceIT {

    private static final String DEFAULT_ISO_CODE = "AAA";
    private static final String UPDATED_ISO_CODE = "BBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/countries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCountryMockMvc;

    private Country country;

    private Country insertedCountry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country createEntity() {
        return new Country().isoCode(DEFAULT_ISO_CODE).name(DEFAULT_NAME).region(DEFAULT_REGION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country createUpdatedEntity() {
        return new Country().isoCode(UPDATED_ISO_CODE).name(UPDATED_NAME).region(UPDATED_REGION);
    }

    @BeforeEach
    void initTest() {
        country = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCountry != null) {
            countryRepository.delete(insertedCountry);
            insertedCountry = null;
        }
    }

    @Test
    @Transactional
    void createCountry() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);
        var returnedCountryDTO = om.readValue(
            restCountryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CountryDTO.class
        );

        // Validate the Country in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCountry = countryMapper.toEntity(returnedCountryDTO);
        assertCountryUpdatableFieldsEquals(returnedCountry, getPersistedCountry(returnedCountry));

        insertedCountry = returnedCountry;
    }

    @Test
    @Transactional
    void createCountryWithExistingId() throws Exception {
        // Create the Country with an existing ID
        country.setId(1L);
        CountryDTO countryDTO = countryMapper.toDto(country);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCountryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIsoCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        country.setIsoCode(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);

        restCountryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        country.setName(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);

        restCountryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCountries() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.saveAndFlush(country);

        // Get all the countryList
        restCountryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(country.getId().intValue())))
            .andExpect(jsonPath("$.[*].isoCode").value(hasItem(DEFAULT_ISO_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)));
    }

    @Test
    @Transactional
    void getCountry() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.saveAndFlush(country);

        // Get the country
        restCountryMockMvc
            .perform(get(ENTITY_API_URL_ID, country.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(country.getId().intValue()))
            .andExpect(jsonPath("$.isoCode").value(DEFAULT_ISO_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION));
    }

    @Test
    @Transactional
    void getNonExistingCountry() throws Exception {
        // Get the country
        restCountryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCountry() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.saveAndFlush(country);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the country
        Country updatedCountry = countryRepository.findById(country.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCountry are not directly saved in db
        em.detach(updatedCountry);
        updatedCountry.isoCode(UPDATED_ISO_CODE).name(UPDATED_NAME).region(UPDATED_REGION);
        CountryDTO countryDTO = countryMapper.toDto(updatedCountry);

        restCountryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, countryDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCountryToMatchAllProperties(updatedCountry);
    }

    @Test
    @Transactional
    void putNonExistingCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, countryDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(countryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(countryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCountryWithPatch() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.saveAndFlush(country);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the country using partial update
        Country partialUpdatedCountry = new Country();
        partialUpdatedCountry.setId(country.getId());

        partialUpdatedCountry.isoCode(UPDATED_ISO_CODE).name(UPDATED_NAME).region(UPDATED_REGION);

        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCountry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCountry))
            )
            .andExpect(status().isOk());

        // Validate the Country in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCountryUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCountry, country), getPersistedCountry(country));
    }

    @Test
    @Transactional
    void fullUpdateCountryWithPatch() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.saveAndFlush(country);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the country using partial update
        Country partialUpdatedCountry = new Country();
        partialUpdatedCountry.setId(country.getId());

        partialUpdatedCountry.isoCode(UPDATED_ISO_CODE).name(UPDATED_NAME).region(UPDATED_REGION);

        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCountry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCountry))
            )
            .andExpect(status().isOk());

        // Validate the Country in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCountryUpdatableFieldsEquals(partialUpdatedCountry, getPersistedCountry(partialUpdatedCountry));
    }

    @Test
    @Transactional
    void patchNonExistingCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, countryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(countryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(countryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCountryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(countryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCountry() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.saveAndFlush(country);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the country
        restCountryMockMvc
            .perform(delete(ENTITY_API_URL_ID, country.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return countryRepository.count();
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

    protected Country getPersistedCountry(Country country) {
        return countryRepository.findById(country.getId()).orElseThrow();
    }

    protected void assertPersistedCountryToMatchAllProperties(Country expectedCountry) {
        assertCountryAllPropertiesEquals(expectedCountry, getPersistedCountry(expectedCountry));
    }

    protected void assertPersistedCountryToMatchUpdatableProperties(Country expectedCountry) {
        assertCountryAllUpdatablePropertiesEquals(expectedCountry, getPersistedCountry(expectedCountry));
    }
}
