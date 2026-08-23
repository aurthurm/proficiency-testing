# Phase 8 — Ancillary Modules & Dashboards Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build working admin screens for the ancillary entities already scaffolded in `proficiency-testing` (Partner, ContactMessage, FeedbackQuestion/ParticipantFeedback, Announcement, AuditLog, ScheduledJob, ApiRequestLog), add the audit-capture mechanism that doesn't exist yet, and build a new operational dashboard — reaching parity with the *useful* parts of the legacy admin module's equivalent screens, not a blind port of every legacy action.

**Architecture:** Each task follows this repo's existing JHipster pattern per entity: `web/rest/<Entity>Resource.java` (already generated, CRUD-complete) + a thin custom endpoint only where legacy has real behavior beyond CRUD (cancel-job, audit capture) + a React module under `src/main/webapp/app/entities/<kebab-name>/` (list/detail/update/delete-dialog/reducer/reducer.spec/index — the exact 7-file shape already used by every other entity, confirmed against `entities/enrollment/`).

**Tech Stack:** Spring Boot 4, Spring Data JPA, MapStruct, React 19 + Redux 5 + TypeScript (JHipster React blueprint), JUnit 5 + `*ResourceIT.java` integration tests, Vitest/`*.spec.ts(x)` frontend tests.

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md`, Phase 8 section ("Ancillary modules & dashboards").

## Global Constraints

- Zimbabwe-scoped build only (per the master plan's top-level scope decision) — no cross-country features anywhere in this phase.
- Every new backend endpoint gets a `*ResourceIT.java` integration test; every new/changed frontend screen gets a `*-reducer.spec.ts` at minimum.
- Follow the existing generated-CRUD file shape exactly for any entity screen — do not introduce a different frontend pattern for "just this one entity."
- This phase is explicitly lower-risk/lower-complexity per the master plan — do not let scope creep in from Phase 3/5/6 dependencies; where a task's real behavior needs data from those phases (the dashboard does), note the dependency and stub/skip gracefully if that data isn't present yet, don't block this phase's own delivery on it.

---

## Scope correction from research (read this before Task 4)

The master plan assumed legacy's "Announcement" feature maps directly onto the current app's `Announcement` entity. It does not. Confirmed by reading `application/modules/admin/controllers/AnnouncementController.php` and `application/services/Announcement.php` in the legacy app:

- **Legacy "announcement"** is a targeted non-participation follow-up tool: an admin composes a subject/message, selects specific participant IDs (typically non-responders to a shipment — the query filters `shipment_participant_map` where `response_status NOT IN ('responded','late','nottested')`), and the service queues an email to each targeted participant via `Application_Service_Common::insertTempMail()` (the legacy email queue). It also builds a list of data managers for a "push notification" step — but nothing in the method actually sends a push notification, confirming Phase 0.3's research finding that FCM push is vestigial/dead code in this codebase, not a feature to port.
- **Current app's `Announcement` entity** (`title`, `body`, `status: ContentStatus`, `publishedFrom`, `publishedTo`) models a completely different concept: a static site bulletin with a publish window, no targeting, no email integration.

**Decision for this plan:** build the current `Announcement` entity as-is (a simple site-bulletin CRUD screen, Task 4 below) — that is genuinely this phase's scope. Legacy's targeted non-participation email tool is a *different, more complex feature* that depends on Phase 3 (shipment participation status) and Phase 0.3/7 (email queue) — it does not belong in this phase and is not built here. If the team wants that capability, it needs its own task in Phase 7 (Notifications), added when Phase 3's data is available. Do not conflate the two under one entity.

---

### Task 1: Partner management

**Files:**
- Modify: `src/main/webapp/app/entities/partner/partner.tsx`, `partner-detail.tsx`, `partner-update.tsx`, `partner-delete-dialog.tsx`, `partner.reducer.ts`, `partner-reducer.spec.ts`, `index.tsx` (all already exist as generated scaffolding — confirmed entity fields: `id`, `name`, `link`, `logoRef`, `sortOrder`, `status`)
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/PartnerResourceIT.java` (already exists, generated)

Legacy reference confirmed (`application/modules/admin/controllers/PartnersController.php`): simple add/edit/list, gated by the `config-ept` admin privilege — no business logic beyond CRUD and an audit-log line on add (`"Added a new partner - {name}"`, category `config`). This is the simplest task in the phase — do it first to confirm the review/test loop before tackling the others.

- [ ] **Step 1: Write the failing frontend test for sort-order display**

```typescript
// src/main/webapp/app/entities/partner/partner-reducer.spec.ts (extend existing file)
it('displays partners ordered by sortOrder ascending', async () => {
  const partners = [
    { id: 2, name: 'Second', sortOrder: 2 },
    { id: 1, name: 'First', sortOrder: 1 },
  ];
  // existing getEntities thunk already fetches; assert the reducer's
  // default sort matches what the list screen renders
  const state = reducer(initialState, { type: 'partner/fetch_entity_list/fulfilled', payload: { data: partners } });
  expect(state.entities.map(p => p.id)).toEqual([1, 2]);
});
```

- [ ] **Step 2: Run it to verify it fails**

Run: `npm test -- partner-reducer.spec.ts`
Expected: FAIL — the generated reducer doesn't sort by `sortOrder`, it returns API order as-is.

- [ ] **Step 3: Add sort-by-sortOrder to the list screen (not the reducer — display concern, not state concern)**

```typescript
// src/main/webapp/app/entities/partner/partner.tsx — in the render, before mapping rows:
const sortedPartners = [...partnerList].sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0));
```

Update the reducer spec above to assert against the component's sort function instead of the reducer if it's colocated there — keep the test and the implementation in the same layer.

- [ ] **Step 4: Run it to verify it passes**

Run: `npm test -- partner-reducer.spec.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/app/entities/partner/
git commit -m "feat: sort partner list by sortOrder"
```

---

### Task 2: Contact/help message inbox

**Files:**
- Modify: `src/main/webapp/app/entities/contact-message/contact-message.tsx`, `contact-message-detail.tsx`, `index.tsx` (already exist, generated)
- Create: `src/main/webapp/app/entities/contact-message/contact-message-mark-handled.tsx` (a small action button component — legacy's Help controller doesn't have a distinct one, but the entity's `isHandled: boolean` field implies a workflow the generated CRUD screen doesn't expose as anything more than a plain checkbox in the edit form)
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/ContactMessageResource.java:` add a `PATCH /api/contact-messages/{id}/mark-handled` endpoint (partial update is generic in the generated resource; this one is a named, single-purpose action matching how an operator actually works — one click, not "edit the whole record")
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/ContactMessageResourceIT.java` (extend)

Entity fields confirmed: `id`, `name`, `email`, `subject`, `message`, `ipAddress`, `submittedAt`, `isHandled`.

- [ ] **Step 1: Write the failing integration test**

```java
// src/test/java/zw/org/nmrl/ept/web/rest/ContactMessageResourceIT.java
@Test
@Transactional
void markHandledSetsIsHandledTrue() throws Exception {
    ContactMessage saved = contactMessageRepository.saveAndFlush(createEntity());
    assertThat(saved.getIsHandled()).isFalse();

    restContactMessageMockMvc
        .perform(patch(ENTITY_API_URL_ID, saved.getId()).with(csrf()))
        .andExpect(status().isOk());

    ContactMessage updated = contactMessageRepository.findById(saved.getId()).orElseThrow();
    assertThat(updated.getIsHandled()).isTrue();
}
```

- [ ] **Step 2: Run it to verify it fails**

Run: `./mvnw -Dtest=ContactMessageResourceIT#markHandledSetsIsHandledTrue test`
Expected: FAIL — 404, endpoint doesn't exist yet.

- [ ] **Step 3: Add the endpoint**

```java
// src/main/java/zw/org/nmrl/ept/web/rest/ContactMessageResource.java
@PatchMapping("/contact-messages/{id}/mark-handled")
public ResponseEntity<ContactMessageDTO> markHandled(@PathVariable Long id) {
    ContactMessageDTO existing = contactMessageService.findOne(id)
        .orElseThrow(() -> new BadRequestAlertException("Contact message not found", ENTITY_NAME, "idnotfound"));
    existing.setIsHandled(true);
    ContactMessageDTO result = contactMessageService.update(existing);
    return ResponseEntity.ok(result);
}
```

- [ ] **Step 4: Run it to verify it passes**

Run: `./mvnw -Dtest=ContactMessageResourceIT#markHandledSetsIsHandledTrue test`
Expected: PASS

- [ ] **Step 5: Add the "Mark handled" button to the frontend list/detail screens, calling the new endpoint via a one-line addition to `contact-message.reducer.ts`'s action set**

- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/web/rest/ContactMessageResource.java src/test/java/zw/org/nmrl/ept/web/rest/ContactMessageResourceIT.java src/main/webapp/app/entities/contact-message/
git commit -m "feat: add mark-handled action to contact message inbox"
```

---

### Task 3: Feedback question bank & response reporting

**Files:**
- Modify: `src/main/webapp/app/entities/feedback-question/*` (generated, entity fields confirmed: `id`, `questionText`, `displayOrder`, `status`)
- Create: `src/main/webapp/app/modules/feedback-report/feedback-report.tsx` — a per-shipment response summary view, not a generated entity screen (legacy's `shipment-questions` action is exactly this: "show me all feedback responses for shipment X")
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/ParticipantFeedbackResource.java`: add `GET /api/participant-feedbacks/by-shipment/{shipmentId}` (custom query endpoint — `ParticipantFeedback` fields confirmed: `id`, `answer`, `submittedAt`; the entity must already carry a shipment reference — verify the actual FK field name on `ParticipantFeedback` via `get_symbol_source` before writing this task's real implementation, it wasn't confirmed in this research pass)
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/ParticipantFeedbackResourceIT.java` (extend)

Legacy reference confirmed (`FeedbackResponsesController.php`): `questionsAction()` manages the question bank, `shipmentQuestionsAction` (route: `shipment-questions`) reports responses filtered to one shipment, gated by `config-ept` privilege.

- [ ] **Step 1: Confirm the actual FK field name on `ParticipantFeedback` before writing any test**

Run: use jCodemunch `get_symbol_source` on `ParticipantFeedback` in this repo. Record the real field name (likely `shipment` or `shipmentId`) — do not guess it in the test below; substitute the confirmed name.

- [ ] **Step 2: Write the failing integration test** (using the confirmed field name from Step 1, shown here as `shipment` — substitute if different)

```java
// src/test/java/zw/org/nmrl/ept/web/rest/ParticipantFeedbackResourceIT.java
@Test
@Transactional
void getByShipmentReturnsOnlyThatShipmentsResponses() throws Exception {
    Shipment shipmentA = shipmentRepository.saveAndFlush(createShipmentEntity());
    Shipment shipmentB = shipmentRepository.saveAndFlush(createShipmentEntity());
    participantFeedbackRepository.saveAndFlush(createEntity().shipment(shipmentA));
    participantFeedbackRepository.saveAndFlush(createEntity().shipment(shipmentB));

    restParticipantFeedbackMockMvc
        .perform(get("/api/participant-feedbacks/by-shipment/{shipmentId}", shipmentA.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
}
```

- [ ] **Step 3: Run it to verify it fails**

Run: `./mvnw -Dtest=ParticipantFeedbackResourceIT#getByShipmentReturnsOnlyThatShipmentsResponses test`
Expected: FAIL — 404, endpoint doesn't exist.

- [ ] **Step 4: Add the endpoint and repository method**

```java
// src/main/java/zw/org/nmrl/ept/repository/ParticipantFeedbackRepository.java
List<ParticipantFeedback> findByShipmentId(Long shipmentId);
```

```java
// src/main/java/zw/org/nmrl/ept/web/rest/ParticipantFeedbackResource.java
@GetMapping("/participant-feedbacks/by-shipment/{shipmentId}")
public List<ParticipantFeedbackDTO> getByShipment(@PathVariable Long shipmentId) {
    return participantFeedbackRepository.findByShipmentId(shipmentId).stream()
        .map(participantFeedbackMapper::toDto)
        .toList();
}
```

- [ ] **Step 5: Run it to verify it passes**

Run: `./mvnw -Dtest=ParticipantFeedbackResourceIT#getByShipmentReturnsOnlyThatShipmentsResponses test`
Expected: PASS

- [ ] **Step 6: Build the `feedback-report` frontend view (a per-shipment list, not a generated CRUD table) and commit**

```bash
git add src/main/java/zw/org/nmrl/ept/repository/ParticipantFeedbackRepository.java src/main/java/zw/org/nmrl/ept/web/rest/ParticipantFeedbackResource.java src/test/java/zw/org/nmrl/ept/web/rest/ParticipantFeedbackResourceIT.java src/main/webapp/app/modules/feedback-report/ src/main/webapp/app/entities/feedback-question/
git commit -m "feat: add per-shipment feedback response report"
```

---

### Task 4: Announcement (site bulletin — see scope-correction note above, NOT legacy's targeted-messaging feature)

**Files:**
- Modify: `src/main/webapp/app/entities/announcement/*` (generated, fields: `title`, `body`, `status: ContentStatus`, `publishedFrom`, `publishedTo`)
- Modify: `src/main/webapp/app/entities/announcement/announcement.tsx`: filter the public-facing list to `publishedFrom <= now <= publishedTo` and `status == PUBLISHED` (the generated list screen shows every row regardless of publish window — that's an admin view; a participant-facing view needs the window filter)
- Test: extend `announcement-reducer.spec.ts`

- [ ] **Step 1: Write the failing frontend test**

```typescript
// src/main/webapp/app/entities/announcement/announcement-reducer.spec.ts
it('excludes announcements outside their publish window', () => {
  const now = new Date('2026-08-23T00:00:00Z');
  const announcements = [
    { id: 1, status: 'PUBLISHED', publishedFrom: '2026-01-01T00:00:00Z', publishedTo: '2026-12-31T00:00:00Z' },
    { id: 2, status: 'PUBLISHED', publishedFrom: '2027-01-01T00:00:00Z', publishedTo: '2027-12-31T00:00:00Z' },
  ];
  expect(filterActiveAnnouncements(announcements, now).map(a => a.id)).toEqual([1]);
});
```

- [ ] **Step 2: Run it to verify it fails**

Run: `npm test -- announcement-reducer.spec.ts`
Expected: FAIL — `filterActiveAnnouncements` doesn't exist yet.

- [ ] **Step 3: Implement the filter**

```typescript
// src/main/webapp/app/entities/announcement/announcement.tsx
export const filterActiveAnnouncements = (announcements: IAnnouncement[], now: Date = new Date()) =>
  announcements.filter(
    a =>
      a.status === 'PUBLISHED' &&
      (!a.publishedFrom || new Date(a.publishedFrom) <= now) &&
      (!a.publishedTo || new Date(a.publishedTo) >= now),
  );
```

- [ ] **Step 4: Run it to verify it passes**

Run: `npm test -- announcement-reducer.spec.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/app/entities/announcement/
git commit -m "feat: filter announcements to their publish window"
```

---

### Task 5: Audit-capture infrastructure (does not exist yet — confirmed by research, not "extend existing")

**Confirmed finding, not an assumption:** grepped the entire backend for any code writing to `AuditLog` besides the generated CRUD stack (`AuditLogResource`/`AuditLogServiceImpl`/`AuditLogRepository`) — nothing does. JHipster's `AbstractAuditingEntity`/`SpringSecurityAuditorAware` only populate `createdBy`/`lastModifiedBy` on regular entities; they do not write `AuditLog` rows. This must be built, matching legacy's per-action pattern (`Application_Model_DbTable_AuditLog::addNewAuditLog($message, $category)`, called explicitly at the point of each significant admin action — seen in `AnnouncementController::composeAction`, `PartnersController::addAction`, etc.).

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/AuditLogRecorder.java` — a small injectable service other services call explicitly at significant actions, matching legacy's explicit-call pattern (not a blanket AOP interceptor — legacy is selective about what it logs, e.g. "Added a new partner", not every field-level change)
- Modify: `src/main/webapp/app/entities/audit-log/audit-log.tsx` — add filtering by `action`/date range (generated list has no filter UI beyond the default pagination)
- Test: `src/test/java/zw/org/nmrl/ept/service/AuditLogRecorderTest.java`

- [ ] **Step 1: Write the failing unit test**

```java
// src/test/java/zw/org/nmrl/ept/service/AuditLogRecorderTest.java
@Test
void recordCapturesActionStatementAndCurrentUser() {
    AuditLogRecorder recorder = new AuditLogRecorder(auditLogRepository);
    // SecurityContext set up with a known principal in @BeforeEach, per existing test conventions in this repo

    recorder.record(AuditAction.CREATE, "Added a new partner - Global Fund");

    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository).save(captor.capture());
    assertThat(captor.getValue().getStatement()).isEqualTo("Added a new partner - Global Fund");
    assertThat(captor.getValue().getPerformedOn()).isNotNull();
}
```

- [ ] **Step 2: Run it to verify it fails**

Run: `./mvnw -Dtest=AuditLogRecorderTest test`
Expected: FAIL — `AuditLogRecorder` doesn't exist.

- [ ] **Step 3: Implement it**

```java
// src/main/java/zw/org/nmrl/ept/service/AuditLogRecorder.java
package zw.org.nmrl.ept.service;

import java.time.Instant;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import zw.org.nmrl.ept.domain.AuditLog;
import zw.org.nmrl.ept.domain.enumeration.AuditAction;
import zw.org.nmrl.ept.repository.AuditLogRepository;

@Service
public class AuditLogRecorder {

    private final AuditLogRepository auditLogRepository;

    public AuditLogRecorder(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(AuditAction action, String statement) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setStatement(statement);
        log.setPerformedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        log.setPerformedOn(Instant.now());
        auditLogRepository.save(log);
    }
}
```

- [ ] **Step 4: Run it to verify it passes**

Run: `./mvnw -Dtest=AuditLogRecorderTest test`
Expected: PASS

- [ ] **Step 5: Wire `AuditLogRecorder.record(...)` into Task 1's Partner-add flow and Task 4's Announcement-publish flow as the first two real call sites, proving the pattern end to end — do not wire it into every service in this task; that's follow-up work as each later phase's significant actions are identified**

- [ ] **Step 6: Add action/date-range filters to the `audit-log.tsx` list screen**

- [ ] **Step 7: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/AuditLogRecorder.java src/test/java/zw/org/nmrl/ept/service/AuditLogRecorderTest.java src/main/webapp/app/entities/audit-log/
git commit -m "feat: add audit-log capture service and wire first two call sites"
```

---

### Task 6: Job tracking UI (list + cancel)

**Confirmed real behavior, not read-only:** legacy's `JobTrackingController::cancelJobAction()` is a genuine action (`Application_Service_Evaluation::cancelJob($jobId, $queueType)`), not just a viewer. The current app's `ScheduledJob` entity already has `JobStatus.CANCELLED` in its enum, confirming this was anticipated in the data model.

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/ScheduledJobResource.java`: add `POST /api/scheduled-jobs/{id}/cancel`
- Modify: `src/main/webapp/app/entities/scheduled-job/scheduled-job.tsx`: add a "Cancel" button for rows where `status IN (PENDING, RUNNING)`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/ScheduledJobResourceIT.java` (extend)

- [ ] **Step 1: Write the failing integration test**

```java
// src/test/java/zw/org/nmrl/ept/web/rest/ScheduledJobResourceIT.java
@Test
@Transactional
void cancelSetsStatusToCancelled() throws Exception {
    ScheduledJob saved = scheduledJobRepository.saveAndFlush(createEntity().status(JobStatus.RUNNING));

    restScheduledJobMockMvc
        .perform(post(ENTITY_API_URL_ID + "/cancel", saved.getId()).with(csrf()))
        .andExpect(status().isOk());

    ScheduledJob updated = scheduledJobRepository.findById(saved.getId()).orElseThrow();
    assertThat(updated.getStatus()).isEqualTo(JobStatus.CANCELLED);
}

@Test
@Transactional
void cancelRejectsAlreadyCompletedJob() throws Exception {
    ScheduledJob saved = scheduledJobRepository.saveAndFlush(createEntity().status(JobStatus.COMPLETED));

    restScheduledJobMockMvc
        .perform(post(ENTITY_API_URL_ID + "/cancel", saved.getId()).with(csrf()))
        .andExpect(status().isBadRequest());
}
```

- [ ] **Step 2: Run it to verify both fail**

Run: `./mvnw -Dtest=ScheduledJobResourceIT#cancelSetsStatusToCancelled+cancelRejectsAlreadyCompletedJob test`
Expected: FAIL — 404, endpoint doesn't exist.

- [ ] **Step 3: Implement the endpoint**

```java
// src/main/java/zw/org/nmrl/ept/web/rest/ScheduledJobResource.java
@PostMapping("/scheduled-jobs/{id}/cancel")
public ResponseEntity<ScheduledJobDTO> cancel(@PathVariable Long id) {
    ScheduledJobDTO job = scheduledJobService.findOne(id)
        .orElseThrow(() -> new BadRequestAlertException("Job not found", ENTITY_NAME, "idnotfound"));
    if (job.getStatus() != JobStatus.PENDING && job.getStatus() != JobStatus.RUNNING) {
        throw new BadRequestAlertException("Only pending or running jobs can be cancelled", ENTITY_NAME, "invalidstatus");
    }
    job.setStatus(JobStatus.CANCELLED);
    return ResponseEntity.ok(scheduledJobService.update(job));
}
```

- [ ] **Step 4: Run it to verify both pass**

Run: `./mvnw -Dtest=ScheduledJobResourceIT#cancelSetsStatusToCancelled+cancelRejectsAlreadyCompletedJob test`
Expected: PASS

- [ ] **Step 5: Add the Cancel button, wired to the new endpoint, conditionally rendered by status. Commit.**

```bash
git add src/main/java/zw/org/nmrl/ept/web/rest/ScheduledJobResource.java src/test/java/zw/org/nmrl/ept/web/rest/ScheduledJobResourceIT.java src/main/webapp/app/entities/scheduled-job/
git commit -m "feat: add job cancellation to scheduled job tracking"
```

**Note for later phases:** this endpoint only flips the DB row's status — it does not yet interrupt an in-flight `@Scheduled` poller execution (that requires Phase 0.2's job-processor to check for `CANCELLED` status between work units). Cross-reference with the Phase 0 implementation plan when Phase 0.2's processor is built; this task's cancel button will be inert until that check exists, which is acceptable sequencing (the button and the enforcement can ship independently and be joined later) but must not be forgotten.

---

### Task 7: API request log viewer

**Files:**
- Modify: `src/main/webapp/app/entities/api-request-log/api-request-log.tsx` (generated; fields confirmed: `transactionId`, `requestedBy`, `requestedOn`, `numberOfRecords`, `requestType`, `testType`, `apiUrl`, `dataFormat`)
- No backend changes — legacy's `ApiHistoryController::indexAction()` is read-only (`fetchAllApiSyncDetailsByGrid`/`fetchTrackApiHistoryList`), matching the generated CRUD read path exactly.

- [ ] **Step 1: Write the failing frontend test for default sort**

```typescript
// src/main/webapp/app/entities/api-request-log/api-request-log-reducer.spec.ts
it('defaults to newest-first ordering by requestedOn', () => {
  // assert the initial getEntities() thunk call includes sort=requestedOn,desc
  expect(getEntities()).toEqual(expect.objectContaining({ meta: expect.objectContaining({ requestUrl: expect.stringContaining('sort=requestedOn,desc') }) }));
});
```

- [ ] **Step 2: Run it to verify it fails**

Run: `npm test -- api-request-log-reducer.spec.ts`
Expected: FAIL — generated default sort is by `id`, not `requestedOn`.

- [ ] **Step 3: Change the default sort in the reducer's initial `ITEMS_PER_PAGE`/sort constant**

```typescript
// src/main/webapp/app/entities/api-request-log/api-request-log.reducer.ts
const initialState: EntityState<IApiRequestLog> = {
  ...
  // change default sort constant from 'id,asc' to:
  // 'requestedOn,desc'
};
```

- [ ] **Step 4: Run it to verify it passes**

Run: `npm test -- api-request-log-reducer.spec.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/app/entities/api-request-log/
git commit -m "feat: default API request log to newest-first ordering"
```

---

### Task 8: Operational dashboard (new value-add, not a legacy port)

Per the master plan, this is scoped to actual stakeholder need at build time, not assumed legacy parity (legacy's own dashboard sophistication wasn't characterized during research). Build the smallest useful version now; treat it as extensible rather than final.

**Dependency note:** two of the three widgets below need data this phase alone doesn't produce (Phase 3's shipment status, Phase 5's pass/fail results). Build the widget shell and the participant-count widget now (data already exists); wire the other two once Phase 3/5 land — don't block this task's own delivery waiting for them.

**Files:**
- Create: `src/main/webapp/app/modules/dashboard/dashboard.tsx`
- Create: `src/main/webapp/app/modules/dashboard/dashboard.reducer.ts`
- Create: `src/main/webapp/app/modules/dashboard/dashboard-reducer.spec.ts`
- Create: `src/main/java/zw/org/nmrl/ept/web/rest/DashboardResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/DashboardResourceIT.java`

- [ ] **Step 1: Write the failing integration test for the participant-count endpoint**

```java
// src/test/java/zw/org/nmrl/ept/web/rest/DashboardResourceIT.java
@Test
@Transactional
void getParticipantCountReturnsActiveCount() throws Exception {
    participantRepository.saveAndFlush(createEntity().status(ParticipantStatus.ACTIVE));
    participantRepository.saveAndFlush(createEntity().status(ParticipantStatus.INACTIVE));

    restDashboardMockMvc
        .perform(get("/api/dashboard/participant-count"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(1))
        .andExpect(jsonPath("$.total").value(2));
}
```

- [ ] **Step 2: Run it to verify it fails**

Run: `./mvnw -Dtest=DashboardResourceIT test`
Expected: FAIL — `DashboardResource` doesn't exist. (Confirm the real `Participant` status field/enum name via jCodemunch before implementing — Phase 2's plan is the source of truth for this entity's exact shape; don't assume `ParticipantStatus`/`status` without checking.)

- [ ] **Step 3: Implement the endpoint**

```java
// src/main/java/zw/org/nmrl/ept/web/rest/DashboardResource.java
@RestController
@RequestMapping("/api/dashboard")
public class DashboardResource {

    private final ParticipantRepository participantRepository;

    public DashboardResource(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @GetMapping("/participant-count")
    public ParticipantCountDTO getParticipantCount() {
        long total = participantRepository.count();
        long active = participantRepository.countByStatus(ParticipantStatus.ACTIVE);
        return new ParticipantCountDTO(active, total);
    }
}
```

(Add `ParticipantCountDTO` as a small record in the same package, and `countByStatus` to `ParticipantRepository` if it doesn't already exist.)

- [ ] **Step 4: Run it to verify it passes**

Run: `./mvnw -Dtest=DashboardResourceIT test`
Expected: PASS

- [ ] **Step 5: Build the dashboard shell with the participant-count widget wired in, plus two placeholder widget slots visually present but disabled/greyed with a "available after Phase 3/5" label — not silently missing, explicitly marked as pending**

- [ ] **Step 6: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/web/rest/DashboardResource.java src/test/java/zw/org/nmrl/ept/web/rest/DashboardResourceIT.java src/main/webapp/app/modules/dashboard/
git commit -m "feat: add operational dashboard shell with participant-count widget"
```

---

### Task 9: Explicit descopes (record the decision, don't build)

Two legacy Phase-8-adjacent controllers were found during research that should **not** be ported as separate features:

- **`LogViewerController.php`** — a raw application-log-file tailer (reads server log files directly, including a delete-log-file action). This is an ops/observability concern, not a business feature, and doesn't correspond to any entity in the current app. Consistent with the master plan's Phase 0.2 stance on ops scripts ("infra concern, not business logic"): recommend standard log aggregation (structured logging already via Logback + whatever the deployment's log shipping/aggregation is, e.g. `journalctl`/a hosted log service) instead of building an in-app log-file browser. No task needed.
- **`AlertsController.php`** — turned out to be specifically email-delivery-failure monitoring (`Application_Service_Common::getEmailFailureInGrid()`), not a general alerting system. Once Phase 0.3's `EmailMessage` entity has a delivery-status field, add an "email failures" widget to Task 8's dashboard rather than building a separate "Alerts" module — same underlying data, no need for a second screen.

- [ ] Record this decision in `ept_project_plan.md`'s Phase 8 section when this plan is executed, so the master roadmap reflects the narrower real scope.

---

## Self-Review

**1. Spec coverage:** Phase 8's master-plan bullets — announcements/partners/help/feedback (Tasks 1-4), audit log viewer (Task 5), job tracking (Task 6), API request log viewer (Task 7), dashboards (Task 8) — all covered. `LogViewerController`/`AlertsController` (mentioned in the master plan's "Legacy reference" line but not its task bullets) are explicitly addressed in Task 9 rather than silently dropped.

**2. Placeholder scan:** No TBD/TODO markers. One deliberate exception, flagged as such rather than hidden: Task 3 Step 1 and Task 8 Step 2 require confirming a field name via jCodemunch before the exact code can be finalized — this is real, necessary uncertainty (not yet confirmed in this research pass) called out explicitly with an action to resolve it, not glossed over.

**3. Type consistency:** `AuditAction`, `JobStatus`, `ParticipantStatus` (pending confirmation) used consistently with the enum names confirmed via direct source reads in this session. `AuditLogRecorder.record(AuditAction, String)` signature is used identically in Task 5's test and implementation.
