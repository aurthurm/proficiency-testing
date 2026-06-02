package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ShipmentParticipantMapAsserts.*;
import static zw.org.nmrl.ept.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
import zw.org.nmrl.ept.domain.ModeOfReceipt;
import zw.org.nmrl.ept.domain.NotTestedReason;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.enumeration.FinalResult;
import zw.org.nmrl.ept.domain.enumeration.QcStatus;
import zw.org.nmrl.ept.domain.enumeration.ResponseStatus;
import zw.org.nmrl.ept.repository.ShipmentParticipantMapRepository;
import zw.org.nmrl.ept.service.dto.ShipmentParticipantMapDTO;
import zw.org.nmrl.ept.service.mapper.ShipmentParticipantMapMapper;

/**
 * Integration tests for the {@link ShipmentParticipantMapResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShipmentParticipantMapResourceIT {

    private static final ResponseStatus DEFAULT_RESPONSE_STATUS = ResponseStatus.NOT_STARTED;
    private static final ResponseStatus UPDATED_RESPONSE_STATUS = ResponseStatus.IN_PROGRESS;

    private static final LocalDate DEFAULT_SHIPMENT_RECEIPT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SHIPMENT_RECEIPT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_SHIPMENT_RECEIPT_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_SHIPMENT_TEST_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SHIPMENT_TEST_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_SHIPMENT_TEST_DATE = LocalDate.ofEpochDay(-1L);

    private static final Instant DEFAULT_SHIPMENT_TEST_REPORT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SHIPMENT_TEST_REPORT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SUBMITTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMITTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EVALUATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EVALUATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_EXCLUDED = false;
    private static final Boolean UPDATED_IS_EXCLUDED = true;

    private static final Boolean DEFAULT_IS_RESPONSE_LATE = false;
    private static final Boolean UPDATED_IS_RESPONSE_LATE = true;

    private static final Boolean DEFAULT_IS_PT_TEST_NOT_PERFORMED = false;
    private static final Boolean UPDATED_IS_PT_TEST_NOT_PERFORMED = true;

    private static final String DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SUPERVISOR_APPROVED = false;
    private static final Boolean UPDATED_SUPERVISOR_APPROVED = true;

    private static final String DEFAULT_PARTICIPANT_SUPERVISOR = "AAAAAAAAAA";
    private static final String UPDATED_PARTICIPANT_SUPERVISOR = "BBBBBBBBBB";

    private static final String DEFAULT_USER_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_USER_COMMENT = "BBBBBBBBBB";

    private static final Double DEFAULT_SHIPMENT_SCORE = 1D;
    private static final Double UPDATED_SHIPMENT_SCORE = 2D;
    private static final Double SMALLER_SHIPMENT_SCORE = 1D - 1D;

    private static final Double DEFAULT_DOCUMENTATION_SCORE = 1D;
    private static final Double UPDATED_DOCUMENTATION_SCORE = 2D;
    private static final Double SMALLER_DOCUMENTATION_SCORE = 1D - 1D;

    private static final FinalResult DEFAULT_FINAL_RESULT = FinalResult.PASS;
    private static final FinalResult UPDATED_FINAL_RESULT = FinalResult.FAIL;

    private static final String DEFAULT_FAILURE_REASON = "AAAAAAAAAA";
    private static final String UPDATED_FAILURE_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_EVALUATION_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_EVALUATION_COMMENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_FOLLOWUP = false;
    private static final Boolean UPDATED_IS_FOLLOWUP = true;

    private static final Boolean DEFAULT_MANUAL_OVERRIDE = false;
    private static final Boolean UPDATED_MANUAL_OVERRIDE = true;

    private static final QcStatus DEFAULT_QC_STATUS = QcStatus.PENDING;
    private static final QcStatus UPDATED_QC_STATUS = QcStatus.PASSED;

    private static final LocalDate DEFAULT_QC_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_QC_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_QC_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_QC_DONE_BY = "AAAAAAAAAA";
    private static final String UPDATED_QC_DONE_BY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SYNCED_TO_MOBILE = false;
    private static final Boolean UPDATED_SYNCED_TO_MOBILE = true;

    private static final Instant DEFAULT_SYNCED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SYNCED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/shipment-participant-maps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentParticipantMapRepository shipmentParticipantMapRepository;

    @Autowired
    private ShipmentParticipantMapMapper shipmentParticipantMapMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShipmentParticipantMapMockMvc;

    private ShipmentParticipantMap shipmentParticipantMap;

    private ShipmentParticipantMap insertedShipmentParticipantMap;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentParticipantMap createEntity(EntityManager em) {
        ShipmentParticipantMap shipmentParticipantMap = new ShipmentParticipantMap()
            .responseStatus(DEFAULT_RESPONSE_STATUS)
            .shipmentReceiptDate(DEFAULT_SHIPMENT_RECEIPT_DATE)
            .shipmentTestDate(DEFAULT_SHIPMENT_TEST_DATE)
            .shipmentTestReportDate(DEFAULT_SHIPMENT_TEST_REPORT_DATE)
            .submittedAt(DEFAULT_SUBMITTED_AT)
            .evaluatedAt(DEFAULT_EVALUATED_AT)
            .isExcluded(DEFAULT_IS_EXCLUDED)
            .isResponseLate(DEFAULT_IS_RESPONSE_LATE)
            .isPtTestNotPerformed(DEFAULT_IS_PT_TEST_NOT_PERFORMED)
            .ptTestNotPerformedComments(DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS)
            .supervisorApproved(DEFAULT_SUPERVISOR_APPROVED)
            .participantSupervisor(DEFAULT_PARTICIPANT_SUPERVISOR)
            .userComment(DEFAULT_USER_COMMENT)
            .shipmentScore(DEFAULT_SHIPMENT_SCORE)
            .documentationScore(DEFAULT_DOCUMENTATION_SCORE)
            .finalResult(DEFAULT_FINAL_RESULT)
            .failureReason(DEFAULT_FAILURE_REASON)
            .evaluationComment(DEFAULT_EVALUATION_COMMENT)
            .isFollowup(DEFAULT_IS_FOLLOWUP)
            .manualOverride(DEFAULT_MANUAL_OVERRIDE)
            .qcStatus(DEFAULT_QC_STATUS)
            .qcDate(DEFAULT_QC_DATE)
            .qcDoneBy(DEFAULT_QC_DONE_BY)
            .syncedToMobile(DEFAULT_SYNCED_TO_MOBILE)
            .syncedOn(DEFAULT_SYNCED_ON);
        // Add required entity
        Shipment shipment;
        if (TestUtil.findAll(em, Shipment.class).isEmpty()) {
            shipment = ShipmentResourceIT.createEntity(em);
            em.persist(shipment);
            em.flush();
        } else {
            shipment = TestUtil.findAll(em, Shipment.class).get(0);
        }
        shipmentParticipantMap.setShipment(shipment);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        shipmentParticipantMap.setParticipant(participant);
        return shipmentParticipantMap;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShipmentParticipantMap createUpdatedEntity(EntityManager em) {
        ShipmentParticipantMap updatedShipmentParticipantMap = new ShipmentParticipantMap()
            .responseStatus(UPDATED_RESPONSE_STATUS)
            .shipmentReceiptDate(UPDATED_SHIPMENT_RECEIPT_DATE)
            .shipmentTestDate(UPDATED_SHIPMENT_TEST_DATE)
            .shipmentTestReportDate(UPDATED_SHIPMENT_TEST_REPORT_DATE)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .evaluatedAt(UPDATED_EVALUATED_AT)
            .isExcluded(UPDATED_IS_EXCLUDED)
            .isResponseLate(UPDATED_IS_RESPONSE_LATE)
            .isPtTestNotPerformed(UPDATED_IS_PT_TEST_NOT_PERFORMED)
            .ptTestNotPerformedComments(UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS)
            .supervisorApproved(UPDATED_SUPERVISOR_APPROVED)
            .participantSupervisor(UPDATED_PARTICIPANT_SUPERVISOR)
            .userComment(UPDATED_USER_COMMENT)
            .shipmentScore(UPDATED_SHIPMENT_SCORE)
            .documentationScore(UPDATED_DOCUMENTATION_SCORE)
            .finalResult(UPDATED_FINAL_RESULT)
            .failureReason(UPDATED_FAILURE_REASON)
            .evaluationComment(UPDATED_EVALUATION_COMMENT)
            .isFollowup(UPDATED_IS_FOLLOWUP)
            .manualOverride(UPDATED_MANUAL_OVERRIDE)
            .qcStatus(UPDATED_QC_STATUS)
            .qcDate(UPDATED_QC_DATE)
            .qcDoneBy(UPDATED_QC_DONE_BY)
            .syncedToMobile(UPDATED_SYNCED_TO_MOBILE)
            .syncedOn(UPDATED_SYNCED_ON);
        // Add required entity
        Shipment shipment;
        if (TestUtil.findAll(em, Shipment.class).isEmpty()) {
            shipment = ShipmentResourceIT.createUpdatedEntity(em);
            em.persist(shipment);
            em.flush();
        } else {
            shipment = TestUtil.findAll(em, Shipment.class).get(0);
        }
        updatedShipmentParticipantMap.setShipment(shipment);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createUpdatedEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        updatedShipmentParticipantMap.setParticipant(participant);
        return updatedShipmentParticipantMap;
    }

    @BeforeEach
    void initTest() {
        shipmentParticipantMap = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedShipmentParticipantMap != null) {
            shipmentParticipantMapRepository.delete(insertedShipmentParticipantMap);
            insertedShipmentParticipantMap = null;
        }
    }

    @Test
    @Transactional
    void createShipmentParticipantMap() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShipmentParticipantMap
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(shipmentParticipantMap);
        var returnedShipmentParticipantMapDTO = om.readValue(
            restShipmentParticipantMapMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentParticipantMapDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShipmentParticipantMapDTO.class
        );

        // Validate the ShipmentParticipantMap in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipmentParticipantMap = shipmentParticipantMapMapper.toEntity(returnedShipmentParticipantMapDTO);
        assertShipmentParticipantMapUpdatableFieldsEquals(
            returnedShipmentParticipantMap,
            getPersistedShipmentParticipantMap(returnedShipmentParticipantMap)
        );

        insertedShipmentParticipantMap = returnedShipmentParticipantMap;
    }

    @Test
    @Transactional
    void createShipmentParticipantMapWithExistingId() throws Exception {
        // Create the ShipmentParticipantMap with an existing ID
        shipmentParticipantMap.setId(1L);
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(shipmentParticipantMap);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentParticipantMapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentParticipantMapDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShipmentParticipantMap in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMaps() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList
        restShipmentParticipantMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentParticipantMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].responseStatus").value(hasItem(DEFAULT_RESPONSE_STATUS.toString())))
            .andExpect(jsonPath("$.[*].shipmentReceiptDate").value(hasItem(DEFAULT_SHIPMENT_RECEIPT_DATE.toString())))
            .andExpect(jsonPath("$.[*].shipmentTestDate").value(hasItem(DEFAULT_SHIPMENT_TEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].shipmentTestReportDate").value(hasItem(DEFAULT_SHIPMENT_TEST_REPORT_DATE.toString())))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].evaluatedAt").value(hasItem(DEFAULT_EVALUATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isExcluded").value(hasItem(DEFAULT_IS_EXCLUDED)))
            .andExpect(jsonPath("$.[*].isResponseLate").value(hasItem(DEFAULT_IS_RESPONSE_LATE)))
            .andExpect(jsonPath("$.[*].isPtTestNotPerformed").value(hasItem(DEFAULT_IS_PT_TEST_NOT_PERFORMED)))
            .andExpect(jsonPath("$.[*].ptTestNotPerformedComments").value(hasItem(DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS)))
            .andExpect(jsonPath("$.[*].supervisorApproved").value(hasItem(DEFAULT_SUPERVISOR_APPROVED)))
            .andExpect(jsonPath("$.[*].participantSupervisor").value(hasItem(DEFAULT_PARTICIPANT_SUPERVISOR)))
            .andExpect(jsonPath("$.[*].userComment").value(hasItem(DEFAULT_USER_COMMENT)))
            .andExpect(jsonPath("$.[*].shipmentScore").value(hasItem(DEFAULT_SHIPMENT_SCORE)))
            .andExpect(jsonPath("$.[*].documentationScore").value(hasItem(DEFAULT_DOCUMENTATION_SCORE)))
            .andExpect(jsonPath("$.[*].finalResult").value(hasItem(DEFAULT_FINAL_RESULT.toString())))
            .andExpect(jsonPath("$.[*].failureReason").value(hasItem(DEFAULT_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].evaluationComment").value(hasItem(DEFAULT_EVALUATION_COMMENT)))
            .andExpect(jsonPath("$.[*].isFollowup").value(hasItem(DEFAULT_IS_FOLLOWUP)))
            .andExpect(jsonPath("$.[*].manualOverride").value(hasItem(DEFAULT_MANUAL_OVERRIDE)))
            .andExpect(jsonPath("$.[*].qcStatus").value(hasItem(DEFAULT_QC_STATUS.toString())))
            .andExpect(jsonPath("$.[*].qcDate").value(hasItem(DEFAULT_QC_DATE.toString())))
            .andExpect(jsonPath("$.[*].qcDoneBy").value(hasItem(DEFAULT_QC_DONE_BY)))
            .andExpect(jsonPath("$.[*].syncedToMobile").value(hasItem(DEFAULT_SYNCED_TO_MOBILE)))
            .andExpect(jsonPath("$.[*].syncedOn").value(hasItem(DEFAULT_SYNCED_ON.toString())));
    }

    @Test
    @Transactional
    void getShipmentParticipantMap() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get the shipmentParticipantMap
        restShipmentParticipantMapMockMvc
            .perform(get(ENTITY_API_URL_ID, shipmentParticipantMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipmentParticipantMap.getId().intValue()))
            .andExpect(jsonPath("$.responseStatus").value(DEFAULT_RESPONSE_STATUS.toString()))
            .andExpect(jsonPath("$.shipmentReceiptDate").value(DEFAULT_SHIPMENT_RECEIPT_DATE.toString()))
            .andExpect(jsonPath("$.shipmentTestDate").value(DEFAULT_SHIPMENT_TEST_DATE.toString()))
            .andExpect(jsonPath("$.shipmentTestReportDate").value(DEFAULT_SHIPMENT_TEST_REPORT_DATE.toString()))
            .andExpect(jsonPath("$.submittedAt").value(DEFAULT_SUBMITTED_AT.toString()))
            .andExpect(jsonPath("$.evaluatedAt").value(DEFAULT_EVALUATED_AT.toString()))
            .andExpect(jsonPath("$.isExcluded").value(DEFAULT_IS_EXCLUDED))
            .andExpect(jsonPath("$.isResponseLate").value(DEFAULT_IS_RESPONSE_LATE))
            .andExpect(jsonPath("$.isPtTestNotPerformed").value(DEFAULT_IS_PT_TEST_NOT_PERFORMED))
            .andExpect(jsonPath("$.ptTestNotPerformedComments").value(DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS))
            .andExpect(jsonPath("$.supervisorApproved").value(DEFAULT_SUPERVISOR_APPROVED))
            .andExpect(jsonPath("$.participantSupervisor").value(DEFAULT_PARTICIPANT_SUPERVISOR))
            .andExpect(jsonPath("$.userComment").value(DEFAULT_USER_COMMENT))
            .andExpect(jsonPath("$.shipmentScore").value(DEFAULT_SHIPMENT_SCORE))
            .andExpect(jsonPath("$.documentationScore").value(DEFAULT_DOCUMENTATION_SCORE))
            .andExpect(jsonPath("$.finalResult").value(DEFAULT_FINAL_RESULT.toString()))
            .andExpect(jsonPath("$.failureReason").value(DEFAULT_FAILURE_REASON))
            .andExpect(jsonPath("$.evaluationComment").value(DEFAULT_EVALUATION_COMMENT))
            .andExpect(jsonPath("$.isFollowup").value(DEFAULT_IS_FOLLOWUP))
            .andExpect(jsonPath("$.manualOverride").value(DEFAULT_MANUAL_OVERRIDE))
            .andExpect(jsonPath("$.qcStatus").value(DEFAULT_QC_STATUS.toString()))
            .andExpect(jsonPath("$.qcDate").value(DEFAULT_QC_DATE.toString()))
            .andExpect(jsonPath("$.qcDoneBy").value(DEFAULT_QC_DONE_BY))
            .andExpect(jsonPath("$.syncedToMobile").value(DEFAULT_SYNCED_TO_MOBILE))
            .andExpect(jsonPath("$.syncedOn").value(DEFAULT_SYNCED_ON.toString()));
    }

    @Test
    @Transactional
    void getShipmentParticipantMapsByIdFiltering() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        Long id = shipmentParticipantMap.getId();

        defaultShipmentParticipantMapFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultShipmentParticipantMapFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultShipmentParticipantMapFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByResponseStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where responseStatus equals to
        defaultShipmentParticipantMapFiltering(
            "responseStatus.equals=" + DEFAULT_RESPONSE_STATUS,
            "responseStatus.equals=" + UPDATED_RESPONSE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByResponseStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where responseStatus in
        defaultShipmentParticipantMapFiltering(
            "responseStatus.in=" + DEFAULT_RESPONSE_STATUS + "," + UPDATED_RESPONSE_STATUS,
            "responseStatus.in=" + UPDATED_RESPONSE_STATUS
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByResponseStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where responseStatus is not null
        defaultShipmentParticipantMapFiltering("responseStatus.specified=true", "responseStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentReceiptDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentReceiptDate equals to
        defaultShipmentParticipantMapFiltering(
            "shipmentReceiptDate.equals=" + DEFAULT_SHIPMENT_RECEIPT_DATE,
            "shipmentReceiptDate.equals=" + UPDATED_SHIPMENT_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentReceiptDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentReceiptDate in
        defaultShipmentParticipantMapFiltering(
            "shipmentReceiptDate.in=" + DEFAULT_SHIPMENT_RECEIPT_DATE + "," + UPDATED_SHIPMENT_RECEIPT_DATE,
            "shipmentReceiptDate.in=" + UPDATED_SHIPMENT_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentReceiptDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentReceiptDate is not null
        defaultShipmentParticipantMapFiltering("shipmentReceiptDate.specified=true", "shipmentReceiptDate.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentReceiptDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentReceiptDate is greater than or equal to
        defaultShipmentParticipantMapFiltering(
            "shipmentReceiptDate.greaterThanOrEqual=" + DEFAULT_SHIPMENT_RECEIPT_DATE,
            "shipmentReceiptDate.greaterThanOrEqual=" + UPDATED_SHIPMENT_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentReceiptDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentReceiptDate is less than or equal to
        defaultShipmentParticipantMapFiltering(
            "shipmentReceiptDate.lessThanOrEqual=" + DEFAULT_SHIPMENT_RECEIPT_DATE,
            "shipmentReceiptDate.lessThanOrEqual=" + SMALLER_SHIPMENT_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentReceiptDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentReceiptDate is less than
        defaultShipmentParticipantMapFiltering(
            "shipmentReceiptDate.lessThan=" + UPDATED_SHIPMENT_RECEIPT_DATE,
            "shipmentReceiptDate.lessThan=" + DEFAULT_SHIPMENT_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentReceiptDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentReceiptDate is greater than
        defaultShipmentParticipantMapFiltering(
            "shipmentReceiptDate.greaterThan=" + SMALLER_SHIPMENT_RECEIPT_DATE,
            "shipmentReceiptDate.greaterThan=" + DEFAULT_SHIPMENT_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestDate equals to
        defaultShipmentParticipantMapFiltering(
            "shipmentTestDate.equals=" + DEFAULT_SHIPMENT_TEST_DATE,
            "shipmentTestDate.equals=" + UPDATED_SHIPMENT_TEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestDate in
        defaultShipmentParticipantMapFiltering(
            "shipmentTestDate.in=" + DEFAULT_SHIPMENT_TEST_DATE + "," + UPDATED_SHIPMENT_TEST_DATE,
            "shipmentTestDate.in=" + UPDATED_SHIPMENT_TEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestDate is not null
        defaultShipmentParticipantMapFiltering("shipmentTestDate.specified=true", "shipmentTestDate.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestDate is greater than or equal to
        defaultShipmentParticipantMapFiltering(
            "shipmentTestDate.greaterThanOrEqual=" + DEFAULT_SHIPMENT_TEST_DATE,
            "shipmentTestDate.greaterThanOrEqual=" + UPDATED_SHIPMENT_TEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestDate is less than or equal to
        defaultShipmentParticipantMapFiltering(
            "shipmentTestDate.lessThanOrEqual=" + DEFAULT_SHIPMENT_TEST_DATE,
            "shipmentTestDate.lessThanOrEqual=" + SMALLER_SHIPMENT_TEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestDate is less than
        defaultShipmentParticipantMapFiltering(
            "shipmentTestDate.lessThan=" + UPDATED_SHIPMENT_TEST_DATE,
            "shipmentTestDate.lessThan=" + DEFAULT_SHIPMENT_TEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestDate is greater than
        defaultShipmentParticipantMapFiltering(
            "shipmentTestDate.greaterThan=" + SMALLER_SHIPMENT_TEST_DATE,
            "shipmentTestDate.greaterThan=" + DEFAULT_SHIPMENT_TEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestReportDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestReportDate equals to
        defaultShipmentParticipantMapFiltering(
            "shipmentTestReportDate.equals=" + DEFAULT_SHIPMENT_TEST_REPORT_DATE,
            "shipmentTestReportDate.equals=" + UPDATED_SHIPMENT_TEST_REPORT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestReportDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestReportDate in
        defaultShipmentParticipantMapFiltering(
            "shipmentTestReportDate.in=" + DEFAULT_SHIPMENT_TEST_REPORT_DATE + "," + UPDATED_SHIPMENT_TEST_REPORT_DATE,
            "shipmentTestReportDate.in=" + UPDATED_SHIPMENT_TEST_REPORT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentTestReportDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentTestReportDate is not null
        defaultShipmentParticipantMapFiltering("shipmentTestReportDate.specified=true", "shipmentTestReportDate.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySubmittedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where submittedAt equals to
        defaultShipmentParticipantMapFiltering("submittedAt.equals=" + DEFAULT_SUBMITTED_AT, "submittedAt.equals=" + UPDATED_SUBMITTED_AT);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySubmittedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where submittedAt in
        defaultShipmentParticipantMapFiltering(
            "submittedAt.in=" + DEFAULT_SUBMITTED_AT + "," + UPDATED_SUBMITTED_AT,
            "submittedAt.in=" + UPDATED_SUBMITTED_AT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySubmittedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where submittedAt is not null
        defaultShipmentParticipantMapFiltering("submittedAt.specified=true", "submittedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByEvaluatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where evaluatedAt equals to
        defaultShipmentParticipantMapFiltering("evaluatedAt.equals=" + DEFAULT_EVALUATED_AT, "evaluatedAt.equals=" + UPDATED_EVALUATED_AT);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByEvaluatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where evaluatedAt in
        defaultShipmentParticipantMapFiltering(
            "evaluatedAt.in=" + DEFAULT_EVALUATED_AT + "," + UPDATED_EVALUATED_AT,
            "evaluatedAt.in=" + UPDATED_EVALUATED_AT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByEvaluatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where evaluatedAt is not null
        defaultShipmentParticipantMapFiltering("evaluatedAt.specified=true", "evaluatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsExcludedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isExcluded equals to
        defaultShipmentParticipantMapFiltering("isExcluded.equals=" + DEFAULT_IS_EXCLUDED, "isExcluded.equals=" + UPDATED_IS_EXCLUDED);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsExcludedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isExcluded in
        defaultShipmentParticipantMapFiltering(
            "isExcluded.in=" + DEFAULT_IS_EXCLUDED + "," + UPDATED_IS_EXCLUDED,
            "isExcluded.in=" + UPDATED_IS_EXCLUDED
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsExcludedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isExcluded is not null
        defaultShipmentParticipantMapFiltering("isExcluded.specified=true", "isExcluded.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsResponseLateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isResponseLate equals to
        defaultShipmentParticipantMapFiltering(
            "isResponseLate.equals=" + DEFAULT_IS_RESPONSE_LATE,
            "isResponseLate.equals=" + UPDATED_IS_RESPONSE_LATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsResponseLateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isResponseLate in
        defaultShipmentParticipantMapFiltering(
            "isResponseLate.in=" + DEFAULT_IS_RESPONSE_LATE + "," + UPDATED_IS_RESPONSE_LATE,
            "isResponseLate.in=" + UPDATED_IS_RESPONSE_LATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsResponseLateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isResponseLate is not null
        defaultShipmentParticipantMapFiltering("isResponseLate.specified=true", "isResponseLate.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsPtTestNotPerformedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isPtTestNotPerformed equals to
        defaultShipmentParticipantMapFiltering(
            "isPtTestNotPerformed.equals=" + DEFAULT_IS_PT_TEST_NOT_PERFORMED,
            "isPtTestNotPerformed.equals=" + UPDATED_IS_PT_TEST_NOT_PERFORMED
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsPtTestNotPerformedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isPtTestNotPerformed in
        defaultShipmentParticipantMapFiltering(
            "isPtTestNotPerformed.in=" + DEFAULT_IS_PT_TEST_NOT_PERFORMED + "," + UPDATED_IS_PT_TEST_NOT_PERFORMED,
            "isPtTestNotPerformed.in=" + UPDATED_IS_PT_TEST_NOT_PERFORMED
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsPtTestNotPerformedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isPtTestNotPerformed is not null
        defaultShipmentParticipantMapFiltering("isPtTestNotPerformed.specified=true", "isPtTestNotPerformed.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByPtTestNotPerformedCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where ptTestNotPerformedComments equals to
        defaultShipmentParticipantMapFiltering(
            "ptTestNotPerformedComments.equals=" + DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS,
            "ptTestNotPerformedComments.equals=" + UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByPtTestNotPerformedCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where ptTestNotPerformedComments in
        defaultShipmentParticipantMapFiltering(
            "ptTestNotPerformedComments.in=" + DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS + "," + UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS,
            "ptTestNotPerformedComments.in=" + UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByPtTestNotPerformedCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where ptTestNotPerformedComments is not null
        defaultShipmentParticipantMapFiltering("ptTestNotPerformedComments.specified=true", "ptTestNotPerformedComments.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByPtTestNotPerformedCommentsContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where ptTestNotPerformedComments contains
        defaultShipmentParticipantMapFiltering(
            "ptTestNotPerformedComments.contains=" + DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS,
            "ptTestNotPerformedComments.contains=" + UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByPtTestNotPerformedCommentsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where ptTestNotPerformedComments does not contain
        defaultShipmentParticipantMapFiltering(
            "ptTestNotPerformedComments.doesNotContain=" + UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS,
            "ptTestNotPerformedComments.doesNotContain=" + DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySupervisorApprovedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where supervisorApproved equals to
        defaultShipmentParticipantMapFiltering(
            "supervisorApproved.equals=" + DEFAULT_SUPERVISOR_APPROVED,
            "supervisorApproved.equals=" + UPDATED_SUPERVISOR_APPROVED
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySupervisorApprovedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where supervisorApproved in
        defaultShipmentParticipantMapFiltering(
            "supervisorApproved.in=" + DEFAULT_SUPERVISOR_APPROVED + "," + UPDATED_SUPERVISOR_APPROVED,
            "supervisorApproved.in=" + UPDATED_SUPERVISOR_APPROVED
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySupervisorApprovedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where supervisorApproved is not null
        defaultShipmentParticipantMapFiltering("supervisorApproved.specified=true", "supervisorApproved.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByParticipantSupervisorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where participantSupervisor equals to
        defaultShipmentParticipantMapFiltering(
            "participantSupervisor.equals=" + DEFAULT_PARTICIPANT_SUPERVISOR,
            "participantSupervisor.equals=" + UPDATED_PARTICIPANT_SUPERVISOR
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByParticipantSupervisorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where participantSupervisor in
        defaultShipmentParticipantMapFiltering(
            "participantSupervisor.in=" + DEFAULT_PARTICIPANT_SUPERVISOR + "," + UPDATED_PARTICIPANT_SUPERVISOR,
            "participantSupervisor.in=" + UPDATED_PARTICIPANT_SUPERVISOR
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByParticipantSupervisorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where participantSupervisor is not null
        defaultShipmentParticipantMapFiltering("participantSupervisor.specified=true", "participantSupervisor.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByParticipantSupervisorContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where participantSupervisor contains
        defaultShipmentParticipantMapFiltering(
            "participantSupervisor.contains=" + DEFAULT_PARTICIPANT_SUPERVISOR,
            "participantSupervisor.contains=" + UPDATED_PARTICIPANT_SUPERVISOR
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByParticipantSupervisorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where participantSupervisor does not contain
        defaultShipmentParticipantMapFiltering(
            "participantSupervisor.doesNotContain=" + UPDATED_PARTICIPANT_SUPERVISOR,
            "participantSupervisor.doesNotContain=" + DEFAULT_PARTICIPANT_SUPERVISOR
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByUserCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where userComment equals to
        defaultShipmentParticipantMapFiltering("userComment.equals=" + DEFAULT_USER_COMMENT, "userComment.equals=" + UPDATED_USER_COMMENT);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByUserCommentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where userComment in
        defaultShipmentParticipantMapFiltering(
            "userComment.in=" + DEFAULT_USER_COMMENT + "," + UPDATED_USER_COMMENT,
            "userComment.in=" + UPDATED_USER_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByUserCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where userComment is not null
        defaultShipmentParticipantMapFiltering("userComment.specified=true", "userComment.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByUserCommentContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where userComment contains
        defaultShipmentParticipantMapFiltering(
            "userComment.contains=" + DEFAULT_USER_COMMENT,
            "userComment.contains=" + UPDATED_USER_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByUserCommentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where userComment does not contain
        defaultShipmentParticipantMapFiltering(
            "userComment.doesNotContain=" + UPDATED_USER_COMMENT,
            "userComment.doesNotContain=" + DEFAULT_USER_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentScore equals to
        defaultShipmentParticipantMapFiltering(
            "shipmentScore.equals=" + DEFAULT_SHIPMENT_SCORE,
            "shipmentScore.equals=" + UPDATED_SHIPMENT_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentScore in
        defaultShipmentParticipantMapFiltering(
            "shipmentScore.in=" + DEFAULT_SHIPMENT_SCORE + "," + UPDATED_SHIPMENT_SCORE,
            "shipmentScore.in=" + UPDATED_SHIPMENT_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentScore is not null
        defaultShipmentParticipantMapFiltering("shipmentScore.specified=true", "shipmentScore.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentScore is greater than or equal to
        defaultShipmentParticipantMapFiltering(
            "shipmentScore.greaterThanOrEqual=" + DEFAULT_SHIPMENT_SCORE,
            "shipmentScore.greaterThanOrEqual=" + UPDATED_SHIPMENT_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentScore is less than or equal to
        defaultShipmentParticipantMapFiltering(
            "shipmentScore.lessThanOrEqual=" + DEFAULT_SHIPMENT_SCORE,
            "shipmentScore.lessThanOrEqual=" + SMALLER_SHIPMENT_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentScore is less than
        defaultShipmentParticipantMapFiltering(
            "shipmentScore.lessThan=" + UPDATED_SHIPMENT_SCORE,
            "shipmentScore.lessThan=" + DEFAULT_SHIPMENT_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where shipmentScore is greater than
        defaultShipmentParticipantMapFiltering(
            "shipmentScore.greaterThan=" + SMALLER_SHIPMENT_SCORE,
            "shipmentScore.greaterThan=" + DEFAULT_SHIPMENT_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByDocumentationScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where documentationScore equals to
        defaultShipmentParticipantMapFiltering(
            "documentationScore.equals=" + DEFAULT_DOCUMENTATION_SCORE,
            "documentationScore.equals=" + UPDATED_DOCUMENTATION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByDocumentationScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where documentationScore in
        defaultShipmentParticipantMapFiltering(
            "documentationScore.in=" + DEFAULT_DOCUMENTATION_SCORE + "," + UPDATED_DOCUMENTATION_SCORE,
            "documentationScore.in=" + UPDATED_DOCUMENTATION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByDocumentationScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where documentationScore is not null
        defaultShipmentParticipantMapFiltering("documentationScore.specified=true", "documentationScore.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByDocumentationScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where documentationScore is greater than or equal to
        defaultShipmentParticipantMapFiltering(
            "documentationScore.greaterThanOrEqual=" + DEFAULT_DOCUMENTATION_SCORE,
            "documentationScore.greaterThanOrEqual=" + UPDATED_DOCUMENTATION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByDocumentationScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where documentationScore is less than or equal to
        defaultShipmentParticipantMapFiltering(
            "documentationScore.lessThanOrEqual=" + DEFAULT_DOCUMENTATION_SCORE,
            "documentationScore.lessThanOrEqual=" + SMALLER_DOCUMENTATION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByDocumentationScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where documentationScore is less than
        defaultShipmentParticipantMapFiltering(
            "documentationScore.lessThan=" + UPDATED_DOCUMENTATION_SCORE,
            "documentationScore.lessThan=" + DEFAULT_DOCUMENTATION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByDocumentationScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where documentationScore is greater than
        defaultShipmentParticipantMapFiltering(
            "documentationScore.greaterThan=" + SMALLER_DOCUMENTATION_SCORE,
            "documentationScore.greaterThan=" + DEFAULT_DOCUMENTATION_SCORE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByFinalResultIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where finalResult equals to
        defaultShipmentParticipantMapFiltering("finalResult.equals=" + DEFAULT_FINAL_RESULT, "finalResult.equals=" + UPDATED_FINAL_RESULT);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByFinalResultIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where finalResult in
        defaultShipmentParticipantMapFiltering(
            "finalResult.in=" + DEFAULT_FINAL_RESULT + "," + UPDATED_FINAL_RESULT,
            "finalResult.in=" + UPDATED_FINAL_RESULT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByFinalResultIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where finalResult is not null
        defaultShipmentParticipantMapFiltering("finalResult.specified=true", "finalResult.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByEvaluationCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where evaluationComment equals to
        defaultShipmentParticipantMapFiltering(
            "evaluationComment.equals=" + DEFAULT_EVALUATION_COMMENT,
            "evaluationComment.equals=" + UPDATED_EVALUATION_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByEvaluationCommentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where evaluationComment in
        defaultShipmentParticipantMapFiltering(
            "evaluationComment.in=" + DEFAULT_EVALUATION_COMMENT + "," + UPDATED_EVALUATION_COMMENT,
            "evaluationComment.in=" + UPDATED_EVALUATION_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByEvaluationCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where evaluationComment is not null
        defaultShipmentParticipantMapFiltering("evaluationComment.specified=true", "evaluationComment.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByEvaluationCommentContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where evaluationComment contains
        defaultShipmentParticipantMapFiltering(
            "evaluationComment.contains=" + DEFAULT_EVALUATION_COMMENT,
            "evaluationComment.contains=" + UPDATED_EVALUATION_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByEvaluationCommentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where evaluationComment does not contain
        defaultShipmentParticipantMapFiltering(
            "evaluationComment.doesNotContain=" + UPDATED_EVALUATION_COMMENT,
            "evaluationComment.doesNotContain=" + DEFAULT_EVALUATION_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsFollowupIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isFollowup equals to
        defaultShipmentParticipantMapFiltering("isFollowup.equals=" + DEFAULT_IS_FOLLOWUP, "isFollowup.equals=" + UPDATED_IS_FOLLOWUP);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsFollowupIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isFollowup in
        defaultShipmentParticipantMapFiltering(
            "isFollowup.in=" + DEFAULT_IS_FOLLOWUP + "," + UPDATED_IS_FOLLOWUP,
            "isFollowup.in=" + UPDATED_IS_FOLLOWUP
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByIsFollowupIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where isFollowup is not null
        defaultShipmentParticipantMapFiltering("isFollowup.specified=true", "isFollowup.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByManualOverrideIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where manualOverride equals to
        defaultShipmentParticipantMapFiltering(
            "manualOverride.equals=" + DEFAULT_MANUAL_OVERRIDE,
            "manualOverride.equals=" + UPDATED_MANUAL_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByManualOverrideIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where manualOverride in
        defaultShipmentParticipantMapFiltering(
            "manualOverride.in=" + DEFAULT_MANUAL_OVERRIDE + "," + UPDATED_MANUAL_OVERRIDE,
            "manualOverride.in=" + UPDATED_MANUAL_OVERRIDE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByManualOverrideIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where manualOverride is not null
        defaultShipmentParticipantMapFiltering("manualOverride.specified=true", "manualOverride.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcStatus equals to
        defaultShipmentParticipantMapFiltering("qcStatus.equals=" + DEFAULT_QC_STATUS, "qcStatus.equals=" + UPDATED_QC_STATUS);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcStatus in
        defaultShipmentParticipantMapFiltering(
            "qcStatus.in=" + DEFAULT_QC_STATUS + "," + UPDATED_QC_STATUS,
            "qcStatus.in=" + UPDATED_QC_STATUS
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcStatus is not null
        defaultShipmentParticipantMapFiltering("qcStatus.specified=true", "qcStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDate equals to
        defaultShipmentParticipantMapFiltering("qcDate.equals=" + DEFAULT_QC_DATE, "qcDate.equals=" + UPDATED_QC_DATE);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDate in
        defaultShipmentParticipantMapFiltering("qcDate.in=" + DEFAULT_QC_DATE + "," + UPDATED_QC_DATE, "qcDate.in=" + UPDATED_QC_DATE);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDate is not null
        defaultShipmentParticipantMapFiltering("qcDate.specified=true", "qcDate.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDate is greater than or equal to
        defaultShipmentParticipantMapFiltering(
            "qcDate.greaterThanOrEqual=" + DEFAULT_QC_DATE,
            "qcDate.greaterThanOrEqual=" + UPDATED_QC_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDate is less than or equal to
        defaultShipmentParticipantMapFiltering("qcDate.lessThanOrEqual=" + DEFAULT_QC_DATE, "qcDate.lessThanOrEqual=" + SMALLER_QC_DATE);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDate is less than
        defaultShipmentParticipantMapFiltering("qcDate.lessThan=" + UPDATED_QC_DATE, "qcDate.lessThan=" + DEFAULT_QC_DATE);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDate is greater than
        defaultShipmentParticipantMapFiltering("qcDate.greaterThan=" + SMALLER_QC_DATE, "qcDate.greaterThan=" + DEFAULT_QC_DATE);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDoneByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDoneBy equals to
        defaultShipmentParticipantMapFiltering("qcDoneBy.equals=" + DEFAULT_QC_DONE_BY, "qcDoneBy.equals=" + UPDATED_QC_DONE_BY);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDoneByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDoneBy in
        defaultShipmentParticipantMapFiltering(
            "qcDoneBy.in=" + DEFAULT_QC_DONE_BY + "," + UPDATED_QC_DONE_BY,
            "qcDoneBy.in=" + UPDATED_QC_DONE_BY
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDoneByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDoneBy is not null
        defaultShipmentParticipantMapFiltering("qcDoneBy.specified=true", "qcDoneBy.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDoneByContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDoneBy contains
        defaultShipmentParticipantMapFiltering("qcDoneBy.contains=" + DEFAULT_QC_DONE_BY, "qcDoneBy.contains=" + UPDATED_QC_DONE_BY);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByQcDoneByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where qcDoneBy does not contain
        defaultShipmentParticipantMapFiltering(
            "qcDoneBy.doesNotContain=" + UPDATED_QC_DONE_BY,
            "qcDoneBy.doesNotContain=" + DEFAULT_QC_DONE_BY
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySyncedToMobileIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where syncedToMobile equals to
        defaultShipmentParticipantMapFiltering(
            "syncedToMobile.equals=" + DEFAULT_SYNCED_TO_MOBILE,
            "syncedToMobile.equals=" + UPDATED_SYNCED_TO_MOBILE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySyncedToMobileIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where syncedToMobile in
        defaultShipmentParticipantMapFiltering(
            "syncedToMobile.in=" + DEFAULT_SYNCED_TO_MOBILE + "," + UPDATED_SYNCED_TO_MOBILE,
            "syncedToMobile.in=" + UPDATED_SYNCED_TO_MOBILE
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySyncedToMobileIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where syncedToMobile is not null
        defaultShipmentParticipantMapFiltering("syncedToMobile.specified=true", "syncedToMobile.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySyncedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where syncedOn equals to
        defaultShipmentParticipantMapFiltering("syncedOn.equals=" + DEFAULT_SYNCED_ON, "syncedOn.equals=" + UPDATED_SYNCED_ON);
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySyncedOnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where syncedOn in
        defaultShipmentParticipantMapFiltering(
            "syncedOn.in=" + DEFAULT_SYNCED_ON + "," + UPDATED_SYNCED_ON,
            "syncedOn.in=" + UPDATED_SYNCED_ON
        );
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsBySyncedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        // Get all the shipmentParticipantMapList where syncedOn is not null
        defaultShipmentParticipantMapFiltering("syncedOn.specified=true", "syncedOn.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByModeOfReceiptIsEqualToSomething() throws Exception {
        ModeOfReceipt modeOfReceipt;
        if (TestUtil.findAll(em, ModeOfReceipt.class).isEmpty()) {
            shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);
            modeOfReceipt = ModeOfReceiptResourceIT.createEntity();
        } else {
            modeOfReceipt = TestUtil.findAll(em, ModeOfReceipt.class).get(0);
        }
        em.persist(modeOfReceipt);
        em.flush();
        shipmentParticipantMap.setModeOfReceipt(modeOfReceipt);
        shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);
        Long modeOfReceiptId = modeOfReceipt.getId();
        // Get all the shipmentParticipantMapList where modeOfReceipt equals to modeOfReceiptId
        defaultShipmentParticipantMapShouldBeFound("modeOfReceiptId.equals=" + modeOfReceiptId);

        // Get all the shipmentParticipantMapList where modeOfReceipt equals to (modeOfReceiptId + 1)
        defaultShipmentParticipantMapShouldNotBeFound("modeOfReceiptId.equals=" + (modeOfReceiptId + 1));
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByNotTestedReasonIsEqualToSomething() throws Exception {
        NotTestedReason notTestedReason;
        if (TestUtil.findAll(em, NotTestedReason.class).isEmpty()) {
            shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);
            notTestedReason = NotTestedReasonResourceIT.createEntity();
        } else {
            notTestedReason = TestUtil.findAll(em, NotTestedReason.class).get(0);
        }
        em.persist(notTestedReason);
        em.flush();
        shipmentParticipantMap.setNotTestedReason(notTestedReason);
        shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);
        Long notTestedReasonId = notTestedReason.getId();
        // Get all the shipmentParticipantMapList where notTestedReason equals to notTestedReasonId
        defaultShipmentParticipantMapShouldBeFound("notTestedReasonId.equals=" + notTestedReasonId);

        // Get all the shipmentParticipantMapList where notTestedReason equals to (notTestedReasonId + 1)
        defaultShipmentParticipantMapShouldNotBeFound("notTestedReasonId.equals=" + (notTestedReasonId + 1));
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByShipmentIsEqualToSomething() throws Exception {
        Shipment shipment;
        if (TestUtil.findAll(em, Shipment.class).isEmpty()) {
            shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);
            shipment = ShipmentResourceIT.createEntity(em);
        } else {
            shipment = TestUtil.findAll(em, Shipment.class).get(0);
        }
        em.persist(shipment);
        em.flush();
        shipmentParticipantMap.setShipment(shipment);
        shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);
        Long shipmentId = shipment.getId();
        // Get all the shipmentParticipantMapList where shipment equals to shipmentId
        defaultShipmentParticipantMapShouldBeFound("shipmentId.equals=" + shipmentId);

        // Get all the shipmentParticipantMapList where shipment equals to (shipmentId + 1)
        defaultShipmentParticipantMapShouldNotBeFound("shipmentId.equals=" + (shipmentId + 1));
    }

    @Test
    @Transactional
    void getAllShipmentParticipantMapsByParticipantIsEqualToSomething() throws Exception {
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);
            participant = ParticipantResourceIT.createEntity();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        em.persist(participant);
        em.flush();
        shipmentParticipantMap.setParticipant(participant);
        shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);
        Long participantId = participant.getId();
        // Get all the shipmentParticipantMapList where participant equals to participantId
        defaultShipmentParticipantMapShouldBeFound("participantId.equals=" + participantId);

        // Get all the shipmentParticipantMapList where participant equals to (participantId + 1)
        defaultShipmentParticipantMapShouldNotBeFound("participantId.equals=" + (participantId + 1));
    }

    private void defaultShipmentParticipantMapFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultShipmentParticipantMapShouldBeFound(shouldBeFound);
        defaultShipmentParticipantMapShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShipmentParticipantMapShouldBeFound(String filter) throws Exception {
        restShipmentParticipantMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipmentParticipantMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].responseStatus").value(hasItem(DEFAULT_RESPONSE_STATUS.toString())))
            .andExpect(jsonPath("$.[*].shipmentReceiptDate").value(hasItem(DEFAULT_SHIPMENT_RECEIPT_DATE.toString())))
            .andExpect(jsonPath("$.[*].shipmentTestDate").value(hasItem(DEFAULT_SHIPMENT_TEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].shipmentTestReportDate").value(hasItem(DEFAULT_SHIPMENT_TEST_REPORT_DATE.toString())))
            .andExpect(jsonPath("$.[*].submittedAt").value(hasItem(DEFAULT_SUBMITTED_AT.toString())))
            .andExpect(jsonPath("$.[*].evaluatedAt").value(hasItem(DEFAULT_EVALUATED_AT.toString())))
            .andExpect(jsonPath("$.[*].isExcluded").value(hasItem(DEFAULT_IS_EXCLUDED)))
            .andExpect(jsonPath("$.[*].isResponseLate").value(hasItem(DEFAULT_IS_RESPONSE_LATE)))
            .andExpect(jsonPath("$.[*].isPtTestNotPerformed").value(hasItem(DEFAULT_IS_PT_TEST_NOT_PERFORMED)))
            .andExpect(jsonPath("$.[*].ptTestNotPerformedComments").value(hasItem(DEFAULT_PT_TEST_NOT_PERFORMED_COMMENTS)))
            .andExpect(jsonPath("$.[*].supervisorApproved").value(hasItem(DEFAULT_SUPERVISOR_APPROVED)))
            .andExpect(jsonPath("$.[*].participantSupervisor").value(hasItem(DEFAULT_PARTICIPANT_SUPERVISOR)))
            .andExpect(jsonPath("$.[*].userComment").value(hasItem(DEFAULT_USER_COMMENT)))
            .andExpect(jsonPath("$.[*].shipmentScore").value(hasItem(DEFAULT_SHIPMENT_SCORE)))
            .andExpect(jsonPath("$.[*].documentationScore").value(hasItem(DEFAULT_DOCUMENTATION_SCORE)))
            .andExpect(jsonPath("$.[*].finalResult").value(hasItem(DEFAULT_FINAL_RESULT.toString())))
            .andExpect(jsonPath("$.[*].failureReason").value(hasItem(DEFAULT_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].evaluationComment").value(hasItem(DEFAULT_EVALUATION_COMMENT)))
            .andExpect(jsonPath("$.[*].isFollowup").value(hasItem(DEFAULT_IS_FOLLOWUP)))
            .andExpect(jsonPath("$.[*].manualOverride").value(hasItem(DEFAULT_MANUAL_OVERRIDE)))
            .andExpect(jsonPath("$.[*].qcStatus").value(hasItem(DEFAULT_QC_STATUS.toString())))
            .andExpect(jsonPath("$.[*].qcDate").value(hasItem(DEFAULT_QC_DATE.toString())))
            .andExpect(jsonPath("$.[*].qcDoneBy").value(hasItem(DEFAULT_QC_DONE_BY)))
            .andExpect(jsonPath("$.[*].syncedToMobile").value(hasItem(DEFAULT_SYNCED_TO_MOBILE)))
            .andExpect(jsonPath("$.[*].syncedOn").value(hasItem(DEFAULT_SYNCED_ON.toString())));

        // Check, that the count call also returns 1
        restShipmentParticipantMapMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShipmentParticipantMapShouldNotBeFound(String filter) throws Exception {
        restShipmentParticipantMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShipmentParticipantMapMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShipmentParticipantMap() throws Exception {
        // Get the shipmentParticipantMap
        restShipmentParticipantMapMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShipmentParticipantMap() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentParticipantMap
        ShipmentParticipantMap updatedShipmentParticipantMap = shipmentParticipantMapRepository
            .findById(shipmentParticipantMap.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedShipmentParticipantMap are not directly saved in db
        em.detach(updatedShipmentParticipantMap);
        updatedShipmentParticipantMap
            .responseStatus(UPDATED_RESPONSE_STATUS)
            .shipmentReceiptDate(UPDATED_SHIPMENT_RECEIPT_DATE)
            .shipmentTestDate(UPDATED_SHIPMENT_TEST_DATE)
            .shipmentTestReportDate(UPDATED_SHIPMENT_TEST_REPORT_DATE)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .evaluatedAt(UPDATED_EVALUATED_AT)
            .isExcluded(UPDATED_IS_EXCLUDED)
            .isResponseLate(UPDATED_IS_RESPONSE_LATE)
            .isPtTestNotPerformed(UPDATED_IS_PT_TEST_NOT_PERFORMED)
            .ptTestNotPerformedComments(UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS)
            .supervisorApproved(UPDATED_SUPERVISOR_APPROVED)
            .participantSupervisor(UPDATED_PARTICIPANT_SUPERVISOR)
            .userComment(UPDATED_USER_COMMENT)
            .shipmentScore(UPDATED_SHIPMENT_SCORE)
            .documentationScore(UPDATED_DOCUMENTATION_SCORE)
            .finalResult(UPDATED_FINAL_RESULT)
            .failureReason(UPDATED_FAILURE_REASON)
            .evaluationComment(UPDATED_EVALUATION_COMMENT)
            .isFollowup(UPDATED_IS_FOLLOWUP)
            .manualOverride(UPDATED_MANUAL_OVERRIDE)
            .qcStatus(UPDATED_QC_STATUS)
            .qcDate(UPDATED_QC_DATE)
            .qcDoneBy(UPDATED_QC_DONE_BY)
            .syncedToMobile(UPDATED_SYNCED_TO_MOBILE)
            .syncedOn(UPDATED_SYNCED_ON);
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(updatedShipmentParticipantMap);

        restShipmentParticipantMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentParticipantMapDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentParticipantMapDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentParticipantMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentParticipantMapToMatchAllProperties(updatedShipmentParticipantMap);
    }

    @Test
    @Transactional
    void putNonExistingShipmentParticipantMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentParticipantMap.setId(longCount.incrementAndGet());

        // Create the ShipmentParticipantMap
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(shipmentParticipantMap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentParticipantMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentParticipantMapDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentParticipantMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentParticipantMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipmentParticipantMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentParticipantMap.setId(longCount.incrementAndGet());

        // Create the ShipmentParticipantMap
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(shipmentParticipantMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentParticipantMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentParticipantMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentParticipantMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipmentParticipantMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentParticipantMap.setId(longCount.incrementAndGet());

        // Create the ShipmentParticipantMap
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(shipmentParticipantMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentParticipantMapMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentParticipantMapDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentParticipantMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShipmentParticipantMapWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentParticipantMap using partial update
        ShipmentParticipantMap partialUpdatedShipmentParticipantMap = new ShipmentParticipantMap();
        partialUpdatedShipmentParticipantMap.setId(shipmentParticipantMap.getId());

        partialUpdatedShipmentParticipantMap
            .responseStatus(UPDATED_RESPONSE_STATUS)
            .shipmentReceiptDate(UPDATED_SHIPMENT_RECEIPT_DATE)
            .shipmentTestReportDate(UPDATED_SHIPMENT_TEST_REPORT_DATE)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .isExcluded(UPDATED_IS_EXCLUDED)
            .ptTestNotPerformedComments(UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS)
            .supervisorApproved(UPDATED_SUPERVISOR_APPROVED)
            .participantSupervisor(UPDATED_PARTICIPANT_SUPERVISOR)
            .userComment(UPDATED_USER_COMMENT)
            .documentationScore(UPDATED_DOCUMENTATION_SCORE)
            .failureReason(UPDATED_FAILURE_REASON)
            .evaluationComment(UPDATED_EVALUATION_COMMENT)
            .qcDate(UPDATED_QC_DATE)
            .qcDoneBy(UPDATED_QC_DONE_BY)
            .syncedToMobile(UPDATED_SYNCED_TO_MOBILE)
            .syncedOn(UPDATED_SYNCED_ON);

        restShipmentParticipantMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentParticipantMap.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentParticipantMap))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentParticipantMap in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentParticipantMapUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShipmentParticipantMap, shipmentParticipantMap),
            getPersistedShipmentParticipantMap(shipmentParticipantMap)
        );
    }

    @Test
    @Transactional
    void fullUpdateShipmentParticipantMapWithPatch() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipmentParticipantMap using partial update
        ShipmentParticipantMap partialUpdatedShipmentParticipantMap = new ShipmentParticipantMap();
        partialUpdatedShipmentParticipantMap.setId(shipmentParticipantMap.getId());

        partialUpdatedShipmentParticipantMap
            .responseStatus(UPDATED_RESPONSE_STATUS)
            .shipmentReceiptDate(UPDATED_SHIPMENT_RECEIPT_DATE)
            .shipmentTestDate(UPDATED_SHIPMENT_TEST_DATE)
            .shipmentTestReportDate(UPDATED_SHIPMENT_TEST_REPORT_DATE)
            .submittedAt(UPDATED_SUBMITTED_AT)
            .evaluatedAt(UPDATED_EVALUATED_AT)
            .isExcluded(UPDATED_IS_EXCLUDED)
            .isResponseLate(UPDATED_IS_RESPONSE_LATE)
            .isPtTestNotPerformed(UPDATED_IS_PT_TEST_NOT_PERFORMED)
            .ptTestNotPerformedComments(UPDATED_PT_TEST_NOT_PERFORMED_COMMENTS)
            .supervisorApproved(UPDATED_SUPERVISOR_APPROVED)
            .participantSupervisor(UPDATED_PARTICIPANT_SUPERVISOR)
            .userComment(UPDATED_USER_COMMENT)
            .shipmentScore(UPDATED_SHIPMENT_SCORE)
            .documentationScore(UPDATED_DOCUMENTATION_SCORE)
            .finalResult(UPDATED_FINAL_RESULT)
            .failureReason(UPDATED_FAILURE_REASON)
            .evaluationComment(UPDATED_EVALUATION_COMMENT)
            .isFollowup(UPDATED_IS_FOLLOWUP)
            .manualOverride(UPDATED_MANUAL_OVERRIDE)
            .qcStatus(UPDATED_QC_STATUS)
            .qcDate(UPDATED_QC_DATE)
            .qcDoneBy(UPDATED_QC_DONE_BY)
            .syncedToMobile(UPDATED_SYNCED_TO_MOBILE)
            .syncedOn(UPDATED_SYNCED_ON);

        restShipmentParticipantMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipmentParticipantMap.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipmentParticipantMap))
            )
            .andExpect(status().isOk());

        // Validate the ShipmentParticipantMap in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentParticipantMapUpdatableFieldsEquals(
            partialUpdatedShipmentParticipantMap,
            getPersistedShipmentParticipantMap(partialUpdatedShipmentParticipantMap)
        );
    }

    @Test
    @Transactional
    void patchNonExistingShipmentParticipantMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentParticipantMap.setId(longCount.incrementAndGet());

        // Create the ShipmentParticipantMap
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(shipmentParticipantMap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentParticipantMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shipmentParticipantMapDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentParticipantMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentParticipantMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipmentParticipantMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentParticipantMap.setId(longCount.incrementAndGet());

        // Create the ShipmentParticipantMap
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(shipmentParticipantMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentParticipantMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentParticipantMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShipmentParticipantMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipmentParticipantMap() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipmentParticipantMap.setId(longCount.incrementAndGet());

        // Create the ShipmentParticipantMap
        ShipmentParticipantMapDTO shipmentParticipantMapDTO = shipmentParticipantMapMapper.toDto(shipmentParticipantMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentParticipantMapMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentParticipantMapDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShipmentParticipantMap in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShipmentParticipantMap() throws Exception {
        // Initialize the database
        insertedShipmentParticipantMap = shipmentParticipantMapRepository.saveAndFlush(shipmentParticipantMap);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipmentParticipantMap
        restShipmentParticipantMapMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipmentParticipantMap.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shipmentParticipantMapRepository.count();
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

    protected ShipmentParticipantMap getPersistedShipmentParticipantMap(ShipmentParticipantMap shipmentParticipantMap) {
        return shipmentParticipantMapRepository.findById(shipmentParticipantMap.getId()).orElseThrow();
    }

    protected void assertPersistedShipmentParticipantMapToMatchAllProperties(ShipmentParticipantMap expectedShipmentParticipantMap) {
        assertShipmentParticipantMapAllPropertiesEquals(
            expectedShipmentParticipantMap,
            getPersistedShipmentParticipantMap(expectedShipmentParticipantMap)
        );
    }

    protected void assertPersistedShipmentParticipantMapToMatchUpdatableProperties(ShipmentParticipantMap expectedShipmentParticipantMap) {
        assertShipmentParticipantMapAllUpdatablePropertiesEquals(
            expectedShipmentParticipantMap,
            getPersistedShipmentParticipantMap(expectedShipmentParticipantMap)
        );
    }
}
