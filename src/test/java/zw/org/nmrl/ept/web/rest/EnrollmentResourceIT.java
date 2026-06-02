package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.EnrollmentAsserts.*;
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
import zw.org.nmrl.ept.domain.Enrollment;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.domain.enumeration.EnrollmentStatus;
import zw.org.nmrl.ept.repository.EnrollmentRepository;
import zw.org.nmrl.ept.service.dto.EnrollmentDTO;
import zw.org.nmrl.ept.service.mapper.EnrollmentMapper;

/**
 * Integration tests for the {@link EnrollmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnrollmentResourceIT {

    private static final EnrollmentStatus DEFAULT_STATUS = EnrollmentStatus.ENROLLED;
    private static final EnrollmentStatus UPDATED_STATUS = EnrollmentStatus.SUSPENDED;

    private static final LocalDate DEFAULT_ENROLLED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ENROLLED_ON = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ENROLLED_ON = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_WITHDRAWN_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_WITHDRAWN_ON = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_WITHDRAWN_ON = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/enrollments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnrollmentMockMvc;

    private Enrollment enrollment;

    private Enrollment insertedEnrollment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enrollment createEntity(EntityManager em) {
        Enrollment enrollment = new Enrollment().status(DEFAULT_STATUS).enrolledOn(DEFAULT_ENROLLED_ON).withdrawnOn(DEFAULT_WITHDRAWN_ON);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        enrollment.setParticipant(participant);
        // Add required entity
        Scheme scheme;
        if (TestUtil.findAll(em, Scheme.class).isEmpty()) {
            scheme = SchemeResourceIT.createEntity();
            em.persist(scheme);
            em.flush();
        } else {
            scheme = TestUtil.findAll(em, Scheme.class).get(0);
        }
        enrollment.setScheme(scheme);
        return enrollment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enrollment createUpdatedEntity(EntityManager em) {
        Enrollment updatedEnrollment = new Enrollment()
            .status(UPDATED_STATUS)
            .enrolledOn(UPDATED_ENROLLED_ON)
            .withdrawnOn(UPDATED_WITHDRAWN_ON);
        // Add required entity
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            participant = ParticipantResourceIT.createUpdatedEntity();
            em.persist(participant);
            em.flush();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        updatedEnrollment.setParticipant(participant);
        // Add required entity
        Scheme scheme;
        if (TestUtil.findAll(em, Scheme.class).isEmpty()) {
            scheme = SchemeResourceIT.createUpdatedEntity();
            em.persist(scheme);
            em.flush();
        } else {
            scheme = TestUtil.findAll(em, Scheme.class).get(0);
        }
        updatedEnrollment.setScheme(scheme);
        return updatedEnrollment;
    }

    @BeforeEach
    void initTest() {
        enrollment = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedEnrollment != null) {
            enrollmentRepository.delete(insertedEnrollment);
            insertedEnrollment = null;
        }
    }

    @Test
    @Transactional
    void createEnrollment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);
        var returnedEnrollmentDTO = om.readValue(
            restEnrollmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(enrollmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EnrollmentDTO.class
        );

        // Validate the Enrollment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEnrollment = enrollmentMapper.toEntity(returnedEnrollmentDTO);
        assertEnrollmentUpdatableFieldsEquals(returnedEnrollment, getPersistedEnrollment(returnedEnrollment));

        insertedEnrollment = returnedEnrollment;
    }

    @Test
    @Transactional
    void createEnrollmentWithExistingId() throws Exception {
        // Create the Enrollment with an existing ID
        enrollment.setId(1L);
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnrollmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(enrollmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        enrollment.setStatus(null);

        // Create the Enrollment, which fails.
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        restEnrollmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(enrollmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEnrolledOnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        enrollment.setEnrolledOn(null);

        // Create the Enrollment, which fails.
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        restEnrollmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(enrollmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEnrollments() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enrollment.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].enrolledOn").value(hasItem(DEFAULT_ENROLLED_ON.toString())))
            .andExpect(jsonPath("$.[*].withdrawnOn").value(hasItem(DEFAULT_WITHDRAWN_ON.toString())));
    }

    @Test
    @Transactional
    void getEnrollment() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get the enrollment
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL_ID, enrollment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(enrollment.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.enrolledOn").value(DEFAULT_ENROLLED_ON.toString()))
            .andExpect(jsonPath("$.withdrawnOn").value(DEFAULT_WITHDRAWN_ON.toString()));
    }

    @Test
    @Transactional
    void getEnrollmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        Long id = enrollment.getId();

        defaultEnrollmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEnrollmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEnrollmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where status equals to
        defaultEnrollmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where status in
        defaultEnrollmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where status is not null
        defaultEnrollmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrolledOnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrolledOn equals to
        defaultEnrollmentFiltering("enrolledOn.equals=" + DEFAULT_ENROLLED_ON, "enrolledOn.equals=" + UPDATED_ENROLLED_ON);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrolledOnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrolledOn in
        defaultEnrollmentFiltering(
            "enrolledOn.in=" + DEFAULT_ENROLLED_ON + "," + UPDATED_ENROLLED_ON,
            "enrolledOn.in=" + UPDATED_ENROLLED_ON
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrolledOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrolledOn is not null
        defaultEnrollmentFiltering("enrolledOn.specified=true", "enrolledOn.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrolledOnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrolledOn is greater than or equal to
        defaultEnrollmentFiltering(
            "enrolledOn.greaterThanOrEqual=" + DEFAULT_ENROLLED_ON,
            "enrolledOn.greaterThanOrEqual=" + UPDATED_ENROLLED_ON
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrolledOnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrolledOn is less than or equal to
        defaultEnrollmentFiltering(
            "enrolledOn.lessThanOrEqual=" + DEFAULT_ENROLLED_ON,
            "enrolledOn.lessThanOrEqual=" + SMALLER_ENROLLED_ON
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrolledOnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrolledOn is less than
        defaultEnrollmentFiltering("enrolledOn.lessThan=" + UPDATED_ENROLLED_ON, "enrolledOn.lessThan=" + DEFAULT_ENROLLED_ON);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByEnrolledOnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where enrolledOn is greater than
        defaultEnrollmentFiltering("enrolledOn.greaterThan=" + SMALLER_ENROLLED_ON, "enrolledOn.greaterThan=" + DEFAULT_ENROLLED_ON);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByWithdrawnOnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where withdrawnOn equals to
        defaultEnrollmentFiltering("withdrawnOn.equals=" + DEFAULT_WITHDRAWN_ON, "withdrawnOn.equals=" + UPDATED_WITHDRAWN_ON);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByWithdrawnOnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where withdrawnOn in
        defaultEnrollmentFiltering(
            "withdrawnOn.in=" + DEFAULT_WITHDRAWN_ON + "," + UPDATED_WITHDRAWN_ON,
            "withdrawnOn.in=" + UPDATED_WITHDRAWN_ON
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByWithdrawnOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where withdrawnOn is not null
        defaultEnrollmentFiltering("withdrawnOn.specified=true", "withdrawnOn.specified=false");
    }

    @Test
    @Transactional
    void getAllEnrollmentsByWithdrawnOnIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where withdrawnOn is greater than or equal to
        defaultEnrollmentFiltering(
            "withdrawnOn.greaterThanOrEqual=" + DEFAULT_WITHDRAWN_ON,
            "withdrawnOn.greaterThanOrEqual=" + UPDATED_WITHDRAWN_ON
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByWithdrawnOnIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where withdrawnOn is less than or equal to
        defaultEnrollmentFiltering(
            "withdrawnOn.lessThanOrEqual=" + DEFAULT_WITHDRAWN_ON,
            "withdrawnOn.lessThanOrEqual=" + SMALLER_WITHDRAWN_ON
        );
    }

    @Test
    @Transactional
    void getAllEnrollmentsByWithdrawnOnIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where withdrawnOn is less than
        defaultEnrollmentFiltering("withdrawnOn.lessThan=" + UPDATED_WITHDRAWN_ON, "withdrawnOn.lessThan=" + DEFAULT_WITHDRAWN_ON);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByWithdrawnOnIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList where withdrawnOn is greater than
        defaultEnrollmentFiltering("withdrawnOn.greaterThan=" + SMALLER_WITHDRAWN_ON, "withdrawnOn.greaterThan=" + DEFAULT_WITHDRAWN_ON);
    }

    @Test
    @Transactional
    void getAllEnrollmentsByParticipantIsEqualToSomething() throws Exception {
        Participant participant;
        if (TestUtil.findAll(em, Participant.class).isEmpty()) {
            enrollmentRepository.saveAndFlush(enrollment);
            participant = ParticipantResourceIT.createEntity();
        } else {
            participant = TestUtil.findAll(em, Participant.class).get(0);
        }
        em.persist(participant);
        em.flush();
        enrollment.setParticipant(participant);
        enrollmentRepository.saveAndFlush(enrollment);
        Long participantId = participant.getId();
        // Get all the enrollmentList where participant equals to participantId
        defaultEnrollmentShouldBeFound("participantId.equals=" + participantId);

        // Get all the enrollmentList where participant equals to (participantId + 1)
        defaultEnrollmentShouldNotBeFound("participantId.equals=" + (participantId + 1));
    }

    @Test
    @Transactional
    void getAllEnrollmentsBySchemeIsEqualToSomething() throws Exception {
        Scheme scheme;
        if (TestUtil.findAll(em, Scheme.class).isEmpty()) {
            enrollmentRepository.saveAndFlush(enrollment);
            scheme = SchemeResourceIT.createEntity();
        } else {
            scheme = TestUtil.findAll(em, Scheme.class).get(0);
        }
        em.persist(scheme);
        em.flush();
        enrollment.setScheme(scheme);
        enrollmentRepository.saveAndFlush(enrollment);
        Long schemeId = scheme.getId();
        // Get all the enrollmentList where scheme equals to schemeId
        defaultEnrollmentShouldBeFound("schemeId.equals=" + schemeId);

        // Get all the enrollmentList where scheme equals to (schemeId + 1)
        defaultEnrollmentShouldNotBeFound("schemeId.equals=" + (schemeId + 1));
    }

    private void defaultEnrollmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEnrollmentShouldBeFound(shouldBeFound);
        defaultEnrollmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEnrollmentShouldBeFound(String filter) throws Exception {
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enrollment.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].enrolledOn").value(hasItem(DEFAULT_ENROLLED_ON.toString())))
            .andExpect(jsonPath("$.[*].withdrawnOn").value(hasItem(DEFAULT_WITHDRAWN_ON.toString())));

        // Check, that the count call also returns 1
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEnrollmentShouldNotBeFound(String filter) throws Exception {
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEnrollment() throws Exception {
        // Get the enrollment
        restEnrollmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEnrollment() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the enrollment
        Enrollment updatedEnrollment = enrollmentRepository.findById(enrollment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEnrollment are not directly saved in db
        em.detach(updatedEnrollment);
        updatedEnrollment.status(UPDATED_STATUS).enrolledOn(UPDATED_ENROLLED_ON).withdrawnOn(UPDATED_WITHDRAWN_ON);
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(updatedEnrollment);

        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enrollmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnrollmentToMatchAllProperties(updatedEnrollment);
    }

    @Test
    @Transactional
    void putNonExistingEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enrollmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(enrollmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnrollmentWithPatch() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the enrollment using partial update
        Enrollment partialUpdatedEnrollment = new Enrollment();
        partialUpdatedEnrollment.setId(enrollment.getId());

        partialUpdatedEnrollment.enrolledOn(UPDATED_ENROLLED_ON);

        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnrollment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnrollmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnrollment, enrollment),
            getPersistedEnrollment(enrollment)
        );
    }

    @Test
    @Transactional
    void fullUpdateEnrollmentWithPatch() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the enrollment using partial update
        Enrollment partialUpdatedEnrollment = new Enrollment();
        partialUpdatedEnrollment.setId(enrollment.getId());

        partialUpdatedEnrollment.status(UPDATED_STATUS).enrolledOn(UPDATED_ENROLLED_ON).withdrawnOn(UPDATED_WITHDRAWN_ON);

        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnrollment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnrollmentUpdatableFieldsEquals(partialUpdatedEnrollment, getPersistedEnrollment(partialUpdatedEnrollment));
    }

    @Test
    @Transactional
    void patchNonExistingEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, enrollmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(enrollmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnrollment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        enrollment.setId(longCount.incrementAndGet());

        // Create the Enrollment
        EnrollmentDTO enrollmentDTO = enrollmentMapper.toDto(enrollment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(enrollmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enrollment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEnrollment() throws Exception {
        // Initialize the database
        insertedEnrollment = enrollmentRepository.saveAndFlush(enrollment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the enrollment
        restEnrollmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, enrollment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return enrollmentRepository.count();
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

    protected Enrollment getPersistedEnrollment(Enrollment enrollment) {
        return enrollmentRepository.findById(enrollment.getId()).orElseThrow();
    }

    protected void assertPersistedEnrollmentToMatchAllProperties(Enrollment expectedEnrollment) {
        assertEnrollmentAllPropertiesEquals(expectedEnrollment, getPersistedEnrollment(expectedEnrollment));
    }

    protected void assertPersistedEnrollmentToMatchUpdatableProperties(Enrollment expectedEnrollment) {
        assertEnrollmentAllUpdatablePropertiesEquals(expectedEnrollment, getPersistedEnrollment(expectedEnrollment));
    }
}
