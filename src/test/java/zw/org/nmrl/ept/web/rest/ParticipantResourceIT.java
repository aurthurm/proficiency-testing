package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ParticipantAsserts.*;
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
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.enumeration.Status;
import zw.org.nmrl.ept.repository.ParticipantRepository;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.mapper.ParticipantMapper;

/**
 * Integration tests for the {@link ParticipantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParticipantResourceIT {

    private static final String DEFAULT_UNIQUE_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_UNIQUE_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_INSTITUTE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INSTITUTE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTMENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "a@4dI.\"6.#D";
    private static final String UPDATED_EMAIL = "l-lk^@x.\"";

    private static final String DEFAULT_ADDITIONAL_EMAIL = "38q@)&_T(;.~";
    private static final String UPDATED_ADDITIONAL_EMAIL = "=u@G22xD.9:qpP";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_SHIPPING_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SHIPPING_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_DISTRICT = "AAAAAAAAAA";
    private static final String UPDATED_DISTRICT = "BBBBBBBBBB";

    private static final String DEFAULT_ZIP = "AAAAAAAAAA";
    private static final String UPDATED_ZIP = "BBBBBBBBBB";

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    private static final String DEFAULT_AFFILIATION = "AAAAAAAAAA";
    private static final String UPDATED_AFFILIATION = "BBBBBBBBBB";

    private static final String DEFAULT_NETWORK_TIER = "AAAAAAAAAA";
    private static final String UPDATED_NETWORK_TIER = "BBBBBBBBBB";

    private static final String DEFAULT_SITE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SITE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_FUNDING_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_FUNDING_SOURCE = "BBBBBBBBBB";

    private static final Long DEFAULT_TESTING_VOLUME = 1L;
    private static final Long UPDATED_TESTING_VOLUME = 2L;
    private static final Long SMALLER_TESTING_VOLUME = 1L - 1L;

    private static final String DEFAULT_PEPFAR_ID = "AAAAAAAAAA";
    private static final String UPDATED_PEPFAR_ID = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;
    private static final Double SMALLER_LATITUDE = 1D - 1D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;
    private static final Double SMALLER_LONGITUDE = 1D - 1D;

    private static final String DEFAULT_LAB_DIRECTOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAB_DIRECTOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAB_DIRECTOR_EMAIL = "1=L29@\\b.nH(";
    private static final String UPDATED_LAB_DIRECTOR_EMAIL = "{yx,\\@(.8E";

    private static final String DEFAULT_CONTACT_PERSON_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_PERSON_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_PERSON_EMAIL = "\\@,.4KKLM";
    private static final String UPDATED_CONTACT_PERSON_EMAIL = "{}V1+@o9d`9.r}<+9y";

    private static final String DEFAULT_CONTACT_PERSON_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_PERSON_PHONE = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/participants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ParticipantMapper participantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParticipantMockMvc;

    private Participant participant;

    private Participant insertedParticipant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participant createEntity() {
        return new Participant()
            .uniqueIdentifier(DEFAULT_UNIQUE_IDENTIFIER)
            .instituteName(DEFAULT_INSTITUTE_NAME)
            .departmentName(DEFAULT_DEPARTMENT_NAME)
            .email(DEFAULT_EMAIL)
            .additionalEmail(DEFAULT_ADDITIONAL_EMAIL)
            .address(DEFAULT_ADDRESS)
            .shippingAddress(DEFAULT_SHIPPING_ADDRESS)
            .city(DEFAULT_CITY)
            .state(DEFAULT_STATE)
            .district(DEFAULT_DISTRICT)
            .zip(DEFAULT_ZIP)
            .region(DEFAULT_REGION)
            .phone(DEFAULT_PHONE)
            .mobile(DEFAULT_MOBILE)
            .affiliation(DEFAULT_AFFILIATION)
            .networkTier(DEFAULT_NETWORK_TIER)
            .siteType(DEFAULT_SITE_TYPE)
            .fundingSource(DEFAULT_FUNDING_SOURCE)
            .testingVolume(DEFAULT_TESTING_VOLUME)
            .pepfarId(DEFAULT_PEPFAR_ID)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .labDirectorName(DEFAULT_LAB_DIRECTOR_NAME)
            .labDirectorEmail(DEFAULT_LAB_DIRECTOR_EMAIL)
            .contactPersonName(DEFAULT_CONTACT_PERSON_NAME)
            .contactPersonEmail(DEFAULT_CONTACT_PERSON_EMAIL)
            .contactPersonPhone(DEFAULT_CONTACT_PERSON_PHONE)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participant createUpdatedEntity() {
        return new Participant()
            .uniqueIdentifier(UPDATED_UNIQUE_IDENTIFIER)
            .instituteName(UPDATED_INSTITUTE_NAME)
            .departmentName(UPDATED_DEPARTMENT_NAME)
            .email(UPDATED_EMAIL)
            .additionalEmail(UPDATED_ADDITIONAL_EMAIL)
            .address(UPDATED_ADDRESS)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .district(UPDATED_DISTRICT)
            .zip(UPDATED_ZIP)
            .region(UPDATED_REGION)
            .phone(UPDATED_PHONE)
            .mobile(UPDATED_MOBILE)
            .affiliation(UPDATED_AFFILIATION)
            .networkTier(UPDATED_NETWORK_TIER)
            .siteType(UPDATED_SITE_TYPE)
            .fundingSource(UPDATED_FUNDING_SOURCE)
            .testingVolume(UPDATED_TESTING_VOLUME)
            .pepfarId(UPDATED_PEPFAR_ID)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .labDirectorName(UPDATED_LAB_DIRECTOR_NAME)
            .labDirectorEmail(UPDATED_LAB_DIRECTOR_EMAIL)
            .contactPersonName(UPDATED_CONTACT_PERSON_NAME)
            .contactPersonEmail(UPDATED_CONTACT_PERSON_EMAIL)
            .contactPersonPhone(UPDATED_CONTACT_PERSON_PHONE)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        participant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedParticipant != null) {
            participantRepository.delete(insertedParticipant);
            insertedParticipant = null;
        }
    }

    @Test
    @Transactional
    void createParticipant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);
        var returnedParticipantDTO = om.readValue(
            restParticipantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParticipantDTO.class
        );

        // Validate the Participant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParticipant = participantMapper.toEntity(returnedParticipantDTO);
        assertParticipantUpdatableFieldsEquals(returnedParticipant, getPersistedParticipant(returnedParticipant));

        insertedParticipant = returnedParticipant;
    }

    @Test
    @Transactional
    void createParticipantWithExistingId() throws Exception {
        // Create the Participant with an existing ID
        participant.setId(1L);
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUniqueIdentifierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        participant.setUniqueIdentifier(null);

        // Create the Participant, which fails.
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        restParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInstituteNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        participant.setInstituteName(null);

        // Create the Participant, which fails.
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        restParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        participant.setEmail(null);

        // Create the Participant, which fails.
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        restParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAffiliationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        participant.setAffiliation(null);

        // Create the Participant, which fails.
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        restParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        participant.setStatus(null);

        // Create the Participant, which fails.
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        restParticipantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParticipants() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList
        restParticipantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].uniqueIdentifier").value(hasItem(DEFAULT_UNIQUE_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].instituteName").value(hasItem(DEFAULT_INSTITUTE_NAME)))
            .andExpect(jsonPath("$.[*].departmentName").value(hasItem(DEFAULT_DEPARTMENT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].additionalEmail").value(hasItem(DEFAULT_ADDITIONAL_EMAIL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].shippingAddress").value(hasItem(DEFAULT_SHIPPING_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].district").value(hasItem(DEFAULT_DISTRICT)))
            .andExpect(jsonPath("$.[*].zip").value(hasItem(DEFAULT_ZIP)))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE)))
            .andExpect(jsonPath("$.[*].affiliation").value(hasItem(DEFAULT_AFFILIATION)))
            .andExpect(jsonPath("$.[*].networkTier").value(hasItem(DEFAULT_NETWORK_TIER)))
            .andExpect(jsonPath("$.[*].siteType").value(hasItem(DEFAULT_SITE_TYPE)))
            .andExpect(jsonPath("$.[*].fundingSource").value(hasItem(DEFAULT_FUNDING_SOURCE)))
            .andExpect(jsonPath("$.[*].testingVolume").value(hasItem(DEFAULT_TESTING_VOLUME.intValue())))
            .andExpect(jsonPath("$.[*].pepfarId").value(hasItem(DEFAULT_PEPFAR_ID)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].labDirectorName").value(hasItem(DEFAULT_LAB_DIRECTOR_NAME)))
            .andExpect(jsonPath("$.[*].labDirectorEmail").value(hasItem(DEFAULT_LAB_DIRECTOR_EMAIL)))
            .andExpect(jsonPath("$.[*].contactPersonName").value(hasItem(DEFAULT_CONTACT_PERSON_NAME)))
            .andExpect(jsonPath("$.[*].contactPersonEmail").value(hasItem(DEFAULT_CONTACT_PERSON_EMAIL)))
            .andExpect(jsonPath("$.[*].contactPersonPhone").value(hasItem(DEFAULT_CONTACT_PERSON_PHONE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getParticipant() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get the participant
        restParticipantMockMvc
            .perform(get(ENTITY_API_URL_ID, participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participant.getId().intValue()))
            .andExpect(jsonPath("$.uniqueIdentifier").value(DEFAULT_UNIQUE_IDENTIFIER))
            .andExpect(jsonPath("$.instituteName").value(DEFAULT_INSTITUTE_NAME))
            .andExpect(jsonPath("$.departmentName").value(DEFAULT_DEPARTMENT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.additionalEmail").value(DEFAULT_ADDITIONAL_EMAIL))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.shippingAddress").value(DEFAULT_SHIPPING_ADDRESS))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.district").value(DEFAULT_DISTRICT))
            .andExpect(jsonPath("$.zip").value(DEFAULT_ZIP))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE))
            .andExpect(jsonPath("$.affiliation").value(DEFAULT_AFFILIATION))
            .andExpect(jsonPath("$.networkTier").value(DEFAULT_NETWORK_TIER))
            .andExpect(jsonPath("$.siteType").value(DEFAULT_SITE_TYPE))
            .andExpect(jsonPath("$.fundingSource").value(DEFAULT_FUNDING_SOURCE))
            .andExpect(jsonPath("$.testingVolume").value(DEFAULT_TESTING_VOLUME.intValue()))
            .andExpect(jsonPath("$.pepfarId").value(DEFAULT_PEPFAR_ID))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.labDirectorName").value(DEFAULT_LAB_DIRECTOR_NAME))
            .andExpect(jsonPath("$.labDirectorEmail").value(DEFAULT_LAB_DIRECTOR_EMAIL))
            .andExpect(jsonPath("$.contactPersonName").value(DEFAULT_CONTACT_PERSON_NAME))
            .andExpect(jsonPath("$.contactPersonEmail").value(DEFAULT_CONTACT_PERSON_EMAIL))
            .andExpect(jsonPath("$.contactPersonPhone").value(DEFAULT_CONTACT_PERSON_PHONE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getParticipantsByIdFiltering() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        Long id = participant.getId();

        defaultParticipantFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultParticipantFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultParticipantFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllParticipantsByUniqueIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where uniqueIdentifier equals to
        defaultParticipantFiltering(
            "uniqueIdentifier.equals=" + DEFAULT_UNIQUE_IDENTIFIER,
            "uniqueIdentifier.equals=" + UPDATED_UNIQUE_IDENTIFIER
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByUniqueIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where uniqueIdentifier in
        defaultParticipantFiltering(
            "uniqueIdentifier.in=" + DEFAULT_UNIQUE_IDENTIFIER + "," + UPDATED_UNIQUE_IDENTIFIER,
            "uniqueIdentifier.in=" + UPDATED_UNIQUE_IDENTIFIER
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByUniqueIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where uniqueIdentifier is not null
        defaultParticipantFiltering("uniqueIdentifier.specified=true", "uniqueIdentifier.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByUniqueIdentifierContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where uniqueIdentifier contains
        defaultParticipantFiltering(
            "uniqueIdentifier.contains=" + DEFAULT_UNIQUE_IDENTIFIER,
            "uniqueIdentifier.contains=" + UPDATED_UNIQUE_IDENTIFIER
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByUniqueIdentifierNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where uniqueIdentifier does not contain
        defaultParticipantFiltering(
            "uniqueIdentifier.doesNotContain=" + UPDATED_UNIQUE_IDENTIFIER,
            "uniqueIdentifier.doesNotContain=" + DEFAULT_UNIQUE_IDENTIFIER
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByInstituteNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where instituteName equals to
        defaultParticipantFiltering("instituteName.equals=" + DEFAULT_INSTITUTE_NAME, "instituteName.equals=" + UPDATED_INSTITUTE_NAME);
    }

    @Test
    @Transactional
    void getAllParticipantsByInstituteNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where instituteName in
        defaultParticipantFiltering(
            "instituteName.in=" + DEFAULT_INSTITUTE_NAME + "," + UPDATED_INSTITUTE_NAME,
            "instituteName.in=" + UPDATED_INSTITUTE_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByInstituteNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where instituteName is not null
        defaultParticipantFiltering("instituteName.specified=true", "instituteName.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByInstituteNameContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where instituteName contains
        defaultParticipantFiltering("instituteName.contains=" + DEFAULT_INSTITUTE_NAME, "instituteName.contains=" + UPDATED_INSTITUTE_NAME);
    }

    @Test
    @Transactional
    void getAllParticipantsByInstituteNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where instituteName does not contain
        defaultParticipantFiltering(
            "instituteName.doesNotContain=" + UPDATED_INSTITUTE_NAME,
            "instituteName.doesNotContain=" + DEFAULT_INSTITUTE_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByDepartmentNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where departmentName equals to
        defaultParticipantFiltering("departmentName.equals=" + DEFAULT_DEPARTMENT_NAME, "departmentName.equals=" + UPDATED_DEPARTMENT_NAME);
    }

    @Test
    @Transactional
    void getAllParticipantsByDepartmentNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where departmentName in
        defaultParticipantFiltering(
            "departmentName.in=" + DEFAULT_DEPARTMENT_NAME + "," + UPDATED_DEPARTMENT_NAME,
            "departmentName.in=" + UPDATED_DEPARTMENT_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByDepartmentNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where departmentName is not null
        defaultParticipantFiltering("departmentName.specified=true", "departmentName.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByDepartmentNameContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where departmentName contains
        defaultParticipantFiltering(
            "departmentName.contains=" + DEFAULT_DEPARTMENT_NAME,
            "departmentName.contains=" + UPDATED_DEPARTMENT_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByDepartmentNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where departmentName does not contain
        defaultParticipantFiltering(
            "departmentName.doesNotContain=" + UPDATED_DEPARTMENT_NAME,
            "departmentName.doesNotContain=" + DEFAULT_DEPARTMENT_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where email equals to
        defaultParticipantFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllParticipantsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where email in
        defaultParticipantFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllParticipantsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where email is not null
        defaultParticipantFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where email contains
        defaultParticipantFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllParticipantsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where email does not contain
        defaultParticipantFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllParticipantsByAdditionalEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalEmail equals to
        defaultParticipantFiltering(
            "additionalEmail.equals=" + DEFAULT_ADDITIONAL_EMAIL,
            "additionalEmail.equals=" + UPDATED_ADDITIONAL_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByAdditionalEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalEmail in
        defaultParticipantFiltering(
            "additionalEmail.in=" + DEFAULT_ADDITIONAL_EMAIL + "," + UPDATED_ADDITIONAL_EMAIL,
            "additionalEmail.in=" + UPDATED_ADDITIONAL_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByAdditionalEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalEmail is not null
        defaultParticipantFiltering("additionalEmail.specified=true", "additionalEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByAdditionalEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalEmail contains
        defaultParticipantFiltering(
            "additionalEmail.contains=" + DEFAULT_ADDITIONAL_EMAIL,
            "additionalEmail.contains=" + UPDATED_ADDITIONAL_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByAdditionalEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalEmail does not contain
        defaultParticipantFiltering(
            "additionalEmail.doesNotContain=" + UPDATED_ADDITIONAL_EMAIL,
            "additionalEmail.doesNotContain=" + DEFAULT_ADDITIONAL_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where address equals to
        defaultParticipantFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllParticipantsByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where address in
        defaultParticipantFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllParticipantsByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where address is not null
        defaultParticipantFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where address contains
        defaultParticipantFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllParticipantsByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where address does not contain
        defaultParticipantFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllParticipantsByShippingAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where shippingAddress equals to
        defaultParticipantFiltering(
            "shippingAddress.equals=" + DEFAULT_SHIPPING_ADDRESS,
            "shippingAddress.equals=" + UPDATED_SHIPPING_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByShippingAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where shippingAddress in
        defaultParticipantFiltering(
            "shippingAddress.in=" + DEFAULT_SHIPPING_ADDRESS + "," + UPDATED_SHIPPING_ADDRESS,
            "shippingAddress.in=" + UPDATED_SHIPPING_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByShippingAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where shippingAddress is not null
        defaultParticipantFiltering("shippingAddress.specified=true", "shippingAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByShippingAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where shippingAddress contains
        defaultParticipantFiltering(
            "shippingAddress.contains=" + DEFAULT_SHIPPING_ADDRESS,
            "shippingAddress.contains=" + UPDATED_SHIPPING_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByShippingAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where shippingAddress does not contain
        defaultParticipantFiltering(
            "shippingAddress.doesNotContain=" + UPDATED_SHIPPING_ADDRESS,
            "shippingAddress.doesNotContain=" + DEFAULT_SHIPPING_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where city equals to
        defaultParticipantFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllParticipantsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where city in
        defaultParticipantFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllParticipantsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where city is not null
        defaultParticipantFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByCityContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where city contains
        defaultParticipantFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllParticipantsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where city does not contain
        defaultParticipantFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    @Transactional
    void getAllParticipantsByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where state equals to
        defaultParticipantFiltering("state.equals=" + DEFAULT_STATE, "state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllParticipantsByStateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where state in
        defaultParticipantFiltering("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE, "state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllParticipantsByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where state is not null
        defaultParticipantFiltering("state.specified=true", "state.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByStateContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where state contains
        defaultParticipantFiltering("state.contains=" + DEFAULT_STATE, "state.contains=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllParticipantsByStateNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where state does not contain
        defaultParticipantFiltering("state.doesNotContain=" + UPDATED_STATE, "state.doesNotContain=" + DEFAULT_STATE);
    }

    @Test
    @Transactional
    void getAllParticipantsByDistrictIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where district equals to
        defaultParticipantFiltering("district.equals=" + DEFAULT_DISTRICT, "district.equals=" + UPDATED_DISTRICT);
    }

    @Test
    @Transactional
    void getAllParticipantsByDistrictIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where district in
        defaultParticipantFiltering("district.in=" + DEFAULT_DISTRICT + "," + UPDATED_DISTRICT, "district.in=" + UPDATED_DISTRICT);
    }

    @Test
    @Transactional
    void getAllParticipantsByDistrictIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where district is not null
        defaultParticipantFiltering("district.specified=true", "district.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByDistrictContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where district contains
        defaultParticipantFiltering("district.contains=" + DEFAULT_DISTRICT, "district.contains=" + UPDATED_DISTRICT);
    }

    @Test
    @Transactional
    void getAllParticipantsByDistrictNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where district does not contain
        defaultParticipantFiltering("district.doesNotContain=" + UPDATED_DISTRICT, "district.doesNotContain=" + DEFAULT_DISTRICT);
    }

    @Test
    @Transactional
    void getAllParticipantsByZipIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where zip equals to
        defaultParticipantFiltering("zip.equals=" + DEFAULT_ZIP, "zip.equals=" + UPDATED_ZIP);
    }

    @Test
    @Transactional
    void getAllParticipantsByZipIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where zip in
        defaultParticipantFiltering("zip.in=" + DEFAULT_ZIP + "," + UPDATED_ZIP, "zip.in=" + UPDATED_ZIP);
    }

    @Test
    @Transactional
    void getAllParticipantsByZipIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where zip is not null
        defaultParticipantFiltering("zip.specified=true", "zip.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByZipContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where zip contains
        defaultParticipantFiltering("zip.contains=" + DEFAULT_ZIP, "zip.contains=" + UPDATED_ZIP);
    }

    @Test
    @Transactional
    void getAllParticipantsByZipNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where zip does not contain
        defaultParticipantFiltering("zip.doesNotContain=" + UPDATED_ZIP, "zip.doesNotContain=" + DEFAULT_ZIP);
    }

    @Test
    @Transactional
    void getAllParticipantsByRegionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where region equals to
        defaultParticipantFiltering("region.equals=" + DEFAULT_REGION, "region.equals=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllParticipantsByRegionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where region in
        defaultParticipantFiltering("region.in=" + DEFAULT_REGION + "," + UPDATED_REGION, "region.in=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllParticipantsByRegionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where region is not null
        defaultParticipantFiltering("region.specified=true", "region.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByRegionContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where region contains
        defaultParticipantFiltering("region.contains=" + DEFAULT_REGION, "region.contains=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllParticipantsByRegionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where region does not contain
        defaultParticipantFiltering("region.doesNotContain=" + UPDATED_REGION, "region.doesNotContain=" + DEFAULT_REGION);
    }

    @Test
    @Transactional
    void getAllParticipantsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where phone equals to
        defaultParticipantFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllParticipantsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where phone in
        defaultParticipantFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllParticipantsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where phone is not null
        defaultParticipantFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where phone contains
        defaultParticipantFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllParticipantsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where phone does not contain
        defaultParticipantFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllParticipantsByMobileIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where mobile equals to
        defaultParticipantFiltering("mobile.equals=" + DEFAULT_MOBILE, "mobile.equals=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void getAllParticipantsByMobileIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where mobile in
        defaultParticipantFiltering("mobile.in=" + DEFAULT_MOBILE + "," + UPDATED_MOBILE, "mobile.in=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void getAllParticipantsByMobileIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where mobile is not null
        defaultParticipantFiltering("mobile.specified=true", "mobile.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByMobileContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where mobile contains
        defaultParticipantFiltering("mobile.contains=" + DEFAULT_MOBILE, "mobile.contains=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void getAllParticipantsByMobileNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where mobile does not contain
        defaultParticipantFiltering("mobile.doesNotContain=" + UPDATED_MOBILE, "mobile.doesNotContain=" + DEFAULT_MOBILE);
    }

    @Test
    @Transactional
    void getAllParticipantsByAffiliationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where affiliation equals to
        defaultParticipantFiltering("affiliation.equals=" + DEFAULT_AFFILIATION, "affiliation.equals=" + UPDATED_AFFILIATION);
    }

    @Test
    @Transactional
    void getAllParticipantsByAffiliationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where affiliation in
        defaultParticipantFiltering(
            "affiliation.in=" + DEFAULT_AFFILIATION + "," + UPDATED_AFFILIATION,
            "affiliation.in=" + UPDATED_AFFILIATION
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByAffiliationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where affiliation is not null
        defaultParticipantFiltering("affiliation.specified=true", "affiliation.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByAffiliationContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where affiliation contains
        defaultParticipantFiltering("affiliation.contains=" + DEFAULT_AFFILIATION, "affiliation.contains=" + UPDATED_AFFILIATION);
    }

    @Test
    @Transactional
    void getAllParticipantsByAffiliationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where affiliation does not contain
        defaultParticipantFiltering(
            "affiliation.doesNotContain=" + UPDATED_AFFILIATION,
            "affiliation.doesNotContain=" + DEFAULT_AFFILIATION
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByNetworkTierIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where networkTier equals to
        defaultParticipantFiltering("networkTier.equals=" + DEFAULT_NETWORK_TIER, "networkTier.equals=" + UPDATED_NETWORK_TIER);
    }

    @Test
    @Transactional
    void getAllParticipantsByNetworkTierIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where networkTier in
        defaultParticipantFiltering(
            "networkTier.in=" + DEFAULT_NETWORK_TIER + "," + UPDATED_NETWORK_TIER,
            "networkTier.in=" + UPDATED_NETWORK_TIER
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByNetworkTierIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where networkTier is not null
        defaultParticipantFiltering("networkTier.specified=true", "networkTier.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByNetworkTierContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where networkTier contains
        defaultParticipantFiltering("networkTier.contains=" + DEFAULT_NETWORK_TIER, "networkTier.contains=" + UPDATED_NETWORK_TIER);
    }

    @Test
    @Transactional
    void getAllParticipantsByNetworkTierNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where networkTier does not contain
        defaultParticipantFiltering(
            "networkTier.doesNotContain=" + UPDATED_NETWORK_TIER,
            "networkTier.doesNotContain=" + DEFAULT_NETWORK_TIER
        );
    }

    @Test
    @Transactional
    void getAllParticipantsBySiteTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where siteType equals to
        defaultParticipantFiltering("siteType.equals=" + DEFAULT_SITE_TYPE, "siteType.equals=" + UPDATED_SITE_TYPE);
    }

    @Test
    @Transactional
    void getAllParticipantsBySiteTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where siteType in
        defaultParticipantFiltering("siteType.in=" + DEFAULT_SITE_TYPE + "," + UPDATED_SITE_TYPE, "siteType.in=" + UPDATED_SITE_TYPE);
    }

    @Test
    @Transactional
    void getAllParticipantsBySiteTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where siteType is not null
        defaultParticipantFiltering("siteType.specified=true", "siteType.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsBySiteTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where siteType contains
        defaultParticipantFiltering("siteType.contains=" + DEFAULT_SITE_TYPE, "siteType.contains=" + UPDATED_SITE_TYPE);
    }

    @Test
    @Transactional
    void getAllParticipantsBySiteTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where siteType does not contain
        defaultParticipantFiltering("siteType.doesNotContain=" + UPDATED_SITE_TYPE, "siteType.doesNotContain=" + DEFAULT_SITE_TYPE);
    }

    @Test
    @Transactional
    void getAllParticipantsByFundingSourceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where fundingSource equals to
        defaultParticipantFiltering("fundingSource.equals=" + DEFAULT_FUNDING_SOURCE, "fundingSource.equals=" + UPDATED_FUNDING_SOURCE);
    }

    @Test
    @Transactional
    void getAllParticipantsByFundingSourceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where fundingSource in
        defaultParticipantFiltering(
            "fundingSource.in=" + DEFAULT_FUNDING_SOURCE + "," + UPDATED_FUNDING_SOURCE,
            "fundingSource.in=" + UPDATED_FUNDING_SOURCE
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByFundingSourceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where fundingSource is not null
        defaultParticipantFiltering("fundingSource.specified=true", "fundingSource.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByFundingSourceContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where fundingSource contains
        defaultParticipantFiltering("fundingSource.contains=" + DEFAULT_FUNDING_SOURCE, "fundingSource.contains=" + UPDATED_FUNDING_SOURCE);
    }

    @Test
    @Transactional
    void getAllParticipantsByFundingSourceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where fundingSource does not contain
        defaultParticipantFiltering(
            "fundingSource.doesNotContain=" + UPDATED_FUNDING_SOURCE,
            "fundingSource.doesNotContain=" + DEFAULT_FUNDING_SOURCE
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByTestingVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where testingVolume equals to
        defaultParticipantFiltering("testingVolume.equals=" + DEFAULT_TESTING_VOLUME, "testingVolume.equals=" + UPDATED_TESTING_VOLUME);
    }

    @Test
    @Transactional
    void getAllParticipantsByTestingVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where testingVolume in
        defaultParticipantFiltering(
            "testingVolume.in=" + DEFAULT_TESTING_VOLUME + "," + UPDATED_TESTING_VOLUME,
            "testingVolume.in=" + UPDATED_TESTING_VOLUME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByTestingVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where testingVolume is not null
        defaultParticipantFiltering("testingVolume.specified=true", "testingVolume.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByTestingVolumeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where testingVolume is greater than or equal to
        defaultParticipantFiltering(
            "testingVolume.greaterThanOrEqual=" + DEFAULT_TESTING_VOLUME,
            "testingVolume.greaterThanOrEqual=" + UPDATED_TESTING_VOLUME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByTestingVolumeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where testingVolume is less than or equal to
        defaultParticipantFiltering(
            "testingVolume.lessThanOrEqual=" + DEFAULT_TESTING_VOLUME,
            "testingVolume.lessThanOrEqual=" + SMALLER_TESTING_VOLUME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByTestingVolumeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where testingVolume is less than
        defaultParticipantFiltering("testingVolume.lessThan=" + UPDATED_TESTING_VOLUME, "testingVolume.lessThan=" + DEFAULT_TESTING_VOLUME);
    }

    @Test
    @Transactional
    void getAllParticipantsByTestingVolumeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where testingVolume is greater than
        defaultParticipantFiltering(
            "testingVolume.greaterThan=" + SMALLER_TESTING_VOLUME,
            "testingVolume.greaterThan=" + DEFAULT_TESTING_VOLUME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByPepfarIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where pepfarId equals to
        defaultParticipantFiltering("pepfarId.equals=" + DEFAULT_PEPFAR_ID, "pepfarId.equals=" + UPDATED_PEPFAR_ID);
    }

    @Test
    @Transactional
    void getAllParticipantsByPepfarIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where pepfarId in
        defaultParticipantFiltering("pepfarId.in=" + DEFAULT_PEPFAR_ID + "," + UPDATED_PEPFAR_ID, "pepfarId.in=" + UPDATED_PEPFAR_ID);
    }

    @Test
    @Transactional
    void getAllParticipantsByPepfarIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where pepfarId is not null
        defaultParticipantFiltering("pepfarId.specified=true", "pepfarId.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByPepfarIdContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where pepfarId contains
        defaultParticipantFiltering("pepfarId.contains=" + DEFAULT_PEPFAR_ID, "pepfarId.contains=" + UPDATED_PEPFAR_ID);
    }

    @Test
    @Transactional
    void getAllParticipantsByPepfarIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where pepfarId does not contain
        defaultParticipantFiltering("pepfarId.doesNotContain=" + UPDATED_PEPFAR_ID, "pepfarId.doesNotContain=" + DEFAULT_PEPFAR_ID);
    }

    @Test
    @Transactional
    void getAllParticipantsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where latitude equals to
        defaultParticipantFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where latitude in
        defaultParticipantFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where latitude is not null
        defaultParticipantFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where latitude is greater than or equal to
        defaultParticipantFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where latitude is less than or equal to
        defaultParticipantFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where latitude is less than
        defaultParticipantFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where latitude is greater than
        defaultParticipantFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where longitude equals to
        defaultParticipantFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where longitude in
        defaultParticipantFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where longitude is not null
        defaultParticipantFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where longitude is greater than or equal to
        defaultParticipantFiltering(
            "longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE,
            "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where longitude is less than or equal to
        defaultParticipantFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where longitude is less than
        defaultParticipantFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where longitude is greater than
        defaultParticipantFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorName equals to
        defaultParticipantFiltering(
            "labDirectorName.equals=" + DEFAULT_LAB_DIRECTOR_NAME,
            "labDirectorName.equals=" + UPDATED_LAB_DIRECTOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorName in
        defaultParticipantFiltering(
            "labDirectorName.in=" + DEFAULT_LAB_DIRECTOR_NAME + "," + UPDATED_LAB_DIRECTOR_NAME,
            "labDirectorName.in=" + UPDATED_LAB_DIRECTOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorName is not null
        defaultParticipantFiltering("labDirectorName.specified=true", "labDirectorName.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorNameContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorName contains
        defaultParticipantFiltering(
            "labDirectorName.contains=" + DEFAULT_LAB_DIRECTOR_NAME,
            "labDirectorName.contains=" + UPDATED_LAB_DIRECTOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorName does not contain
        defaultParticipantFiltering(
            "labDirectorName.doesNotContain=" + UPDATED_LAB_DIRECTOR_NAME,
            "labDirectorName.doesNotContain=" + DEFAULT_LAB_DIRECTOR_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorEmail equals to
        defaultParticipantFiltering(
            "labDirectorEmail.equals=" + DEFAULT_LAB_DIRECTOR_EMAIL,
            "labDirectorEmail.equals=" + UPDATED_LAB_DIRECTOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorEmail in
        defaultParticipantFiltering(
            "labDirectorEmail.in=" + DEFAULT_LAB_DIRECTOR_EMAIL + "," + UPDATED_LAB_DIRECTOR_EMAIL,
            "labDirectorEmail.in=" + UPDATED_LAB_DIRECTOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorEmail is not null
        defaultParticipantFiltering("labDirectorEmail.specified=true", "labDirectorEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorEmail contains
        defaultParticipantFiltering(
            "labDirectorEmail.contains=" + DEFAULT_LAB_DIRECTOR_EMAIL,
            "labDirectorEmail.contains=" + UPDATED_LAB_DIRECTOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByLabDirectorEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where labDirectorEmail does not contain
        defaultParticipantFiltering(
            "labDirectorEmail.doesNotContain=" + UPDATED_LAB_DIRECTOR_EMAIL,
            "labDirectorEmail.doesNotContain=" + DEFAULT_LAB_DIRECTOR_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonName equals to
        defaultParticipantFiltering(
            "contactPersonName.equals=" + DEFAULT_CONTACT_PERSON_NAME,
            "contactPersonName.equals=" + UPDATED_CONTACT_PERSON_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonName in
        defaultParticipantFiltering(
            "contactPersonName.in=" + DEFAULT_CONTACT_PERSON_NAME + "," + UPDATED_CONTACT_PERSON_NAME,
            "contactPersonName.in=" + UPDATED_CONTACT_PERSON_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonName is not null
        defaultParticipantFiltering("contactPersonName.specified=true", "contactPersonName.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonNameContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonName contains
        defaultParticipantFiltering(
            "contactPersonName.contains=" + DEFAULT_CONTACT_PERSON_NAME,
            "contactPersonName.contains=" + UPDATED_CONTACT_PERSON_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonName does not contain
        defaultParticipantFiltering(
            "contactPersonName.doesNotContain=" + UPDATED_CONTACT_PERSON_NAME,
            "contactPersonName.doesNotContain=" + DEFAULT_CONTACT_PERSON_NAME
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonEmail equals to
        defaultParticipantFiltering(
            "contactPersonEmail.equals=" + DEFAULT_CONTACT_PERSON_EMAIL,
            "contactPersonEmail.equals=" + UPDATED_CONTACT_PERSON_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonEmail in
        defaultParticipantFiltering(
            "contactPersonEmail.in=" + DEFAULT_CONTACT_PERSON_EMAIL + "," + UPDATED_CONTACT_PERSON_EMAIL,
            "contactPersonEmail.in=" + UPDATED_CONTACT_PERSON_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonEmail is not null
        defaultParticipantFiltering("contactPersonEmail.specified=true", "contactPersonEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonEmail contains
        defaultParticipantFiltering(
            "contactPersonEmail.contains=" + DEFAULT_CONTACT_PERSON_EMAIL,
            "contactPersonEmail.contains=" + UPDATED_CONTACT_PERSON_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonEmail does not contain
        defaultParticipantFiltering(
            "contactPersonEmail.doesNotContain=" + UPDATED_CONTACT_PERSON_EMAIL,
            "contactPersonEmail.doesNotContain=" + DEFAULT_CONTACT_PERSON_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonPhone equals to
        defaultParticipantFiltering(
            "contactPersonPhone.equals=" + DEFAULT_CONTACT_PERSON_PHONE,
            "contactPersonPhone.equals=" + UPDATED_CONTACT_PERSON_PHONE
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonPhone in
        defaultParticipantFiltering(
            "contactPersonPhone.in=" + DEFAULT_CONTACT_PERSON_PHONE + "," + UPDATED_CONTACT_PERSON_PHONE,
            "contactPersonPhone.in=" + UPDATED_CONTACT_PERSON_PHONE
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonPhone is not null
        defaultParticipantFiltering("contactPersonPhone.specified=true", "contactPersonPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonPhone contains
        defaultParticipantFiltering(
            "contactPersonPhone.contains=" + DEFAULT_CONTACT_PERSON_PHONE,
            "contactPersonPhone.contains=" + UPDATED_CONTACT_PERSON_PHONE
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByContactPersonPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where contactPersonPhone does not contain
        defaultParticipantFiltering(
            "contactPersonPhone.doesNotContain=" + UPDATED_CONTACT_PERSON_PHONE,
            "contactPersonPhone.doesNotContain=" + DEFAULT_CONTACT_PERSON_PHONE
        );
    }

    @Test
    @Transactional
    void getAllParticipantsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where status equals to
        defaultParticipantFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParticipantsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where status in
        defaultParticipantFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllParticipantsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        // Get all the participantList where status is not null
        defaultParticipantFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllParticipantsByCountryIsEqualToSomething() throws Exception {
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            participantRepository.saveAndFlush(participant);
            country = CountryResourceIT.createEntity();
        } else {
            country = TestUtil.findAll(em, Country.class).get(0);
        }
        em.persist(country);
        em.flush();
        participant.setCountry(country);
        participantRepository.saveAndFlush(participant);
        Long countryId = country.getId();
        // Get all the participantList where country equals to countryId
        defaultParticipantShouldBeFound("countryId.equals=" + countryId);

        // Get all the participantList where country equals to (countryId + 1)
        defaultParticipantShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    @Test
    @Transactional
    void getAllParticipantsByDataManagersIsEqualToSomething() throws Exception {
        DataManager dataManagers;
        if (TestUtil.findAll(em, DataManager.class).isEmpty()) {
            participantRepository.saveAndFlush(participant);
            dataManagers = DataManagerResourceIT.createEntity();
        } else {
            dataManagers = TestUtil.findAll(em, DataManager.class).get(0);
        }
        em.persist(dataManagers);
        em.flush();
        participant.addDataManagers(dataManagers);
        participantRepository.saveAndFlush(participant);
        Long dataManagersId = dataManagers.getId();
        // Get all the participantList where dataManagers equals to dataManagersId
        defaultParticipantShouldBeFound("dataManagersId.equals=" + dataManagersId);

        // Get all the participantList where dataManagers equals to (dataManagersId + 1)
        defaultParticipantShouldNotBeFound("dataManagersId.equals=" + (dataManagersId + 1));
    }

    private void defaultParticipantFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultParticipantShouldBeFound(shouldBeFound);
        defaultParticipantShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParticipantShouldBeFound(String filter) throws Exception {
        restParticipantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].uniqueIdentifier").value(hasItem(DEFAULT_UNIQUE_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].instituteName").value(hasItem(DEFAULT_INSTITUTE_NAME)))
            .andExpect(jsonPath("$.[*].departmentName").value(hasItem(DEFAULT_DEPARTMENT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].additionalEmail").value(hasItem(DEFAULT_ADDITIONAL_EMAIL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].shippingAddress").value(hasItem(DEFAULT_SHIPPING_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].district").value(hasItem(DEFAULT_DISTRICT)))
            .andExpect(jsonPath("$.[*].zip").value(hasItem(DEFAULT_ZIP)))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE)))
            .andExpect(jsonPath("$.[*].affiliation").value(hasItem(DEFAULT_AFFILIATION)))
            .andExpect(jsonPath("$.[*].networkTier").value(hasItem(DEFAULT_NETWORK_TIER)))
            .andExpect(jsonPath("$.[*].siteType").value(hasItem(DEFAULT_SITE_TYPE)))
            .andExpect(jsonPath("$.[*].fundingSource").value(hasItem(DEFAULT_FUNDING_SOURCE)))
            .andExpect(jsonPath("$.[*].testingVolume").value(hasItem(DEFAULT_TESTING_VOLUME.intValue())))
            .andExpect(jsonPath("$.[*].pepfarId").value(hasItem(DEFAULT_PEPFAR_ID)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].labDirectorName").value(hasItem(DEFAULT_LAB_DIRECTOR_NAME)))
            .andExpect(jsonPath("$.[*].labDirectorEmail").value(hasItem(DEFAULT_LAB_DIRECTOR_EMAIL)))
            .andExpect(jsonPath("$.[*].contactPersonName").value(hasItem(DEFAULT_CONTACT_PERSON_NAME)))
            .andExpect(jsonPath("$.[*].contactPersonEmail").value(hasItem(DEFAULT_CONTACT_PERSON_EMAIL)))
            .andExpect(jsonPath("$.[*].contactPersonPhone").value(hasItem(DEFAULT_CONTACT_PERSON_PHONE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restParticipantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParticipantShouldNotBeFound(String filter) throws Exception {
        restParticipantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParticipantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingParticipant() throws Exception {
        // Get the participant
        restParticipantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParticipant() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participant
        Participant updatedParticipant = participantRepository.findById(participant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParticipant are not directly saved in db
        em.detach(updatedParticipant);
        updatedParticipant
            .uniqueIdentifier(UPDATED_UNIQUE_IDENTIFIER)
            .instituteName(UPDATED_INSTITUTE_NAME)
            .departmentName(UPDATED_DEPARTMENT_NAME)
            .email(UPDATED_EMAIL)
            .additionalEmail(UPDATED_ADDITIONAL_EMAIL)
            .address(UPDATED_ADDRESS)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .district(UPDATED_DISTRICT)
            .zip(UPDATED_ZIP)
            .region(UPDATED_REGION)
            .phone(UPDATED_PHONE)
            .mobile(UPDATED_MOBILE)
            .affiliation(UPDATED_AFFILIATION)
            .networkTier(UPDATED_NETWORK_TIER)
            .siteType(UPDATED_SITE_TYPE)
            .fundingSource(UPDATED_FUNDING_SOURCE)
            .testingVolume(UPDATED_TESTING_VOLUME)
            .pepfarId(UPDATED_PEPFAR_ID)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .labDirectorName(UPDATED_LAB_DIRECTOR_NAME)
            .labDirectorEmail(UPDATED_LAB_DIRECTOR_EMAIL)
            .contactPersonName(UPDATED_CONTACT_PERSON_NAME)
            .contactPersonEmail(UPDATED_CONTACT_PERSON_EMAIL)
            .contactPersonPhone(UPDATED_CONTACT_PERSON_PHONE)
            .status(UPDATED_STATUS);
        ParticipantDTO participantDTO = participantMapper.toDto(updatedParticipant);

        restParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParticipantToMatchAllProperties(updatedParticipant);
    }

    @Test
    @Transactional
    void putNonExistingParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participant.setId(longCount.incrementAndGet());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, participantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participant.setId(longCount.incrementAndGet());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participant.setId(longCount.incrementAndGet());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParticipantWithPatch() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participant using partial update
        Participant partialUpdatedParticipant = new Participant();
        partialUpdatedParticipant.setId(participant.getId());

        partialUpdatedParticipant
            .uniqueIdentifier(UPDATED_UNIQUE_IDENTIFIER)
            .departmentName(UPDATED_DEPARTMENT_NAME)
            .email(UPDATED_EMAIL)
            .additionalEmail(UPDATED_ADDITIONAL_EMAIL)
            .district(UPDATED_DISTRICT)
            .phone(UPDATED_PHONE)
            .mobile(UPDATED_MOBILE)
            .affiliation(UPDATED_AFFILIATION)
            .networkTier(UPDATED_NETWORK_TIER)
            .longitude(UPDATED_LONGITUDE)
            .contactPersonName(UPDATED_CONTACT_PERSON_NAME)
            .contactPersonEmail(UPDATED_CONTACT_PERSON_EMAIL);

        restParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipant))
            )
            .andExpect(status().isOk());

        // Validate the Participant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParticipant, participant),
            getPersistedParticipant(participant)
        );
    }

    @Test
    @Transactional
    void fullUpdateParticipantWithPatch() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the participant using partial update
        Participant partialUpdatedParticipant = new Participant();
        partialUpdatedParticipant.setId(participant.getId());

        partialUpdatedParticipant
            .uniqueIdentifier(UPDATED_UNIQUE_IDENTIFIER)
            .instituteName(UPDATED_INSTITUTE_NAME)
            .departmentName(UPDATED_DEPARTMENT_NAME)
            .email(UPDATED_EMAIL)
            .additionalEmail(UPDATED_ADDITIONAL_EMAIL)
            .address(UPDATED_ADDRESS)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .district(UPDATED_DISTRICT)
            .zip(UPDATED_ZIP)
            .region(UPDATED_REGION)
            .phone(UPDATED_PHONE)
            .mobile(UPDATED_MOBILE)
            .affiliation(UPDATED_AFFILIATION)
            .networkTier(UPDATED_NETWORK_TIER)
            .siteType(UPDATED_SITE_TYPE)
            .fundingSource(UPDATED_FUNDING_SOURCE)
            .testingVolume(UPDATED_TESTING_VOLUME)
            .pepfarId(UPDATED_PEPFAR_ID)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .labDirectorName(UPDATED_LAB_DIRECTOR_NAME)
            .labDirectorEmail(UPDATED_LAB_DIRECTOR_EMAIL)
            .contactPersonName(UPDATED_CONTACT_PERSON_NAME)
            .contactPersonEmail(UPDATED_CONTACT_PERSON_EMAIL)
            .contactPersonPhone(UPDATED_CONTACT_PERSON_PHONE)
            .status(UPDATED_STATUS);

        restParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParticipant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParticipant))
            )
            .andExpect(status().isOk());

        // Validate the Participant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParticipantUpdatableFieldsEquals(partialUpdatedParticipant, getPersistedParticipant(partialUpdatedParticipant));
    }

    @Test
    @Transactional
    void patchNonExistingParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participant.setId(longCount.incrementAndGet());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, participantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participant.setId(longCount.incrementAndGet());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(participantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParticipant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        participant.setId(longCount.incrementAndGet());

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParticipantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(participantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Participant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParticipant() throws Exception {
        // Initialize the database
        insertedParticipant = participantRepository.saveAndFlush(participant);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the participant
        restParticipantMockMvc
            .perform(delete(ENTITY_API_URL_ID, participant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return participantRepository.count();
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

    protected Participant getPersistedParticipant(Participant participant) {
        return participantRepository.findById(participant.getId()).orElseThrow();
    }

    protected void assertPersistedParticipantToMatchAllProperties(Participant expectedParticipant) {
        assertParticipantAllPropertiesEquals(expectedParticipant, getPersistedParticipant(expectedParticipant));
    }

    protected void assertPersistedParticipantToMatchUpdatableProperties(Participant expectedParticipant) {
        assertParticipantAllUpdatablePropertiesEquals(expectedParticipant, getPersistedParticipant(expectedParticipant));
    }
}
