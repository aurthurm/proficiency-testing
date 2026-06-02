package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ParticipantResultAsserts.*;
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
import zw.org.nmrl.ept.domain.Assay;
import zw.org.nmrl.ept.domain.ParticipantResult;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.ShipmentSample;
import zw.org.nmrl.ept.domain.TestKit;
import zw.org.nmrl.ept.repository.ParticipantResultRepository;
import zw.org.nmrl.ept.service.dto.ParticipantResultDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantResultMapper;

/**
 * Integration tests for the {@link ParticipantResultResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParticipantResultResourceIT {

    private static final String DEFAULT_REPORTED_QUALITATIVE_RESULT = "AAAAAAAAAA";
    private static final String UPDATED_REPORTED_QUALITATIVE_RESULT = "BBBBBBBBBB";

    private static final Double DEFAULT_REPORTED_QUANTITATIVE_VALUE = 1D;
    private static final Double UPDATED_REPORTED_QUANTITATIVE_VALUE = 2D;
    private static final Double SMALLER_REPORTED_QUANTITATIVE_VALUE = 1D - 1D;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_LOT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_LOT_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_EXPIRY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRY_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_EXPIRY_DATE = LocalDate.ofEpochDay(-1L);

    private static final Double DEFAULT_Z_SCORE = 1D;
    private static final Double UPDATED_Z_SCORE = 2D;
    private static final Double SMALLER_Z_SCORE = 1D - 1D;

    private static final Double DEFAULT_CALCULATED_SCORE = 1D;
    private static final Double UPDATED_CALCULATED_SCORE = 2D;
    private static final Double SMALLER_CALCULATED_SCORE = 1D - 1D;

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/participant-results";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParticipantResultRepository participantResultRepository;

    @Autowired
    private ParticipantResultMapper participantResultMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParticipantResultMockMvc;

    private ParticipantResult participantResult;

    private ParticipantResult insertedParticipantResult;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParticipantResult createEntity(EntityManager em) {
        ParticipantResult participantResult = new ParticipantResult()
            .reportedQualitativeResult(DEFAULT_REPORTED_QUALITATIVE_RESULT)
            .reportedQuantitativeValue(DEFAULT_REPORTED_QUANTITATIVE_VALUE)
            .unit(DEFAULT_UNIT)
            .lotNumber(DEFAULT_LOT_NUMBER)
            .expiryDate(DEFAULT_EXPIRY_DATE)
            .zScore(DEFAULT_Z_SCORE)
            .calculatedScore(DEFAULT_CALCULATED_SCORE)
            .comments(DEFAULT_COMMENTS);
        // Add required entity
        ShipmentSample shipmentSample;
        if (TestUtil.findAll(em, ShipmentSample.class).isEmpty()) {
            shipmentSample = ShipmentSampleResourceIT.createEntity(em);
            em.persist(shipmentSample);
            em.flush();
        } else {
            shipmentSample = TestUtil.findAll(em, ShipmentSample.class).get(0);
        }
        participantResult.setSample(shipmentSample);
        // Add required entity
        ShipmentParticipantMap shipmentParticipantMap;
        if (TestUtil.findAll(em, ShipmentParticipantMap.class).isEmpty()) {
            shipmentParticipantMap = ShipmentParticipantMapResourceIT.createEntity(em);
            em.persist(shipmentParticipantMap);
            em.flush();
        } else {
            shipmentParticipantMap = TestUtil.findAll(em, ShipmentParticipantMap.class).get(0);
        }
        participantResult.setShipmentParticipantMap(shipmentParticipantMap);
        return participantResult;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParticipantResult createUpdatedEntity(EntityManager em) {
        ParticipantResult updatedParticipantResult = new ParticipantResult()
            .reportedQualitativeResult(UPDATED_REPORTED_QUALITATIVE_RESULT)
            .reportedQuantitativeValue(UPDATED_REPORTED_QUANTITATIVE_VALUE)
            .unit(UPDATED_UNIT)
            .lotNumber(UPDATED_LOT_NUMBER)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .zScore(UPDATED_Z_SCORE)
            .calculatedScore(UPDATED_CALCULATED_SCORE)
            .comments(UPDATED_COMMENTS);
        // Add required entity
        ShipmentSample shipmentSample;
        if (TestUtil.findAll(em, ShipmentSample.class).isEmpty()) {
            shipmentSample = ShipmentSampleResourceIT.createUpdatedEntity(em);
            em.persist(shipmentSample);
            em.flush();
        } else {
            shipmentSample = TestUtil.findAll(em, ShipmentSample.class).get(0);
        }
        updatedParticipantResult.setSample(shipmentSample);
        // Add required entity
        ShipmentParticipantMap shipmentParticipantMap;
        if (TestUtil.findAll(em, ShipmentParticipantMap.class).isEmpty()) {
            shipmentParticipantMap = ShipmentParticipantMapResourceIT.createUpdatedEntity(em);
            em.persist(shipmentParticipantMap);
            em.flush();
        } else {
            shipmentParticipantMap = TestUtil.findAll(em, ShipmentParticipantMap.class).get(0);
        }
        updatedParticipantResult.setShipmentParticipantMap(shipmentParticipantMap);
        return updatedParticipantResult;
    }

    @BeforeEach
    void initTest() {
        participantResult = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedParticipantResult != null) {
            participantResultRepository.delete(insertedParticipantResult);
            insertedParticipantResult = null;
        }
    }

    @Test
    @Transactional
    void createParticipantResult() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ParticipantResult
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(participantResult);
        var returnedParticipantResultDTO = om.readValue(
            restParticipantResultMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantResultDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParticipantResultDTO.class
        );

        // Validate the ParticipantResult in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParticipantResult = participantResultMapper.toEntity(returnedParticipantResultDTO);
        assertParticipantResultUpdatableFieldsEquals(returnedParticipantResult, getPersistedParticipantResult(returnedParticipantResult));

        insertedParticipantResult = returnedParticipantResult;
    }

    @Test
    @Transactional
    void createParticipantResultWithExistingId() throws Exception {
        // Create the ParticipantResult with an existing ID
        participantResult.setId(1L);
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(participantResult);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantResultMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantResultDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParticipantResult in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllParticipantResults() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList
        restParticipantResultMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participantResult.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportedQualitativeResult").value(hasItem(DEFAULT_REPORTED_QUALITATIVE_RESULT)))
            .andExpect(jsonPath("$.[*].reportedQuantitativeValue").value(hasItem(DEFAULT_REPORTED_QUANTITATIVE_VALUE)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].lotNumber").value(hasItem(DEFAULT_LOT_NUMBER)))
            .andExpect(jsonPath("$.[*].expiryDate").value(hasItem(DEFAULT_EXPIRY_DATE.toString())))
            .andExpect(jsonPath("$.[*].zScore").value(hasItem(DEFAULT_Z_SCORE)))
            .andExpect(jsonPath("$.[*].calculatedScore").value(hasItem(DEFAULT_CALCULATED_SCORE)))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));
    }

    @Test
    @Transactional
    void getParticipantResult() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get the participantResult
        restParticipantResultMockMvc
            .perform(get(ENTITY_API_URL_ID, participantResult.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participantResult.getId().intValue()))
            .andExpect(jsonPath("$.reportedQualitativeResult").value(DEFAULT_REPORTED_QUALITATIVE_RESULT))
            .andExpect(jsonPath("$.reportedQuantitativeValue").value(DEFAULT_REPORTED_QUANTITATIVE_VALUE))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.lotNumber").value(DEFAULT_LOT_NUMBER))
            .andExpect(jsonPath("$.expiryDate").value(DEFAULT_EXPIRY_DATE.toString()))
            .andExpect(jsonPath("$.zScore").value(DEFAULT_Z_SCORE))
            .andExpect(jsonPath("$.calculatedScore").value(DEFAULT_CALCULATED_SCORE))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS));
    }

    @Test
    @Transactional
    void getParticipantResultsByIdFiltering() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        Long id = participantResult.getId();

        defaultParticipantResultFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultParticipantResultFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultParticipantResultFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQualitativeResultIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQualitativeResult equals to
        defaultParticipantResultFiltering(
            "reportedQualitativeResult.equals=" + DEFAULT_REPORTED_QUALITATIVE_RESULT,
            "reportedQualitativeResult.equals=" + UPDATED_REPORTED_QUALITATIVE_RESULT
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQualitativeResultIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQualitativeResult in
        defaultParticipantResultFiltering(
            "reportedQualitativeResult.in=" + DEFAULT_REPORTED_QUALITATIVE_RESULT + "," + UPDATED_REPORTED_QUALITATIVE_RESULT,
            "reportedQualitativeResult.in=" + UPDATED_REPORTED_QUALITATIVE_RESULT
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQualitativeResultIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQualitativeResult is not null
        defaultParticipantResultFiltering("reportedQualitativeResult.specified=true", "reportedQualitativeResult.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQualitativeResultContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQualitativeResult contains
        defaultParticipantResultFiltering(
            "reportedQualitativeResult.contains=" + DEFAULT_REPORTED_QUALITATIVE_RESULT,
            "reportedQualitativeResult.contains=" + UPDATED_REPORTED_QUALITATIVE_RESULT
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQualitativeResultNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQualitativeResult does not contain
        defaultParticipantResultFiltering(
            "reportedQualitativeResult.doesNotContain=" + UPDATED_REPORTED_QUALITATIVE_RESULT,
            "reportedQualitativeResult.doesNotContain=" + DEFAULT_REPORTED_QUALITATIVE_RESULT
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQuantitativeValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQuantitativeValue equals to
        defaultParticipantResultFiltering(
            "reportedQuantitativeValue.equals=" + DEFAULT_REPORTED_QUANTITATIVE_VALUE,
            "reportedQuantitativeValue.equals=" + UPDATED_REPORTED_QUANTITATIVE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQuantitativeValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQuantitativeValue in
        defaultParticipantResultFiltering(
            "reportedQuantitativeValue.in=" + DEFAULT_REPORTED_QUANTITATIVE_VALUE + "," + UPDATED_REPORTED_QUANTITATIVE_VALUE,
            "reportedQuantitativeValue.in=" + UPDATED_REPORTED_QUANTITATIVE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQuantitativeValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQuantitativeValue is not null
        defaultParticipantResultFiltering("reportedQuantitativeValue.specified=true", "reportedQuantitativeValue.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQuantitativeValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQuantitativeValue is greater than or equal to
        defaultParticipantResultFiltering(
            "reportedQuantitativeValue.greaterThanOrEqual=" + DEFAULT_REPORTED_QUANTITATIVE_VALUE,
            "reportedQuantitativeValue.greaterThanOrEqual=" + UPDATED_REPORTED_QUANTITATIVE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQuantitativeValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQuantitativeValue is less than or equal to
        defaultParticipantResultFiltering(
            "reportedQuantitativeValue.lessThanOrEqual=" + DEFAULT_REPORTED_QUANTITATIVE_VALUE,
            "reportedQuantitativeValue.lessThanOrEqual=" + SMALLER_REPORTED_QUANTITATIVE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQuantitativeValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQuantitativeValue is less than
        defaultParticipantResultFiltering(
            "reportedQuantitativeValue.lessThan=" + UPDATED_REPORTED_QUANTITATIVE_VALUE,
            "reportedQuantitativeValue.lessThan=" + DEFAULT_REPORTED_QUANTITATIVE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByReportedQuantitativeValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where reportedQuantitativeValue is greater than
        defaultParticipantResultFiltering(
            "reportedQuantitativeValue.greaterThan=" + SMALLER_REPORTED_QUANTITATIVE_VALUE,
            "reportedQuantitativeValue.greaterThan=" + DEFAULT_REPORTED_QUANTITATIVE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where unit equals to
        defaultParticipantResultFiltering("unit.equals=" + DEFAULT_UNIT, "unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where unit in
        defaultParticipantResultFiltering("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT, "unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where unit is not null
        defaultParticipantResultFiltering("unit.specified=true", "unit.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantResultsByUnitContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where unit contains
        defaultParticipantResultFiltering("unit.contains=" + DEFAULT_UNIT, "unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where unit does not contain
        defaultParticipantResultFiltering("unit.doesNotContain=" + UPDATED_UNIT, "unit.doesNotContain=" + DEFAULT_UNIT);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByLotNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where lotNumber equals to
        defaultParticipantResultFiltering("lotNumber.equals=" + DEFAULT_LOT_NUMBER, "lotNumber.equals=" + UPDATED_LOT_NUMBER);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByLotNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where lotNumber in
        defaultParticipantResultFiltering(
            "lotNumber.in=" + DEFAULT_LOT_NUMBER + "," + UPDATED_LOT_NUMBER,
            "lotNumber.in=" + UPDATED_LOT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByLotNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where lotNumber is not null
        defaultParticipantResultFiltering("lotNumber.specified=true", "lotNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantResultsByLotNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where lotNumber contains
        defaultParticipantResultFiltering("lotNumber.contains=" + DEFAULT_LOT_NUMBER, "lotNumber.contains=" + UPDATED_LOT_NUMBER);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByLotNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where lotNumber does not contain
        defaultParticipantResultFiltering(
            "lotNumber.doesNotContain=" + UPDATED_LOT_NUMBER,
            "lotNumber.doesNotContain=" + DEFAULT_LOT_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByExpiryDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where expiryDate equals to
        defaultParticipantResultFiltering("expiryDate.equals=" + DEFAULT_EXPIRY_DATE, "expiryDate.equals=" + UPDATED_EXPIRY_DATE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByExpiryDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where expiryDate in
        defaultParticipantResultFiltering(
            "expiryDate.in=" + DEFAULT_EXPIRY_DATE + "," + UPDATED_EXPIRY_DATE,
            "expiryDate.in=" + UPDATED_EXPIRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByExpiryDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where expiryDate is not null
        defaultParticipantResultFiltering("expiryDate.specified=true", "expiryDate.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantResultsByExpiryDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where expiryDate is greater than or equal to
        defaultParticipantResultFiltering(
            "expiryDate.greaterThanOrEqual=" + DEFAULT_EXPIRY_DATE,
            "expiryDate.greaterThanOrEqual=" + UPDATED_EXPIRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByExpiryDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where expiryDate is less than or equal to
        defaultParticipantResultFiltering(
            "expiryDate.lessThanOrEqual=" + DEFAULT_EXPIRY_DATE,
            "expiryDate.lessThanOrEqual=" + SMALLER_EXPIRY_DATE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByExpiryDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where expiryDate is less than
        defaultParticipantResultFiltering("expiryDate.lessThan=" + UPDATED_EXPIRY_DATE, "expiryDate.lessThan=" + DEFAULT_EXPIRY_DATE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByExpiryDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where expiryDate is greater than
        defaultParticipantResultFiltering("expiryDate.greaterThan=" + SMALLER_EXPIRY_DATE, "expiryDate.greaterThan=" + DEFAULT_EXPIRY_DATE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByzScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where zScore equals to
        defaultParticipantResultFiltering("zScore.equals=" + DEFAULT_Z_SCORE, "zScore.equals=" + UPDATED_Z_SCORE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByzScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where zScore in
        defaultParticipantResultFiltering("zScore.in=" + DEFAULT_Z_SCORE + "," + UPDATED_Z_SCORE, "zScore.in=" + UPDATED_Z_SCORE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByzScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where zScore is not null
        defaultParticipantResultFiltering("zScore.specified=true", "zScore.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantResultsByzScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where zScore is greater than or equal to
        defaultParticipantResultFiltering("zScore.greaterThanOrEqual=" + DEFAULT_Z_SCORE, "zScore.greaterThanOrEqual=" + UPDATED_Z_SCORE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByzScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where zScore is less than or equal to
        defaultParticipantResultFiltering("zScore.lessThanOrEqual=" + DEFAULT_Z_SCORE, "zScore.lessThanOrEqual=" + SMALLER_Z_SCORE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByzScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where zScore is less than
        defaultParticipantResultFiltering("zScore.lessThan=" + UPDATED_Z_SCORE, "zScore.lessThan=" + DEFAULT_Z_SCORE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByzScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where zScore is greater than
        defaultParticipantResultFiltering("zScore.greaterThan=" + SMALLER_Z_SCORE, "zScore.greaterThan=" + DEFAULT_Z_SCORE);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCalculatedScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where calculatedScore equals to
        defaultParticipantResultFiltering(
            "calculatedScore.equals=" + DEFAULT_CALCULATED_SCORE,
            "calculatedScore.equals=" + UPDATED_CALCULATED_SCORE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCalculatedScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where calculatedScore in
        defaultParticipantResultFiltering(
            "calculatedScore.in=" + DEFAULT_CALCULATED_SCORE + "," + UPDATED_CALCULATED_SCORE,
            "calculatedScore.in=" + UPDATED_CALCULATED_SCORE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCalculatedScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where calculatedScore is not null
        defaultParticipantResultFiltering("calculatedScore.specified=true", "calculatedScore.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCalculatedScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where calculatedScore is greater than or equal to
        defaultParticipantResultFiltering(
            "calculatedScore.greaterThanOrEqual=" + DEFAULT_CALCULATED_SCORE,
            "calculatedScore.greaterThanOrEqual=" + UPDATED_CALCULATED_SCORE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCalculatedScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where calculatedScore is less than or equal to
        defaultParticipantResultFiltering(
            "calculatedScore.lessThanOrEqual=" + DEFAULT_CALCULATED_SCORE,
            "calculatedScore.lessThanOrEqual=" + SMALLER_CALCULATED_SCORE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCalculatedScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where calculatedScore is less than
        defaultParticipantResultFiltering(
            "calculatedScore.lessThan=" + UPDATED_CALCULATED_SCORE,
            "calculatedScore.lessThan=" + DEFAULT_CALCULATED_SCORE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCalculatedScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where calculatedScore is greater than
        defaultParticipantResultFiltering(
            "calculatedScore.greaterThan=" + SMALLER_CALCULATED_SCORE,
            "calculatedScore.greaterThan=" + DEFAULT_CALCULATED_SCORE
        );
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where comments equals to
        defaultParticipantResultFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where comments in
        defaultParticipantResultFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where comments is not null
        defaultParticipantResultFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCommentsContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where comments contains
        defaultParticipantResultFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByCommentsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        // Get all the participantResultList where comments does not contain
        defaultParticipantResultFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    @Test
    @Transactional
    void getAllParticipantResultsByAssayIsEqualToSomething() throws Exception {
        Assay assay;
        if (TestUtil.findAll(em, Assay.class).isEmpty()) {
            participantResultRepository.saveAndFlush(participantResult);
            assay = AssayResourceIT.createEntity();
        } else {
            assay = TestUtil.findAll(em, Assay.class).get(0);
        }
        em.persist(assay);
        em.flush();
        participantResult.setAssay(assay);
        participantResultRepository.saveAndFlush(participantResult);
        Long assayId = assay.getId();
        // Get all the participantResultList where assay equals to assayId
        defaultParticipantResultShouldBeFound("assayId.equals=" + assayId);

        // Get all the participantResultList where assay equals to (assayId + 1)
        defaultParticipantResultShouldNotBeFound("assayId.equals=" + (assayId + 1));
    }

    @Test
    @Transactional
    void getAllParticipantResultsByTestKitIsEqualToSomething() throws Exception {
        TestKit testKit;
        if (TestUtil.findAll(em, TestKit.class).isEmpty()) {
            participantResultRepository.saveAndFlush(participantResult);
            testKit = TestKitResourceIT.createEntity();
        } else {
            testKit = TestUtil.findAll(em, TestKit.class).get(0);
        }
        em.persist(testKit);
        em.flush();
        participantResult.setTestKit(testKit);
        participantResultRepository.saveAndFlush(participantResult);
        Long testKitId = testKit.getId();
        // Get all the participantResultList where testKit equals to testKitId
        defaultParticipantResultShouldBeFound("testKitId.equals=" + testKitId);

        // Get all the participantResultList where testKit equals to (testKitId + 1)
        defaultParticipantResultShouldNotBeFound("testKitId.equals=" + (testKitId + 1));
    }

    @Test
    @Transactional
    void getAllParticipantResultsBySampleIsEqualToSomething() throws Exception {
        ShipmentSample sample;
        if (TestUtil.findAll(em, ShipmentSample.class).isEmpty()) {
            participantResultRepository.saveAndFlush(participantResult);
            sample = ShipmentSampleResourceIT.createEntity(em);
        } else {
            sample = TestUtil.findAll(em, ShipmentSample.class).get(0);
        }
        em.persist(sample);
        em.flush();
        participantResult.setSample(sample);
        participantResultRepository.saveAndFlush(participantResult);
        Long sampleId = sample.getId();
        // Get all the participantResultList where sample equals to sampleId
        defaultParticipantResultShouldBeFound("sampleId.equals=" + sampleId);

        // Get all the participantResultList where sample equals to (sampleId + 1)
        defaultParticipantResultShouldNotBeFound("sampleId.equals=" + (sampleId + 1));
    }

    @Test
    @Transactional
    void getAllParticipantResultsByShipmentParticipantMapIsEqualToSomething() throws Exception {
        ShipmentParticipantMap shipmentParticipantMap;
        if (TestUtil.findAll(em, ShipmentParticipantMap.class).isEmpty()) {
            participantResultRepository.saveAndFlush(participantResult);
            shipmentParticipantMap = ShipmentParticipantMapResourceIT.createEntity(em);
        } else {
            shipmentParticipantMap = TestUtil.findAll(em, ShipmentParticipantMap.class).get(0);
        }
        em.persist(shipmentParticipantMap);
        em.flush();
        participantResult.setShipmentParticipantMap(shipmentParticipantMap);
        participantResultRepository.saveAndFlush(participantResult);
        Long shipmentParticipantMapId = shipmentParticipantMap.getId();
        // Get all the participantResultList where shipmentParticipantMap equals to shipmentParticipantMapId
        defaultParticipantResultShouldBeFound("shipmentParticipantMapId.equals=" + shipmentParticipantMapId);

        // Get all the participantResultList where shipmentParticipantMap equals to (shipmentParticipantMapId + 1)
        defaultParticipantResultShouldNotBeFound("shipmentParticipantMapId.equals=" + (shipmentParticipantMapId + 1));
    }

    private void defaultParticipantResultFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultParticipantResultShouldBeFound(shouldBeFound);
        defaultParticipantResultShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParticipantResultShouldBeFound(String filter) throws Exception {
        restParticipantResultMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participantResult.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportedQualitativeResult").value(hasItem(DEFAULT_REPORTED_QUALITATIVE_RESULT)))
            .andExpect(jsonPath("$.[*].reportedQuantitativeValue").value(hasItem(DEFAULT_REPORTED_QUANTITATIVE_VALUE)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].lotNumber").value(hasItem(DEFAULT_LOT_NUMBER)))
            .andExpect(jsonPath("$.[*].expiryDate").value(hasItem(DEFAULT_EXPIRY_DATE.toString())))
            .andExpect(jsonPath("$.[*].zScore").value(hasItem(DEFAULT_Z_SCORE)))
            .andExpect(jsonPath("$.[*].calculatedScore").value(hasItem(DEFAULT_CALCULATED_SCORE)))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));

        // Check, that the count call also returns 1
        restParticipantResultMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParticipantResultShouldNotBeFound(String filter) throws Exception {
        restParticipantResultMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParticipantResultMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingParticipantResult() throws Exception {
        // Get the participantResult
        restParticipantResultMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParticipantResult() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantResult
        ParticipantResult updatedParticipantResult = participantResultRepository.findById(participantResult.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParticipantResult are not directly saved in db
        em.detach(updatedParticipantResult);
        updatedParticipantResult
            .reportedQualitativeResult(UPDATED_REPORTED_QUALITATIVE_RESULT)
            .reportedQuantitativeValue(UPDATED_REPORTED_QUANTITATIVE_VALUE)
            .unit(UPDATED_UNIT)
            .lotNumber(UPDATED_LOT_NUMBER)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .zScore(UPDATED_Z_SCORE)
            .calculatedScore(UPDATED_CALCULATED_SCORE)
            .comments(UPDATED_COMMENTS);
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(updatedParticipantResult);

        restParticipantResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantResultDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantResultDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParticipantResultToMatchAllProperties(updatedParticipantResult);
    }

    @Test
    @Transactional
    void putNonExistingParticipantResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantResult.setId(longCount.incrementAndGet());

        // Create the ParticipantResult
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(participantResult);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantResultDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParticipantResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantResult.setId(longCount.incrementAndGet());

        // Create the ParticipantResult
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(participantResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParticipantResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantResult.setId(longCount.incrementAndGet());

        // Create the ParticipantResult
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(participantResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantResultMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantResultDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParticipantResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParticipantResultWithPatch() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantResult using partial update
        ParticipantResult partialUpdatedParticipantResult = new ParticipantResult();
        partialUpdatedParticipantResult.setId(participantResult.getId());

        partialUpdatedParticipantResult
            .reportedQualitativeResult(UPDATED_REPORTED_QUALITATIVE_RESULT)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .zScore(UPDATED_Z_SCORE)
            .calculatedScore(UPDATED_CALCULATED_SCORE)
            .comments(UPDATED_COMMENTS);

        restParticipantResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipantResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipantResult))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantResult in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantResultUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParticipantResult, participantResult),
            getPersistedParticipantResult(participantResult)
        );
    }

    @Test
    @Transactional
    void fullUpdateParticipantResultWithPatch() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participantResult using partial update
        ParticipantResult partialUpdatedParticipantResult = new ParticipantResult();
        partialUpdatedParticipantResult.setId(participantResult.getId());

        partialUpdatedParticipantResult
            .reportedQualitativeResult(UPDATED_REPORTED_QUALITATIVE_RESULT)
            .reportedQuantitativeValue(UPDATED_REPORTED_QUANTITATIVE_VALUE)
            .unit(UPDATED_UNIT)
            .lotNumber(UPDATED_LOT_NUMBER)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .zScore(UPDATED_Z_SCORE)
            .calculatedScore(UPDATED_CALCULATED_SCORE)
            .comments(UPDATED_COMMENTS);

        restParticipantResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipantResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipantResult))
            )
            .andExpect(status().isOk());

        // Validate the ParticipantResult in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantResultUpdatableFieldsEquals(
            partialUpdatedParticipantResult,
            getPersistedParticipantResult(partialUpdatedParticipantResult)
        );
    }

    @Test
    @Transactional
    void patchNonExistingParticipantResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantResult.setId(longCount.incrementAndGet());

        // Create the ParticipantResult
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(participantResult);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, participantResultDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParticipantResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantResult.setId(longCount.incrementAndGet());

        // Create the ParticipantResult
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(participantResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParticipantResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParticipantResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participantResult.setId(longCount.incrementAndGet());

        // Create the ParticipantResult
        ParticipantResultDTO participantResultDTO = participantResultMapper.toDto(participantResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantResultMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(participantResultDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParticipantResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParticipantResult() throws Exception {
        // Initialize the database
        insertedParticipantResult = participantResultRepository.saveAndFlush(participantResult);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the participantResult
        restParticipantResultMockMvc
            .perform(delete(ENTITY_API_URL_ID, participantResult.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return participantResultRepository.count();
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

    protected ParticipantResult getPersistedParticipantResult(ParticipantResult participantResult) {
        return participantResultRepository.findById(participantResult.getId()).orElseThrow();
    }

    protected void assertPersistedParticipantResultToMatchAllProperties(ParticipantResult expectedParticipantResult) {
        assertParticipantResultAllPropertiesEquals(expectedParticipantResult, getPersistedParticipantResult(expectedParticipantResult));
    }

    protected void assertPersistedParticipantResultToMatchUpdatableProperties(ParticipantResult expectedParticipantResult) {
        assertParticipantResultAllUpdatablePropertiesEquals(
            expectedParticipantResult,
            getPersistedParticipantResult(expectedParticipantResult)
        );
    }
}
