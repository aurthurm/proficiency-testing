package zw.org.nmrl.ept.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static zw.org.nmrl.ept.domain.EmailMessageAsserts.*;
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
import zw.org.nmrl.ept.domain.EmailMessage;
import zw.org.nmrl.ept.domain.MailTemplate;
import zw.org.nmrl.ept.domain.enumeration.EmailStatus;
import zw.org.nmrl.ept.repository.EmailMessageRepository;
import zw.org.nmrl.ept.service.dto.EmailMessageDTO;
import zw.org.nmrl.ept.service.mapper.EmailMessageMapper;

/**
 * Integration tests for the {@link EmailMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EmailMessageResourceIT {

    private static final String DEFAULT_FROM_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_FROM_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_FROM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FROM_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REPLY_TO = "AAAAAAAAAA";
    private static final String UPDATED_REPLY_TO = "BBBBBBBBBB";

    private static final String DEFAULT_TO_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_TO_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_CC = "AAAAAAAAAA";
    private static final String UPDATED_CC = "BBBBBBBBBB";

    private static final String DEFAULT_BCC = "AAAAAAAAAA";
    private static final String UPDATED_BCC = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_ATTACHMENT_REF = "AAAAAAAAAA";
    private static final String UPDATED_ATTACHMENT_REF = "BBBBBBBBBB";

    private static final EmailStatus DEFAULT_STATUS = EmailStatus.PENDING;
    private static final EmailStatus UPDATED_STATUS = EmailStatus.SENT;

    private static final String DEFAULT_FAILURE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FAILURE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_FAILURE_REASON = "AAAAAAAAAA";
    private static final String UPDATED_FAILURE_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_QUEUED_ON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_QUEUED_ON = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_RETRY_COUNT = 1;
    private static final Integer UPDATED_RETRY_COUNT = 2;
    private static final Integer SMALLER_RETRY_COUNT = 1 - 1;

    private static final String ENTITY_API_URL = "/api/email-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EmailMessageRepository emailMessageRepository;

    @Autowired
    private EmailMessageMapper emailMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmailMessageMockMvc;

    private EmailMessage emailMessage;

    private EmailMessage insertedEmailMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmailMessage createEntity() {
        return new EmailMessage()
            .fromEmail(DEFAULT_FROM_EMAIL)
            .fromName(DEFAULT_FROM_NAME)
            .replyTo(DEFAULT_REPLY_TO)
            .toEmail(DEFAULT_TO_EMAIL)
            .cc(DEFAULT_CC)
            .bcc(DEFAULT_BCC)
            .subject(DEFAULT_SUBJECT)
            .body(DEFAULT_BODY)
            .attachmentRef(DEFAULT_ATTACHMENT_REF)
            .status(DEFAULT_STATUS)
            .failureType(DEFAULT_FAILURE_TYPE)
            .failureReason(DEFAULT_FAILURE_REASON)
            .queuedOn(DEFAULT_QUEUED_ON)
            .sentAt(DEFAULT_SENT_AT)
            .retryCount(DEFAULT_RETRY_COUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmailMessage createUpdatedEntity() {
        return new EmailMessage()
            .fromEmail(UPDATED_FROM_EMAIL)
            .fromName(UPDATED_FROM_NAME)
            .replyTo(UPDATED_REPLY_TO)
            .toEmail(UPDATED_TO_EMAIL)
            .cc(UPDATED_CC)
            .bcc(UPDATED_BCC)
            .subject(UPDATED_SUBJECT)
            .body(UPDATED_BODY)
            .attachmentRef(UPDATED_ATTACHMENT_REF)
            .status(UPDATED_STATUS)
            .failureType(UPDATED_FAILURE_TYPE)
            .failureReason(UPDATED_FAILURE_REASON)
            .queuedOn(UPDATED_QUEUED_ON)
            .sentAt(UPDATED_SENT_AT)
            .retryCount(UPDATED_RETRY_COUNT);
    }

    @BeforeEach
    void initTest() {
        emailMessage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEmailMessage != null) {
            emailMessageRepository.delete(insertedEmailMessage);
            insertedEmailMessage = null;
        }
    }

    @Test
    @Transactional
    void createEmailMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EmailMessage
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);
        var returnedEmailMessageDTO = om.readValue(
            restEmailMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EmailMessageDTO.class
        );

        // Validate the EmailMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEmailMessage = emailMessageMapper.toEntity(returnedEmailMessageDTO);
        assertEmailMessageUpdatableFieldsEquals(returnedEmailMessage, getPersistedEmailMessage(returnedEmailMessage));

        insertedEmailMessage = returnedEmailMessage;
    }

    @Test
    @Transactional
    void createEmailMessageWithExistingId() throws Exception {
        // Create the EmailMessage with an existing ID
        emailMessage.setId(1L);
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmailMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EmailMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkToEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        emailMessage.setToEmail(null);

        // Create the EmailMessage, which fails.
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        restEmailMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        emailMessage.setStatus(null);

        // Create the EmailMessage, which fails.
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        restEmailMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEmailMessages() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList
        restEmailMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emailMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].fromEmail").value(hasItem(DEFAULT_FROM_EMAIL)))
            .andExpect(jsonPath("$.[*].fromName").value(hasItem(DEFAULT_FROM_NAME)))
            .andExpect(jsonPath("$.[*].replyTo").value(hasItem(DEFAULT_REPLY_TO)))
            .andExpect(jsonPath("$.[*].toEmail").value(hasItem(DEFAULT_TO_EMAIL)))
            .andExpect(jsonPath("$.[*].cc").value(hasItem(DEFAULT_CC)))
            .andExpect(jsonPath("$.[*].bcc").value(hasItem(DEFAULT_BCC)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].attachmentRef").value(hasItem(DEFAULT_ATTACHMENT_REF)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].failureType").value(hasItem(DEFAULT_FAILURE_TYPE)))
            .andExpect(jsonPath("$.[*].failureReason").value(hasItem(DEFAULT_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].queuedOn").value(hasItem(DEFAULT_QUEUED_ON.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)));
    }

    @Test
    @Transactional
    void getEmailMessage() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get the emailMessage
        restEmailMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, emailMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(emailMessage.getId().intValue()))
            .andExpect(jsonPath("$.fromEmail").value(DEFAULT_FROM_EMAIL))
            .andExpect(jsonPath("$.fromName").value(DEFAULT_FROM_NAME))
            .andExpect(jsonPath("$.replyTo").value(DEFAULT_REPLY_TO))
            .andExpect(jsonPath("$.toEmail").value(DEFAULT_TO_EMAIL))
            .andExpect(jsonPath("$.cc").value(DEFAULT_CC))
            .andExpect(jsonPath("$.bcc").value(DEFAULT_BCC))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.attachmentRef").value(DEFAULT_ATTACHMENT_REF))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.failureType").value(DEFAULT_FAILURE_TYPE))
            .andExpect(jsonPath("$.failureReason").value(DEFAULT_FAILURE_REASON))
            .andExpect(jsonPath("$.queuedOn").value(DEFAULT_QUEUED_ON.toString()))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()))
            .andExpect(jsonPath("$.retryCount").value(DEFAULT_RETRY_COUNT));
    }

    @Test
    @Transactional
    void getEmailMessagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        Long id = emailMessage.getId();

        defaultEmailMessageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEmailMessageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEmailMessageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromEmail equals to
        defaultEmailMessageFiltering("fromEmail.equals=" + DEFAULT_FROM_EMAIL, "fromEmail.equals=" + UPDATED_FROM_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromEmail in
        defaultEmailMessageFiltering("fromEmail.in=" + DEFAULT_FROM_EMAIL + "," + UPDATED_FROM_EMAIL, "fromEmail.in=" + UPDATED_FROM_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromEmail is not null
        defaultEmailMessageFiltering("fromEmail.specified=true", "fromEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromEmail contains
        defaultEmailMessageFiltering("fromEmail.contains=" + DEFAULT_FROM_EMAIL, "fromEmail.contains=" + UPDATED_FROM_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromEmail does not contain
        defaultEmailMessageFiltering("fromEmail.doesNotContain=" + UPDATED_FROM_EMAIL, "fromEmail.doesNotContain=" + DEFAULT_FROM_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromName equals to
        defaultEmailMessageFiltering("fromName.equals=" + DEFAULT_FROM_NAME, "fromName.equals=" + UPDATED_FROM_NAME);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromName in
        defaultEmailMessageFiltering("fromName.in=" + DEFAULT_FROM_NAME + "," + UPDATED_FROM_NAME, "fromName.in=" + UPDATED_FROM_NAME);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromName is not null
        defaultEmailMessageFiltering("fromName.specified=true", "fromName.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromNameContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromName contains
        defaultEmailMessageFiltering("fromName.contains=" + DEFAULT_FROM_NAME, "fromName.contains=" + UPDATED_FROM_NAME);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFromNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where fromName does not contain
        defaultEmailMessageFiltering("fromName.doesNotContain=" + UPDATED_FROM_NAME, "fromName.doesNotContain=" + DEFAULT_FROM_NAME);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByReplyToIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where replyTo equals to
        defaultEmailMessageFiltering("replyTo.equals=" + DEFAULT_REPLY_TO, "replyTo.equals=" + UPDATED_REPLY_TO);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByReplyToIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where replyTo in
        defaultEmailMessageFiltering("replyTo.in=" + DEFAULT_REPLY_TO + "," + UPDATED_REPLY_TO, "replyTo.in=" + UPDATED_REPLY_TO);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByReplyToIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where replyTo is not null
        defaultEmailMessageFiltering("replyTo.specified=true", "replyTo.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByReplyToContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where replyTo contains
        defaultEmailMessageFiltering("replyTo.contains=" + DEFAULT_REPLY_TO, "replyTo.contains=" + UPDATED_REPLY_TO);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByReplyToNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where replyTo does not contain
        defaultEmailMessageFiltering("replyTo.doesNotContain=" + UPDATED_REPLY_TO, "replyTo.doesNotContain=" + DEFAULT_REPLY_TO);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByToEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where toEmail equals to
        defaultEmailMessageFiltering("toEmail.equals=" + DEFAULT_TO_EMAIL, "toEmail.equals=" + UPDATED_TO_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByToEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where toEmail in
        defaultEmailMessageFiltering("toEmail.in=" + DEFAULT_TO_EMAIL + "," + UPDATED_TO_EMAIL, "toEmail.in=" + UPDATED_TO_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByToEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where toEmail is not null
        defaultEmailMessageFiltering("toEmail.specified=true", "toEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByToEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where toEmail contains
        defaultEmailMessageFiltering("toEmail.contains=" + DEFAULT_TO_EMAIL, "toEmail.contains=" + UPDATED_TO_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByToEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where toEmail does not contain
        defaultEmailMessageFiltering("toEmail.doesNotContain=" + UPDATED_TO_EMAIL, "toEmail.doesNotContain=" + DEFAULT_TO_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByCcIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where cc equals to
        defaultEmailMessageFiltering("cc.equals=" + DEFAULT_CC, "cc.equals=" + UPDATED_CC);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByCcIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where cc in
        defaultEmailMessageFiltering("cc.in=" + DEFAULT_CC + "," + UPDATED_CC, "cc.in=" + UPDATED_CC);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByCcIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where cc is not null
        defaultEmailMessageFiltering("cc.specified=true", "cc.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByCcContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where cc contains
        defaultEmailMessageFiltering("cc.contains=" + DEFAULT_CC, "cc.contains=" + UPDATED_CC);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByCcNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where cc does not contain
        defaultEmailMessageFiltering("cc.doesNotContain=" + UPDATED_CC, "cc.doesNotContain=" + DEFAULT_CC);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByBccIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where bcc equals to
        defaultEmailMessageFiltering("bcc.equals=" + DEFAULT_BCC, "bcc.equals=" + UPDATED_BCC);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByBccIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where bcc in
        defaultEmailMessageFiltering("bcc.in=" + DEFAULT_BCC + "," + UPDATED_BCC, "bcc.in=" + UPDATED_BCC);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByBccIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where bcc is not null
        defaultEmailMessageFiltering("bcc.specified=true", "bcc.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByBccContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where bcc contains
        defaultEmailMessageFiltering("bcc.contains=" + DEFAULT_BCC, "bcc.contains=" + UPDATED_BCC);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByBccNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where bcc does not contain
        defaultEmailMessageFiltering("bcc.doesNotContain=" + UPDATED_BCC, "bcc.doesNotContain=" + DEFAULT_BCC);
    }

    @Test
    @Transactional
    void getAllEmailMessagesBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where subject equals to
        defaultEmailMessageFiltering("subject.equals=" + DEFAULT_SUBJECT, "subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where subject in
        defaultEmailMessageFiltering("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT, "subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where subject is not null
        defaultEmailMessageFiltering("subject.specified=true", "subject.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesBySubjectContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where subject contains
        defaultEmailMessageFiltering("subject.contains=" + DEFAULT_SUBJECT, "subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where subject does not contain
        defaultEmailMessageFiltering("subject.doesNotContain=" + UPDATED_SUBJECT, "subject.doesNotContain=" + DEFAULT_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByAttachmentRefIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where attachmentRef equals to
        defaultEmailMessageFiltering("attachmentRef.equals=" + DEFAULT_ATTACHMENT_REF, "attachmentRef.equals=" + UPDATED_ATTACHMENT_REF);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByAttachmentRefIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where attachmentRef in
        defaultEmailMessageFiltering(
            "attachmentRef.in=" + DEFAULT_ATTACHMENT_REF + "," + UPDATED_ATTACHMENT_REF,
            "attachmentRef.in=" + UPDATED_ATTACHMENT_REF
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByAttachmentRefIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where attachmentRef is not null
        defaultEmailMessageFiltering("attachmentRef.specified=true", "attachmentRef.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByAttachmentRefContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where attachmentRef contains
        defaultEmailMessageFiltering(
            "attachmentRef.contains=" + DEFAULT_ATTACHMENT_REF,
            "attachmentRef.contains=" + UPDATED_ATTACHMENT_REF
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByAttachmentRefNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where attachmentRef does not contain
        defaultEmailMessageFiltering(
            "attachmentRef.doesNotContain=" + UPDATED_ATTACHMENT_REF,
            "attachmentRef.doesNotContain=" + DEFAULT_ATTACHMENT_REF
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where status equals to
        defaultEmailMessageFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where status in
        defaultEmailMessageFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where status is not null
        defaultEmailMessageFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureType equals to
        defaultEmailMessageFiltering("failureType.equals=" + DEFAULT_FAILURE_TYPE, "failureType.equals=" + UPDATED_FAILURE_TYPE);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureType in
        defaultEmailMessageFiltering(
            "failureType.in=" + DEFAULT_FAILURE_TYPE + "," + UPDATED_FAILURE_TYPE,
            "failureType.in=" + UPDATED_FAILURE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureType is not null
        defaultEmailMessageFiltering("failureType.specified=true", "failureType.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureType contains
        defaultEmailMessageFiltering("failureType.contains=" + DEFAULT_FAILURE_TYPE, "failureType.contains=" + UPDATED_FAILURE_TYPE);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureType does not contain
        defaultEmailMessageFiltering(
            "failureType.doesNotContain=" + UPDATED_FAILURE_TYPE,
            "failureType.doesNotContain=" + DEFAULT_FAILURE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureReason equals to
        defaultEmailMessageFiltering("failureReason.equals=" + DEFAULT_FAILURE_REASON, "failureReason.equals=" + UPDATED_FAILURE_REASON);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureReason in
        defaultEmailMessageFiltering(
            "failureReason.in=" + DEFAULT_FAILURE_REASON + "," + UPDATED_FAILURE_REASON,
            "failureReason.in=" + UPDATED_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureReason is not null
        defaultEmailMessageFiltering("failureReason.specified=true", "failureReason.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureReason contains
        defaultEmailMessageFiltering(
            "failureReason.contains=" + DEFAULT_FAILURE_REASON,
            "failureReason.contains=" + UPDATED_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByFailureReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where failureReason does not contain
        defaultEmailMessageFiltering(
            "failureReason.doesNotContain=" + UPDATED_FAILURE_REASON,
            "failureReason.doesNotContain=" + DEFAULT_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByQueuedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where queuedOn equals to
        defaultEmailMessageFiltering("queuedOn.equals=" + DEFAULT_QUEUED_ON, "queuedOn.equals=" + UPDATED_QUEUED_ON);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByQueuedOnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where queuedOn in
        defaultEmailMessageFiltering("queuedOn.in=" + DEFAULT_QUEUED_ON + "," + UPDATED_QUEUED_ON, "queuedOn.in=" + UPDATED_QUEUED_ON);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByQueuedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where queuedOn is not null
        defaultEmailMessageFiltering("queuedOn.specified=true", "queuedOn.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesBySentAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where sentAt equals to
        defaultEmailMessageFiltering("sentAt.equals=" + DEFAULT_SENT_AT, "sentAt.equals=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesBySentAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where sentAt in
        defaultEmailMessageFiltering("sentAt.in=" + DEFAULT_SENT_AT + "," + UPDATED_SENT_AT, "sentAt.in=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesBySentAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where sentAt is not null
        defaultEmailMessageFiltering("sentAt.specified=true", "sentAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByRetryCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where retryCount equals to
        defaultEmailMessageFiltering("retryCount.equals=" + DEFAULT_RETRY_COUNT, "retryCount.equals=" + UPDATED_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByRetryCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where retryCount in
        defaultEmailMessageFiltering(
            "retryCount.in=" + DEFAULT_RETRY_COUNT + "," + UPDATED_RETRY_COUNT,
            "retryCount.in=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByRetryCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where retryCount is not null
        defaultEmailMessageFiltering("retryCount.specified=true", "retryCount.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailMessagesByRetryCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where retryCount is greater than or equal to
        defaultEmailMessageFiltering(
            "retryCount.greaterThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.greaterThanOrEqual=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByRetryCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where retryCount is less than or equal to
        defaultEmailMessageFiltering(
            "retryCount.lessThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.lessThanOrEqual=" + SMALLER_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllEmailMessagesByRetryCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where retryCount is less than
        defaultEmailMessageFiltering("retryCount.lessThan=" + UPDATED_RETRY_COUNT, "retryCount.lessThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByRetryCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        // Get all the emailMessageList where retryCount is greater than
        defaultEmailMessageFiltering("retryCount.greaterThan=" + SMALLER_RETRY_COUNT, "retryCount.greaterThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllEmailMessagesByTemplateIsEqualToSomething() throws Exception {
        MailTemplate template;
        if (TestUtil.findAll(em, MailTemplate.class).isEmpty()) {
            emailMessageRepository.saveAndFlush(emailMessage);
            template = MailTemplateResourceIT.createEntity();
        } else {
            template = TestUtil.findAll(em, MailTemplate.class).get(0);
        }
        em.persist(template);
        em.flush();
        emailMessage.setTemplate(template);
        emailMessageRepository.saveAndFlush(emailMessage);
        Long templateId = template.getId();
        // Get all the emailMessageList where template equals to templateId
        defaultEmailMessageShouldBeFound("templateId.equals=" + templateId);

        // Get all the emailMessageList where template equals to (templateId + 1)
        defaultEmailMessageShouldNotBeFound("templateId.equals=" + (templateId + 1));
    }

    private void defaultEmailMessageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEmailMessageShouldBeFound(shouldBeFound);
        defaultEmailMessageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEmailMessageShouldBeFound(String filter) throws Exception {
        restEmailMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emailMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].fromEmail").value(hasItem(DEFAULT_FROM_EMAIL)))
            .andExpect(jsonPath("$.[*].fromName").value(hasItem(DEFAULT_FROM_NAME)))
            .andExpect(jsonPath("$.[*].replyTo").value(hasItem(DEFAULT_REPLY_TO)))
            .andExpect(jsonPath("$.[*].toEmail").value(hasItem(DEFAULT_TO_EMAIL)))
            .andExpect(jsonPath("$.[*].cc").value(hasItem(DEFAULT_CC)))
            .andExpect(jsonPath("$.[*].bcc").value(hasItem(DEFAULT_BCC)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].attachmentRef").value(hasItem(DEFAULT_ATTACHMENT_REF)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].failureType").value(hasItem(DEFAULT_FAILURE_TYPE)))
            .andExpect(jsonPath("$.[*].failureReason").value(hasItem(DEFAULT_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].queuedOn").value(hasItem(DEFAULT_QUEUED_ON.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)));

        // Check, that the count call also returns 1
        restEmailMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEmailMessageShouldNotBeFound(String filter) throws Exception {
        restEmailMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEmailMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEmailMessage() throws Exception {
        // Get the emailMessage
        restEmailMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEmailMessage() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the emailMessage
        EmailMessage updatedEmailMessage = emailMessageRepository.findById(emailMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEmailMessage are not directly saved in db
        em.detach(updatedEmailMessage);
        updatedEmailMessage
            .fromEmail(UPDATED_FROM_EMAIL)
            .fromName(UPDATED_FROM_NAME)
            .replyTo(UPDATED_REPLY_TO)
            .toEmail(UPDATED_TO_EMAIL)
            .cc(UPDATED_CC)
            .bcc(UPDATED_BCC)
            .subject(UPDATED_SUBJECT)
            .body(UPDATED_BODY)
            .attachmentRef(UPDATED_ATTACHMENT_REF)
            .status(UPDATED_STATUS)
            .failureType(UPDATED_FAILURE_TYPE)
            .failureReason(UPDATED_FAILURE_REASON)
            .queuedOn(UPDATED_QUEUED_ON)
            .sentAt(UPDATED_SENT_AT)
            .retryCount(UPDATED_RETRY_COUNT);
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(updatedEmailMessage);

        restEmailMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emailMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(emailMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the EmailMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEmailMessageToMatchAllProperties(updatedEmailMessage);
    }

    @Test
    @Transactional
    void putNonExistingEmailMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        emailMessage.setId(longCount.incrementAndGet());

        // Create the EmailMessage
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emailMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(emailMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmailMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmailMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        emailMessage.setId(longCount.incrementAndGet());

        // Create the EmailMessage
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(emailMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmailMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmailMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        emailMessage.setId(longCount.incrementAndGet());

        // Create the EmailMessage
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EmailMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEmailMessageWithPatch() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the emailMessage using partial update
        EmailMessage partialUpdatedEmailMessage = new EmailMessage();
        partialUpdatedEmailMessage.setId(emailMessage.getId());

        partialUpdatedEmailMessage
            .cc(UPDATED_CC)
            .bcc(UPDATED_BCC)
            .subject(UPDATED_SUBJECT)
            .attachmentRef(UPDATED_ATTACHMENT_REF)
            .failureType(UPDATED_FAILURE_TYPE)
            .queuedOn(UPDATED_QUEUED_ON)
            .sentAt(UPDATED_SENT_AT);

        restEmailMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmailMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEmailMessage))
            )
            .andExpect(status().isOk());

        // Validate the EmailMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmailMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEmailMessage, emailMessage),
            getPersistedEmailMessage(emailMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateEmailMessageWithPatch() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the emailMessage using partial update
        EmailMessage partialUpdatedEmailMessage = new EmailMessage();
        partialUpdatedEmailMessage.setId(emailMessage.getId());

        partialUpdatedEmailMessage
            .fromEmail(UPDATED_FROM_EMAIL)
            .fromName(UPDATED_FROM_NAME)
            .replyTo(UPDATED_REPLY_TO)
            .toEmail(UPDATED_TO_EMAIL)
            .cc(UPDATED_CC)
            .bcc(UPDATED_BCC)
            .subject(UPDATED_SUBJECT)
            .body(UPDATED_BODY)
            .attachmentRef(UPDATED_ATTACHMENT_REF)
            .status(UPDATED_STATUS)
            .failureType(UPDATED_FAILURE_TYPE)
            .failureReason(UPDATED_FAILURE_REASON)
            .queuedOn(UPDATED_QUEUED_ON)
            .sentAt(UPDATED_SENT_AT)
            .retryCount(UPDATED_RETRY_COUNT);

        restEmailMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmailMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEmailMessage))
            )
            .andExpect(status().isOk());

        // Validate the EmailMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmailMessageUpdatableFieldsEquals(partialUpdatedEmailMessage, getPersistedEmailMessage(partialUpdatedEmailMessage));
    }

    @Test
    @Transactional
    void patchNonExistingEmailMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        emailMessage.setId(longCount.incrementAndGet());

        // Create the EmailMessage
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, emailMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(emailMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmailMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmailMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        emailMessage.setId(longCount.incrementAndGet());

        // Create the EmailMessage
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(emailMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmailMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmailMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        emailMessage.setId(longCount.incrementAndGet());

        // Create the EmailMessage
        EmailMessageDTO emailMessageDTO = emailMessageMapper.toDto(emailMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(emailMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EmailMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEmailMessage() throws Exception {
        // Initialize the database
        insertedEmailMessage = emailMessageRepository.saveAndFlush(emailMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the emailMessage
        restEmailMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, emailMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return emailMessageRepository.count();
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

    protected EmailMessage getPersistedEmailMessage(EmailMessage emailMessage) {
        return emailMessageRepository.findById(emailMessage.getId()).orElseThrow();
    }

    protected void assertPersistedEmailMessageToMatchAllProperties(EmailMessage expectedEmailMessage) {
        assertEmailMessageAllPropertiesEquals(expectedEmailMessage, getPersistedEmailMessage(expectedEmailMessage));
    }

    protected void assertPersistedEmailMessageToMatchUpdatableProperties(EmailMessage expectedEmailMessage) {
        assertEmailMessageAllUpdatablePropertiesEquals(expectedEmailMessage, getPersistedEmailMessage(expectedEmailMessage));
    }
}
