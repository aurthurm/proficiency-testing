package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ShipmentSampleAsserts.*;
import static zw.org.nmrl.ept.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.repository.ShipmentSampleRepository;
import zw.org.nmrl.ept.service.dto.ShipmentSampleDTO;
import zw.org.nmrl.ept.service.mapper.ShipmentSampleMapper;

/**
 * Integration tests for the {@link ShipmentSampleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShipmentSampleResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final Boolean DEFAULT_IS_CONTROL = false;
    private static final Boolean UPDATED_IS_CONTROL = true;

    private static final Boolean DEFAULT_IS_MANDATORY = false;
    private static final Boolean UPDATED_IS_MANDATORY = true;

    private static final Double DEFAULT_SAMPLE_SCORE = 1D;
    private static final Double UPDATED_SAMPLE_SCORE = 2D;

    private static final LocalDate DEFAULT_PREPARATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PREPARATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/shipment-samples";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentSampleRepository shipmentSampleRepository;

    @Autowired
    private ShipmentSampleMapper shipmentSampleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShipmentSampleMockMvc;

    private ShipmentSample shipmentSample;

    private ShipmentSample insertedShipmentSample;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentSample createEntity(EntityManager em) {
        ShipmentSample shipmentSample = new ShipmentSample()
            .label(DEFAULT_LABEL)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .isControl(DEFAULT_IS_CONTROL)
            .isMandatory(DEFAULT_IS_MANDATORY)
            .sampleScore(DEFAULT_SAMPLE_SCORE)
            .preparationDate(DEFAULT_PREPARATION_DATE);
        // Add required entity
        Shipment shipment;
        if (TestUtil.findAll(em, Shipment.class).isEmpty()) {
            shipment = ShipmentResourceIT.createEntity(em);
            em.persist(shipment);
            em.flush();
        } else {
            shipment = TestUtil.findAll(em, Shipment.class).get(0);
        }
        shipmentSample.setShipment(shipment);
        return shipmentSample;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentSample createUpdatedEntity(EntityManager em) {
        ShipmentSample updatedShipmentSample = new ShipmentSample()
            .label(UPDATED_LABEL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isControl(UPDATED_IS_CONTROL)
            .isMandatory(UPDATED_IS_MANDATORY)
            .sampleScore(UPDATED_SAMPLE_SCORE)
            .preparationDate(UPDATED_PREPARATION_DATE);
        // Add required entity
        Shipment shipment;
        if (TestUtil.findAll(em, Shipment.class).isEmpty()) {
            shipment = ShipmentResourceIT.createUpdatedEntity(em);
            em.persist(shipment);
            em.flush();
        } else {
            shipment = TestUtil.findAll(em, Shipment.class).get(0);
        }
        updatedShipmentSample.setShipment(shipment);
        return updatedShipmentSample;
    }

    @BeforeEach
    void initTest() {
        shipmentSample = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedShipmentSample != null) {
            shipmentSampleRepository.delete(insertedShipmentSample);
            insertedShipmentSample = null;
        }
    }

    @Test
    @Transactional
    void createShipmentSample() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShipmentSample
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);
        var returnedShipmentSampleDTO = om.readValue(
            restShipmentSampleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentSampleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShipmentSampleDTO.class
        );

        // Validate the ShipmentSample in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipmentSample = shipmentSampleMapper.toEntity(returnedShipmentSampleDTO);
        assertShipmentSampleUpdatableFieldsEquals(returnedShipmentSample, getPersistedShipmentSample(returnedShipmentSample));

        insertedShipmentSample = returnedShipmentSample;
    }

    @Test
    @Transactional
    void createShipmentSampleWithExistingId() throws Exception {
        // Create the ShipmentSample with an existing ID
        shipmentSample.setId(1L);
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentSampleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentSampleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShipmentSample in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipmentSample.setLabel(null);

        // Create the ShipmentSample, which fails.
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);

        restShipmentSampleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentSampleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShipmentSamples() throws Exception {
        // Initialize the database
        insertedShipmentSample = shipmentSampleRepository.saveAndFlush(shipmentSample);

        // Get all the shipmentSampleList
        restShipmentSampleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentSample.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].isControl").value(hasItem(DEFAULT_IS_CONTROL)))
            .andExpect(jsonPath("$.[*].isMandatory").value(hasItem(DEFAULT_IS_MANDATORY)))
            .andExpect(jsonPath("$.[*].sampleScore").value(hasItem(DEFAULT_SAMPLE_SCORE)))
            .andExpect(jsonPath("$.[*].preparationDate").value(hasItem(DEFAULT_PREPARATION_DATE.toString())));
    }

    @Test
    @Transactional
    void getShipmentSample() throws Exception {
        // Initialize the database
        insertedShipmentSample = shipmentSampleRepository.saveAndFlush(shipmentSample);

        // Get the shipmentSample
        restShipmentSampleMockMvc
            .perform(get(ENTITY_API_URL_ID, shipmentSample.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipmentSample.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.isControl").value(DEFAULT_IS_CONTROL))
            .andExpect(jsonPath("$.isMandatory").value(DEFAULT_IS_MANDATORY))
            .andExpect(jsonPath("$.sampleScore").value(DEFAULT_SAMPLE_SCORE))
            .andExpect(jsonPath("$.preparationDate").value(DEFAULT_PREPARATION_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingShipmentSample() throws Exception {
        // Get the shipmentSample
        restShipmentSampleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShipmentSample() throws Exception {
        // Initialize the database
        insertedShipmentSample = shipmentSampleRepository.saveAndFlush(shipmentSample);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentSample
        ShipmentSample updatedShipmentSample = shipmentSampleRepository.findById(shipmentSample.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipmentSample are not directly saved in db
        em.detach(updatedShipmentSample);
        updatedShipmentSample
            .label(UPDATED_LABEL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isControl(UPDATED_IS_CONTROL)
            .isMandatory(UPDATED_IS_MANDATORY)
            .sampleScore(UPDATED_SAMPLE_SCORE)
            .preparationDate(UPDATED_PREPARATION_DATE);
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(updatedShipmentSample);

        restShipmentSampleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentSampleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentSampleDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentSample in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentSampleToMatchAllProperties(updatedShipmentSample);
    }

    @Test
    @Transactional
    void putNonExistingShipmentSample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentSample.setId(longCount.incrementAndGet());

        // Create the ShipmentSample
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentSampleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentSampleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentSampleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentSample in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipmentSample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentSample.setId(longCount.incrementAndGet());

        // Create the ShipmentSample
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentSampleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentSampleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentSample in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipmentSample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentSample.setId(longCount.incrementAndGet());

        // Create the ShipmentSample
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentSampleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentSampleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentSample in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShipmentSampleWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentSample = shipmentSampleRepository.saveAndFlush(shipmentSample);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentSample using partial update
        ShipmentSample partialUpdatedShipmentSample = new ShipmentSample();
        partialUpdatedShipmentSample.setId(shipmentSample.getId());

        partialUpdatedShipmentSample
            .label(UPDATED_LABEL)
            .isControl(UPDATED_IS_CONTROL)
            .isMandatory(UPDATED_IS_MANDATORY)
            .sampleScore(UPDATED_SAMPLE_SCORE);

        restShipmentSampleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentSample.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentSample))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentSample in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentSampleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShipmentSample, shipmentSample),
            getPersistedShipmentSample(shipmentSample)
        );
    }

    @Test
    @Transactional
    void fullUpdateShipmentSampleWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentSample = shipmentSampleRepository.saveAndFlush(shipmentSample);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentSample using partial update
        ShipmentSample partialUpdatedShipmentSample = new ShipmentSample();
        partialUpdatedShipmentSample.setId(shipmentSample.getId());

        partialUpdatedShipmentSample
            .label(UPDATED_LABEL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .isControl(UPDATED_IS_CONTROL)
            .isMandatory(UPDATED_IS_MANDATORY)
            .sampleScore(UPDATED_SAMPLE_SCORE)
            .preparationDate(UPDATED_PREPARATION_DATE);

        restShipmentSampleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentSample.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentSample))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentSample in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentSampleUpdatableFieldsEquals(partialUpdatedShipmentSample, getPersistedShipmentSample(partialUpdatedShipmentSample));
    }

    @Test
    @Transactional
    void patchNonExistingShipmentSample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentSample.setId(longCount.incrementAndGet());

        // Create the ShipmentSample
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentSampleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shipmentSampleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentSampleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentSample in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipmentSample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentSample.setId(longCount.incrementAndGet());

        // Create the ShipmentSample
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentSampleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentSampleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentSample in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipmentSample() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentSample.setId(longCount.incrementAndGet());

        // Create the ShipmentSample
        ShipmentSampleDTO shipmentSampleDTO = shipmentSampleMapper.toDto(shipmentSample);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentSampleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentSampleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentSample in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShipmentSample() throws Exception {
        // Initialize the database
        insertedShipmentSample = shipmentSampleRepository.saveAndFlush(shipmentSample);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipmentSample
        restShipmentSampleMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipmentSample.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shipmentSampleRepository.count();
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

    protected ShipmentSample getPersistedShipmentSample(ShipmentSample shipmentSample) {
        return shipmentSampleRepository.findById(shipmentSample.getId()).orElseThrow();
    }

    protected void assertPersistedShipmentSampleToMatchAllProperties(ShipmentSample expectedShipmentSample) {
        assertShipmentSampleAllPropertiesEquals(expectedShipmentSample, getPersistedShipmentSample(expectedShipmentSample));
    }

    protected void assertPersistedShipmentSampleToMatchUpdatableProperties(ShipmentSample expectedShipmentSample) {
        assertShipmentSampleAllUpdatablePropertiesEquals(expectedShipmentSample, getPersistedShipmentSample(expectedShipmentSample));
    }
}
