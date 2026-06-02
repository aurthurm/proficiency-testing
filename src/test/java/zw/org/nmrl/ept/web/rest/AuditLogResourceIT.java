package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.AuditLogAsserts.*;
import static zw.org.nmrl.ept.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
import zw.org.nmrl.ept.domain.AuditLog;
import zw.org.nmrl.ept.domain.enumeration.AuditAction;
import zw.org.nmrl.ept.repository.AuditLogRepository;
import zw.org.nmrl.ept.service.dto.AuditLogDTO;
import zw.org.nmrl.ept.service.mapper.AuditLogMapper;

/**
 * Integration tests for the {@link AuditLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuditLogResourceIT {

    private static final AuditAction DEFAULT_ACTION = AuditAction.LOGIN;
    private static final AuditAction UPDATED_ACTION = AuditAction.LOGOUT;

    private static final String DEFAULT_STATEMENT = "AAAAAAAAAA";
    private static final String UPDATED_STATEMENT = "BBBBBBBBBB";

    private static final String DEFAULT_PERFORMED_BY = "AAAAAAAAAA";
    private static final String UPDATED_PERFORMED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_PERFORMED_BY_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_PERFORMED_BY_ROLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_PERFORMED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PERFORMED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_USER_AGENT = "AAAAAAAAAA";
    private static final String UPDATED_USER_AGENT = "BBBBBBBBBB";

    private static final String DEFAULT_SESSION_HASH = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_HASH = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audit-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuditLogMockMvc;

    private AuditLog auditLog;

    private AuditLog insertedAuditLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditLog createEntity() {
        return new AuditLog()
            .action(DEFAULT_ACTION)
            .statement(DEFAULT_STATEMENT)
            .performedBy(DEFAULT_PERFORMED_BY)
            .performedByRole(DEFAULT_PERFORMED_BY_ROLE)
            .performedOn(DEFAULT_PERFORMED_ON)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .userAgent(DEFAULT_USER_AGENT)
            .sessionHash(DEFAULT_SESSION_HASH);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditLog createUpdatedEntity() {
        return new AuditLog()
            .action(UPDATED_ACTION)
            .statement(UPDATED_STATEMENT)
            .performedBy(UPDATED_PERFORMED_BY)
            .performedByRole(UPDATED_PERFORMED_BY_ROLE)
            .performedOn(UPDATED_PERFORMED_ON)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT)
            .sessionHash(UPDATED_SESSION_HASH);
    }

    @BeforeEach
    void initTest() {
        auditLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditLog != null) {
            auditLogRepository.delete(insertedAuditLog);
            insertedAuditLog = null;
        }
    }

    @Test
    @Transactional
    void createAuditLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);
        var returnedAuditLogDTO = om.readValue(
            restAuditLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AuditLogDTO.class
        );

        // Validate the AuditLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditLog = auditLogMapper.toEntity(returnedAuditLogDTO);
        assertAuditLogUpdatableFieldsEquals(returnedAuditLog, getPersistedAuditLog(returnedAuditLog));

        insertedAuditLog = returnedAuditLog;
    }

    @Test
    @Transactional
    void createAuditLogWithExistingId() throws Exception {
        // Create the AuditLog with an existing ID
        auditLog.setId(1L);
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuditLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLog.setAction(null);

        // Create the AuditLog, which fails.
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        restAuditLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLog.setStatement(null);

        // Create the AuditLog, which fails.
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        restAuditLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPerformedOnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditLog.setPerformedOn(null);

        // Create the AuditLog, which fails.
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        restAuditLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAuditLogs() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList
        restAuditLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].statement").value(hasItem(DEFAULT_STATEMENT)))
            .andExpect(jsonPath("$.[*].performedBy").value(hasItem(DEFAULT_PERFORMED_BY)))
            .andExpect(jsonPath("$.[*].performedByRole").value(hasItem(DEFAULT_PERFORMED_BY_ROLE)))
            .andExpect(jsonPath("$.[*].performedOn").value(hasItem(DEFAULT_PERFORMED_ON.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)))
            .andExpect(jsonPath("$.[*].sessionHash").value(hasItem(DEFAULT_SESSION_HASH)));
    }

    @Test
    @Transactional
    void getAuditLog() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get the auditLog
        restAuditLogMockMvc
            .perform(get(ENTITY_API_URL_ID, auditLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auditLog.getId().intValue()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()))
            .andExpect(jsonPath("$.statement").value(DEFAULT_STATEMENT))
            .andExpect(jsonPath("$.performedBy").value(DEFAULT_PERFORMED_BY))
            .andExpect(jsonPath("$.performedByRole").value(DEFAULT_PERFORMED_BY_ROLE))
            .andExpect(jsonPath("$.performedOn").value(DEFAULT_PERFORMED_ON.toString()))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.userAgent").value(DEFAULT_USER_AGENT))
            .andExpect(jsonPath("$.sessionHash").value(DEFAULT_SESSION_HASH));
    }

    @Test
    @Transactional
    void getAuditLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        Long id = auditLog.getId();

        defaultAuditLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAuditLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAuditLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuditLogsByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where action equals to
        defaultAuditLogFiltering("action.equals=" + DEFAULT_ACTION, "action.equals=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditLogsByActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where action in
        defaultAuditLogFiltering("action.in=" + DEFAULT_ACTION + "," + UPDATED_ACTION, "action.in=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllAuditLogsByActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where action is not null
        defaultAuditLogFiltering("action.specified=true", "action.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogsByStatementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where statement equals to
        defaultAuditLogFiltering("statement.equals=" + DEFAULT_STATEMENT, "statement.equals=" + UPDATED_STATEMENT);
    }

    @Test
    @Transactional
    void getAllAuditLogsByStatementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where statement in
        defaultAuditLogFiltering("statement.in=" + DEFAULT_STATEMENT + "," + UPDATED_STATEMENT, "statement.in=" + UPDATED_STATEMENT);
    }

    @Test
    @Transactional
    void getAllAuditLogsByStatementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where statement is not null
        defaultAuditLogFiltering("statement.specified=true", "statement.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogsByStatementContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where statement contains
        defaultAuditLogFiltering("statement.contains=" + DEFAULT_STATEMENT, "statement.contains=" + UPDATED_STATEMENT);
    }

    @Test
    @Transactional
    void getAllAuditLogsByStatementNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where statement does not contain
        defaultAuditLogFiltering("statement.doesNotContain=" + UPDATED_STATEMENT, "statement.doesNotContain=" + DEFAULT_STATEMENT);
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedBy equals to
        defaultAuditLogFiltering("performedBy.equals=" + DEFAULT_PERFORMED_BY, "performedBy.equals=" + UPDATED_PERFORMED_BY);
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedBy in
        defaultAuditLogFiltering(
            "performedBy.in=" + DEFAULT_PERFORMED_BY + "," + UPDATED_PERFORMED_BY,
            "performedBy.in=" + UPDATED_PERFORMED_BY
        );
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedBy is not null
        defaultAuditLogFiltering("performedBy.specified=true", "performedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedBy contains
        defaultAuditLogFiltering("performedBy.contains=" + DEFAULT_PERFORMED_BY, "performedBy.contains=" + UPDATED_PERFORMED_BY);
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedBy does not contain
        defaultAuditLogFiltering(
            "performedBy.doesNotContain=" + UPDATED_PERFORMED_BY,
            "performedBy.doesNotContain=" + DEFAULT_PERFORMED_BY
        );
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedByRole equals to
        defaultAuditLogFiltering(
            "performedByRole.equals=" + DEFAULT_PERFORMED_BY_ROLE,
            "performedByRole.equals=" + UPDATED_PERFORMED_BY_ROLE
        );
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedByRole in
        defaultAuditLogFiltering(
            "performedByRole.in=" + DEFAULT_PERFORMED_BY_ROLE + "," + UPDATED_PERFORMED_BY_ROLE,
            "performedByRole.in=" + UPDATED_PERFORMED_BY_ROLE
        );
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedByRole is not null
        defaultAuditLogFiltering("performedByRole.specified=true", "performedByRole.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByRoleContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedByRole contains
        defaultAuditLogFiltering(
            "performedByRole.contains=" + DEFAULT_PERFORMED_BY_ROLE,
            "performedByRole.contains=" + UPDATED_PERFORMED_BY_ROLE
        );
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedByRoleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedByRole does not contain
        defaultAuditLogFiltering(
            "performedByRole.doesNotContain=" + UPDATED_PERFORMED_BY_ROLE,
            "performedByRole.doesNotContain=" + DEFAULT_PERFORMED_BY_ROLE
        );
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedOn equals to
        defaultAuditLogFiltering("performedOn.equals=" + DEFAULT_PERFORMED_ON, "performedOn.equals=" + UPDATED_PERFORMED_ON);
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedOnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedOn in
        defaultAuditLogFiltering(
            "performedOn.in=" + DEFAULT_PERFORMED_ON + "," + UPDATED_PERFORMED_ON,
            "performedOn.in=" + UPDATED_PERFORMED_ON
        );
    }

    @Test
    @Transactional
    void getAllAuditLogsByPerformedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where performedOn is not null
        defaultAuditLogFiltering("performedOn.specified=true", "performedOn.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogsByIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where ipAddress equals to
        defaultAuditLogFiltering("ipAddress.equals=" + DEFAULT_IP_ADDRESS, "ipAddress.equals=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditLogsByIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where ipAddress in
        defaultAuditLogFiltering("ipAddress.in=" + DEFAULT_IP_ADDRESS + "," + UPDATED_IP_ADDRESS, "ipAddress.in=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditLogsByIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where ipAddress is not null
        defaultAuditLogFiltering("ipAddress.specified=true", "ipAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogsByIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where ipAddress contains
        defaultAuditLogFiltering("ipAddress.contains=" + DEFAULT_IP_ADDRESS, "ipAddress.contains=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditLogsByIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where ipAddress does not contain
        defaultAuditLogFiltering("ipAddress.doesNotContain=" + UPDATED_IP_ADDRESS, "ipAddress.doesNotContain=" + DEFAULT_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAuditLogsByUserAgentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where userAgent equals to
        defaultAuditLogFiltering("userAgent.equals=" + DEFAULT_USER_AGENT, "userAgent.equals=" + UPDATED_USER_AGENT);
    }

    @Test
    @Transactional
    void getAllAuditLogsByUserAgentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where userAgent in
        defaultAuditLogFiltering("userAgent.in=" + DEFAULT_USER_AGENT + "," + UPDATED_USER_AGENT, "userAgent.in=" + UPDATED_USER_AGENT);
    }

    @Test
    @Transactional
    void getAllAuditLogsByUserAgentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where userAgent is not null
        defaultAuditLogFiltering("userAgent.specified=true", "userAgent.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogsByUserAgentContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where userAgent contains
        defaultAuditLogFiltering("userAgent.contains=" + DEFAULT_USER_AGENT, "userAgent.contains=" + UPDATED_USER_AGENT);
    }

    @Test
    @Transactional
    void getAllAuditLogsByUserAgentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where userAgent does not contain
        defaultAuditLogFiltering("userAgent.doesNotContain=" + UPDATED_USER_AGENT, "userAgent.doesNotContain=" + DEFAULT_USER_AGENT);
    }

    @Test
    @Transactional
    void getAllAuditLogsBySessionHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where sessionHash equals to
        defaultAuditLogFiltering("sessionHash.equals=" + DEFAULT_SESSION_HASH, "sessionHash.equals=" + UPDATED_SESSION_HASH);
    }

    @Test
    @Transactional
    void getAllAuditLogsBySessionHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where sessionHash in
        defaultAuditLogFiltering(
            "sessionHash.in=" + DEFAULT_SESSION_HASH + "," + UPDATED_SESSION_HASH,
            "sessionHash.in=" + UPDATED_SESSION_HASH
        );
    }

    @Test
    @Transactional
    void getAllAuditLogsBySessionHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where sessionHash is not null
        defaultAuditLogFiltering("sessionHash.specified=true", "sessionHash.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditLogsBySessionHashContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where sessionHash contains
        defaultAuditLogFiltering("sessionHash.contains=" + DEFAULT_SESSION_HASH, "sessionHash.contains=" + UPDATED_SESSION_HASH);
    }

    @Test
    @Transactional
    void getAllAuditLogsBySessionHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        // Get all the auditLogList where sessionHash does not contain
        defaultAuditLogFiltering(
            "sessionHash.doesNotContain=" + UPDATED_SESSION_HASH,
            "sessionHash.doesNotContain=" + DEFAULT_SESSION_HASH
        );
    }

    private void defaultAuditLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAuditLogShouldBeFound(shouldBeFound);
        defaultAuditLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuditLogShouldBeFound(String filter) throws Exception {
        restAuditLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())))
            .andExpect(jsonPath("$.[*].statement").value(hasItem(DEFAULT_STATEMENT)))
            .andExpect(jsonPath("$.[*].performedBy").value(hasItem(DEFAULT_PERFORMED_BY)))
            .andExpect(jsonPath("$.[*].performedByRole").value(hasItem(DEFAULT_PERFORMED_BY_ROLE)))
            .andExpect(jsonPath("$.[*].performedOn").value(hasItem(DEFAULT_PERFORMED_ON.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)))
            .andExpect(jsonPath("$.[*].sessionHash").value(hasItem(DEFAULT_SESSION_HASH)));

        // Check, that the count call also returns 1
        restAuditLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuditLogShouldNotBeFound(String filter) throws Exception {
        restAuditLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuditLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAuditLog() throws Exception {
        // Get the auditLog
        restAuditLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuditLog() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLog
        AuditLog updatedAuditLog = auditLogRepository.findById(auditLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuditLog are not directly saved in db
        em.detach(updatedAuditLog);
        updatedAuditLog
            .action(UPDATED_ACTION)
            .statement(UPDATED_STATEMENT)
            .performedBy(UPDATED_PERFORMED_BY)
            .performedByRole(UPDATED_PERFORMED_BY_ROLE)
            .performedOn(UPDATED_PERFORMED_ON)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT)
            .sessionHash(UPDATED_SESSION_HASH);
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(updatedAuditLog);

        restAuditLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditLogToMatchAllProperties(updatedAuditLog);
    }

    @Test
    @Transactional
    void putNonExistingAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auditLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auditLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuditLogWithPatch() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLog using partial update
        AuditLog partialUpdatedAuditLog = new AuditLog();
        partialUpdatedAuditLog.setId(auditLog.getId());

        partialUpdatedAuditLog
            .statement(UPDATED_STATEMENT)
            .performedBy(UPDATED_PERFORMED_BY)
            .performedByRole(UPDATED_PERFORMED_BY_ROLE)
            .performedOn(UPDATED_PERFORMED_ON)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT)
            .sessionHash(UPDATED_SESSION_HASH);

        restAuditLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuditLog))
            )
            .andExpect(status().isOk());

        // Validate the AuditLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditLogUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAuditLog, auditLog), getPersistedAuditLog(auditLog));
    }

    @Test
    @Transactional
    void fullUpdateAuditLogWithPatch() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditLog using partial update
        AuditLog partialUpdatedAuditLog = new AuditLog();
        partialUpdatedAuditLog.setId(auditLog.getId());

        partialUpdatedAuditLog
            .action(UPDATED_ACTION)
            .statement(UPDATED_STATEMENT)
            .performedBy(UPDATED_PERFORMED_BY)
            .performedByRole(UPDATED_PERFORMED_BY_ROLE)
            .performedOn(UPDATED_PERFORMED_ON)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT)
            .sessionHash(UPDATED_SESSION_HASH);

        restAuditLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuditLog))
            )
            .andExpect(status().isOk());

        // Validate the AuditLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditLogUpdatableFieldsEquals(partialUpdatedAuditLog, getPersistedAuditLog(partialUpdatedAuditLog));
    }

    @Test
    @Transactional
    void patchNonExistingAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auditLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auditLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auditLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuditLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditLog.setId(longCount.incrementAndGet());

        // Create the AuditLog
        AuditLogDTO auditLogDTO = auditLogMapper.toDto(auditLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(auditLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuditLog() throws Exception {
        // Initialize the database
        insertedAuditLog = auditLogRepository.saveAndFlush(auditLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditLog
        restAuditLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, auditLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditLogRepository.count();
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

    protected AuditLog getPersistedAuditLog(AuditLog auditLog) {
        return auditLogRepository.findById(auditLog.getId()).orElseThrow();
    }

    protected void assertPersistedAuditLogToMatchAllProperties(AuditLog expectedAuditLog) {
        assertAuditLogAllPropertiesEquals(expectedAuditLog, getPersistedAuditLog(expectedAuditLog));
    }

    protected void assertPersistedAuditLogToMatchUpdatableProperties(AuditLog expectedAuditLog) {
        assertAuditLogAllUpdatablePropertiesEquals(expectedAuditLog, getPersistedAuditLog(expectedAuditLog));
    }
}
