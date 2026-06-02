package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ShipmentAsserts.*;
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
import zw.org.nmrl.ept.domain.CertificateBatch;
import zw.org.nmrl.ept.domain.Distribution;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.domain.Shipment;
import zw.org.nmrl.ept.domain.enumeration.ShipmentStatus;
import zw.org.nmrl.ept.repository.ShipmentRepository;
import zw.org.nmrl.ept.service.dto.ShipmentDTO;
import zw.org.nmrl.ept.service.mapper.ShipmentMapper;

/**
 * Integration tests for the {@link ShipmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShipmentResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_SHIPMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SHIPMENT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_SHIPMENT_DATE = LocalDate.ofEpochDay(-1L);

    private static final Instant DEFAULT_RESPONSE_DEADLINE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESPONSE_DEADLINE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_RESPONSES_OPEN = false;
    private static final Boolean UPDATED_RESPONSES_OPEN = true;

    private static final Boolean DEFAULT_AUTO_CLOSE_AT_DEADLINE = false;
    private static final Boolean UPDATED_AUTO_CLOSE_AT_DEADLINE = true;

    private static final Boolean DEFAULT_ALLOW_EDITING_RESPONSE = false;
    private static final Boolean UPDATED_ALLOW_EDITING_RESPONSE = true;

    private static final String DEFAULT_ISSUING_AUTHORITY = "AAAAAAAAAA";
    private static final String UPDATED_ISSUING_AUTHORITY = "BBBBBBBBBB";

    private static final String DEFAULT_COORDINATOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COORDINATOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COORDINATOR_EMAIL = "y\\|j@2g.-r&(t[";
    private static final String UPDATED_COORDINATOR_EMAIL = "(b]{;@Gd{msG.5";

    private static final String DEFAULT_COORDINATOR_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_COORDINATOR_PHONE = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_SAMPLES = 1;
    private static final Integer UPDATED_NUMBER_OF_SAMPLES = 2;
    private static final Integer SMALLER_NUMBER_OF_SAMPLES = 1 - 1;

    private static final Integer DEFAULT_MAX_SCORE = 1;
    private static final Integer UPDATED_MAX_SCORE = 2;
    private static final Integer SMALLER_MAX_SCORE = 1 - 1;

    private static final ShipmentStatus DEFAULT_STATUS = ShipmentStatus.DRAFT;
    private static final ShipmentStatus UPDATED_STATUS = ShipmentStatus.CONFIGURED;

    private static final String DEFAULT_ATTRIBUTES = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTES = "BBBBBBBBBB";

    private static final Instant DEFAULT_REPORTS_GENERATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REPORTS_GENERATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FINALIZED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINALIZED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/shipments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentMapper shipmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShipmentMockMvc;

    private Shipment shipment;

    private Shipment insertedShipment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createEntity(EntityManager em) {
        Shipment shipment = new Shipment()
            .code(DEFAULT_CODE)
            .shipmentDate(DEFAULT_SHIPMENT_DATE)
            .responseDeadline(DEFAULT_RESPONSE_DEADLINE)
            .responsesOpen(DEFAULT_RESPONSES_OPEN)
            .autoCloseAtDeadline(DEFAULT_AUTO_CLOSE_AT_DEADLINE)
            .allowEditingResponse(DEFAULT_ALLOW_EDITING_RESPONSE)
            .issuingAuthority(DEFAULT_ISSUING_AUTHORITY)
            .coordinatorName(DEFAULT_COORDINATOR_NAME)
            .coordinatorEmail(DEFAULT_COORDINATOR_EMAIL)
            .coordinatorPhone(DEFAULT_COORDINATOR_PHONE)
            .numberOfSamples(DEFAULT_NUMBER_OF_SAMPLES)
            .maxScore(DEFAULT_MAX_SCORE)
            .status(DEFAULT_STATUS)
            .attributes(DEFAULT_ATTRIBUTES)
            .reportsGeneratedAt(DEFAULT_REPORTS_GENERATED_AT)
            .finalizedAt(DEFAULT_FINALIZED_AT);
        // Add required entity
        Scheme scheme;
        if (TestUtil.findAll(em, Scheme.class).isEmpty()) {
            scheme = SchemeResourceIT.createEntity();
            em.persist(scheme);
            em.flush();
        } else {
            scheme = TestUtil.findAll(em, Scheme.class).get(0);
        }
        shipment.setScheme(scheme);
        return shipment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createUpdatedEntity(EntityManager em) {
        Shipment updatedShipment = new Shipment()
            .code(UPDATED_CODE)
            .shipmentDate(UPDATED_SHIPMENT_DATE)
            .responseDeadline(UPDATED_RESPONSE_DEADLINE)
            .responsesOpen(UPDATED_RESPONSES_OPEN)
            .autoCloseAtDeadline(UPDATED_AUTO_CLOSE_AT_DEADLINE)
            .allowEditingResponse(UPDATED_ALLOW_EDITING_RESPONSE)
            .issuingAuthority(UPDATED_ISSUING_AUTHORITY)
            .coordinatorName(UPDATED_COORDINATOR_NAME)
            .coordinatorEmail(UPDATED_COORDINATOR_EMAIL)
            .coordinatorPhone(UPDATED_COORDINATOR_PHONE)
            .numberOfSamples(UPDATED_NUMBER_OF_SAMPLES)
            .maxScore(UPDATED_MAX_SCORE)
            .status(UPDATED_STATUS)
            .attributes(UPDATED_ATTRIBUTES)
            .reportsGeneratedAt(UPDATED_REPORTS_GENERATED_AT)
            .finalizedAt(UPDATED_FINALIZED_AT);
        // Add required entity
        Scheme scheme;
        if (TestUtil.findAll(em, Scheme.class).isEmpty()) {
            scheme = SchemeResourceIT.createUpdatedEntity();
            em.persist(scheme);
            em.flush();
        } else {
            scheme = TestUtil.findAll(em, Scheme.class).get(0);
        }
        updatedShipment.setScheme(scheme);
        return updatedShipment;
    }

    @BeforeEach
    void initTest() {
        shipment = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedShipment != null) {
            shipmentRepository.delete(insertedShipment);
            insertedShipment = null;
        }
    }

    @Test
    @Transactional
    void createShipment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);
        var returnedShipmentDTO = om.readValue(
            restShipmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShipmentDTO.class
        );

        // Validate the Shipment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipment = shipmentMapper.toEntity(returnedShipmentDTO);
        assertShipmentUpdatableFieldsEquals(returnedShipment, getPersistedShipment(returnedShipment));

        insertedShipment = returnedShipment;
    }

    @Test
    @Transactional
    void createShipmentWithExistingId() throws Exception {
        // Create the Shipment with an existing ID
        shipment.setId(1L);
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setCode(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkShipmentDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setShipmentDate(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkResponseDeadlineIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setResponseDeadline(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setStatus(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShipments() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].shipmentDate").value(hasItem(DEFAULT_SHIPMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].responseDeadline").value(hasItem(DEFAULT_RESPONSE_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].responsesOpen").value(hasItem(DEFAULT_RESPONSES_OPEN)))
            .andExpect(jsonPath("$.[*].autoCloseAtDeadline").value(hasItem(DEFAULT_AUTO_CLOSE_AT_DEADLINE)))
            .andExpect(jsonPath("$.[*].allowEditingResponse").value(hasItem(DEFAULT_ALLOW_EDITING_RESPONSE)))
            .andExpect(jsonPath("$.[*].issuingAuthority").value(hasItem(DEFAULT_ISSUING_AUTHORITY)))
            .andExpect(jsonPath("$.[*].coordinatorName").value(hasItem(DEFAULT_COORDINATOR_NAME)))
            .andExpect(jsonPath("$.[*].coordinatorEmail").value(hasItem(DEFAULT_COORDINATOR_EMAIL)))
            .andExpect(jsonPath("$.[*].coordinatorPhone").value(hasItem(DEFAULT_COORDINATOR_PHONE)))
            .andExpect(jsonPath("$.[*].numberOfSamples").value(hasItem(DEFAULT_NUMBER_OF_SAMPLES)))
            .andExpect(jsonPath("$.[*].maxScore").value(hasItem(DEFAULT_MAX_SCORE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].attributes").value(hasItem(DEFAULT_ATTRIBUTES)))
            .andExpect(jsonPath("$.[*].reportsGeneratedAt").value(hasItem(DEFAULT_REPORTS_GENERATED_AT.toString())))
            .andExpect(jsonPath("$.[*].finalizedAt").value(hasItem(DEFAULT_FINALIZED_AT.toString())));
    }

    @Test
    @Transactional
    void getShipment() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get the shipment
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL_ID, shipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipment.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.shipmentDate").value(DEFAULT_SHIPMENT_DATE.toString()))
            .andExpect(jsonPath("$.responseDeadline").value(DEFAULT_RESPONSE_DEADLINE.toString()))
            .andExpect(jsonPath("$.responsesOpen").value(DEFAULT_RESPONSES_OPEN))
            .andExpect(jsonPath("$.autoCloseAtDeadline").value(DEFAULT_AUTO_CLOSE_AT_DEADLINE))
            .andExpect(jsonPath("$.allowEditingResponse").value(DEFAULT_ALLOW_EDITING_RESPONSE))
            .andExpect(jsonPath("$.issuingAuthority").value(DEFAULT_ISSUING_AUTHORITY))
            .andExpect(jsonPath("$.coordinatorName").value(DEFAULT_COORDINATOR_NAME))
            .andExpect(jsonPath("$.coordinatorEmail").value(DEFAULT_COORDINATOR_EMAIL))
            .andExpect(jsonPath("$.coordinatorPhone").value(DEFAULT_COORDINATOR_PHONE))
            .andExpect(jsonPath("$.numberOfSamples").value(DEFAULT_NUMBER_OF_SAMPLES))
            .andExpect(jsonPath("$.maxScore").value(DEFAULT_MAX_SCORE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.attributes").value(DEFAULT_ATTRIBUTES))
            .andExpect(jsonPath("$.reportsGeneratedAt").value(DEFAULT_REPORTS_GENERATED_AT.toString()))
            .andExpect(jsonPath("$.finalizedAt").value(DEFAULT_FINALIZED_AT.toString()));
    }

    @Test
    @Transactional
    void getShipmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        Long id = shipment.getId();

        defaultShipmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultShipmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultShipmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShipmentsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where code equals to
        defaultShipmentFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllShipmentsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where code in
        defaultShipmentFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllShipmentsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where code is not null
        defaultShipmentFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where code contains
        defaultShipmentFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllShipmentsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where code does not contain
        defaultShipmentFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllShipmentsByShipmentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where shipmentDate equals to
        defaultShipmentFiltering("shipmentDate.equals=" + DEFAULT_SHIPMENT_DATE, "shipmentDate.equals=" + UPDATED_SHIPMENT_DATE);
    }

    @Test
    @Transactional
    void getAllShipmentsByShipmentDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where shipmentDate in
        defaultShipmentFiltering(
            "shipmentDate.in=" + DEFAULT_SHIPMENT_DATE + "," + UPDATED_SHIPMENT_DATE,
            "shipmentDate.in=" + UPDATED_SHIPMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByShipmentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where shipmentDate is not null
        defaultShipmentFiltering("shipmentDate.specified=true", "shipmentDate.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByShipmentDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where shipmentDate is greater than or equal to
        defaultShipmentFiltering(
            "shipmentDate.greaterThanOrEqual=" + DEFAULT_SHIPMENT_DATE,
            "shipmentDate.greaterThanOrEqual=" + UPDATED_SHIPMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByShipmentDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where shipmentDate is less than or equal to
        defaultShipmentFiltering(
            "shipmentDate.lessThanOrEqual=" + DEFAULT_SHIPMENT_DATE,
            "shipmentDate.lessThanOrEqual=" + SMALLER_SHIPMENT_DATE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByShipmentDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where shipmentDate is less than
        defaultShipmentFiltering("shipmentDate.lessThan=" + UPDATED_SHIPMENT_DATE, "shipmentDate.lessThan=" + DEFAULT_SHIPMENT_DATE);
    }

    @Test
    @Transactional
    void getAllShipmentsByShipmentDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where shipmentDate is greater than
        defaultShipmentFiltering("shipmentDate.greaterThan=" + SMALLER_SHIPMENT_DATE, "shipmentDate.greaterThan=" + DEFAULT_SHIPMENT_DATE);
    }

    @Test
    @Transactional
    void getAllShipmentsByResponseDeadlineIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where responseDeadline equals to
        defaultShipmentFiltering(
            "responseDeadline.equals=" + DEFAULT_RESPONSE_DEADLINE,
            "responseDeadline.equals=" + UPDATED_RESPONSE_DEADLINE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByResponseDeadlineIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where responseDeadline in
        defaultShipmentFiltering(
            "responseDeadline.in=" + DEFAULT_RESPONSE_DEADLINE + "," + UPDATED_RESPONSE_DEADLINE,
            "responseDeadline.in=" + UPDATED_RESPONSE_DEADLINE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByResponseDeadlineIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where responseDeadline is not null
        defaultShipmentFiltering("responseDeadline.specified=true", "responseDeadline.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByResponsesOpenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where responsesOpen equals to
        defaultShipmentFiltering("responsesOpen.equals=" + DEFAULT_RESPONSES_OPEN, "responsesOpen.equals=" + UPDATED_RESPONSES_OPEN);
    }

    @Test
    @Transactional
    void getAllShipmentsByResponsesOpenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where responsesOpen in
        defaultShipmentFiltering(
            "responsesOpen.in=" + DEFAULT_RESPONSES_OPEN + "," + UPDATED_RESPONSES_OPEN,
            "responsesOpen.in=" + UPDATED_RESPONSES_OPEN
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByResponsesOpenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where responsesOpen is not null
        defaultShipmentFiltering("responsesOpen.specified=true", "responsesOpen.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByAutoCloseAtDeadlineIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where autoCloseAtDeadline equals to
        defaultShipmentFiltering(
            "autoCloseAtDeadline.equals=" + DEFAULT_AUTO_CLOSE_AT_DEADLINE,
            "autoCloseAtDeadline.equals=" + UPDATED_AUTO_CLOSE_AT_DEADLINE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByAutoCloseAtDeadlineIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where autoCloseAtDeadline in
        defaultShipmentFiltering(
            "autoCloseAtDeadline.in=" + DEFAULT_AUTO_CLOSE_AT_DEADLINE + "," + UPDATED_AUTO_CLOSE_AT_DEADLINE,
            "autoCloseAtDeadline.in=" + UPDATED_AUTO_CLOSE_AT_DEADLINE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByAutoCloseAtDeadlineIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where autoCloseAtDeadline is not null
        defaultShipmentFiltering("autoCloseAtDeadline.specified=true", "autoCloseAtDeadline.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByAllowEditingResponseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where allowEditingResponse equals to
        defaultShipmentFiltering(
            "allowEditingResponse.equals=" + DEFAULT_ALLOW_EDITING_RESPONSE,
            "allowEditingResponse.equals=" + UPDATED_ALLOW_EDITING_RESPONSE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByAllowEditingResponseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where allowEditingResponse in
        defaultShipmentFiltering(
            "allowEditingResponse.in=" + DEFAULT_ALLOW_EDITING_RESPONSE + "," + UPDATED_ALLOW_EDITING_RESPONSE,
            "allowEditingResponse.in=" + UPDATED_ALLOW_EDITING_RESPONSE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByAllowEditingResponseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where allowEditingResponse is not null
        defaultShipmentFiltering("allowEditingResponse.specified=true", "allowEditingResponse.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByIssuingAuthorityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where issuingAuthority equals to
        defaultShipmentFiltering(
            "issuingAuthority.equals=" + DEFAULT_ISSUING_AUTHORITY,
            "issuingAuthority.equals=" + UPDATED_ISSUING_AUTHORITY
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByIssuingAuthorityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where issuingAuthority in
        defaultShipmentFiltering(
            "issuingAuthority.in=" + DEFAULT_ISSUING_AUTHORITY + "," + UPDATED_ISSUING_AUTHORITY,
            "issuingAuthority.in=" + UPDATED_ISSUING_AUTHORITY
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByIssuingAuthorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where issuingAuthority is not null
        defaultShipmentFiltering("issuingAuthority.specified=true", "issuingAuthority.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByIssuingAuthorityContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where issuingAuthority contains
        defaultShipmentFiltering(
            "issuingAuthority.contains=" + DEFAULT_ISSUING_AUTHORITY,
            "issuingAuthority.contains=" + UPDATED_ISSUING_AUTHORITY
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByIssuingAuthorityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where issuingAuthority does not contain
        defaultShipmentFiltering(
            "issuingAuthority.doesNotContain=" + UPDATED_ISSUING_AUTHORITY,
            "issuingAuthority.doesNotContain=" + DEFAULT_ISSUING_AUTHORITY
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorName equals to
        defaultShipmentFiltering(
            "coordinatorName.equals=" + DEFAULT_COORDINATOR_NAME,
            "coordinatorName.equals=" + UPDATED_COORDINATOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorName in
        defaultShipmentFiltering(
            "coordinatorName.in=" + DEFAULT_COORDINATOR_NAME + "," + UPDATED_COORDINATOR_NAME,
            "coordinatorName.in=" + UPDATED_COORDINATOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorName is not null
        defaultShipmentFiltering("coordinatorName.specified=true", "coordinatorName.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorNameContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorName contains
        defaultShipmentFiltering(
            "coordinatorName.contains=" + DEFAULT_COORDINATOR_NAME,
            "coordinatorName.contains=" + UPDATED_COORDINATOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorName does not contain
        defaultShipmentFiltering(
            "coordinatorName.doesNotContain=" + UPDATED_COORDINATOR_NAME,
            "coordinatorName.doesNotContain=" + DEFAULT_COORDINATOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorEmail equals to
        defaultShipmentFiltering(
            "coordinatorEmail.equals=" + DEFAULT_COORDINATOR_EMAIL,
            "coordinatorEmail.equals=" + UPDATED_COORDINATOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorEmail in
        defaultShipmentFiltering(
            "coordinatorEmail.in=" + DEFAULT_COORDINATOR_EMAIL + "," + UPDATED_COORDINATOR_EMAIL,
            "coordinatorEmail.in=" + UPDATED_COORDINATOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorEmail is not null
        defaultShipmentFiltering("coordinatorEmail.specified=true", "coordinatorEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorEmail contains
        defaultShipmentFiltering(
            "coordinatorEmail.contains=" + DEFAULT_COORDINATOR_EMAIL,
            "coordinatorEmail.contains=" + UPDATED_COORDINATOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorEmail does not contain
        defaultShipmentFiltering(
            "coordinatorEmail.doesNotContain=" + UPDATED_COORDINATOR_EMAIL,
            "coordinatorEmail.doesNotContain=" + DEFAULT_COORDINATOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorPhone equals to
        defaultShipmentFiltering(
            "coordinatorPhone.equals=" + DEFAULT_COORDINATOR_PHONE,
            "coordinatorPhone.equals=" + UPDATED_COORDINATOR_PHONE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorPhone in
        defaultShipmentFiltering(
            "coordinatorPhone.in=" + DEFAULT_COORDINATOR_PHONE + "," + UPDATED_COORDINATOR_PHONE,
            "coordinatorPhone.in=" + UPDATED_COORDINATOR_PHONE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorPhone is not null
        defaultShipmentFiltering("coordinatorPhone.specified=true", "coordinatorPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorPhone contains
        defaultShipmentFiltering(
            "coordinatorPhone.contains=" + DEFAULT_COORDINATOR_PHONE,
            "coordinatorPhone.contains=" + UPDATED_COORDINATOR_PHONE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByCoordinatorPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where coordinatorPhone does not contain
        defaultShipmentFiltering(
            "coordinatorPhone.doesNotContain=" + UPDATED_COORDINATOR_PHONE,
            "coordinatorPhone.doesNotContain=" + DEFAULT_COORDINATOR_PHONE
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByNumberOfSamplesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where numberOfSamples equals to
        defaultShipmentFiltering(
            "numberOfSamples.equals=" + DEFAULT_NUMBER_OF_SAMPLES,
            "numberOfSamples.equals=" + UPDATED_NUMBER_OF_SAMPLES
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByNumberOfSamplesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where numberOfSamples in
        defaultShipmentFiltering(
            "numberOfSamples.in=" + DEFAULT_NUMBER_OF_SAMPLES + "," + UPDATED_NUMBER_OF_SAMPLES,
            "numberOfSamples.in=" + UPDATED_NUMBER_OF_SAMPLES
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByNumberOfSamplesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where numberOfSamples is not null
        defaultShipmentFiltering("numberOfSamples.specified=true", "numberOfSamples.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByNumberOfSamplesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where numberOfSamples is greater than or equal to
        defaultShipmentFiltering(
            "numberOfSamples.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_SAMPLES,
            "numberOfSamples.greaterThanOrEqual=" + UPDATED_NUMBER_OF_SAMPLES
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByNumberOfSamplesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where numberOfSamples is less than or equal to
        defaultShipmentFiltering(
            "numberOfSamples.lessThanOrEqual=" + DEFAULT_NUMBER_OF_SAMPLES,
            "numberOfSamples.lessThanOrEqual=" + SMALLER_NUMBER_OF_SAMPLES
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByNumberOfSamplesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where numberOfSamples is less than
        defaultShipmentFiltering(
            "numberOfSamples.lessThan=" + UPDATED_NUMBER_OF_SAMPLES,
            "numberOfSamples.lessThan=" + DEFAULT_NUMBER_OF_SAMPLES
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByNumberOfSamplesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where numberOfSamples is greater than
        defaultShipmentFiltering(
            "numberOfSamples.greaterThan=" + SMALLER_NUMBER_OF_SAMPLES,
            "numberOfSamples.greaterThan=" + DEFAULT_NUMBER_OF_SAMPLES
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByMaxScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where maxScore equals to
        defaultShipmentFiltering("maxScore.equals=" + DEFAULT_MAX_SCORE, "maxScore.equals=" + UPDATED_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllShipmentsByMaxScoreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where maxScore in
        defaultShipmentFiltering("maxScore.in=" + DEFAULT_MAX_SCORE + "," + UPDATED_MAX_SCORE, "maxScore.in=" + UPDATED_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllShipmentsByMaxScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where maxScore is not null
        defaultShipmentFiltering("maxScore.specified=true", "maxScore.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByMaxScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where maxScore is greater than or equal to
        defaultShipmentFiltering("maxScore.greaterThanOrEqual=" + DEFAULT_MAX_SCORE, "maxScore.greaterThanOrEqual=" + UPDATED_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllShipmentsByMaxScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where maxScore is less than or equal to
        defaultShipmentFiltering("maxScore.lessThanOrEqual=" + DEFAULT_MAX_SCORE, "maxScore.lessThanOrEqual=" + SMALLER_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllShipmentsByMaxScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where maxScore is less than
        defaultShipmentFiltering("maxScore.lessThan=" + UPDATED_MAX_SCORE, "maxScore.lessThan=" + DEFAULT_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllShipmentsByMaxScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where maxScore is greater than
        defaultShipmentFiltering("maxScore.greaterThan=" + SMALLER_MAX_SCORE, "maxScore.greaterThan=" + DEFAULT_MAX_SCORE);
    }

    @Test
    @Transactional
    void getAllShipmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where status equals to
        defaultShipmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShipmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where status in
        defaultShipmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShipmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where status is not null
        defaultShipmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByReportsGeneratedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where reportsGeneratedAt equals to
        defaultShipmentFiltering(
            "reportsGeneratedAt.equals=" + DEFAULT_REPORTS_GENERATED_AT,
            "reportsGeneratedAt.equals=" + UPDATED_REPORTS_GENERATED_AT
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByReportsGeneratedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where reportsGeneratedAt in
        defaultShipmentFiltering(
            "reportsGeneratedAt.in=" + DEFAULT_REPORTS_GENERATED_AT + "," + UPDATED_REPORTS_GENERATED_AT,
            "reportsGeneratedAt.in=" + UPDATED_REPORTS_GENERATED_AT
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByReportsGeneratedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where reportsGeneratedAt is not null
        defaultShipmentFiltering("reportsGeneratedAt.specified=true", "reportsGeneratedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByFinalizedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where finalizedAt equals to
        defaultShipmentFiltering("finalizedAt.equals=" + DEFAULT_FINALIZED_AT, "finalizedAt.equals=" + UPDATED_FINALIZED_AT);
    }

    @Test
    @Transactional
    void getAllShipmentsByFinalizedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where finalizedAt in
        defaultShipmentFiltering(
            "finalizedAt.in=" + DEFAULT_FINALIZED_AT + "," + UPDATED_FINALIZED_AT,
            "finalizedAt.in=" + UPDATED_FINALIZED_AT
        );
    }

    @Test
    @Transactional
    void getAllShipmentsByFinalizedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where finalizedAt is not null
        defaultShipmentFiltering("finalizedAt.specified=true", "finalizedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByDistributionIsEqualToSomething() throws Exception {
        Distribution distribution;
        if (TestUtil.findAll(em, Distribution.class).isEmpty()) {
            shipmentRepository.saveAndFlush(shipment);
            distribution = DistributionResourceIT.createEntity();
        } else {
            distribution = TestUtil.findAll(em, Distribution.class).get(0);
        }
        em.persist(distribution);
        em.flush();
        shipment.setDistribution(distribution);
        shipmentRepository.saveAndFlush(shipment);
        Long distributionId = distribution.getId();
        // Get all the shipmentList where distribution equals to distributionId
        defaultShipmentShouldBeFound("distributionId.equals=" + distributionId);

        // Get all the shipmentList where distribution equals to (distributionId + 1)
        defaultShipmentShouldNotBeFound("distributionId.equals=" + (distributionId + 1));
    }

    @Test
    @Transactional
    void getAllShipmentsBySchemeIsEqualToSomething() throws Exception {
        Scheme scheme;
        if (TestUtil.findAll(em, Scheme.class).isEmpty()) {
            shipmentRepository.saveAndFlush(shipment);
            scheme = SchemeResourceIT.createEntity();
        } else {
            scheme = TestUtil.findAll(em, Scheme.class).get(0);
        }
        em.persist(scheme);
        em.flush();
        shipment.setScheme(scheme);
        shipmentRepository.saveAndFlush(shipment);
        Long schemeId = scheme.getId();
        // Get all the shipmentList where scheme equals to schemeId
        defaultShipmentShouldBeFound("schemeId.equals=" + schemeId);

        // Get all the shipmentList where scheme equals to (schemeId + 1)
        defaultShipmentShouldNotBeFound("schemeId.equals=" + (schemeId + 1));
    }

    @Test
    @Transactional
    void getAllShipmentsByCertificateBatchesIsEqualToSomething() throws Exception {
        CertificateBatch certificateBatches;
        if (TestUtil.findAll(em, CertificateBatch.class).isEmpty()) {
            shipmentRepository.saveAndFlush(shipment);
            certificateBatches = CertificateBatchResourceIT.createEntity();
        } else {
            certificateBatches = TestUtil.findAll(em, CertificateBatch.class).get(0);
        }
        em.persist(certificateBatches);
        em.flush();
        shipment.addCertificateBatches(certificateBatches);
        shipmentRepository.saveAndFlush(shipment);
        Long certificateBatchesId = certificateBatches.getId();
        // Get all the shipmentList where certificateBatches equals to certificateBatchesId
        defaultShipmentShouldBeFound("certificateBatchesId.equals=" + certificateBatchesId);

        // Get all the shipmentList where certificateBatches equals to (certificateBatchesId + 1)
        defaultShipmentShouldNotBeFound("certificateBatchesId.equals=" + (certificateBatchesId + 1));
    }

    private void defaultShipmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultShipmentShouldBeFound(shouldBeFound);
        defaultShipmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShipmentShouldBeFound(String filter) throws Exception {
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].shipmentDate").value(hasItem(DEFAULT_SHIPMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].responseDeadline").value(hasItem(DEFAULT_RESPONSE_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].responsesOpen").value(hasItem(DEFAULT_RESPONSES_OPEN)))
            .andExpect(jsonPath("$.[*].autoCloseAtDeadline").value(hasItem(DEFAULT_AUTO_CLOSE_AT_DEADLINE)))
            .andExpect(jsonPath("$.[*].allowEditingResponse").value(hasItem(DEFAULT_ALLOW_EDITING_RESPONSE)))
            .andExpect(jsonPath("$.[*].issuingAuthority").value(hasItem(DEFAULT_ISSUING_AUTHORITY)))
            .andExpect(jsonPath("$.[*].coordinatorName").value(hasItem(DEFAULT_COORDINATOR_NAME)))
            .andExpect(jsonPath("$.[*].coordinatorEmail").value(hasItem(DEFAULT_COORDINATOR_EMAIL)))
            .andExpect(jsonPath("$.[*].coordinatorPhone").value(hasItem(DEFAULT_COORDINATOR_PHONE)))
            .andExpect(jsonPath("$.[*].numberOfSamples").value(hasItem(DEFAULT_NUMBER_OF_SAMPLES)))
            .andExpect(jsonPath("$.[*].maxScore").value(hasItem(DEFAULT_MAX_SCORE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].attributes").value(hasItem(DEFAULT_ATTRIBUTES)))
            .andExpect(jsonPath("$.[*].reportsGeneratedAt").value(hasItem(DEFAULT_REPORTS_GENERATED_AT.toString())))
            .andExpect(jsonPath("$.[*].finalizedAt").value(hasItem(DEFAULT_FINALIZED_AT.toString())));

        // Check, that the count call also returns 1
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShipmentShouldNotBeFound(String filter) throws Exception {
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShipment() throws Exception {
        // Get the shipment
        restShipmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShipment() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment
        Shipment updatedShipment = shipmentRepository.findById(shipment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipment are not directly saved in db
        em.detach(updatedShipment);
        updatedShipment
            .code(UPDATED_CODE)
            .shipmentDate(UPDATED_SHIPMENT_DATE)
            .responseDeadline(UPDATED_RESPONSE_DEADLINE)
            .responsesOpen(UPDATED_RESPONSES_OPEN)
            .autoCloseAtDeadline(UPDATED_AUTO_CLOSE_AT_DEADLINE)
            .allowEditingResponse(UPDATED_ALLOW_EDITING_RESPONSE)
            .issuingAuthority(UPDATED_ISSUING_AUTHORITY)
            .coordinatorName(UPDATED_COORDINATOR_NAME)
            .coordinatorEmail(UPDATED_COORDINATOR_EMAIL)
            .coordinatorPhone(UPDATED_COORDINATOR_PHONE)
            .numberOfSamples(UPDATED_NUMBER_OF_SAMPLES)
            .maxScore(UPDATED_MAX_SCORE)
            .status(UPDATED_STATUS)
            .attributes(UPDATED_ATTRIBUTES)
            .reportsGeneratedAt(UPDATED_REPORTS_GENERATED_AT)
            .finalizedAt(UPDATED_FINALIZED_AT);
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(updatedShipment);

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentToMatchAllProperties(updatedShipment);
    }

    @Test
    @Transactional
    void putNonExistingShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShipmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment using partial update
        Shipment partialUpdatedShipment = new Shipment();
        partialUpdatedShipment.setId(shipment.getId());

        partialUpdatedShipment
            .shipmentDate(UPDATED_SHIPMENT_DATE)
            .responseDeadline(UPDATED_RESPONSE_DEADLINE)
            .autoCloseAtDeadline(UPDATED_AUTO_CLOSE_AT_DEADLINE)
            .allowEditingResponse(UPDATED_ALLOW_EDITING_RESPONSE)
            .issuingAuthority(UPDATED_ISSUING_AUTHORITY)
            .coordinatorName(UPDATED_COORDINATOR_NAME)
            .maxScore(UPDATED_MAX_SCORE)
            .attributes(UPDATED_ATTRIBUTES);

        restShipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipment))
            )
            .andExpect(status().isOk());

        // Validate the Shipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedShipment, shipment), getPersistedShipment(shipment));
    }

    @Test
    @Transactional
    void fullUpdateShipmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment using partial update
        Shipment partialUpdatedShipment = new Shipment();
        partialUpdatedShipment.setId(shipment.getId());

        partialUpdatedShipment
            .code(UPDATED_CODE)
            .shipmentDate(UPDATED_SHIPMENT_DATE)
            .responseDeadline(UPDATED_RESPONSE_DEADLINE)
            .responsesOpen(UPDATED_RESPONSES_OPEN)
            .autoCloseAtDeadline(UPDATED_AUTO_CLOSE_AT_DEADLINE)
            .allowEditingResponse(UPDATED_ALLOW_EDITING_RESPONSE)
            .issuingAuthority(UPDATED_ISSUING_AUTHORITY)
            .coordinatorName(UPDATED_COORDINATOR_NAME)
            .coordinatorEmail(UPDATED_COORDINATOR_EMAIL)
            .coordinatorPhone(UPDATED_COORDINATOR_PHONE)
            .numberOfSamples(UPDATED_NUMBER_OF_SAMPLES)
            .maxScore(UPDATED_MAX_SCORE)
            .status(UPDATED_STATUS)
            .attributes(UPDATED_ATTRIBUTES)
            .reportsGeneratedAt(UPDATED_REPORTS_GENERATED_AT)
            .finalizedAt(UPDATED_FINALIZED_AT);

        restShipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipment))
            )
            .andExpect(status().isOk());

        // Validate the Shipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentUpdatableFieldsEquals(partialUpdatedShipment, getPersistedShipment(partialUpdatedShipment));
    }

    @Test
    @Transactional
    void patchNonExistingShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shipmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShipment() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipment
        restShipmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shipmentRepository.count();
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

    protected Shipment getPersistedShipment(Shipment shipment) {
        return shipmentRepository.findById(shipment.getId()).orElseThrow();
    }

    protected void assertPersistedShipmentToMatchAllProperties(Shipment expectedShipment) {
        assertShipmentAllPropertiesEquals(expectedShipment, getPersistedShipment(expectedShipment));
    }

    protected void assertPersistedShipmentToMatchUpdatableProperties(Shipment expectedShipment) {
        assertShipmentAllUpdatablePropertiesEquals(expectedShipment, getPersistedShipment(expectedShipment));
    }
}
