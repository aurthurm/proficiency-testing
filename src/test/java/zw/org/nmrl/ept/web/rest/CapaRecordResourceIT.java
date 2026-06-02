package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.CapaRecordAsserts.*;
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
import zw.org.nmrl.ept.domain.CapaRecord;
import zw.org.nmrl.ept.domain.CorrectiveAction;
import zw.org.nmrl.ept.domain.ShipmentParticipantMap;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.CapaRecordRepository;
import zw.org.nmrl.ept.service.dto.CapaRecordDTO;
import zw.org.nmrl.ept.service.mapper.CapaRecordMapper;

/**
 * Integration tests for the {@link CapaRecordResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CapaRecordResourceIT {

    private static final String DEFAULT_ROOT_CAUSE = "AAAAAAAAAA";
    private static final String UPDATED_ROOT_CAUSE = "BBBBBBBBBB";

    private static final String DEFAULT_ACTION_TAKEN = "AAAAAAAAAA";
    private static final String UPDATED_ACTION_TAKEN = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ACTION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ACTION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ACTION_DATE = LocalDate.ofEpochDay(-1L);

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final LocalDate DEFAULT_FOLLOW_UP_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FOLLOW_UP_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_FOLLOW_UP_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/capa-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CapaRecordRepository capaRecordRepository;

    @Autowired
    private CapaRecordMapper capaRecordMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCapaRecordMockMvc;

    private CapaRecord capaRecord;

    private CapaRecord insertedCapaRecord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CapaRecord createEntity(EntityManager em) {
        CapaRecord capaRecord = new CapaRecord()
            .rootCause(DEFAULT_ROOT_CAUSE)
            .actionTaken(DEFAULT_ACTION_TAKEN)
            .actionDate(DEFAULT_ACTION_DATE)
            .status(DEFAULT_STATUS)
            .followUpDate(DEFAULT_FOLLOW_UP_DATE);
        // Add required entity
        ShipmentParticipantMap shipmentParticipantMap;
        if (TestUtil.findAll(em, ShipmentParticipantMap.class).isEmpty()) {
            shipmentParticipantMap = ShipmentParticipantMapResourceIT.createEntity(em);
            em.persist(shipmentParticipantMap);
            em.flush();
        } else {
            shipmentParticipantMap = TestUtil.findAll(em, ShipmentParticipantMap.class).get(0);
        }
        capaRecord.setShipmentParticipantMap(shipmentParticipantMap);
        return capaRecord;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CapaRecord createUpdatedEntity(EntityManager em) {
        CapaRecord updatedCapaRecord = new CapaRecord()
            .rootCause(UPDATED_ROOT_CAUSE)
            .actionTaken(UPDATED_ACTION_TAKEN)
            .actionDate(UPDATED_ACTION_DATE)
            .status(UPDATED_STATUS)
            .followUpDate(UPDATED_FOLLOW_UP_DATE);
        // Add required entity
        ShipmentParticipantMap shipmentParticipantMap;
        if (TestUtil.findAll(em, ShipmentParticipantMap.class).isEmpty()) {
            shipmentParticipantMap = ShipmentParticipantMapResourceIT.createUpdatedEntity(em);
            em.persist(shipmentParticipantMap);
            em.flush();
        } else {
            shipmentParticipantMap = TestUtil.findAll(em, ShipmentParticipantMap.class).get(0);
        }
        updatedCapaRecord.setShipmentParticipantMap(shipmentParticipantMap);
        return updatedCapaRecord;
    }

    @BeforeEach
    void initTest() {
        capaRecord = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCapaRecord != null) {
            capaRecordRepository.delete(insertedCapaRecord);
            insertedCapaRecord = null;
        }
    }

    @Test
    @Transactional
    void createCapaRecord() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CapaRecord
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(capaRecord);
        var returnedCapaRecordDTO = om.readValue(
            restCapaRecordMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(capaRecordDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CapaRecordDTO.class
        );

        // Validate the CapaRecord in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCapaRecord = capaRecordMapper.toEntity(returnedCapaRecordDTO);
        assertCapaRecordUpdatableFieldsEquals(returnedCapaRecord, getPersistedCapaRecord(returnedCapaRecord));

        insertedCapaRecord = returnedCapaRecord;
    }

    @Test
    @Transactional
    void createCapaRecordWithExistingId() throws Exception {
        // Create the CapaRecord with an existing ID
        capaRecord.setId(1L);
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(capaRecord);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCapaRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(capaRecordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CapaRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCapaRecords() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList
        restCapaRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capaRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].rootCause").value(hasItem(DEFAULT_ROOT_CAUSE)))
            .andExpect(jsonPath("$.[*].actionTaken").value(hasItem(DEFAULT_ACTION_TAKEN)))
            .andExpect(jsonPath("$.[*].actionDate").value(hasItem(DEFAULT_ACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].followUpDate").value(hasItem(DEFAULT_FOLLOW_UP_DATE.toString())));
    }

    @Test
    @Transactional
    void getCapaRecord() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get the capaRecord
        restCapaRecordMockMvc
            .perform(get(ENTITY_API_URL_ID, capaRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(capaRecord.getId().intValue()))
            .andExpect(jsonPath("$.rootCause").value(DEFAULT_ROOT_CAUSE))
            .andExpect(jsonPath("$.actionTaken").value(DEFAULT_ACTION_TAKEN))
            .andExpect(jsonPath("$.actionDate").value(DEFAULT_ACTION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.followUpDate").value(DEFAULT_FOLLOW_UP_DATE.toString()));
    }

    @Test
    @Transactional
    void getCapaRecordsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        Long id = capaRecord.getId();

        defaultCapaRecordFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCapaRecordFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCapaRecordFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByRootCauseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where rootCause equals to
        defaultCapaRecordFiltering("rootCause.equals=" + DEFAULT_ROOT_CAUSE, "rootCause.equals=" + UPDATED_ROOT_CAUSE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByRootCauseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where rootCause in
        defaultCapaRecordFiltering("rootCause.in=" + DEFAULT_ROOT_CAUSE + "," + UPDATED_ROOT_CAUSE, "rootCause.in=" + UPDATED_ROOT_CAUSE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByRootCauseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where rootCause is not null
        defaultCapaRecordFiltering("rootCause.specified=true", "rootCause.specified=false");
    }

    @Test
    @Transactional
    void getAllCapaRecordsByRootCauseContainsSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where rootCause contains
        defaultCapaRecordFiltering("rootCause.contains=" + DEFAULT_ROOT_CAUSE, "rootCause.contains=" + UPDATED_ROOT_CAUSE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByRootCauseNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where rootCause does not contain
        defaultCapaRecordFiltering("rootCause.doesNotContain=" + UPDATED_ROOT_CAUSE, "rootCause.doesNotContain=" + DEFAULT_ROOT_CAUSE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionTakenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionTaken equals to
        defaultCapaRecordFiltering("actionTaken.equals=" + DEFAULT_ACTION_TAKEN, "actionTaken.equals=" + UPDATED_ACTION_TAKEN);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionTakenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionTaken in
        defaultCapaRecordFiltering(
            "actionTaken.in=" + DEFAULT_ACTION_TAKEN + "," + UPDATED_ACTION_TAKEN,
            "actionTaken.in=" + UPDATED_ACTION_TAKEN
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionTakenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionTaken is not null
        defaultCapaRecordFiltering("actionTaken.specified=true", "actionTaken.specified=false");
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionTakenContainsSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionTaken contains
        defaultCapaRecordFiltering("actionTaken.contains=" + DEFAULT_ACTION_TAKEN, "actionTaken.contains=" + UPDATED_ACTION_TAKEN);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionTakenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionTaken does not contain
        defaultCapaRecordFiltering(
            "actionTaken.doesNotContain=" + UPDATED_ACTION_TAKEN,
            "actionTaken.doesNotContain=" + DEFAULT_ACTION_TAKEN
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionDate equals to
        defaultCapaRecordFiltering("actionDate.equals=" + DEFAULT_ACTION_DATE, "actionDate.equals=" + UPDATED_ACTION_DATE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionDate in
        defaultCapaRecordFiltering(
            "actionDate.in=" + DEFAULT_ACTION_DATE + "," + UPDATED_ACTION_DATE,
            "actionDate.in=" + UPDATED_ACTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionDate is not null
        defaultCapaRecordFiltering("actionDate.specified=true", "actionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionDate is greater than or equal to
        defaultCapaRecordFiltering(
            "actionDate.greaterThanOrEqual=" + DEFAULT_ACTION_DATE,
            "actionDate.greaterThanOrEqual=" + UPDATED_ACTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionDate is less than or equal to
        defaultCapaRecordFiltering(
            "actionDate.lessThanOrEqual=" + DEFAULT_ACTION_DATE,
            "actionDate.lessThanOrEqual=" + SMALLER_ACTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionDate is less than
        defaultCapaRecordFiltering("actionDate.lessThan=" + UPDATED_ACTION_DATE, "actionDate.lessThan=" + DEFAULT_ACTION_DATE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByActionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where actionDate is greater than
        defaultCapaRecordFiltering("actionDate.greaterThan=" + SMALLER_ACTION_DATE, "actionDate.greaterThan=" + DEFAULT_ACTION_DATE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where status equals to
        defaultCapaRecordFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where status in
        defaultCapaRecordFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where status is not null
        defaultCapaRecordFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCapaRecordsByFollowUpDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where followUpDate equals to
        defaultCapaRecordFiltering("followUpDate.equals=" + DEFAULT_FOLLOW_UP_DATE, "followUpDate.equals=" + UPDATED_FOLLOW_UP_DATE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByFollowUpDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where followUpDate in
        defaultCapaRecordFiltering(
            "followUpDate.in=" + DEFAULT_FOLLOW_UP_DATE + "," + UPDATED_FOLLOW_UP_DATE,
            "followUpDate.in=" + UPDATED_FOLLOW_UP_DATE
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByFollowUpDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where followUpDate is not null
        defaultCapaRecordFiltering("followUpDate.specified=true", "followUpDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCapaRecordsByFollowUpDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where followUpDate is greater than or equal to
        defaultCapaRecordFiltering(
            "followUpDate.greaterThanOrEqual=" + DEFAULT_FOLLOW_UP_DATE,
            "followUpDate.greaterThanOrEqual=" + UPDATED_FOLLOW_UP_DATE
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByFollowUpDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where followUpDate is less than or equal to
        defaultCapaRecordFiltering(
            "followUpDate.lessThanOrEqual=" + DEFAULT_FOLLOW_UP_DATE,
            "followUpDate.lessThanOrEqual=" + SMALLER_FOLLOW_UP_DATE
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByFollowUpDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where followUpDate is less than
        defaultCapaRecordFiltering("followUpDate.lessThan=" + UPDATED_FOLLOW_UP_DATE, "followUpDate.lessThan=" + DEFAULT_FOLLOW_UP_DATE);
    }

    @Test
    @Transactional
    void getAllCapaRecordsByFollowUpDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        // Get all the capaRecordList where followUpDate is greater than
        defaultCapaRecordFiltering(
            "followUpDate.greaterThan=" + SMALLER_FOLLOW_UP_DATE,
            "followUpDate.greaterThan=" + DEFAULT_FOLLOW_UP_DATE
        );
    }

    @Test
    @Transactional
    void getAllCapaRecordsByCorrectiveActionIsEqualToSomething() throws Exception {
        CorrectiveAction correctiveAction;
        if (TestUtil.findAll(em, CorrectiveAction.class).isEmpty()) {
            capaRecordRepository.saveAndFlush(capaRecord);
            correctiveAction = CorrectiveActionResourceIT.createEntity();
        } else {
            correctiveAction = TestUtil.findAll(em, CorrectiveAction.class).get(0);
        }
        em.persist(correctiveAction);
        em.flush();
        capaRecord.setCorrectiveAction(correctiveAction);
        capaRecordRepository.saveAndFlush(capaRecord);
        Long correctiveActionId = correctiveAction.getId();
        // Get all the capaRecordList where correctiveAction equals to correctiveActionId
        defaultCapaRecordShouldBeFound("correctiveActionId.equals=" + correctiveActionId);

        // Get all the capaRecordList where correctiveAction equals to (correctiveActionId + 1)
        defaultCapaRecordShouldNotBeFound("correctiveActionId.equals=" + (correctiveActionId + 1));
    }

    @Test
    @Transactional
    void getAllCapaRecordsByShipmentParticipantMapIsEqualToSomething() throws Exception {
        ShipmentParticipantMap shipmentParticipantMap;
        if (TestUtil.findAll(em, ShipmentParticipantMap.class).isEmpty()) {
            capaRecordRepository.saveAndFlush(capaRecord);
            shipmentParticipantMap = ShipmentParticipantMapResourceIT.createEntity(em);
        } else {
            shipmentParticipantMap = TestUtil.findAll(em, ShipmentParticipantMap.class).get(0);
        }
        em.persist(shipmentParticipantMap);
        em.flush();
        capaRecord.setShipmentParticipantMap(shipmentParticipantMap);
        capaRecordRepository.saveAndFlush(capaRecord);
        Long shipmentParticipantMapId = shipmentParticipantMap.getId();
        // Get all the capaRecordList where shipmentParticipantMap equals to shipmentParticipantMapId
        defaultCapaRecordShouldBeFound("shipmentParticipantMapId.equals=" + shipmentParticipantMapId);

        // Get all the capaRecordList where shipmentParticipantMap equals to (shipmentParticipantMapId + 1)
        defaultCapaRecordShouldNotBeFound("shipmentParticipantMapId.equals=" + (shipmentParticipantMapId + 1));
    }

    private void defaultCapaRecordFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCapaRecordShouldBeFound(shouldBeFound);
        defaultCapaRecordShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCapaRecordShouldBeFound(String filter) throws Exception {
        restCapaRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capaRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].rootCause").value(hasItem(DEFAULT_ROOT_CAUSE)))
            .andExpect(jsonPath("$.[*].actionTaken").value(hasItem(DEFAULT_ACTION_TAKEN)))
            .andExpect(jsonPath("$.[*].actionDate").value(hasItem(DEFAULT_ACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].followUpDate").value(hasItem(DEFAULT_FOLLOW_UP_DATE.toString())));

        // Check, that the count call also returns 1
        restCapaRecordMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCapaRecordShouldNotBeFound(String filter) throws Exception {
        restCapaRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCapaRecordMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCapaRecord() throws Exception {
        // Get the capaRecord
        restCapaRecordMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCapaRecord() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the capaRecord
        CapaRecord updatedCapaRecord = capaRecordRepository.findById(capaRecord.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCapaRecord are not directly saved in db
        em.detach(updatedCapaRecord);
        updatedCapaRecord
            .rootCause(UPDATED_ROOT_CAUSE)
            .actionTaken(UPDATED_ACTION_TAKEN)
            .actionDate(UPDATED_ACTION_DATE)
            .status(UPDATED_STATUS)
            .followUpDate(UPDATED_FOLLOW_UP_DATE);
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(updatedCapaRecord);

        restCapaRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, capaRecordDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(capaRecordDTO))
            )
            .andExpect(status().isOk());

        // Validate the CapaRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCapaRecordToMatchAllProperties(updatedCapaRecord);
    }

    @Test
    @Transactional
    void putNonExistingCapaRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        capaRecord.setId(longCount.incrementAndGet());

        // Create the CapaRecord
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(capaRecord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapaRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, capaRecordDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(capaRecordDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapaRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCapaRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        capaRecord.setId(longCount.incrementAndGet());

        // Create the CapaRecord
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(capaRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapaRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(capaRecordDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapaRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCapaRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        capaRecord.setId(longCount.incrementAndGet());

        // Create the CapaRecord
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(capaRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapaRecordMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(capaRecordDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CapaRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCapaRecordWithPatch() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the capaRecord using partial update
        CapaRecord partialUpdatedCapaRecord = new CapaRecord();
        partialUpdatedCapaRecord.setId(capaRecord.getId());

        partialUpdatedCapaRecord.rootCause(UPDATED_ROOT_CAUSE).status(UPDATED_STATUS);

        restCapaRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCapaRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCapaRecord))
            )
            .andExpect(status().isOk());

        // Validate the CapaRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCapaRecordUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCapaRecord, capaRecord),
            getPersistedCapaRecord(capaRecord)
        );
    }

    @Test
    @Transactional
    void fullUpdateCapaRecordWithPatch() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the capaRecord using partial update
        CapaRecord partialUpdatedCapaRecord = new CapaRecord();
        partialUpdatedCapaRecord.setId(capaRecord.getId());

        partialUpdatedCapaRecord
            .rootCause(UPDATED_ROOT_CAUSE)
            .actionTaken(UPDATED_ACTION_TAKEN)
            .actionDate(UPDATED_ACTION_DATE)
            .status(UPDATED_STATUS)
            .followUpDate(UPDATED_FOLLOW_UP_DATE);

        restCapaRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCapaRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCapaRecord))
            )
            .andExpect(status().isOk());

        // Validate the CapaRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCapaRecordUpdatableFieldsEquals(partialUpdatedCapaRecord, getPersistedCapaRecord(partialUpdatedCapaRecord));
    }

    @Test
    @Transactional
    void patchNonExistingCapaRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        capaRecord.setId(longCount.incrementAndGet());

        // Create the CapaRecord
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(capaRecord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapaRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, capaRecordDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(capaRecordDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapaRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCapaRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        capaRecord.setId(longCount.incrementAndGet());

        // Create the CapaRecord
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(capaRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapaRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(capaRecordDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CapaRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCapaRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        capaRecord.setId(longCount.incrementAndGet());

        // Create the CapaRecord
        CapaRecordDTO capaRecordDTO = capaRecordMapper.toDto(capaRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapaRecordMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(capaRecordDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CapaRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCapaRecord() throws Exception {
        // Initialize the database
        insertedCapaRecord = capaRecordRepository.saveAndFlush(capaRecord);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the capaRecord
        restCapaRecordMockMvc
            .perform(delete(ENTITY_API_URL_ID, capaRecord.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return capaRecordRepository.count();
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

    protected CapaRecord getPersistedCapaRecord(CapaRecord capaRecord) {
        return capaRecordRepository.findById(capaRecord.getId()).orElseThrow();
    }

    protected void assertPersistedCapaRecordToMatchAllProperties(CapaRecord expectedCapaRecord) {
        assertCapaRecordAllPropertiesEquals(expectedCapaRecord, getPersistedCapaRecord(expectedCapaRecord));
    }

    protected void assertPersistedCapaRecordToMatchUpdatableProperties(CapaRecord expectedCapaRecord) {
        assertCapaRecordAllUpdatablePropertiesEquals(expectedCapaRecord, getPersistedCapaRecord(expectedCapaRecord));
    }
}
