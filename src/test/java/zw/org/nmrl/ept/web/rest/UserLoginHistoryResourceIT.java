package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.UserLoginHistoryAsserts.*;
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
import zw.org.nmrl.ept.domain.UserLoginHistory;
import zw.org.nmrl.ept.domain.enumeration.LoginStatus;
import zw.org.nmrl.ept.repository.UserLoginHistoryRepository;
import zw.org.nmrl.ept.service.dto.UserLoginHistoryDTO;
import zw.org.nmrl.ept.service.mapper.UserLoginHistoryMapper;

/**
 * Integration tests for the {@link UserLoginHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserLoginHistoryResourceIT {

    private static final String DEFAULT_LOGIN_ID = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN_ID = "BBBBBBBBBB";

    private static final String DEFAULT_LOGIN_CONTEXT = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN_CONTEXT = "BBBBBBBBBB";

    private static final LoginStatus DEFAULT_LOGIN_STATUS = LoginStatus.SUCCESS;
    private static final LoginStatus UPDATED_LOGIN_STATUS = LoginStatus.FAILED;

    private static final Instant DEFAULT_ATTEMPTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ATTEMPTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_BROWSER = "AAAAAAAAAA";
    private static final String UPDATED_BROWSER = "BBBBBBBBBB";

    private static final String DEFAULT_OPERATING_SYSTEM = "AAAAAAAAAA";
    private static final String UPDATED_OPERATING_SYSTEM = "BBBBBBBBBB";

    private static final String DEFAULT_SESSION_HASH = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_HASH = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-login-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserLoginHistoryRepository userLoginHistoryRepository;

    @Autowired
    private UserLoginHistoryMapper userLoginHistoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserLoginHistoryMockMvc;

    private UserLoginHistory userLoginHistory;

    private UserLoginHistory insertedUserLoginHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLoginHistory createEntity() {
        return new UserLoginHistory()
            .loginId(DEFAULT_LOGIN_ID)
            .loginContext(DEFAULT_LOGIN_CONTEXT)
            .loginStatus(DEFAULT_LOGIN_STATUS)
            .attemptedAt(DEFAULT_ATTEMPTED_AT)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .browser(DEFAULT_BROWSER)
            .operatingSystem(DEFAULT_OPERATING_SYSTEM)
            .sessionHash(DEFAULT_SESSION_HASH);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLoginHistory createUpdatedEntity() {
        return new UserLoginHistory()
            .loginId(UPDATED_LOGIN_ID)
            .loginContext(UPDATED_LOGIN_CONTEXT)
            .loginStatus(UPDATED_LOGIN_STATUS)
            .attemptedAt(UPDATED_ATTEMPTED_AT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .browser(UPDATED_BROWSER)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .sessionHash(UPDATED_SESSION_HASH);
    }

    @BeforeEach
    void initTest() {
        userLoginHistory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserLoginHistory != null) {
            userLoginHistoryRepository.delete(insertedUserLoginHistory);
            insertedUserLoginHistory = null;
        }
    }

    @Test
    @Transactional
    void createUserLoginHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserLoginHistory
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);
        var returnedUserLoginHistoryDTO = om.readValue(
            restUserLoginHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLoginHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserLoginHistoryDTO.class
        );

        // Validate the UserLoginHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserLoginHistory = userLoginHistoryMapper.toEntity(returnedUserLoginHistoryDTO);
        assertUserLoginHistoryUpdatableFieldsEquals(returnedUserLoginHistory, getPersistedUserLoginHistory(returnedUserLoginHistory));

        insertedUserLoginHistory = returnedUserLoginHistory;
    }

    @Test
    @Transactional
    void createUserLoginHistoryWithExistingId() throws Exception {
        // Create the UserLoginHistory with an existing ID
        userLoginHistory.setId(1L);
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserLoginHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLoginHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserLoginHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLoginStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userLoginHistory.setLoginStatus(null);

        // Create the UserLoginHistory, which fails.
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        restUserLoginHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLoginHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAttemptedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userLoginHistory.setAttemptedAt(null);

        // Create the UserLoginHistory, which fails.
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        restUserLoginHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLoginHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserLoginHistories() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList
        restUserLoginHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userLoginHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].loginId").value(hasItem(DEFAULT_LOGIN_ID)))
            .andExpect(jsonPath("$.[*].loginContext").value(hasItem(DEFAULT_LOGIN_CONTEXT)))
            .andExpect(jsonPath("$.[*].loginStatus").value(hasItem(DEFAULT_LOGIN_STATUS.toString())))
            .andExpect(jsonPath("$.[*].attemptedAt").value(hasItem(DEFAULT_ATTEMPTED_AT.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].browser").value(hasItem(DEFAULT_BROWSER)))
            .andExpect(jsonPath("$.[*].operatingSystem").value(hasItem(DEFAULT_OPERATING_SYSTEM)))
            .andExpect(jsonPath("$.[*].sessionHash").value(hasItem(DEFAULT_SESSION_HASH)));
    }

    @Test
    @Transactional
    void getUserLoginHistory() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get the userLoginHistory
        restUserLoginHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, userLoginHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userLoginHistory.getId().intValue()))
            .andExpect(jsonPath("$.loginId").value(DEFAULT_LOGIN_ID))
            .andExpect(jsonPath("$.loginContext").value(DEFAULT_LOGIN_CONTEXT))
            .andExpect(jsonPath("$.loginStatus").value(DEFAULT_LOGIN_STATUS.toString()))
            .andExpect(jsonPath("$.attemptedAt").value(DEFAULT_ATTEMPTED_AT.toString()))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.browser").value(DEFAULT_BROWSER))
            .andExpect(jsonPath("$.operatingSystem").value(DEFAULT_OPERATING_SYSTEM))
            .andExpect(jsonPath("$.sessionHash").value(DEFAULT_SESSION_HASH));
    }

    @Test
    @Transactional
    void getUserLoginHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        Long id = userLoginHistory.getId();

        defaultUserLoginHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultUserLoginHistoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultUserLoginHistoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginId equals to
        defaultUserLoginHistoryFiltering("loginId.equals=" + DEFAULT_LOGIN_ID, "loginId.equals=" + UPDATED_LOGIN_ID);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginId in
        defaultUserLoginHistoryFiltering("loginId.in=" + DEFAULT_LOGIN_ID + "," + UPDATED_LOGIN_ID, "loginId.in=" + UPDATED_LOGIN_ID);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginId is not null
        defaultUserLoginHistoryFiltering("loginId.specified=true", "loginId.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginIdContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginId contains
        defaultUserLoginHistoryFiltering("loginId.contains=" + DEFAULT_LOGIN_ID, "loginId.contains=" + UPDATED_LOGIN_ID);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginId does not contain
        defaultUserLoginHistoryFiltering("loginId.doesNotContain=" + UPDATED_LOGIN_ID, "loginId.doesNotContain=" + DEFAULT_LOGIN_ID);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginContextIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginContext equals to
        defaultUserLoginHistoryFiltering("loginContext.equals=" + DEFAULT_LOGIN_CONTEXT, "loginContext.equals=" + UPDATED_LOGIN_CONTEXT);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginContextIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginContext in
        defaultUserLoginHistoryFiltering(
            "loginContext.in=" + DEFAULT_LOGIN_CONTEXT + "," + UPDATED_LOGIN_CONTEXT,
            "loginContext.in=" + UPDATED_LOGIN_CONTEXT
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginContextIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginContext is not null
        defaultUserLoginHistoryFiltering("loginContext.specified=true", "loginContext.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginContextContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginContext contains
        defaultUserLoginHistoryFiltering(
            "loginContext.contains=" + DEFAULT_LOGIN_CONTEXT,
            "loginContext.contains=" + UPDATED_LOGIN_CONTEXT
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginContextNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginContext does not contain
        defaultUserLoginHistoryFiltering(
            "loginContext.doesNotContain=" + UPDATED_LOGIN_CONTEXT,
            "loginContext.doesNotContain=" + DEFAULT_LOGIN_CONTEXT
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginStatus equals to
        defaultUserLoginHistoryFiltering("loginStatus.equals=" + DEFAULT_LOGIN_STATUS, "loginStatus.equals=" + UPDATED_LOGIN_STATUS);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginStatus in
        defaultUserLoginHistoryFiltering(
            "loginStatus.in=" + DEFAULT_LOGIN_STATUS + "," + UPDATED_LOGIN_STATUS,
            "loginStatus.in=" + UPDATED_LOGIN_STATUS
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByLoginStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where loginStatus is not null
        defaultUserLoginHistoryFiltering("loginStatus.specified=true", "loginStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByAttemptedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where attemptedAt equals to
        defaultUserLoginHistoryFiltering("attemptedAt.equals=" + DEFAULT_ATTEMPTED_AT, "attemptedAt.equals=" + UPDATED_ATTEMPTED_AT);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByAttemptedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where attemptedAt in
        defaultUserLoginHistoryFiltering(
            "attemptedAt.in=" + DEFAULT_ATTEMPTED_AT + "," + UPDATED_ATTEMPTED_AT,
            "attemptedAt.in=" + UPDATED_ATTEMPTED_AT
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByAttemptedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where attemptedAt is not null
        defaultUserLoginHistoryFiltering("attemptedAt.specified=true", "attemptedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where ipAddress equals to
        defaultUserLoginHistoryFiltering("ipAddress.equals=" + DEFAULT_IP_ADDRESS, "ipAddress.equals=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where ipAddress in
        defaultUserLoginHistoryFiltering(
            "ipAddress.in=" + DEFAULT_IP_ADDRESS + "," + UPDATED_IP_ADDRESS,
            "ipAddress.in=" + UPDATED_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where ipAddress is not null
        defaultUserLoginHistoryFiltering("ipAddress.specified=true", "ipAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where ipAddress contains
        defaultUserLoginHistoryFiltering("ipAddress.contains=" + DEFAULT_IP_ADDRESS, "ipAddress.contains=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where ipAddress does not contain
        defaultUserLoginHistoryFiltering(
            "ipAddress.doesNotContain=" + UPDATED_IP_ADDRESS,
            "ipAddress.doesNotContain=" + DEFAULT_IP_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByBrowserIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where browser equals to
        defaultUserLoginHistoryFiltering("browser.equals=" + DEFAULT_BROWSER, "browser.equals=" + UPDATED_BROWSER);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByBrowserIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where browser in
        defaultUserLoginHistoryFiltering("browser.in=" + DEFAULT_BROWSER + "," + UPDATED_BROWSER, "browser.in=" + UPDATED_BROWSER);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByBrowserIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where browser is not null
        defaultUserLoginHistoryFiltering("browser.specified=true", "browser.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByBrowserContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where browser contains
        defaultUserLoginHistoryFiltering("browser.contains=" + DEFAULT_BROWSER, "browser.contains=" + UPDATED_BROWSER);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByBrowserNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where browser does not contain
        defaultUserLoginHistoryFiltering("browser.doesNotContain=" + UPDATED_BROWSER, "browser.doesNotContain=" + DEFAULT_BROWSER);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByOperatingSystemIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where operatingSystem equals to
        defaultUserLoginHistoryFiltering(
            "operatingSystem.equals=" + DEFAULT_OPERATING_SYSTEM,
            "operatingSystem.equals=" + UPDATED_OPERATING_SYSTEM
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByOperatingSystemIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where operatingSystem in
        defaultUserLoginHistoryFiltering(
            "operatingSystem.in=" + DEFAULT_OPERATING_SYSTEM + "," + UPDATED_OPERATING_SYSTEM,
            "operatingSystem.in=" + UPDATED_OPERATING_SYSTEM
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByOperatingSystemIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where operatingSystem is not null
        defaultUserLoginHistoryFiltering("operatingSystem.specified=true", "operatingSystem.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByOperatingSystemContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where operatingSystem contains
        defaultUserLoginHistoryFiltering(
            "operatingSystem.contains=" + DEFAULT_OPERATING_SYSTEM,
            "operatingSystem.contains=" + UPDATED_OPERATING_SYSTEM
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesByOperatingSystemNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where operatingSystem does not contain
        defaultUserLoginHistoryFiltering(
            "operatingSystem.doesNotContain=" + UPDATED_OPERATING_SYSTEM,
            "operatingSystem.doesNotContain=" + DEFAULT_OPERATING_SYSTEM
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesBySessionHashIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where sessionHash equals to
        defaultUserLoginHistoryFiltering("sessionHash.equals=" + DEFAULT_SESSION_HASH, "sessionHash.equals=" + UPDATED_SESSION_HASH);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesBySessionHashIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where sessionHash in
        defaultUserLoginHistoryFiltering(
            "sessionHash.in=" + DEFAULT_SESSION_HASH + "," + UPDATED_SESSION_HASH,
            "sessionHash.in=" + UPDATED_SESSION_HASH
        );
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesBySessionHashIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where sessionHash is not null
        defaultUserLoginHistoryFiltering("sessionHash.specified=true", "sessionHash.specified=false");
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesBySessionHashContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where sessionHash contains
        defaultUserLoginHistoryFiltering("sessionHash.contains=" + DEFAULT_SESSION_HASH, "sessionHash.contains=" + UPDATED_SESSION_HASH);
    }

    @Test
    @Transactional
    void getAllUserLoginHistoriesBySessionHashNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        // Get all the userLoginHistoryList where sessionHash does not contain
        defaultUserLoginHistoryFiltering(
            "sessionHash.doesNotContain=" + UPDATED_SESSION_HASH,
            "sessionHash.doesNotContain=" + DEFAULT_SESSION_HASH
        );
    }

    private void defaultUserLoginHistoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUserLoginHistoryShouldBeFound(shouldBeFound);
        defaultUserLoginHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserLoginHistoryShouldBeFound(String filter) throws Exception {
        restUserLoginHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userLoginHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].loginId").value(hasItem(DEFAULT_LOGIN_ID)))
            .andExpect(jsonPath("$.[*].loginContext").value(hasItem(DEFAULT_LOGIN_CONTEXT)))
            .andExpect(jsonPath("$.[*].loginStatus").value(hasItem(DEFAULT_LOGIN_STATUS.toString())))
            .andExpect(jsonPath("$.[*].attemptedAt").value(hasItem(DEFAULT_ATTEMPTED_AT.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].browser").value(hasItem(DEFAULT_BROWSER)))
            .andExpect(jsonPath("$.[*].operatingSystem").value(hasItem(DEFAULT_OPERATING_SYSTEM)))
            .andExpect(jsonPath("$.[*].sessionHash").value(hasItem(DEFAULT_SESSION_HASH)));

        // Check, that the count call also returns 1
        restUserLoginHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserLoginHistoryShouldNotBeFound(String filter) throws Exception {
        restUserLoginHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserLoginHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserLoginHistory() throws Exception {
        // Get the userLoginHistory
        restUserLoginHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserLoginHistory() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userLoginHistory
        UserLoginHistory updatedUserLoginHistory = userLoginHistoryRepository.findById(userLoginHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserLoginHistory are not directly saved in db
        em.detach(updatedUserLoginHistory);
        updatedUserLoginHistory
            .loginId(UPDATED_LOGIN_ID)
            .loginContext(UPDATED_LOGIN_CONTEXT)
            .loginStatus(UPDATED_LOGIN_STATUS)
            .attemptedAt(UPDATED_ATTEMPTED_AT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .browser(UPDATED_BROWSER)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .sessionHash(UPDATED_SESSION_HASH);
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(updatedUserLoginHistory);

        restUserLoginHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userLoginHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userLoginHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserLoginHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserLoginHistoryToMatchAllProperties(updatedUserLoginHistory);
    }

    @Test
    @Transactional
    void putNonExistingUserLoginHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLoginHistory.setId(longCount.incrementAndGet());

        // Create the UserLoginHistory
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserLoginHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userLoginHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userLoginHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLoginHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserLoginHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLoginHistory.setId(longCount.incrementAndGet());

        // Create the UserLoginHistory
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLoginHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userLoginHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLoginHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserLoginHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLoginHistory.setId(longCount.incrementAndGet());

        // Create the UserLoginHistory
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLoginHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userLoginHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserLoginHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserLoginHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userLoginHistory using partial update
        UserLoginHistory partialUpdatedUserLoginHistory = new UserLoginHistory();
        partialUpdatedUserLoginHistory.setId(userLoginHistory.getId());

        partialUpdatedUserLoginHistory
            .loginId(UPDATED_LOGIN_ID)
            .loginContext(UPDATED_LOGIN_CONTEXT)
            .attemptedAt(UPDATED_ATTEMPTED_AT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .browser(UPDATED_BROWSER)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .sessionHash(UPDATED_SESSION_HASH);

        restUserLoginHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserLoginHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserLoginHistory))
            )
            .andExpect(status().isOk());

        // Validate the UserLoginHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserLoginHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserLoginHistory, userLoginHistory),
            getPersistedUserLoginHistory(userLoginHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserLoginHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userLoginHistory using partial update
        UserLoginHistory partialUpdatedUserLoginHistory = new UserLoginHistory();
        partialUpdatedUserLoginHistory.setId(userLoginHistory.getId());

        partialUpdatedUserLoginHistory
            .loginId(UPDATED_LOGIN_ID)
            .loginContext(UPDATED_LOGIN_CONTEXT)
            .loginStatus(UPDATED_LOGIN_STATUS)
            .attemptedAt(UPDATED_ATTEMPTED_AT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .browser(UPDATED_BROWSER)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .sessionHash(UPDATED_SESSION_HASH);

        restUserLoginHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserLoginHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserLoginHistory))
            )
            .andExpect(status().isOk());

        // Validate the UserLoginHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserLoginHistoryUpdatableFieldsEquals(
            partialUpdatedUserLoginHistory,
            getPersistedUserLoginHistory(partialUpdatedUserLoginHistory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingUserLoginHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLoginHistory.setId(longCount.incrementAndGet());

        // Create the UserLoginHistory
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserLoginHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userLoginHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userLoginHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLoginHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserLoginHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLoginHistory.setId(longCount.incrementAndGet());

        // Create the UserLoginHistory
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLoginHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userLoginHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserLoginHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserLoginHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userLoginHistory.setId(longCount.incrementAndGet());

        // Create the UserLoginHistory
        UserLoginHistoryDTO userLoginHistoryDTO = userLoginHistoryMapper.toDto(userLoginHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserLoginHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userLoginHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserLoginHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserLoginHistory() throws Exception {
        // Initialize the database
        insertedUserLoginHistory = userLoginHistoryRepository.saveAndFlush(userLoginHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userLoginHistory
        restUserLoginHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, userLoginHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userLoginHistoryRepository.count();
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

    protected UserLoginHistory getPersistedUserLoginHistory(UserLoginHistory userLoginHistory) {
        return userLoginHistoryRepository.findById(userLoginHistory.getId()).orElseThrow();
    }

    protected void assertPersistedUserLoginHistoryToMatchAllProperties(UserLoginHistory expectedUserLoginHistory) {
        assertUserLoginHistoryAllPropertiesEquals(expectedUserLoginHistory, getPersistedUserLoginHistory(expectedUserLoginHistory));
    }

    protected void assertPersistedUserLoginHistoryToMatchUpdatableProperties(UserLoginHistory expectedUserLoginHistory) {
        assertUserLoginHistoryAllUpdatablePropertiesEquals(
            expectedUserLoginHistory,
            getPersistedUserLoginHistory(expectedUserLoginHistory)
        );
    }
}
