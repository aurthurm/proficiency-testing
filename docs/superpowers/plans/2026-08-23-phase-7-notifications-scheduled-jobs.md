# Phase 7 — Notifications & Scheduled Jobs Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reach parity with the legacy app's remaining scheduled jobs — participant/data-manager email validation, report-ready notification emails, and the targeted non-participation follow-up tool (legacy calls this "Announcement," which is misleading — see Task 5) — on top of Phase 0.3's already-planned email/job infrastructure.

**Architecture:** Two new job types dispatched through Phase 0.2's generic `ScheduledJob` poller: a cron-triggered `EMAIL_VALIDATION` job (every 4 hours, batched) and an on-demand `SHIPMENT_REPORT_MAIL` job type enqueued by Phase 6's report generation. A new admin-facing "notify participants who haven't responded" feature reuses Phase 0.3's `MailTemplate`/`EmailMessage` queue.

**Tech Stack:** Spring Boot `@Scheduled` + ShedLock (Phase 0.2), `javax.naming.directory.InitialDirContext` for DNS MX lookups (JDK standard library, zero new dependency), Liquibase changelog for new columns, JHipster React module conventions for the follow-up-email admin screen.

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md` (Phase 7 section) — this plan implements it; both should be read together.

## Global Constraints

- Every scheduled job in this phase MUST be guarded by ShedLock, per Phase 0.2's resolved decision — no exceptions.
- No new runtime dependency for MX lookups — use the JDK's built-in JNDI DNS resolver.
- Email sending goes through Phase 0.3's `EmailMessage` queue — never send synchronously from a request thread or from inside these jobs directly.
- Match legacy's exact batching/recheck semantics for email validation (batch size 500, recheck after 30 days, oldest-checked-first) unless a documented reason says otherwise.

---

## Corrections to the master plan (`ept_project_plan.md`), found while grounding this plan in the real legacy code

The master plan's Phase 7 legacy-reference list was written from a summarized research pass, not a direct read. Three things in it turned out to be wrong or incomplete once checked against the actual files in `/home/administrator/Documents/Development/ept`:

1. **`check-participant-emails.php` doesn't exist in `scheduled-jobs/`.** It's at `bin/check-participant-emails.php`, invoked from `ScheduledTasks.php` via `BIN_PATH`, not `SCHEDULED_JOBS_FOLDER`. Read in full — it's small (241 lines), well-commented, and self-contained. Confirmed real cron cadence: `'15 */4 * * *'` (every 4 hours at :15), `--batch=500` default, `--recheck-days=30` default, `--max-batches=10000` safety cap.

2. **`send-reports-mail.php` is NOT on the Crunz cron schedule at all** — grepping `ScheduledTasks.php` confirms it's absent. It's instead a job-queue *job type*: `Shipments.php:3870-3875` enqueues a `scheduled_jobs` row with `job = 'send-reports-mail.php -s {shipmentId}'` when a shipment's report is generated, and `execute-job-queue.php` (the generic every-minute poller, `scheduled-jobs/execute-job-queue.php:11`'s allowlist includes `'send-reports-mail.php'`) picks it up and runs it. So "report-ready notification email" is not a separate cron job with its own cadence — it's a job type dispatched through the same generic queue Phase 0.2 already builds. Task 4 below reflects this; do not build a standalone cron job for it.

   Read `Shipments.php:3887-3931` (`fetchReportsMail()`) in full: it queries shipments in `shipped`/`evaluated`/`finalized` status, checks for both an individual and a summary PDF file on disk (`{DOWNLOADS_FOLDER}/reports/{shipment_code}/{shipment_code}-{map_id}.pdf` and `...-summary.pdf`), and if both exist, renders the `send_participant_report_mail` `MailTemplate` (placeholders `##NAME##`, `##SHIPCODE##`, `##SHIPTYPE##`, `##IND_REPORT_LINK##`, `##SUM_REPORT_LINK##` — the two link placeholders are base64-encoded file paths behind a `/d/{token}` download route) and queues it via `insertTempMail()`. It also inserts an internal `notify` table row for admin visibility (out of scope here — no `notify`-equivalent exists in the current app and the master plan doesn't ask for one; skip it).

3. **The master plan's "Message/announcement notification job, if `ContactMessage`/`Announcement` entities need push-on-create behavior" undersells what this actually is.** Confirmed directly (`application/services/Announcement.php`, `composeNewAnnouncement()`): it queries `shipment_participant_map` joined to `shipment`/`participant`, with a comment reading *"Same non-participation test as `Shipments::getShipmentNotParticipated()`"* — this is a **targeted follow-up email tool for participants who haven't responded to a specific shipment**, not a generic bulletin/announcement broadcast. (A sibling phase plan, Phase 8, independently found the same thing while scoping the current app's `Announcement` entity — that entity is a static bulletin and does NOT model this feature; this plan builds the real thing as new work, per Phase 8's note that it belongs here once Phase 3's shipment-participation data exists, which it now does.) Task 5 below builds this correctly, not as a repurposed `Announcement` entity.

---

## Task 1: Email-status tracking columns on Participant and DataManager

**Files:**
- Create: `src/main/resources/config/liquibase/changelog/20260823000100_added_email_status_tracking.xml`
- Modify: `src/main/resources/config/liquibase/master.xml` (add the new changelog include)
- Modify: `src/main/java/zw/org/nmrl/ept/domain/Participant.java`
- Modify: `src/main/java/zw/org/nmrl/ept/domain/DataManager.java`
- Test: `src/test/java/zw/org/nmrl/ept/domain/ParticipantTest.java`, `src/test/java/zw/org/nmrl/ept/domain/DataManagerTest.java`

**Interfaces:**
- Produces: `Participant.getEmailStatus()`/`setEmailStatus(EmailValidityStatus)`, `Participant.getAdditionalEmailStatus()`/`setAdditionalEmailStatus(EmailValidityStatus)`, `Participant.getEmailStatusCheckedAt()`/`setEmailStatusCheckedAt(Instant)` — mirrored on `DataManager` with `primaryEmailStatus`/`secondaryEmailStatus`/`emailStatusCheckedAt`. New enum `zw.org.nmrl.ept.domain.enumeration.EmailValidityStatus { UNKNOWN, VALID, INVALID_SYNTAX, INVALID_DOMAIN }` (mirrors legacy's four classification buckets exactly).

Confirmed via `grep -n "emailStatus\|email_status" src/main/java/zw/org/nmrl/ept/domain/{Participant,DataManager}.java` that neither field exists yet — this is new, not an extension.

- [ ] **Step 1: Write the failing entity test**

```java
// src/test/java/zw/org/nmrl/ept/domain/ParticipantTest.java — add to existing test class
@Test
void emailStatusFieldsDefaultToNull() {
    Participant participant = new Participant();
    assertThat(participant.getEmailStatus()).isNull();
    assertThat(participant.getEmailStatusCheckedAt()).isNull();
    participant.setEmailStatus(EmailValidityStatus.VALID);
    assertThat(participant.getEmailStatus()).isEqualTo(EmailValidityStatus.VALID);
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=ParticipantTest#emailStatusFieldsDefaultToNull`
Expected: FAIL — compile error, `getEmailStatus()` doesn't exist.

- [ ] **Step 3: Create the enum**

```java
// src/main/java/zw/org/nmrl/ept/domain/enumeration/EmailValidityStatus.java
package zw.org.nmrl.ept.domain.enumeration;

public enum EmailValidityStatus {
    UNKNOWN,
    VALID,
    INVALID_SYNTAX,
    INVALID_DOMAIN,
}
```

- [ ] **Step 4: Add the Liquibase changelog**

```xml
<!-- src/main/resources/config/liquibase/changelog/20260823000100_added_email_status_tracking.xml -->
<?xml version="1.1" encoding="UTF-8" standalone="no"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">
    <changeSet id="20260823000100-1" author="ept">
        <addColumn tableName="participant">
            <column name="email_status" type="varchar(32)"/>
            <column name="additional_email_status" type="varchar(32)"/>
            <column name="email_status_checked_at" type="timestamp"/>
        </addColumn>
        <addColumn tableName="data_manager">
            <column name="primary_email_status" type="varchar(32)"/>
            <column name="secondary_email_status" type="varchar(32)"/>
            <column name="email_status_checked_at" type="timestamp"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```

Add `<include file="config/liquibase/changelog/20260823000100_added_email_status_tracking.xml" relativeToChangelogFile="false"/>` to `master.xml` after the most recent existing entry.

- [ ] **Step 5: Add the fields to `Participant.java`**

```java
// added to the existing Participant entity class
@Enumerated(EnumType.STRING)
@Column(name = "email_status")
private EmailValidityStatus emailStatus;

@Enumerated(EnumType.STRING)
@Column(name = "additional_email_status")
private EmailValidityStatus additionalEmailStatus;

@Column(name = "email_status_checked_at")
private Instant emailStatusCheckedAt;

// standard getters/setters/fluent-setters following this file's existing pattern for every other field
```

- [ ] **Step 6: Add the mirrored fields to `DataManager.java`**

Same shape, `primaryEmailStatus`/`secondaryEmailStatus`/`emailStatusCheckedAt`, following `DataManager.java`'s existing field/getter/setter pattern.

- [ ] **Step 7: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=ParticipantTest#emailStatusFieldsDefaultToNull,DataManagerTest`
Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add src/main/resources/config/liquibase/changelog/20260823000100_added_email_status_tracking.xml src/main/resources/config/liquibase/master.xml src/main/java/zw/org/nmrl/ept/domain/Participant.java src/main/java/zw/org/nmrl/ept/domain/DataManager.java src/main/java/zw/org/nmrl/ept/domain/enumeration/EmailValidityStatus.java src/test/java/zw/org/nmrl/ept/domain/ParticipantTest.java src/test/java/zw/org/nmrl/ept/domain/DataManagerTest.java
git commit -m "feat: add email validity tracking columns to participant and data manager"
```

---

## Task 2: Email validation service (syntax + MX check)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/EmailValidationService.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/EmailValidationServiceTest.java`

**Interfaces:**
- Consumes: `Participant`/`DataManager` repositories (Task 1's new columns).
- Produces: `EmailValidationService.classify(String email): EmailValidityStatus` (pure function, MX-cached per invocation batch via an injected/shared cache map — mirrors legacy's `$mxCache` per-run dedupe), `EmailValidationService.validateBatch(int batchSize, int recheckDays): ValidationBatchResult` where `ValidationBatchResult` is a small record `(int participantsScanned, int dataManagersScanned)`.

Legacy's classification logic (`bin/check-participant-emails.php:84-98`), to port exactly:
- Empty/blank → `UNKNOWN` (not an error).
- Fails RFC syntax check → `INVALID_SYNTAX`.
- Passes syntax, domain has no MX record → `INVALID_DOMAIN`.
- Passes syntax, domain has an MX record → `VALID`.

- [ ] **Step 1: Write the failing unit test for `classify()`**

```java
// src/test/java/zw/org/nmrl/ept/service/EmailValidationServiceTest.java
@Test
void classifiesBlankAsUnknown() {
    assertThat(new EmailValidationService().classify(null)).isEqualTo(EmailValidityStatus.UNKNOWN);
    assertThat(new EmailValidationService().classify("  ")).isEqualTo(EmailValidityStatus.UNKNOWN);
}

@Test
void classifiesMalformedAddressAsInvalidSyntax() {
    assertThat(new EmailValidationService().classify("not-an-email")).isEqualTo(EmailValidityStatus.INVALID_SYNTAX);
}

@Test
void classifiesWellFormedAddressWithMxRecordAsValid() {
    // gmail.com is a stable, always-present-MX domain safe to depend on in a unit test
    assertThat(new EmailValidationService().classify("someone@gmail.com")).isEqualTo(EmailValidityStatus.VALID);
}

@Test
void classifiesWellFormedAddressWithNoMxRecordAsInvalidDomain() {
    // a syntactically valid domain guaranteed never to have an MX record
    assertThat(new EmailValidationService().classify("someone@no-such-domain-ept-test.invalid")).isEqualTo(EmailValidityStatus.INVALID_DOMAIN);
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=EmailValidationServiceTest`
Expected: FAIL — class doesn't exist.

- [ ] **Step 3: Implement `EmailValidationService`**

```java
package zw.org.nmrl.ept.service;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.domain.enumeration.EmailValidityStatus;

@Service
public class EmailValidationService {

    private final Map<String, Boolean> mxCache = new ConcurrentHashMap<>();

    public EmailValidityStatus classify(String rawAddress) {
        String address = rawAddress == null ? "" : rawAddress.trim();
        if (address.isEmpty()) {
            return EmailValidityStatus.UNKNOWN;
        }
        String normalized = normalizeSyntax(address);
        if (normalized == null) {
            return EmailValidityStatus.INVALID_SYNTAX;
        }
        String domain = normalized.substring(normalized.lastIndexOf('@') + 1).toLowerCase();
        return mxCache.computeIfAbsent(domain, this::hasMxRecord) ? EmailValidityStatus.VALID : EmailValidityStatus.INVALID_DOMAIN;
    }

    private String normalizeSyntax(String address) {
        try {
            InternetAddress parsed = new InternetAddress(address);
            parsed.validate();
            return parsed.getAddress();
        } catch (AddressException e) {
            return null;
        }
    }

    private boolean hasMxRecord(String domain) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        try {
            InitialDirContext ictx = new InitialDirContext(env);
            Attributes attrs = ictx.getAttributes(domain, new String[] { "MX" });
            Attribute mx = attrs.get("MX");
            return mx != null && mx.size() > 0;
        } catch (NamingException e) {
            return false;
        }
    }
}
```

Note: `jakarta.mail.internet.InternetAddress` for syntax validation requires the `jakarta.mail` dependency, which Phase 0.3 already adds for `JavaMailSender`/Jakarta Mail — do not add a second dependency here, confirm it's already on the classpath before writing this task's detailed sub-plan for real.

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=EmailValidationServiceTest`
Expected: PASS (the MX-record tests require network access in CI — if the CI environment has no outbound DNS, mark these two with `@Tag("requires-network")` and exclude via the surefire profile, mirroring how `LegacyBatchMigrationIntegrationTest`-style tests are already gated in this repo).

- [ ] **Step 5: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/EmailValidationService.java src/test/java/zw/org/nmrl/ept/service/EmailValidationServiceTest.java
git commit -m "feat: add email syntax + MX validation service"
```

---

## Task 3: Batched email validation job + scheduled trigger

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/EmailValidationBatchService.java`
- Create: `src/main/java/zw/org/nmrl/ept/config/EmailValidationSchedulingConfig.java`
- Modify: `src/main/resources/config/application.yml` (add `application.email-validation.batch-size: 500`, `application.email-validation.recheck-days: 30`)
- Test: `src/test/java/zw/org/nmrl/ept/service/EmailValidationBatchServiceIT.java`

**Interfaces:**
- Consumes: `EmailValidationService.classify(String)` (Task 2), `ParticipantRepository`, `DataManagerRepository`.
- Produces: `EmailValidationBatchService.runBatch(int batchSize, int recheckDays): BatchSummary` where `BatchSummary` is `(int participantsScanned, int dataManagersScanned, int validCount, int invalidDomainCount, int invalidSyntaxCount)`.

Query semantics to match `bin/check-participant-emails.php:124-146` exactly: select rows where at least one email column is non-blank AND (`emailStatusCheckedAt IS NULL` OR older than `recheckDays`), ordered oldest-checked-first, limited to `batchSize`. Use a `@Query` JPQL method on each repository, not an in-memory filter — the legacy version is batch-oriented specifically to avoid loading the whole table.

- [ ] **Step 1: Write the failing integration test**

```java
// src/test/java/zw/org/nmrl/ept/service/EmailValidationBatchServiceIT.java
@SpringBootTest
@Transactional
class EmailValidationBatchServiceIT {

    @Autowired
    private EmailValidationBatchService batchService;
    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    void marksNeverCheckedParticipantAsScanned() {
        Participant p = new Participant();
        p.setEmail("test@gmail.com");
        // ... other required fields per existing ParticipantResourceIT fixture pattern
        participantRepository.saveAndFlush(p);

        BatchSummary result = batchService.runBatch(10, 30);

        assertThat(result.participantsScanned()).isGreaterThanOrEqualTo(1);
        Participant reloaded = participantRepository.findById(p.getId()).orElseThrow();
        assertThat(reloaded.getEmailStatusCheckedAt()).isNotNull();
    }

    @Test
    void doesNotRescanRecentlyCheckedParticipant() {
        Participant p = new Participant();
        p.setEmail("test2@gmail.com");
        p.setEmailStatusCheckedAt(Instant.now());
        participantRepository.saveAndFlush(p);

        BatchSummary result = batchService.runBatch(10, 30);

        // this participant was checked "now", well within the 30-day recheck window, so it must not be rescanned
        Participant reloaded = participantRepository.findById(p.getId()).orElseThrow();
        assertThat(reloaded.getEmailStatusCheckedAt()).isEqualTo(p.getEmailStatusCheckedAt());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=EmailValidationBatchServiceIT`
Expected: FAIL — `EmailValidationBatchService` doesn't exist.

- [ ] **Step 3: Add the batch query methods to the repositories**

```java
// added to ParticipantRepository.java
@Query(
    "select p from Participant p where (p.email is not null and p.email <> '' or p.additionalEmail is not null and p.additionalEmail <> '') " +
    "and (p.emailStatusCheckedAt is null or p.emailStatusCheckedAt < :cutoff) order by p.emailStatusCheckedAt asc nulls first"
)
Page<Participant> findDueForEmailValidation(@Param("cutoff") Instant cutoff, Pageable pageable);
```

```java
// added to DataManagerRepository.java, mirrored — findDueForEmailValidation with primaryEmail/secondaryEmail
```

Confirm the exact existing field names for participant's secondary email address and data manager's two email fields via `grep -n "private String.*[Ee]mail" src/main/java/zw/org/nmrl/ept/domain/{Participant,DataManager}.java` before writing this — do not guess the field name.

- [ ] **Step 4: Implement `EmailValidationBatchService`**

```java
package zw.org.nmrl.ept.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailValidationBatchService {

    private final EmailValidationService emailValidationService;
    private final ParticipantRepository participantRepository;
    private final DataManagerRepository dataManagerRepository;

    // constructor injection, standard pattern

    @Transactional
    public BatchSummary runBatch(int batchSize, int recheckDays) {
        Instant cutoff = Instant.now().minus(recheckDays, ChronoUnit.DAYS);
        Pageable page = PageRequest.of(0, batchSize);
        int[] participantCounts = validateParticipants(cutoff, page);
        int[] dataManagerCounts = validateDataManagers(cutoff, page);
        return new BatchSummary(participantCounts[0], dataManagerCounts[0], participantCounts[1] + dataManagerCounts[1], participantCounts[2] + dataManagerCounts[2], participantCounts[3] + dataManagerCounts[3]);
    }

    // validateParticipants/validateDataManagers: fetch findDueForEmailValidation page, classify() each
    // email column via EmailValidationService, set the two status fields + emailStatusCheckedAt = now(),
    // save, and tally valid/invalidDomain/invalidSyntax counts — mirrors bin/check-participant-emails.php's
    // $processBatch closure at lines 100-122.
}

public record BatchSummary(int participantsScanned, int dataManagersScanned, int validCount, int invalidDomainCount, int invalidSyntaxCount) {}
```

- [ ] **Step 5: Wire the scheduled trigger**

```java
package zw.org.nmrl.ept.config;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailValidationSchedulingConfig {

    private final EmailValidationBatchService batchService;

    @Value("${application.email-validation.batch-size:500}")
    private int batchSize;

    @Value("${application.email-validation.recheck-days:30}")
    private int recheckDays;

    // constructor injection

    // cron matches legacy exactly: '15 */4 * * *' -> every 4 hours at :15
    @Scheduled(cron = "0 15 */4 * * *")
    @SchedulerLock(name = "email-validation-batch", lockAtLeastFor = "PT1M", lockAtMostFor = "PT30M")
    public void run() {
        batchService.runBatch(batchSize, recheckDays);
    }
}
```

- [ ] **Step 6: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=EmailValidationBatchServiceIT`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/EmailValidationBatchService.java src/main/java/zw/org/nmrl/ept/config/EmailValidationSchedulingConfig.java src/main/resources/config/application.yml src/test/java/zw/org/nmrl/ept/service/EmailValidationBatchServiceIT.java
git commit -m "feat: add batched participant/data-manager email validation job"
```

---

## Task 4: Report-ready notification email (job-queue job type, not a separate cron)

**Files:**
- Modify: whatever Phase 0.2 names its job-type dispatcher (e.g. `JobDispatcher`/`ScheduledJobProcessor` — confirm the exact class name from Phase 0.2's plan/implementation before writing this task's real detailed steps; placeholder name `ScheduledJobProcessor` used below)
- Create: `src/main/java/zw/org/nmrl/ept/service/ShipmentReportMailService.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/ShipmentReportMailServiceIT.java`

**Interfaces:**
- Consumes: Phase 0.2's `ScheduledJob` entity/repository and job-dispatch mechanism, Phase 0.3's `MailTemplate` rendering service + `EmailMessage` queue, Phase 6's generated-report file locations (via the Phase 0.4 `FileStoreService`).
- Produces: a new `ScheduledJob` job type — name it `SHIPMENT_REPORT_MAIL` — with payload `{ "shipmentId": <id> }`, and `ShipmentReportMailService.sendReportReadyEmails(Long shipmentId): int` (returns count of emails queued) as the handler Phase 0.2's dispatcher calls for that job type.

This is **not a `@Scheduled` job** — do not add a cron trigger. It's enqueued by whatever Phase 6 task performs report generation, the same way legacy's `Shipments.php:3870-3875` enqueues `send-reports-mail.php -s {id}` immediately after generating a shipment's reports. Cross-reference Phase 6's detailed plan once written and add the enqueue call there if it isn't already covered — this plan only builds the consumer side.

Legacy logic to port from `Shipments.php:3887-3931` (`fetchReportsMail()`):
1. Load the shipment's `shipment_participant_map` rows where status is `shipped`/`evaluated`/`finalized`, excluding `is_excluded = yes` rows, where a report was actually generated.
2. For each, confirm both an individual and a summary report PDF exist on disk for that shipment (via `FileStoreService`, not raw `java.io.File`).
3. Render the `send_participant_report_mail` `MailTemplate` (must exist — migrated already per the migration docs) with placeholders for participant name, shipment code, scheme name, and two download links.
4. Enqueue one `EmailMessage` per participant via Phase 0.3's outbound queue.

- [ ] **Step 1: Write the failing integration test**

```java
// src/test/java/zw/org/nmrl/ept/service/ShipmentReportMailServiceIT.java
@SpringBootTest
@Transactional
class ShipmentReportMailServiceIT {

    @Autowired
    private ShipmentReportMailService reportMailService;
    // ... fixture setup: a finalized Shipment with two ShipmentParticipantMap rows,
    // one is_excluded=true (must be skipped), the send_participant_report_mail MailTemplate seeded,
    // and both PDF files present in the test FileStoreService root.

    @Test
    void queuesOneEmailPerNonExcludedParticipantWithBothReportsPresent() {
        int queued = reportMailService.sendReportReadyEmails(shipment.getId());
        assertThat(queued).isEqualTo(1); // the excluded participant is skipped
    }

    @Test
    void skipsParticipantWhenIndividualReportFileIsMissing() {
        // remove the individual PDF fixture for one participant before calling
        int queued = reportMailService.sendReportReadyEmails(shipment.getId());
        assertThat(queued).isEqualTo(0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=ShipmentReportMailServiceIT`
Expected: FAIL — class doesn't exist.

- [ ] **Step 3: Implement `ShipmentReportMailService`**

(Detailed implementation deferred to when Phase 0.3/Phase 6's real class names and method signatures exist — this task's shape is: query eligible `ShipmentParticipantMap` rows, check file existence via `FileStoreService`, render via Phase 0.3's template service, enqueue via Phase 0.3's `EmailMessage` repository. Write against those real interfaces, not assumed ones, when this task is actually executed.)

- [ ] **Step 4: Register `SHIPMENT_REPORT_MAIL` as a handled job type in Phase 0.2's dispatcher**

Add a `case SHIPMENT_REPORT_MAIL -> reportMailService.sendReportReadyEmails(payload.getLong("shipmentId"));`-shaped branch (exact syntax depends on Phase 0.2's real dispatcher shape) to whatever `switch`/`Map<JobType, Handler>` mechanism Phase 0.2 built.

- [ ] **Step 5: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=ShipmentReportMailServiceIT`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentReportMailService.java src/test/java/zw/org/nmrl/ept/service/ShipmentReportMailServiceIT.java
git commit -m "feat: add shipment report-ready email job type"
```

---

## Task 5: Targeted non-participation follow-up email (the feature legacy calls "Announcement")

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/ShipmentFollowUpService.java`
- Create: `src/main/java/zw/org/nmrl/ept/web/rest/ShipmentFollowUpResource.java`
- Create: `src/main/webapp/app/entities/shipment/follow-up/` (React module — compose screen listing non-responding participants for a shipment, with a send-follow-up action)
- Test: `src/test/java/zw/org/nmrl/ept/service/ShipmentFollowUpServiceIT.java`, `src/test/java/zw/org/nmrl/ept/web/rest/ShipmentFollowUpResourceIT.java`

**Interfaces:**
- Consumes: `ShipmentParticipantMap` repository (for the non-participation query — mirror `Shipments::getShipmentNotParticipated()`'s logic, confirm exact query shape by reading that method in `Shipments.php` when this task is executed, not assumed here), Phase 0.3's `MailTemplate`/`EmailMessage` queue.
- Produces: `ShipmentFollowUpService.findNonRespondingParticipants(Long shipmentId): List<ParticipantSummaryDTO>`, `ShipmentFollowUpService.sendFollowUp(Long shipmentId, List<Long> participantIds, String messageBody): int` (count queued). REST: `GET /api/shipments/{id}/non-responding-participants`, `POST /api/shipments/{id}/follow-up`.

**Do not** repurpose the current `Announcement` entity/resource for this — per the corrections section above, that entity is a genuinely different feature (a static bulletin) and Phase 8's plan already covers it as-is. This is new, additive work.

- [ ] **Step 1: Write the failing integration test for the non-responding query**

```java
// src/test/java/zw/org/nmrl/ept/service/ShipmentFollowUpServiceIT.java
@SpringBootTest
@Transactional
class ShipmentFollowUpServiceIT {

    @Autowired
    private ShipmentFollowUpService followUpService;
    // fixture: one Shipment with one ShipmentParticipantMap that has a result recorded (responded)
    // and one with no result (not responded)

    @Test
    void findsOnlyParticipantsWithoutARecordedResponse() {
        List<ParticipantSummaryDTO> nonResponding = followUpService.findNonRespondingParticipants(shipment.getId());
        assertThat(nonResponding).extracting(ParticipantSummaryDTO::participantId).containsExactly(nonRespondingParticipant.getId());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw -Pprod test -Dtest=ShipmentFollowUpServiceIT`
Expected: FAIL — class doesn't exist.

- [ ] **Step 3: Implement the non-participation query and `sendFollowUp`**

(Port the exact join/filter conditions from `Shipments::getShipmentNotParticipated()` — read that method directly when executing this task, the master research pass didn't capture its full body, only that `Announcement.php` reuses "the same test.")

- [ ] **Step 4: Run test to verify it passes**

Run: `./mvnw -Pprod test -Dtest=ShipmentFollowUpServiceIT`
Expected: PASS

- [ ] **Step 5: Add the REST resource + integration test**

```java
// src/test/java/zw/org/nmrl/ept/web/rest/ShipmentFollowUpResourceIT.java
@Test
void sendFollowUpQueuesEmailsForSelectedParticipants() throws Exception {
    restMockMvc.perform(post("/api/shipments/{id}/follow-up", shipment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"participantIds\":[" + nonRespondingParticipant.getId() + "],\"messageBody\":\"Please submit your results\"}"))
        .andExpect(status().isOk());
    // assert an EmailMessage row now exists addressed to that participant
}
```

- [ ] **Step 6: Run test to verify it passes**

Run: `./mvnw -Pprod verify -Dit.test=ShipmentFollowUpResourceIT`
Expected: PASS

- [ ] **Step 7: Build the React compose screen**

Standard JHipster module shape (list + compose form), following the pattern in an existing entity module (e.g. `src/main/webapp/app/entities/shipment/`) for component structure/reducer conventions — a "Send follow-up" screen listing non-responding participants with checkboxes and a message textarea, calling the two REST endpoints above.

- [ ] **Step 8: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentFollowUpService.java src/main/java/zw/org/nmrl/ept/web/rest/ShipmentFollowUpResource.java src/main/webapp/app/entities/shipment/follow-up/ src/test/java/zw/org/nmrl/ept/service/ShipmentFollowUpServiceIT.java src/test/java/zw/org/nmrl/ept/web/rest/ShipmentFollowUpResourceIT.java
git commit -m "feat: add targeted non-participation follow-up email tool"
```

---

## Task 6: IMAP bounce processing — confirm descope, or implement

Per Phase 0.3's resolved decision, this is **descoped by default**. This task exists only as a checkpoint to revisit that decision now that the rest of Phase 7 is built.

- [ ] Confirm with the team whether IMAP bounce processing is still out of scope. If it remains descoped, mark this task done with no code changes and update `ept_project_plan.md`'s Phase 0.3 entry to note the confirmation date. If it's now in scope, this task needs its own follow-up plan (Jakarta Mail-based IMAP polling per the already-resolved library decision) — do not attempt it inline here without first writing that plan.

---

## Self-review

**Spec coverage:** master plan's Phase 7 bullets — email validation job (Task 3), report-ready notification (Task 4), message/announcement job (Task 5, corrected in scope), bounce-processing revisit (Task 6) — all covered. Schema prerequisite (email-status columns) added as Task 1 since neither the master plan nor prior phases created it.

**Placeholder scan:** Task 4 Step 3 and Task 5 Step 3 are intentionally left as directed stubs pointing at Phase 0.2/0.3/Phase 6's real interfaces rather than invented ones, because those phases' detailed plans/implementations don't exist yet at the time of writing this plan — this is a deliberate cross-phase dependency, not a placeholder in the "vague hand-wave" sense the skill forbids; each stub names exactly what real signature to look up and where. Flag this explicitly to whoever picks up Task 4/5: resolve the real class/method names from Phase 0.2/0.3/6 first, then fill in the step.

**Type consistency:** `EmailValidityStatus` (Task 1) is the single enum used consistently through Tasks 2 and 3. `BatchSummary` (Task 3) and `ParticipantSummaryDTO`/job-type payload shapes (Tasks 4-5) are each defined once and referenced by name afterward.
