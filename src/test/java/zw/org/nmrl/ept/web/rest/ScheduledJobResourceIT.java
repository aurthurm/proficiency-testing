package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.ScheduledJobAsserts.*;
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
import zw.org.nmrl.ept.domain.ScheduledJob;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.domain.enumeration.JobType;
import zw.org.nmrl.ept.repository.ScheduledJobRepository;
import zw.org.nmrl.ept.service.dto.ScheduledJobDTO;
import zw.org.nmrl.ept.service.mapper.ScheduledJobMapper;

/**
 * Integration tests for the {@link ScheduledJobResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScheduledJobResourceIT {

    private static final JobType DEFAULT_JOB_TYPE = JobType.EVALUATION;
    private static final JobType UPDATED_JOB_TYPE = JobType.REPORT_GENERATION;

    private static final JobStatus DEFAULT_STATUS = JobStatus.PENDING;
    private static final JobStatus UPDATED_STATUS = JobStatus.RUNNING;

    private static final String DEFAULT_REQUESTED_BY = "AAAAAAAAAA";
    private static final String UPDATED_REQUESTED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_REQUESTED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUESTED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_HEARTBEAT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_HEARTBEAT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_PROGRESS_COMPLETED = 1;
    private static final Integer UPDATED_PROGRESS_COMPLETED = 2;
    private static final Integer SMALLER_PROGRESS_COMPLETED = 1 - 1;

    private static final Integer DEFAULT_PROGRESS_TOTAL = 1;
    private static final Integer UPDATED_PROGRESS_TOTAL = 2;
    private static final Integer SMALLER_PROGRESS_TOTAL = 1 - 1;

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/scheduled-jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScheduledJobRepository scheduledJobRepository;

    @Autowired
    private ScheduledJobMapper scheduledJobMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduledJobMockMvc;

    private ScheduledJob scheduledJob;

    private ScheduledJob insertedScheduledJob;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduledJob createEntity() {
        return new ScheduledJob()
            .jobType(DEFAULT_JOB_TYPE)
            .status(DEFAULT_STATUS)
            .requestedBy(DEFAULT_REQUESTED_BY)
            .requestedOn(DEFAULT_REQUESTED_ON)
            .startedAt(DEFAULT_STARTED_AT)
            .lastHeartbeat(DEFAULT_LAST_HEARTBEAT)
            .completedAt(DEFAULT_COMPLETED_AT)
            .progressCompleted(DEFAULT_PROGRESS_COMPLETED)
            .progressTotal(DEFAULT_PROGRESS_TOTAL)
            .summary(DEFAULT_SUMMARY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduledJob createUpdatedEntity() {
        return new ScheduledJob()
            .jobType(UPDATED_JOB_TYPE)
            .status(UPDATED_STATUS)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedOn(UPDATED_REQUESTED_ON)
            .startedAt(UPDATED_STARTED_AT)
            .lastHeartbeat(UPDATED_LAST_HEARTBEAT)
            .completedAt(UPDATED_COMPLETED_AT)
            .progressCompleted(UPDATED_PROGRESS_COMPLETED)
            .progressTotal(UPDATED_PROGRESS_TOTAL)
            .summary(UPDATED_SUMMARY);
    }

    @BeforeEach
    void initTest() {
        scheduledJob = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedScheduledJob != null) {
            scheduledJobRepository.delete(insertedScheduledJob);
            insertedScheduledJob = null;
        }
    }

    @Test
    @Transactional
    void createScheduledJob() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ScheduledJob
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);
        var returnedScheduledJobDTO = om.readValue(
            restScheduledJobMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduledJobDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScheduledJobDTO.class
        );

        // Validate the ScheduledJob in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScheduledJob = scheduledJobMapper.toEntity(returnedScheduledJobDTO);
        assertScheduledJobUpdatableFieldsEquals(returnedScheduledJob, getPersistedScheduledJob(returnedScheduledJob));

        insertedScheduledJob = returnedScheduledJob;
    }

    @Test
    @Transactional
    void createScheduledJobWithExistingId() throws Exception {
        // Create the ScheduledJob with an existing ID
        scheduledJob.setId(1L);
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduledJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduledJobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ScheduledJob in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkJobTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduledJob.setJobType(null);

        // Create the ScheduledJob, which fails.
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        restScheduledJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduledJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scheduledJob.setStatus(null);

        // Create the ScheduledJob, which fails.
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        restScheduledJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduledJobDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScheduledJobs() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList
        restScheduledJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduledJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobType").value(hasItem(DEFAULT_JOB_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].requestedBy").value(hasItem(DEFAULT_REQUESTED_BY)))
            .andExpect(jsonPath("$.[*].requestedOn").value(hasItem(DEFAULT_REQUESTED_ON.toString())))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastHeartbeat").value(hasItem(DEFAULT_LAST_HEARTBEAT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].progressCompleted").value(hasItem(DEFAULT_PROGRESS_COMPLETED)))
            .andExpect(jsonPath("$.[*].progressTotal").value(hasItem(DEFAULT_PROGRESS_TOTAL)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)));
    }

    @Test
    @Transactional
    void getScheduledJob() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get the scheduledJob
        restScheduledJobMockMvc
            .perform(get(ENTITY_API_URL_ID, scheduledJob.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scheduledJob.getId().intValue()))
            .andExpect(jsonPath("$.jobType").value(DEFAULT_JOB_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.requestedBy").value(DEFAULT_REQUESTED_BY))
            .andExpect(jsonPath("$.requestedOn").value(DEFAULT_REQUESTED_ON.toString()))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.lastHeartbeat").value(DEFAULT_LAST_HEARTBEAT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()))
            .andExpect(jsonPath("$.progressCompleted").value(DEFAULT_PROGRESS_COMPLETED))
            .andExpect(jsonPath("$.progressTotal").value(DEFAULT_PROGRESS_TOTAL))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY));
    }

    @Test
    @Transactional
    void getScheduledJobsByIdFiltering() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        Long id = scheduledJob.getId();

        defaultScheduledJobFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultScheduledJobFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultScheduledJobFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByJobTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where jobType equals to
        defaultScheduledJobFiltering("jobType.equals=" + DEFAULT_JOB_TYPE, "jobType.equals=" + UPDATED_JOB_TYPE);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByJobTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where jobType in
        defaultScheduledJobFiltering("jobType.in=" + DEFAULT_JOB_TYPE + "," + UPDATED_JOB_TYPE, "jobType.in=" + UPDATED_JOB_TYPE);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByJobTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where jobType is not null
        defaultScheduledJobFiltering("jobType.specified=true", "jobType.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where status equals to
        defaultScheduledJobFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where status in
        defaultScheduledJobFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where status is not null
        defaultScheduledJobFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByRequestedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where requestedBy equals to
        defaultScheduledJobFiltering("requestedBy.equals=" + DEFAULT_REQUESTED_BY, "requestedBy.equals=" + UPDATED_REQUESTED_BY);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByRequestedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where requestedBy in
        defaultScheduledJobFiltering(
            "requestedBy.in=" + DEFAULT_REQUESTED_BY + "," + UPDATED_REQUESTED_BY,
            "requestedBy.in=" + UPDATED_REQUESTED_BY
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByRequestedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where requestedBy is not null
        defaultScheduledJobFiltering("requestedBy.specified=true", "requestedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByRequestedByContainsSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where requestedBy contains
        defaultScheduledJobFiltering("requestedBy.contains=" + DEFAULT_REQUESTED_BY, "requestedBy.contains=" + UPDATED_REQUESTED_BY);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByRequestedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where requestedBy does not contain
        defaultScheduledJobFiltering(
            "requestedBy.doesNotContain=" + UPDATED_REQUESTED_BY,
            "requestedBy.doesNotContain=" + DEFAULT_REQUESTED_BY
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByRequestedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where requestedOn equals to
        defaultScheduledJobFiltering("requestedOn.equals=" + DEFAULT_REQUESTED_ON, "requestedOn.equals=" + UPDATED_REQUESTED_ON);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByRequestedOnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where requestedOn in
        defaultScheduledJobFiltering(
            "requestedOn.in=" + DEFAULT_REQUESTED_ON + "," + UPDATED_REQUESTED_ON,
            "requestedOn.in=" + UPDATED_REQUESTED_ON
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByRequestedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where requestedOn is not null
        defaultScheduledJobFiltering("requestedOn.specified=true", "requestedOn.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByStartedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where startedAt equals to
        defaultScheduledJobFiltering("startedAt.equals=" + DEFAULT_STARTED_AT, "startedAt.equals=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByStartedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where startedAt in
        defaultScheduledJobFiltering("startedAt.in=" + DEFAULT_STARTED_AT + "," + UPDATED_STARTED_AT, "startedAt.in=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByStartedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where startedAt is not null
        defaultScheduledJobFiltering("startedAt.specified=true", "startedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByLastHeartbeatIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where lastHeartbeat equals to
        defaultScheduledJobFiltering("lastHeartbeat.equals=" + DEFAULT_LAST_HEARTBEAT, "lastHeartbeat.equals=" + UPDATED_LAST_HEARTBEAT);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByLastHeartbeatIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where lastHeartbeat in
        defaultScheduledJobFiltering(
            "lastHeartbeat.in=" + DEFAULT_LAST_HEARTBEAT + "," + UPDATED_LAST_HEARTBEAT,
            "lastHeartbeat.in=" + UPDATED_LAST_HEARTBEAT
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByLastHeartbeatIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where lastHeartbeat is not null
        defaultScheduledJobFiltering("lastHeartbeat.specified=true", "lastHeartbeat.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByCompletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where completedAt equals to
        defaultScheduledJobFiltering("completedAt.equals=" + DEFAULT_COMPLETED_AT, "completedAt.equals=" + UPDATED_COMPLETED_AT);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByCompletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where completedAt in
        defaultScheduledJobFiltering(
            "completedAt.in=" + DEFAULT_COMPLETED_AT + "," + UPDATED_COMPLETED_AT,
            "completedAt.in=" + UPDATED_COMPLETED_AT
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByCompletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where completedAt is not null
        defaultScheduledJobFiltering("completedAt.specified=true", "completedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressCompletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressCompleted equals to
        defaultScheduledJobFiltering(
            "progressCompleted.equals=" + DEFAULT_PROGRESS_COMPLETED,
            "progressCompleted.equals=" + UPDATED_PROGRESS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressCompletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressCompleted in
        defaultScheduledJobFiltering(
            "progressCompleted.in=" + DEFAULT_PROGRESS_COMPLETED + "," + UPDATED_PROGRESS_COMPLETED,
            "progressCompleted.in=" + UPDATED_PROGRESS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressCompletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressCompleted is not null
        defaultScheduledJobFiltering("progressCompleted.specified=true", "progressCompleted.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressCompletedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressCompleted is greater than or equal to
        defaultScheduledJobFiltering(
            "progressCompleted.greaterThanOrEqual=" + DEFAULT_PROGRESS_COMPLETED,
            "progressCompleted.greaterThanOrEqual=" + UPDATED_PROGRESS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressCompletedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressCompleted is less than or equal to
        defaultScheduledJobFiltering(
            "progressCompleted.lessThanOrEqual=" + DEFAULT_PROGRESS_COMPLETED,
            "progressCompleted.lessThanOrEqual=" + SMALLER_PROGRESS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressCompletedIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressCompleted is less than
        defaultScheduledJobFiltering(
            "progressCompleted.lessThan=" + UPDATED_PROGRESS_COMPLETED,
            "progressCompleted.lessThan=" + DEFAULT_PROGRESS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressCompletedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressCompleted is greater than
        defaultScheduledJobFiltering(
            "progressCompleted.greaterThan=" + SMALLER_PROGRESS_COMPLETED,
            "progressCompleted.greaterThan=" + DEFAULT_PROGRESS_COMPLETED
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressTotal equals to
        defaultScheduledJobFiltering("progressTotal.equals=" + DEFAULT_PROGRESS_TOTAL, "progressTotal.equals=" + UPDATED_PROGRESS_TOTAL);
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressTotal in
        defaultScheduledJobFiltering(
            "progressTotal.in=" + DEFAULT_PROGRESS_TOTAL + "," + UPDATED_PROGRESS_TOTAL,
            "progressTotal.in=" + UPDATED_PROGRESS_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressTotal is not null
        defaultScheduledJobFiltering("progressTotal.specified=true", "progressTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressTotal is greater than or equal to
        defaultScheduledJobFiltering(
            "progressTotal.greaterThanOrEqual=" + DEFAULT_PROGRESS_TOTAL,
            "progressTotal.greaterThanOrEqual=" + UPDATED_PROGRESS_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressTotal is less than or equal to
        defaultScheduledJobFiltering(
            "progressTotal.lessThanOrEqual=" + DEFAULT_PROGRESS_TOTAL,
            "progressTotal.lessThanOrEqual=" + SMALLER_PROGRESS_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressTotal is less than
        defaultScheduledJobFiltering(
            "progressTotal.lessThan=" + UPDATED_PROGRESS_TOTAL,
            "progressTotal.lessThan=" + DEFAULT_PROGRESS_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllScheduledJobsByProgressTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        // Get all the scheduledJobList where progressTotal is greater than
        defaultScheduledJobFiltering(
            "progressTotal.greaterThan=" + SMALLER_PROGRESS_TOTAL,
            "progressTotal.greaterThan=" + DEFAULT_PROGRESS_TOTAL
        );
    }

    private void defaultScheduledJobFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultScheduledJobShouldBeFound(shouldBeFound);
        defaultScheduledJobShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScheduledJobShouldBeFound(String filter) throws Exception {
        restScheduledJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduledJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobType").value(hasItem(DEFAULT_JOB_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].requestedBy").value(hasItem(DEFAULT_REQUESTED_BY)))
            .andExpect(jsonPath("$.[*].requestedOn").value(hasItem(DEFAULT_REQUESTED_ON.toString())))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastHeartbeat").value(hasItem(DEFAULT_LAST_HEARTBEAT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].progressCompleted").value(hasItem(DEFAULT_PROGRESS_COMPLETED)))
            .andExpect(jsonPath("$.[*].progressTotal").value(hasItem(DEFAULT_PROGRESS_TOTAL)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)));

        // Check, that the count call also returns 1
        restScheduledJobMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScheduledJobShouldNotBeFound(String filter) throws Exception {
        restScheduledJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScheduledJobMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScheduledJob() throws Exception {
        // Get the scheduledJob
        restScheduledJobMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScheduledJob() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduledJob
        ScheduledJob updatedScheduledJob = scheduledJobRepository.findById(scheduledJob.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScheduledJob are not directly saved in db
        em.detach(updatedScheduledJob);
        updatedScheduledJob
            .jobType(UPDATED_JOB_TYPE)
            .status(UPDATED_STATUS)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedOn(UPDATED_REQUESTED_ON)
            .startedAt(UPDATED_STARTED_AT)
            .lastHeartbeat(UPDATED_LAST_HEARTBEAT)
            .completedAt(UPDATED_COMPLETED_AT)
            .progressCompleted(UPDATED_PROGRESS_COMPLETED)
            .progressTotal(UPDATED_PROGRESS_TOTAL)
            .summary(UPDATED_SUMMARY);
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(updatedScheduledJob);

        restScheduledJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduledJobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduledJobDTO))
            )
            .andExpect(status().isOk());

        // Validate the ScheduledJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScheduledJobToMatchAllProperties(updatedScheduledJob);
    }

    @Test
    @Transactional
    void putNonExistingScheduledJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduledJob.setId(longCount.incrementAndGet());

        // Create the ScheduledJob
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduledJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduledJobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduledJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduledJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScheduledJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduledJob.setId(longCount.incrementAndGet());

        // Create the ScheduledJob
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduledJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scheduledJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduledJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScheduledJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduledJob.setId(longCount.incrementAndGet());

        // Create the ScheduledJob
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduledJobMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scheduledJobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduledJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScheduledJobWithPatch() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduledJob using partial update
        ScheduledJob partialUpdatedScheduledJob = new ScheduledJob();
        partialUpdatedScheduledJob.setId(scheduledJob.getId());

        partialUpdatedScheduledJob
            .jobType(UPDATED_JOB_TYPE)
            .status(UPDATED_STATUS)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedOn(UPDATED_REQUESTED_ON)
            .startedAt(UPDATED_STARTED_AT)
            .lastHeartbeat(UPDATED_LAST_HEARTBEAT)
            .completedAt(UPDATED_COMPLETED_AT)
            .summary(UPDATED_SUMMARY);

        restScheduledJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduledJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduledJob))
            )
            .andExpect(status().isOk());

        // Validate the ScheduledJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduledJobUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedScheduledJob, scheduledJob),
            getPersistedScheduledJob(scheduledJob)
        );
    }

    @Test
    @Transactional
    void fullUpdateScheduledJobWithPatch() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scheduledJob using partial update
        ScheduledJob partialUpdatedScheduledJob = new ScheduledJob();
        partialUpdatedScheduledJob.setId(scheduledJob.getId());

        partialUpdatedScheduledJob
            .jobType(UPDATED_JOB_TYPE)
            .status(UPDATED_STATUS)
            .requestedBy(UPDATED_REQUESTED_BY)
            .requestedOn(UPDATED_REQUESTED_ON)
            .startedAt(UPDATED_STARTED_AT)
            .lastHeartbeat(UPDATED_LAST_HEARTBEAT)
            .completedAt(UPDATED_COMPLETED_AT)
            .progressCompleted(UPDATED_PROGRESS_COMPLETED)
            .progressTotal(UPDATED_PROGRESS_TOTAL)
            .summary(UPDATED_SUMMARY);

        restScheduledJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScheduledJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScheduledJob))
            )
            .andExpect(status().isOk());

        // Validate the ScheduledJob in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScheduledJobUpdatableFieldsEquals(partialUpdatedScheduledJob, getPersistedScheduledJob(partialUpdatedScheduledJob));
    }

    @Test
    @Transactional
    void patchNonExistingScheduledJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduledJob.setId(longCount.incrementAndGet());

        // Create the ScheduledJob
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduledJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduledJobDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduledJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduledJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScheduledJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduledJob.setId(longCount.incrementAndGet());

        // Create the ScheduledJob
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduledJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scheduledJobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScheduledJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScheduledJob() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scheduledJob.setId(longCount.incrementAndGet());

        // Create the ScheduledJob
        ScheduledJobDTO scheduledJobDTO = scheduledJobMapper.toDto(scheduledJob);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduledJobMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(scheduledJobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScheduledJob in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScheduledJob() throws Exception {
        // Initialize the database
        insertedScheduledJob = scheduledJobRepository.saveAndFlush(scheduledJob);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scheduledJob
        restScheduledJobMockMvc
            .perform(delete(ENTITY_API_URL_ID, scheduledJob.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scheduledJobRepository.count();
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

    protected ScheduledJob getPersistedScheduledJob(ScheduledJob scheduledJob) {
        return scheduledJobRepository.findById(scheduledJob.getId()).orElseThrow();
    }

    protected void assertPersistedScheduledJobToMatchAllProperties(ScheduledJob expectedScheduledJob) {
        assertScheduledJobAllPropertiesEquals(expectedScheduledJob, getPersistedScheduledJob(expectedScheduledJob));
    }

    protected void assertPersistedScheduledJobToMatchUpdatableProperties(ScheduledJob expectedScheduledJob) {
        assertScheduledJobAllUpdatablePropertiesEquals(expectedScheduledJob, getPersistedScheduledJob(expectedScheduledJob));
    }
}
