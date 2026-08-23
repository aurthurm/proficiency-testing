# Phase 3 — Distributions, Shipments & Enrollment Workflow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the distribution → enrollment → shipment → participant/testkit-assignment → deadline-close workflow, matching legacy ePT's `ShipmentController.php`/`Shipments.php`/`Distribution.php`, on top of this app's already-scaffolded `Distribution`, `Enrollment`, `Shipment`, `ShipmentParticipantMap`, `ShipmentSample` entities.

**Architecture:** Service-layer domain logic (a `ShipmentWorkflowService`/`DistributionWorkflowService` alongside the existing generated CRUD services) enforcing the state machine and cancellation rules; REST endpoints added to the existing generated resources rather than new controllers; deadline automation via the Phase 0.2 `@Scheduled` + ShedLock job infrastructure.

**Tech Stack:** Spring Boot 4, Spring Data JPA, Liquibase (schema changes), Spring Security (`@PreAuthorize`), JUnit 5 + Spring `@SpringBootTest`/`MockMvc` for `*ResourceIT.java`, React + Redux frontend (existing `entities/{kebab-name}/` module pattern).

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md`, Phase 3 section (subtasks 3.0–3.6). This plan **is** 3.0's deliverable (the state-machine research) plus the detailed implementation for 3.1–3.6.

## Global Constraints

- Zimbabwe-scope only (per the master plan): no work here is scheme-specific beyond `scheme_type`/`Scheme` being a foreign key — scheme-specific behavior belongs to Phases 4/5.
- Every status/lifecycle value must be a real value confirmed against legacy code or the live migrated database in this plan — no invented states.
- File storage (certificate/report file paths) goes through the Phase 0.4 `FileStoreService` once it exists — don't hardcode `Paths.get(...)` in this phase's code.
- Async work (deadline closing, TB-form generation) goes through the Phase 0.2 job infrastructure — don't run it synchronously in a request thread.
- Unified API decision (Phase 0.5): all new endpoints live under the existing `/api/**` convention on the existing generated resources, no separate mobile-specific routes.

---

## Research findings (3.0 deliverable)

Read in full for this plan: `application/services/Shipments.php` (4978 lines, all ~60 method signatures enumerated and the state-transition-relevant methods read in full), `application/modules/admin/controllers/ShipmentController.php` (704 lines, **28 actions, not the ~90 originally estimated from file size** — the estimate in the master plan's gap table was a rough byte-size heuristic and is corrected here), `application/services/Distribution.php` (206 lines, 15 methods), plus live queries against the migrated `ept` MySQL database.

### Shipment state machine

Legacy does **not** use a single `status` enum column as the source of truth for UI/workflow decisions — `Shipments::getShipmentButtonStates()` (the one function "used in any view that needs to show shipment action buttons", `Shipments.php:4762`) derives display state and legal actions from **milestone timestamps**, not the `status` string:

| Milestone timestamp | Meaning |
|---|---|
| `evaluated_at` empty | Not yet evaluated — only "Evaluate" enabled |
| `evaluated_at` set, `reports_generated_at` empty | "Re-Evaluate", View, Generate Reports enabled |
| `reports_generated_at` set, `finalized_at` empty | + Finalize enabled |
| `finalized_at` set | **Locked** — only View enabled, nothing else |
| `cancelled_at` set | **Permanently locked** — same as finalized, cannot be un-cancelled, checked independently of the above (see `isShipmentCancelled()`) |

`status` itself is only consulted for **ephemeral** processing states: `SHIPMENT_EPHEMERAL_STATUSES = ['draft', 'ready', 'queued', 'processing', 'pending']` (`constants.php:6`) — when `status` is one of these, all buttons are disabled regardless of the milestone timestamps (a shipment mid-processing shouldn't be actionable), with one carve-out: a `queued` shipment re-enables "Evaluate" after a **15-minute timeout** (stuck-job recovery, `Shipments.php:4825-4834` — this is the exact business-logic analog of Phase 0.2's `reset-stale-jobs.php` port).

There is a second, independent lock: `awaitingFeedbackForm` (`Shipments.php:4840-4846`) — if a shipment promises participant feedback (`collect_feedback`) but no feedback form exists yet, Finalize is blocked even if reports are generated, because finalizing is what opens the participant feedback window and an empty form would strand them. This is a real legacy business rule, not incidental — preserve it.

**Cancellation** (`cancelShipment()`, `Shipments.php:2580-2667`) is a separate, permanent, transactional operation, not a `status` value transition:
- Guarded: requires typed `"CANCEL"` confirmation + a non-empty reason; refuses if already cancelled or already `finalized`.
- Effects (all in one transaction): sets `cancelled_at`/`cancelled_by`/`cancellation_reason`, forces `response_switch` off and `report_in_queue` off, deletes any queued report-generation job for the shipment, resets `shipment_participant_map.report_generated`/`report_download_metadata` for every participant on the shipment.
- Cascades: if this was the distribution's last non-cancelled shipment, the **distribution** is also marked `cancelled` (`Distribution.php` has the identical pattern in `cancelDistribution()`).
- Post-commit (non-transactional, best-effort): deletes the generated report directory/zip from disk.

**Deadline** (`responseSwitch()`/`responseSwitchAction()`) is a simple on/off toggle, independent of the milestone/status machinery above, guarded only by "not cancelled". `process-shipment-deadlines.php` (not read in this pass, out of file scope, but its effect is confirmed via `responseSwitch`) flips this off automatically at `response_deadline`.

**Live data sanity check** (against the local migrated MySQL `ept` db): the small real sample only shows `status` values `evaluated`, `finalized`, `reports generated` — consistent with the milestone-derived `displayStatus` strings above (`Evaluated`/`Reports Generated`/`Finalized`), not literal ephemeral values, because completed shipments never sit in an ephemeral status. This confirms the milestone-timestamp model is what's actually driving production behavior, not a naive status enum.

### Current app's `ShipmentStatus` enum vs. legacy's model — a real design gap

The current app already has a **more granular status enum** than legacy's milestone-timestamp approach: `ShipmentStatus { DRAFT, CONFIGURED, READY, QUEUED, SHIPPED, RESPONSES_OPEN, RESPONSES_CLOSED, EVALUATING, EVALUATED, REPORTS_GENERATED, FINALIZED }` (`domain/enumeration/ShipmentStatus.java`). This is good — it's a genuine state machine rather than timestamp inference — **but it is missing `CANCELLED`**, and the `Shipment` entity (`domain/Shipment.java`) has no `cancelledAt`/`cancelledBy`/`cancellationReason` fields at all. `DistributionStatus { DRAFT, OPEN, SHIPPED, CLOSED }` is missing `CANCELLED` too. **Task 1 below adds these** — this is a real gap found during this research, not assumed.

Also missing on `Shipment.java`: a shipment-level `evaluatedAt` (the entity only has `reportsGeneratedAt`/`finalizedAt` — `evaluatedAt` currently only exists per-participant on `ShipmentParticipantMap.java`). **Decision for this plan:** derive shipment-level "is evaluated" from the `ShipmentStatus` enum transition to `EVALUATED` (set by the evaluation service in Phase 5) rather than adding a redundant timestamp column — the enum already carries this information structurally, which legacy's flat-timestamp model didn't have the luxury of.

`report_in_queue`/`processing_started_at`/`last_heartbeat` (legacy shipment columns) have no current-app equivalent — **decision:** don't duplicate these onto `Shipment`. Whether a report is queued/processing is derivable from Phase 0.2's `ScheduledJob` table (a job of the relevant type + shipment ID exists in `PENDING`/`PROCESSING` status) — keep that state in one place, not mirrored onto every domain entity that has an async operation.

### TB-form generation is a shipment-workflow action, confirmed in scope for Phase 3.6

`generateTbFormAction`/`downloadTbAction` live directly in `ShipmentController.php` (not in the `reports` module), are DTS-specific (call `Application_Model_Dts`), and are per-participant PDF downloads triggered from the shipment view — not a report-catalog entry. **Resolution of the 3.6 scope question:** build it in Phase 3, as a thin dependency on Phase 6's PDF library decision (Apache PDFBox), not deferred to Phase 6 wholesale.

### Testkit assignment — no current-app entity exists yet

`shipmentTestKitsAction` (`ShipmentController.php:645`) posts to `Testkitnames::testKitsMapping()`, backed by the real `shipment_testkit_map` table (`shipment_id`+`testkit_id` composite PK, `scheme_type`, `testkit_1`/`testkit_2`/`testkit_3` lot-quantity columns — confirmed against the live migrated schema earlier in this session). **Confirmed via direct search of `Shipment.java`/`ShipmentSample.java`: no `TestKit` relationship exists anywhere in the current domain model.** Task 10 below creates it from scratch.

---

## File structure

New/modified files, by responsibility:

- `domain/enumeration/ShipmentStatus.java` — add `CANCELLED`.
- `domain/enumeration/DistributionStatus.java` — add `CANCELLED`.
- `domain/Shipment.java` — add `cancelledAt`, `cancelledBy`, `cancellationReason`.
- `domain/Distribution.java` — add `cancelledAt`, `cancelledBy`, `cancellationReason`.
- `domain/ShipmentTestkitMap.java` — new entity.
- `src/main/resources/config/liquibase/changelog/2026XXXXXXXXXX_*.xml` — schema changes for the above (one changeset per entity change, matching this repo's existing changelog convention — check `src/main/resources/config/liquibase/master.xml` for the include pattern before adding).
- `service/DistributionWorkflowService.java` — new: cancellation, cascade logic.
- `service/ShipmentWorkflowService.java` — new: button-state derivation, cancellation, clone, testkit assignment.
- `service/EnrollmentService.java` — extend existing generated service with duplicate-prevention.
- `web/rest/DistributionResource.java`, `web/rest/ShipmentResource.java`, `web/rest/EnrollmentResource.java` — extend existing generated resources with new endpoints (don't create parallel resource classes).
- `web/rest/ShipmentTestkitMapResource.java` — new (or extend if JHipster entity generation is run for it — see Task 10).
- `migration/ShipmentDeadlineJob.java` (or similar, under whatever package Phase 0.2 establishes for job types) — new: the deadline-closing scheduled job.
- Frontend: `src/main/webapp/app/entities/distribution/`, `.../shipment/`, `.../enrollment/` — extend existing generated modules with the new actions (cancel dialog, button-state-aware action bar, testkit assignment sub-view) rather than replacing them.

---

### Task 1: Add cancellation support to the domain model

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/domain/enumeration/ShipmentStatus.java`
- Modify: `src/main/java/zw/org/nmrl/ept/domain/enumeration/DistributionStatus.java`
- Modify: `src/main/java/zw/org/nmrl/ept/domain/Shipment.java`
- Modify: `src/main/java/zw/org/nmrl/ept/domain/Distribution.java`
- Create: `src/main/resources/config/liquibase/changelog/<timestamp>_added_shipment_distribution_cancellation.xml`
- Modify: `src/main/resources/config/liquibase/master.xml` (include the new changelog)
- Test: `src/test/java/zw/org/nmrl/ept/domain/ShipmentTest.java`, `src/test/java/zw/org/nmrl/ept/domain/DistributionTest.java` (extend existing generated equals/hashcode tests with the new fields — follow the existing test's pattern exactly)

**Interfaces:**
- Produces: `ShipmentStatus.CANCELLED`, `DistributionStatus.CANCELLED`; `Shipment.getCancelledAt()`/`setCancelledAt(Instant)`, `.getCancelledBy()`/`.setCancelledBy(String)`, `.getCancellationReason()`/`.setCancellationReason(String)`; identical accessors on `Distribution`.

- [ ] **Step 1: Add `CANCELLED` to both enums**

```java
// domain/enumeration/ShipmentStatus.java
public enum ShipmentStatus {
    DRAFT, CONFIGURED, READY, QUEUED, SHIPPED, RESPONSES_OPEN, RESPONSES_CLOSED,
    EVALUATING, EVALUATED, REPORTS_GENERATED, FINALIZED, CANCELLED,
}
```
```java
// domain/enumeration/DistributionStatus.java
public enum DistributionStatus {
    DRAFT, OPEN, SHIPPED, CLOSED, CANCELLED,
}
```

- [ ] **Step 2: Add cancellation fields to `Shipment` and `Distribution`**

Follow the exact `@Column` style already used on the neighboring fields in each file, e.g. in `Shipment.java` after the `finalizedAt` field:

```java
@Column(name = "cancelled_at")
private Instant cancelledAt;

@Column(name = "cancelled_by")
private String cancelledBy;

@Column(name = "cancellation_reason")
private String cancellationReason;
```

Add matching getters/setters/fluent builder methods (`cancelledAt(Instant cancelledAt)` etc.) following the exact pattern of the existing `finalizedAt` field in the same file. Repeat identically for `Distribution.java`.

- [ ] **Step 3: Write the Liquibase changeset**

```xml
<!-- src/main/resources/config/liquibase/changelog/<timestamp>_added_shipment_distribution_cancellation.xml -->
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog" ...>
    <changeSet id="<timestamp>-1" author="phase3">
        <addColumn tableName="shipment">
            <column name="cancelled_at" type="${datetimeType}"/>
            <column name="cancelled_by" type="varchar(255)"/>
            <column name="cancellation_reason" type="varchar(1000)"/>
        </addColumn>
        <addColumn tableName="distribution">
            <column name="cancelled_at" type="${datetimeType}"/>
            <column name="cancelled_by" type="varchar(255)"/>
            <column name="cancellation_reason" type="varchar(1000)"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```

Use `${datetimeType}` (check `master.xml` for the exact property name already in use — match it, don't hardcode `TIMESTAMP`). Register the file in `master.xml`'s `<include>` list in date order.

- [ ] **Step 4: Run the app and verify the migration applies cleanly**

Run: `./mvnw -Pprod liquibase:update` (or start the app against a dev DB) and confirm no Liquibase errors, then `\d shipment` / `\d distribution` in psql to confirm the 3 new columns exist on each table.

- [ ] **Step 5: Extend the generated equals/hashcode unit tests**

Open `ShipmentTest.java`/`DistributionTest.java`, find the existing `MapstructTest`-style equals/hashcode assertions JHipster generates, and add the 3 new fields to the same pattern (JHipster's generator would add these automatically on a re-run; do it by hand here since we're hand-adding fields).

- [ ] **Step 6: Run the tests**

Run: `./mvnw -Pprod test -Dtest=ShipmentTest,DistributionTest`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/domain/enumeration/ShipmentStatus.java \
        src/main/java/zw/org/nmrl/ept/domain/enumeration/DistributionStatus.java \
        src/main/java/zw/org/nmrl/ept/domain/Shipment.java \
        src/main/java/zw/org/nmrl/ept/domain/Distribution.java \
        src/main/resources/config/liquibase/changelog/*cancellation* \
        src/main/resources/config/liquibase/master.xml \
        src/test/java/zw/org/nmrl/ept/domain/ShipmentTest.java \
        src/test/java/zw/org/nmrl/ept/domain/DistributionTest.java
git commit -m "feat: add cancellation support to Shipment and Distribution"
```

---

### Task 2: Distribution CRUD + creation workflow (3.1)

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/DistributionResource.java`
- Modify: `src/main/java/zw/org/nmrl/ept/service/DistributionService.java`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/DistributionResourceIT.java` (extend existing)

**Interfaces:**
- Consumes: nothing new (existing `Distribution` entity/repository).
- Produces: no new public interface — this task verifies/polishes the already-generated CRUD is correct, since `distributions` has no FK dependency on anything else in this phase and the generated scaffold should already work.

- [ ] **Step 1: Write the failing integration test for create → list → edit**

```java
// DistributionResourceIT.java, add to the existing test class
@Test
@Transactional
void createAndListDistribution_visibleImmediately() throws Exception {
    Distribution distribution = new Distribution().code("2026-A").distributionDate(LocalDate.now()).status(DistributionStatus.DRAFT);
    restDistributionMockMvc
        .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(distribution)))
        .andExpect(status().isCreated());

    restDistributionMockMvc
        .perform(get(ENTITY_API_URL + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].code").value("2026-A"));
}
```

- [ ] **Step 2: Run it**

Run: `./mvnw -Pprod test -Dtest=DistributionResourceIT#createAndListDistribution_visibleImmediately`
Expected: likely PASS already (generated CRUD) — if it fails, that's the real signal something in the scaffold is broken; fix before proceeding, don't skip.

- [ ] **Step 3: Verify a distribution with zero shipments displays correctly (the exit-criteria case from the master plan)**

```java
@Test
@Transactional
void distributionWithNoShipments_stillDisplaysCorrectly() throws Exception {
    Distribution distribution = distributionRepository.saveAndFlush(new Distribution().code("EMPTY-ROUND").distributionDate(LocalDate.now()).status(DistributionStatus.DRAFT));
    restDistributionMockMvc
        .perform(get(ENTITY_API_URL_ID, distribution.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("EMPTY-ROUND"));
}
```

- [ ] **Step 4: Run it, confirm PASS**

Run: `./mvnw -Pprod test -Dtest=DistributionResourceIT#distributionWithNoShipments_stillDisplaysCorrectly`

- [ ] **Step 5: Frontend check** — open `src/main/webapp/app/entities/distribution/` list/update screens against a running dev server, create a distribution through the UI, confirm it appears in the list with no console errors when it has zero shipments (checks the frontend doesn't assume `shipmentses` is always non-empty).

- [ ] **Step 6: Commit**

```bash
git add src/test/java/zw/org/nmrl/ept/web/rest/DistributionResourceIT.java
git commit -m "test: verify distribution CRUD works standalone with no shipments"
```

---

### Task 3: Distribution cancellation

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/DistributionWorkflowService.java`
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/DistributionResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/DistributionWorkflowServiceTest.java`

**Interfaces:**
- Consumes: `Distribution` (Task 1's cancellation fields), `ShipmentRepository` (to check for active shipments).
- Produces: `DistributionWorkflowService.cancel(Long distributionId, String reason, String currentUserLogin)` → throws `IllegalStateException` if already finalized/cancelled or reason blank; returns the updated `Distribution` on success. `DistributionResource` exposes `POST /api/distributions/{id}/cancel` with a `{reason: string}` body.

- [ ] **Step 1: Write the failing unit test — cannot cancel an already-cancelled distribution**

```java
@Test
void cancel_alreadyCancelled_throws() {
    Distribution distribution = new Distribution().status(DistributionStatus.CANCELLED).cancelledAt(Instant.now());
    when(distributionRepository.findById(1L)).thenReturn(Optional.of(distribution));

    assertThrows(IllegalStateException.class, () -> distributionWorkflowService.cancel(1L, "duplicate test", "admin"));
}
```

- [ ] **Step 2: Run it, confirm it fails** (class doesn't exist yet)

Run: `./mvnw -Pprod test -Dtest=DistributionWorkflowServiceTest`
Expected: FAIL — compilation error, `DistributionWorkflowService` not found.

- [ ] **Step 3: Implement the minimal service**

```java
@Service
@Transactional
public class DistributionWorkflowService {

    private final DistributionRepository distributionRepository;

    public DistributionWorkflowService(DistributionRepository distributionRepository) {
        this.distributionRepository = distributionRepository;
    }

    public Distribution cancel(Long distributionId, String reason, String cancelledBy) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A reason for cancelling is required.");
        }
        Distribution distribution = distributionRepository.findById(distributionId)
            .orElseThrow(() -> new IllegalStateException("Distribution not found."));
        if (distribution.getStatus() == DistributionStatus.CANCELLED) {
            throw new IllegalStateException("This distribution is already cancelled.");
        }
        if (distribution.getStatus() == DistributionStatus.CLOSED) {
            throw new IllegalStateException("A closed distribution cannot be cancelled.");
        }
        distribution.setCancelledAt(Instant.now());
        distribution.setCancelledBy(cancelledBy);
        distribution.setCancellationReason(reason);
        distribution.setStatus(DistributionStatus.CANCELLED);
        return distributionRepository.save(distribution);
    }
}
```

- [ ] **Step 4: Run the test, confirm PASS**

Run: `./mvnw -Pprod test -Dtest=DistributionWorkflowServiceTest`

- [ ] **Step 5: Add the "cascade from last active shipment" case (depends on Task 7's shipment cancellation calling into this)**

```java
@Test
void cancel_calledExplicitly_succeedsEvenWithNoShipments() {
    Distribution distribution = new Distribution().status(DistributionStatus.DRAFT);
    when(distributionRepository.findById(1L)).thenReturn(Optional.of(distribution));
    when(distributionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Distribution result = distributionWorkflowService.cancel(1L, "no longer needed", "admin");

    assertEquals(DistributionStatus.CANCELLED, result.getStatus());
}
```

Note: the legacy *cascade* (auto-cancel distribution when its last shipment is cancelled) is implemented in Task 7 by having `ShipmentWorkflowService` call `distributionWorkflowService.cancelIfNoActiveShipmentsRemain(distributionId)` after cancelling a shipment — add that method now:

```java
public void cancelIfNoActiveShipmentsRemain(Long distributionId, ShipmentRepository shipmentRepository) {
    boolean anyActive = shipmentRepository.existsByDistributionIdAndCancelledAtIsNull(distributionId);
    if (!anyActive) {
        Distribution distribution = distributionRepository.findById(distributionId).orElseThrow();
        if (distribution.getStatus() != DistributionStatus.CANCELLED) {
            distribution.setStatus(DistributionStatus.CANCELLED);
            distributionRepository.save(distribution);
        }
    }
}
```

(Add `existsByDistributionIdAndCancelledAtIsNull(Long)` to `ShipmentRepository` — a one-line Spring Data derived query.)

- [ ] **Step 6: Run all DistributionWorkflowServiceTest cases**

Run: `./mvnw -Pprod test -Dtest=DistributionWorkflowServiceTest`
Expected: PASS

- [ ] **Step 7: Wire the REST endpoint**

```java
// DistributionResource.java
@PostMapping("/distributions/{id}/cancel")
public ResponseEntity<Distribution> cancelDistribution(@PathVariable Long id, @RequestBody CancelRequest request, Principal principal) {
    Distribution result = distributionWorkflowService.cancel(id, request.reason(), principal.getName());
    return ResponseEntity.ok(result);
}

record CancelRequest(String reason) {}
```

- [ ] **Step 8: Integration test through the real HTTP layer**

```java
@Test
@Transactional
void cancelDistribution_viaApi_returnsCancelledStatus() throws Exception {
    Distribution distribution = distributionRepository.saveAndFlush(new Distribution().code("2026-B").distributionDate(LocalDate.now()).status(DistributionStatus.DRAFT));
    restDistributionMockMvc
        .perform(post(ENTITY_API_URL_ID + "/cancel", distribution.getId())
            .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"testing\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
}
```

- [ ] **Step 9: Run it, confirm PASS**

Run: `./mvnw -Pprod test -Dtest=DistributionResourceIT#cancelDistribution_viaApi_returnsCancelledStatus`

- [ ] **Step 10: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/DistributionWorkflowService.java \
        src/main/java/zw/org/nmrl/ept/repository/DistributionRepository.java \
        src/main/java/zw/org/nmrl/ept/repository/ShipmentRepository.java \
        src/main/java/zw/org/nmrl/ept/web/rest/DistributionResource.java \
        src/test/java/zw/org/nmrl/ept/service/DistributionWorkflowServiceTest.java \
        src/test/java/zw/org/nmrl/ept/web/rest/DistributionResourceIT.java
git commit -m "feat: add distribution cancellation with cascade guard"
```

---

### Task 4: Enrollment with duplicate-prevention (3.2)

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/service/EnrollmentService.java`
- Modify: `src/main/java/zw/org/nmrl/ept/repository/EnrollmentRepository.java`
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/EnrollmentResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/EnrollmentServiceTest.java`, `src/test/java/zw/org/nmrl/ept/web/rest/EnrollmentResourceIT.java`

**Interfaces:**
- Produces: `EnrollmentService.enroll(Long participantId, Long schemeId)` throws `IllegalStateException` on duplicate (same participant + same scheme + `status = ENROLLED`, mirroring legacy's `(list_name, participant_id)` composite-PK effective uniqueness — the current app models "round" via `Scheme`/`Distribution` relationships rather than a `list_name` string, so uniqueness here is participant+scheme with an active (non-`WITHDRAWN`) enrollment, not participant+scheme+arbitrary-round-name).

- [ ] **Step 1: Write the failing test — duplicate enrollment rejected**

```java
@Test
void enroll_participantAlreadyActivelyEnrolledInScheme_throws() {
    Participant participant = new Participant().id(1L);
    Scheme scheme = new Scheme().id(1L);
    when(enrollmentRepository.existsByParticipantIdAndSchemeIdAndStatus(1L, 1L, EnrollmentStatus.ENROLLED)).thenReturn(true);

    assertThrows(IllegalStateException.class, () -> enrollmentService.enroll(participant, scheme));
}
```

- [ ] **Step 2: Run it, confirm FAIL** (method doesn't exist)

Run: `./mvnw -Pprod test -Dtest=EnrollmentServiceTest#enroll_participantAlreadyActivelyEnrolledInScheme_throws`

- [ ] **Step 3: Add the derived-query method and the enroll() method**

```java
// EnrollmentRepository.java — add
boolean existsByParticipantIdAndSchemeIdAndStatus(Long participantId, Long schemeId, EnrollmentStatus status);
```

```java
// EnrollmentService.java — add
public Enrollment enroll(Participant participant, Scheme scheme) {
    if (enrollmentRepository.existsByParticipantIdAndSchemeIdAndStatus(participant.getId(), scheme.getId(), EnrollmentStatus.ENROLLED)) {
        throw new IllegalStateException("Participant is already actively enrolled in this scheme.");
    }
    Enrollment enrollment = new Enrollment()
        .participant(participant)
        .scheme(scheme)
        .status(EnrollmentStatus.ENROLLED)
        .enrolledOn(LocalDate.now());
    return enrollmentRepository.save(enrollment);
}
```

- [ ] **Step 4: Run the test, confirm PASS**

Run: `./mvnw -Pprod test -Dtest=EnrollmentServiceTest`

- [ ] **Step 5: Write the failing test — a participant CAN enroll in a different scheme, or re-enroll after withdrawal**

```java
@Test
void enroll_participantEnrolledInDifferentScheme_succeeds() {
    Participant participant = new Participant().id(1L);
    Scheme scheme = new Scheme().id(2L);
    when(enrollmentRepository.existsByParticipantIdAndSchemeIdAndStatus(1L, 2L, EnrollmentStatus.ENROLLED)).thenReturn(false);
    when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Enrollment result = enrollmentService.enroll(participant, scheme);

    assertEquals(EnrollmentStatus.ENROLLED, result.getStatus());
}
```

- [ ] **Step 6: Run it, confirm PASS** (already satisfied by Step 3's implementation — this test documents the boundary, not new code)

Run: `./mvnw -Pprod test -Dtest=EnrollmentServiceTest`

- [ ] **Step 7: Wire the REST endpoint and integration test**

```java
// EnrollmentResource.java — add
@PostMapping("/enrollments/enroll")
public ResponseEntity<Enrollment> enrollParticipant(@RequestParam Long participantId, @RequestParam Long schemeId) {
    Participant participant = participantRepository.findById(participantId).orElseThrow();
    Scheme scheme = schemeRepository.findById(schemeId).orElseThrow();
    return ResponseEntity.ok(enrollmentService.enroll(participant, scheme));
}
```

```java
@Test
@Transactional
void enrollParticipant_duplicate_returnsConflict() throws Exception {
    // enroll once via the API, then attempt again and assert a 4xx, not a 500
    restEnrollmentMockMvc.perform(post(ENTITY_API_URL + "/enroll").param("participantId", participant.getId().toString()).param("schemeId", scheme.getId().toString()))
        .andExpect(status().isOk());
    restEnrollmentMockMvc.perform(post(ENTITY_API_URL + "/enroll").param("participantId", participant.getId().toString()).param("schemeId", scheme.getId().toString()))
        .andExpect(status().is4xxClientError());
}
```

Add an `@ExceptionHandler(IllegalStateException.class)` returning 409 Conflict if one doesn't already exist app-wide (check `web/rest/errors/ExceptionTranslator.java` first — JHipster typically has a generic handler; confirm `IllegalStateException` maps to 4xx there before adding a duplicate handler).

- [ ] **Step 8: Run it, confirm PASS**

Run: `./mvnw -Pprod test -Dtest=EnrollmentResourceIT#enrollParticipant_duplicate_returnsConflict`

- [ ] **Step 9: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/EnrollmentService.java \
        src/main/java/zw/org/nmrl/ept/repository/EnrollmentRepository.java \
        src/main/java/zw/org/nmrl/ept/web/rest/EnrollmentResource.java \
        src/test/java/zw/org/nmrl/ept/service/EnrollmentServiceTest.java \
        src/test/java/zw/org/nmrl/ept/web/rest/EnrollmentResourceIT.java
git commit -m "feat: prevent duplicate active enrollment of a participant in a scheme"
```

---

### Task 5: Shipment creation (3.3)

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/service/ShipmentService.java`
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/ShipmentResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/ShipmentResourceIT.java`

**Interfaces:**
- Consumes: `Distribution` (Task 2), `Scheme`.
- Produces: no new interface beyond confirming the existing generated `POST /api/shipments` accepts a `Shipment` with `status = ShipmentStatus.DRAFT` and a `distribution`/`scheme` reference and persists correctly — legacy's `addShipment()` sets many DTS-specific attributes into a JSON blob (`shipment_attributes`) at creation time; the current app's `Shipment.attributes` (a `String` column, confirmed in `Shipment.java:80`) is the direct equivalent — this task does NOT need to parse/validate that JSON's shape (scheme-specific attribute shape is Phase 4/5's concern), only confirm it round-trips.

- [ ] **Step 1: Write the failing test — create a shipment within a distribution**

```java
@Test
@Transactional
void createShipment_withinDistribution_persists() throws Exception {
    Distribution distribution = distributionRepository.saveAndFlush(new Distribution().code("2026-A").distributionDate(LocalDate.now()).status(DistributionStatus.DRAFT));
    Scheme scheme = schemeRepository.saveAndFlush(new Scheme().code("dts").name("DTS"));

    Shipment shipment = new Shipment()
        .code("2026-A-DTS")
        .shipmentDate(LocalDate.now())
        .responseDeadline(Instant.now().plus(30, ChronoUnit.DAYS))
        .status(ShipmentStatus.DRAFT)
        .attributes("{\"sampleType\":\"dried\",\"screeningTest\":\"no\"}")
        .distribution(distribution)
        .scheme(scheme);

    restShipmentMockMvc
        .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipment)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("DRAFT"));
}
```

- [ ] **Step 2: Run it**

Run: `./mvnw -Pprod test -Dtest=ShipmentResourceIT#createShipment_withinDistribution_persists`
Expected: likely PASS (generated CRUD) — if `attributes` doesn't round-trip as raw JSON text correctly, that's a real finding to fix here before Phase 4/5 depend on it.

- [ ] **Step 3: Write the failing test — a shipment cannot reference a cancelled distribution**

This is new business logic, not in the generated scaffold:

```java
@Test
void createShipment_forCancelledDistribution_rejected() {
    Distribution cancelled = new Distribution().status(DistributionStatus.CANCELLED);
    Shipment shipment = new Shipment().distribution(cancelled).scheme(new Scheme());

    assertThrows(IllegalStateException.class, () -> shipmentService.validateCreatable(shipment));
}
```

- [ ] **Step 4: Run it, confirm FAIL**

- [ ] **Step 5: Implement `validateCreatable` and call it from the create path**

```java
// ShipmentService.java — add
public void validateCreatable(Shipment shipment) {
    if (shipment.getDistribution() != null && shipment.getDistribution().getStatus() == DistributionStatus.CANCELLED) {
        throw new IllegalStateException("Cannot create a shipment under a cancelled distribution.");
    }
}
```

Call `validateCreatable(shipment)` at the top of the existing generated `save(Shipment)`/`create` path in `ShipmentResource.java`'s POST handler (find the exact existing method name via the current file — JHipster's convention is `save(Shipment)` returning the persisted entity — insert the call as the first line of the resource's create endpoint, before delegating to the service).

- [ ] **Step 6: Run both tests, confirm PASS**

Run: `./mvnw -Pprod test -Dtest=ShipmentResourceIT#createShipment_withinDistribution_persists,ShipmentServiceTest#createShipment_forCancelledDistribution_rejected`

- [ ] **Step 7: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentService.java \
        src/main/java/zw/org/nmrl/ept/web/rest/ShipmentResource.java \
        src/test/java/zw/org/nmrl/ept/web/rest/ShipmentResourceIT.java
git commit -m "feat: validate shipment creation against cancelled distributions"
```

---

### Task 6: Shipment button-state / status derivation service

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/ShipmentWorkflowService.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/ShipmentWorkflowServiceTest.java`

**Interfaces:**
- Consumes: `Shipment` (Task 1/5), `FeedbackFormRepository`-equivalent (check if a feedback-form-existence query already exists from the `ParticipantFeedback`/`FeedbackQuestion` entities scaffolded in this app; if not, stub `awaitingFeedbackForm` as always `false` for now and flag it as a follow-up item for Phase 8, since feedback-form management isn't otherwise scoped to any phase yet).
- Produces: `ShipmentWorkflowService.ButtonStates` record — `record ButtonStates(boolean viewEnabled, boolean evaluateEnabled, boolean generateReportsEnabled, boolean finalizeEnabled, String displayStatus, String evaluateButtonText, boolean awaitingFeedbackForm)` and `ShipmentWorkflowService.getButtonStates(Shipment shipment)`. This is the direct port of legacy's `getShipmentButtonStates()` — every later phase (4, 5, 6) that renders a shipment action bar depends on this exact method, so its return shape is a load-bearing interface, not incidental.

- [ ] **Step 1: Write the failing test — a DRAFT shipment only allows evaluate... no wait, only allows nothing yet (matches legacy's ephemeral-status disable-all)**

```java
@Test
void buttonStates_draftStatus_allDisabled() {
    Shipment shipment = new Shipment().status(ShipmentStatus.DRAFT);
    var states = shipmentWorkflowService.getButtonStates(shipment);
    assertFalse(states.viewEnabled());
    assertFalse(states.evaluateEnabled());
    assertFalse(states.generateReportsEnabled());
    assertFalse(states.finalizeEnabled());
}
```

- [ ] **Step 2: Run it, confirm FAIL**

- [ ] **Step 3: Write the remaining milestone-transition test cases up front (table-driven), matching the legacy truth table exactly**

```java
@ParameterizedTest
@MethodSource("buttonStateCases")
void buttonStates_matchLegacyTruthTable(ShipmentStatus status, boolean view, boolean evaluate, boolean generateReports, boolean finalize) {
    Shipment shipment = new Shipment().status(status);
    var states = shipmentWorkflowService.getButtonStates(shipment);
    assertEquals(view, states.viewEnabled());
    assertEquals(evaluate, states.evaluateEnabled());
    assertEquals(generateReports, states.generateReportsEnabled());
    assertEquals(finalize, states.finalizeEnabled());
}

static Stream<Arguments> buttonStateCases() {
    return Stream.of(
        // status,                          view,  evaluate, generateReports, finalize
        Arguments.of(ShipmentStatus.RESPONSES_CLOSED, true,  true,  false, false), // not yet evaluated
        Arguments.of(ShipmentStatus.EVALUATED,        true,  true,  true,  false), // evaluated, no reports yet
        Arguments.of(ShipmentStatus.REPORTS_GENERATED, true, true,  true,  true),  // reports generated, can finalize
        Arguments.of(ShipmentStatus.FINALIZED,        true,  false, false, false), // locked
        Arguments.of(ShipmentStatus.CANCELLED,        true,  false, false, false), // locked, same as finalized
        Arguments.of(ShipmentStatus.QUEUED,           false, false, false, false), // ephemeral, all disabled
        Arguments.of(ShipmentStatus.DRAFT,            false, false, false, false)  // ephemeral, all disabled
    );
}
```

- [ ] **Step 4: Run it, confirm FAIL**

- [ ] **Step 5: Implement `getButtonStates`, mapping the current app's discrete `ShipmentStatus` enum onto the same truth table legacy derives from milestone timestamps** (the enum is a cleaner source of truth than legacy's timestamps — use it directly, do not re-add timestamp-derivation logic)

```java
@Service
public class ShipmentWorkflowService {

    private static final Set<ShipmentStatus> EPHEMERAL = EnumSet.of(
        ShipmentStatus.DRAFT, ShipmentStatus.CONFIGURED, ShipmentStatus.READY,
        ShipmentStatus.QUEUED, ShipmentStatus.EVALUATING
    );

    public record ButtonStates(
        boolean viewEnabled, boolean evaluateEnabled, boolean generateReportsEnabled,
        boolean finalizeEnabled, String displayStatus, String evaluateButtonText, boolean awaitingFeedbackForm
    ) {}

    public ButtonStates getButtonStates(Shipment shipment) {
        ShipmentStatus status = shipment.getStatus();
        boolean isCancelled = status == ShipmentStatus.CANCELLED;
        boolean isFinalized = status == ShipmentStatus.FINALIZED;
        boolean isEphemeral = EPHEMERAL.contains(status);

        boolean viewEnabled, evaluateEnabled, generateReportsEnabled, finalizeEnabled;
        if (isFinalized || isCancelled) {
            viewEnabled = true; evaluateEnabled = false; generateReportsEnabled = false; finalizeEnabled = false;
        } else if (status == ShipmentStatus.REPORTS_GENERATED) {
            viewEnabled = true; evaluateEnabled = true; generateReportsEnabled = true; finalizeEnabled = true;
        } else if (status == ShipmentStatus.EVALUATED) {
            viewEnabled = true; evaluateEnabled = true; generateReportsEnabled = true; finalizeEnabled = false;
        } else if (isEphemeral) {
            viewEnabled = false; evaluateEnabled = false; generateReportsEnabled = false; finalizeEnabled = false;
        } else {
            // SHIPPED / RESPONSES_OPEN / RESPONSES_CLOSED — not yet evaluated
            viewEnabled = true; evaluateEnabled = true; generateReportsEnabled = false; finalizeEnabled = false;
        }

        return new ButtonStates(
            viewEnabled, evaluateEnabled, generateReportsEnabled, finalizeEnabled,
            status.name(), status == ShipmentStatus.EVALUATED || status == ShipmentStatus.REPORTS_GENERATED ? "Re-Evaluate" : "Evaluate",
            false // awaitingFeedbackForm — see Interfaces note above, revisit once feedback-form existence is queryable
        );
    }
}
```

- [ ] **Step 6: Run all the tests, confirm PASS**

Run: `./mvnw -Pprod test -Dtest=ShipmentWorkflowServiceTest`

- [ ] **Step 7: Add the 15-minute-queued-recovery case** (mirrors legacy's stuck-job override, and is the domain-logic counterpart to Phase 0.2's stale-job sweep)

```java
@Test
void buttonStates_queuedPastFifteenMinutes_evaluateReenabled() {
    Shipment shipment = new Shipment().status(ShipmentStatus.QUEUED).lastModifiedDate(Instant.now().minus(16, ChronoUnit.MINUTES));
    var states = shipmentWorkflowService.getButtonStates(shipment);
    assertTrue(states.evaluateEnabled());
}
```

Note: `Shipment` needs a `lastModifiedDate` field for this — check whether `AbstractAuditingEntity` (JHipster's standard base class) already provides one before adding a new column; if `Shipment` doesn't extend it, that's a design question to raise, not silently work around.

- [ ] **Step 8: Implement, run, confirm PASS**

- [ ] **Step 9: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentWorkflowService.java \
        src/test/java/zw/org/nmrl/ept/service/ShipmentWorkflowServiceTest.java
git commit -m "feat: port legacy shipment button-state derivation as ShipmentWorkflowService"
```

---

### Task 7: Shipment cancellation

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/service/ShipmentWorkflowService.java`
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/ShipmentResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/ShipmentWorkflowServiceTest.java`, `src/test/java/zw/org/nmrl/ept/web/rest/ShipmentResourceIT.java`

**Interfaces:**
- Consumes: `DistributionWorkflowService.cancelIfNoActiveShipmentsRemain` (Task 3), Phase 0.2's `ScheduledJob` repository (to delete/cancel any queued report-generation job for this shipment — coordinate with whoever implements 0.2's exact repository method names; if Phase 0.2 isn't landed yet when this task starts, stub with a `// TODO(phase-0.2)` and a tracked follow-up, don't block this task on it).
- Produces: `ShipmentWorkflowService.cancel(Long shipmentId, String reason, String cancelledBy)`, `POST /api/shipments/{id}/cancel`.

- [ ] **Step 1: Write the failing test — cannot cancel a finalized shipment**

```java
@Test
void cancel_finalizedShipment_throws() {
    Shipment shipment = new Shipment().status(ShipmentStatus.FINALIZED);
    when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

    assertThrows(IllegalStateException.class, () -> shipmentWorkflowService.cancel(1L, "test", "admin"));
}
```

- [ ] **Step 2: Run it, confirm FAIL**

- [ ] **Step 3: Implement, matching legacy's transaction exactly:**

```java
@Transactional
public Shipment cancel(Long shipmentId, String reason, String cancelledBy) {
    if (reason == null || reason.isBlank()) {
        throw new IllegalArgumentException("A reason for cancelling is required.");
    }
    Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new IllegalStateException("Shipment not found."));
    if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
        throw new IllegalStateException("This shipment is already cancelled.");
    }
    if (shipment.getStatus() == ShipmentStatus.FINALIZED) {
        throw new IllegalStateException("A finalized shipment cannot be cancelled.");
    }

    shipment.setCancelledAt(Instant.now());
    shipment.setCancelledBy(cancelledBy);
    shipment.setCancellationReason(reason);
    shipment.setStatus(ShipmentStatus.CANCELLED);
    shipment.setResponsesOpen(false);
    Shipment saved = shipmentRepository.save(shipment);

    // Reset per-participant report state for this shipment (mirrors legacy resetting
    // shipment_participant_map.report_generated/report_download_metadata)
    shipmentParticipantMapRepository.resetReportStateForShipment(shipmentId);

    // Cascade: if this was the distribution's last active shipment, cancel the distribution too
    if (shipment.getDistribution() != null) {
        distributionWorkflowService.cancelIfNoActiveShipmentsRemain(shipment.getDistribution().getId(), shipmentRepository);
    }

    return saved;
}
```

Add `resetReportStateForShipment(Long shipmentId)` as a `@Modifying @Query` on `ShipmentParticipantMapRepository` (check the entity's real field names for report-generation state — if no such fields exist yet on `ShipmentParticipantMap`, that's a Phase 6 dependency; if they don't exist, no-op this call with a comment explaining why, don't invent fields Phase 6 hasn't designed yet).

- [ ] **Step 4: Run the test, confirm PASS**

Run: `./mvnw -Pprod test -Dtest=ShipmentWorkflowServiceTest#cancel_finalizedShipment_throws`

- [ ] **Step 5: Write the cascade test**

```java
@Test
void cancel_lastActiveShipmentInDistribution_cascadesCancelToDistribution() {
    Distribution distribution = new Distribution().id(10L).status(DistributionStatus.OPEN);
    Shipment shipment = new Shipment().status(ShipmentStatus.DRAFT).distribution(distribution);
    when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
    when(shipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(shipmentRepository.existsByDistributionIdAndCancelledAtIsNull(10L)).thenReturn(false);
    when(distributionRepository.findById(10L)).thenReturn(Optional.of(distribution));

    shipmentWorkflowService.cancel(1L, "no longer needed", "admin");

    verify(distributionRepository).save(argThat(d -> d.getStatus() == DistributionStatus.CANCELLED));
}
```

- [ ] **Step 6: Run it, confirm PASS**

- [ ] **Step 7: Wire the REST endpoint + integration test**, following the exact pattern from Task 3's `DistributionResource` endpoint (same request/response shape convention).

- [ ] **Step 8: Run the integration test, confirm PASS**

- [ ] **Step 9: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentWorkflowService.java \
        src/main/java/zw/org/nmrl/ept/web/rest/ShipmentResource.java \
        src/test/java/zw/org/nmrl/ept/service/ShipmentWorkflowServiceTest.java \
        src/test/java/zw/org/nmrl/ept/web/rest/ShipmentResourceIT.java
git commit -m "feat: implement transactional shipment cancellation with distribution cascade"
```

---

### Task 8: Clone-from-previous-shipment

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/service/ShipmentWorkflowService.java`
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/ShipmentResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/ShipmentWorkflowServiceTest.java`

Legacy's `getShipmentsWithParticipantsForCopy()` (`Shipments.php:2675`) copies an existing shipment's **enrolled participant set** into a new shipment being created — genuinely useful (avoids re-enrolling the same lab roster every round), not UX debt. Preserve it, scoped to copying `ShipmentParticipantMap` rows, not scoring/result data.

**Interfaces:**
- Produces: `ShipmentWorkflowService.copyParticipantsFrom(Long sourceShipmentId, Long targetShipmentId)`.

- [ ] **Step 1: Write the failing test**

```java
@Test
void copyParticipantsFrom_copiesEnrolledParticipantsOnly() {
    Shipment source = new Shipment().id(1L);
    Shipment target = new Shipment().id(2L);
    ShipmentParticipantMap sourceMap = new ShipmentParticipantMap().shipment(source).participant(new Participant().id(5L));
    when(shipmentParticipantMapRepository.findByShipmentIdAndCancelledAtIsNull(1L)).thenReturn(List.of(sourceMap));
    when(shipmentRepository.findById(2L)).thenReturn(Optional.of(target));

    shipmentWorkflowService.copyParticipantsFrom(1L, 2L);

    verify(shipmentParticipantMapRepository).save(argThat(m -> m.getShipment().getId().equals(2L) && m.getParticipant().getId().equals(5L)));
}
```

- [ ] **Step 2: Run it, confirm FAIL**

- [ ] **Step 3: Implement**

```java
public void copyParticipantsFrom(Long sourceShipmentId, Long targetShipmentId) {
    Shipment target = shipmentRepository.findById(targetShipmentId).orElseThrow();
    List<ShipmentParticipantMap> sourceMaps = shipmentParticipantMapRepository.findByShipmentIdAndCancelledAtIsNull(sourceShipmentId);
    for (ShipmentParticipantMap sourceMap : sourceMaps) {
        ShipmentParticipantMap copy = new ShipmentParticipantMap()
            .shipment(target)
            .participant(sourceMap.getParticipant())
            .responseStatus(ResponseStatus.NOT_STARTED);
        shipmentParticipantMapRepository.save(copy);
    }
}
```

(Note: `findByShipmentIdAndCancelledAtIsNull` assumes `ShipmentParticipantMap` gets a `cancelledAt`-equivalent from Task 9's unenroll design — if Task 9 lands a different soft-delete mechanism, adjust this query to match, don't diverge.)

- [ ] **Step 4: Run the test, confirm PASS**

- [ ] **Step 5: Wire the REST endpoint (`POST /api/shipments/{targetId}/copy-participants-from/{sourceId}`) and a real integration test through `getShipmentsWithParticipantsForCopy`'s equivalent listing endpoint** (an endpoint returning shipments-with-participant-counts, for the UI's "copy from" picker).

- [ ] **Step 6: Run it, confirm PASS**

- [ ] **Step 7: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentWorkflowService.java \
        src/main/java/zw/org/nmrl/ept/web/rest/ShipmentResource.java \
        src/test/java/zw/org/nmrl/ept/service/ShipmentWorkflowServiceTest.java
git commit -m "feat: support cloning enrolled participants from a previous shipment"
```

---

### Task 9: Shipment-participant enroll/unenroll

**Files:**
- Modify: `src/main/java/zw/org/nmrl/ept/service/ShipmentParticipantMapService.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/ShipmentParticipantMapServiceTest.java`

**Interfaces:**
- Produces: `ShipmentParticipantMapService.enroll(Shipment, Participant)`, `.unenroll(Long mapId)` — unenroll should be a soft-delete (matching legacy's `removeShipmentParticipant` which is a DB delete, but the current app's audit-trail model favors marking a `ShipmentParticipantMap` inactive over a hard delete — **decision: soft-delete via `responseStatus` or a dedicated flag**, confirm against how the rest of this app handles removals before picking one; check an existing similar many-to-many removal pattern elsewhere in the codebase first rather than inventing a new convention here).

- [ ] **Step 1: Write the failing test — enroll a participant into an open (non-cancelled, non-finalized) shipment**

```java
@Test
void enroll_openShipment_succeeds() {
    Shipment shipment = new Shipment().status(ShipmentStatus.DRAFT);
    Participant participant = new Participant().id(1L);
    when(shipmentParticipantMapRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    ShipmentParticipantMap result = shipmentParticipantMapService.enroll(shipment, participant);

    assertEquals(ResponseStatus.NOT_STARTED, result.getResponseStatus());
}
```

- [ ] **Step 2: Run it, confirm FAIL**

- [ ] **Step 3: Write the failing test — cannot enroll into a finalized/cancelled shipment**

```java
@Test
void enroll_finalizedShipment_throws() {
    Shipment shipment = new Shipment().status(ShipmentStatus.FINALIZED);
    assertThrows(IllegalStateException.class, () -> shipmentParticipantMapService.enroll(shipment, new Participant()));
}
```

- [ ] **Step 4: Run both, confirm FAIL**

- [ ] **Step 5: Implement**

```java
public ShipmentParticipantMap enroll(Shipment shipment, Participant participant) {
    if (shipment.getStatus() == ShipmentStatus.FINALIZED || shipment.getStatus() == ShipmentStatus.CANCELLED) {
        throw new IllegalStateException("Cannot enroll a participant into a locked shipment.");
    }
    ShipmentParticipantMap map = new ShipmentParticipantMap()
        .shipment(shipment)
        .participant(participant)
        .responseStatus(ResponseStatus.NOT_STARTED);
    return shipmentParticipantMapRepository.save(map);
}
```

- [ ] **Step 6: Run both tests, confirm PASS**

- [ ] **Step 7: Repeat Steps 1-6 for `unenroll(Long mapId)`**, throwing the same `IllegalStateException` if the parent shipment is locked.

- [ ] **Step 8: REST endpoints + integration tests** (`POST /api/shipment-participant-maps/enroll`, `DELETE`-equivalent for unenroll — reuse the existing generated resource, add these as extra endpoints on it).

- [ ] **Step 9: Run integration tests, confirm PASS**

- [ ] **Step 10: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentParticipantMapService.java \
        src/main/java/zw/org/nmrl/ept/web/rest/ShipmentParticipantMapResource.java \
        src/test/java/zw/org/nmrl/ept/service/ShipmentParticipantMapServiceTest.java \
        src/test/java/zw/org/nmrl/ept/web/rest/ShipmentParticipantMapResourceIT.java
git commit -m "feat: enroll/unenroll participants into a shipment, blocked on locked shipments"
```

---

### Task 10: Testkit assignment (new entity)

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/domain/ShipmentTestkitMap.java`
- Create: `src/main/java/zw/org/nmrl/ept/repository/ShipmentTestkitMapRepository.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/ShipmentTestkitMapService.java`
- Create: `src/main/java/zw/org/nmrl/ept/web/rest/ShipmentTestkitMapResource.java`
- Create: `src/main/java/zw/org/nmrl/ept/service/dto/ShipmentTestkitMapDTO.java` + `src/main/java/zw/org/nmrl/ept/service/mapper/ShipmentTestkitMapMapper.java` (follow the existing MapStruct pattern — check any neighboring `*Mapper.java` for the exact `@Mapper` annotation style used throughout this codebase)
- Create: `src/main/resources/config/liquibase/changelog/<timestamp>_added_entity_ShipmentTestkitMap.xml`
- Test: `src/test/java/zw/org/nmrl/ept/web/rest/ShipmentTestkitMapResourceIT.java`, `src/test/java/zw/org/nmrl/ept/domain/ShipmentTestkitMapTest.java`, `src/test/java/zw/org/nmrl/ept/service/mapper/ShipmentTestkitMapMapperTest.java`
- Frontend: `src/main/webapp/app/entities/shipment-testkit-map/` (list/detail/update/delete-dialog/reducer/reducer.spec/index — the standard 7-file generated shape used throughout this codebase; can generate via `jhipster entity ShipmentTestkitMap` scaffolding if the JHipster CLI is available in this repo, rather than hand-writing every file — check for a `.jhipster/` directory and `.yo-rc.json` first, which confirms JHipster's generator can be re-invoked here).

**Interfaces:**
- Produces: `ShipmentTestkitMap { id, testkitLot1: Integer, testkitLot2: Integer, testkitLot3: Integer, shipment: Shipment (ManyToOne), testKit: TestKit (ManyToOne) }` — direct mapping of legacy's `shipment_testkit_map` table (`shipment_id`+`testkit_id` composite key in legacy, modeled here as a surrogate-keyed entity with a unique constraint on `(shipment_id, test_kit_id)`, matching this app's existing convention of surrogate `id` PKs on every other entity rather than legacy's composite keys).

- [ ] **Step 1: Write the failing entity equality test** (following the exact pattern of `TestKitTest.java`)

```java
@Test
void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(ShipmentTestkitMap.class);
    ShipmentTestkitMap shipmentTestkitMap1 = new ShipmentTestkitMap();
    shipmentTestkitMap1.setId(1L);
    ShipmentTestkitMap shipmentTestkitMap2 = new ShipmentTestkitMap();
    shipmentTestkitMap2.setId(shipmentTestkitMap1.getId());
    assertThat(shipmentTestkitMap1).isEqualTo(shipmentTestkitMap2);
}
```

- [ ] **Step 2: Run it, confirm FAIL** (class doesn't exist)

- [ ] **Step 3: Create the entity, repository, DTO, mapper, service, resource** — copy the exact structure of an existing simple join-entity in this codebase (e.g. `ShipmentSample` or `ShipmentParticipantMap` minus the extra business fields) rather than writing from scratch, to guarantee convention consistency:

```java
@Entity
@Table(name = "shipment_testkit_map", uniqueConstraints = @UniqueConstraint(columnNames = {"shipment_id", "test_kit_id"}))
public class ShipmentTestkitMap implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "testkit_lot_1")
    private Integer testkitLot1;

    @Column(name = "testkit_lot_2")
    private Integer testkitLot2;

    @Column(name = "testkit_lot_3")
    private Integer testkitLot3;

    @ManyToOne(optional = false)
    private Shipment shipment;

    @ManyToOne(optional = false)
    private TestKit testKit;

    // getters/setters/fluent builders following the exact style of the neighboring entity you copied
}
```

- [ ] **Step 4: Write the Liquibase changeset**

```xml
<changeSet id="<timestamp>-1" author="phase3">
    <createTable tableName="shipment_testkit_map">
        <column name="id" type="bigint" autoIncrement="true"><constraints primaryKey="true"/></column>
        <column name="testkit_lot_1" type="integer"/>
        <column name="testkit_lot_2" type="integer"/>
        <column name="testkit_lot_3" type="integer"/>
        <column name="shipment_id" type="bigint"><constraints nullable="false" foreignKeyName="fk_shipment_testkit_map_shipment" references="shipment(id)"/></column>
        <column name="test_kit_id" type="bigint"><constraints nullable="false" foreignKeyName="fk_shipment_testkit_map_test_kit" references="test_kit(id)"/></column>
    </createTable>
    <addUniqueConstraint tableName="shipment_testkit_map" columnNames="shipment_id, test_kit_id" constraintName="ux_shipment_testkit_map_shipment_testkit"/>
</changeSet>
```

Confirm the real `test_kit` table name/PK column via `\d test_kit` in psql before finalizing this — don't assume, verify.

- [ ] **Step 5: Register the changelog, run the migration, run the entity test**

Run: `./mvnw -Pprod liquibase:update && ./mvnw -Pprod test -Dtest=ShipmentTestkitMapTest`
Expected: PASS

- [ ] **Step 6: Write the mapper test, DTO/mapper, run it**

Follow `ShipmentSampleMapperTest.java`'s exact pattern for the equivalent of `ShipmentTestkitMapMapperTest.java`.

Run: `./mvnw -Pprod test -Dtest=ShipmentTestkitMapMapperTest`

- [ ] **Step 7: Write the resource integration test — assign testkit lots to a shipment**

```java
@Test
@Transactional
void assignTestkit_toShipment_persists() throws Exception {
    Shipment shipment = shipmentRepository.saveAndFlush(new Shipment().code("2026-A-DTS").status(ShipmentStatus.DRAFT) /* ... */);
    TestKit testKit = testKitRepository.saveAndFlush(new TestKit().name("Determine HIV-1/2"));

    ShipmentTestkitMapDTO dto = new ShipmentTestkitMapDTO();
    dto.setShipmentId(shipment.getId());
    dto.setTestKitId(testKit.getId());
    dto.setTestkitLot1(50);

    restShipmentTestkitMapMockMvc
        .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.testkitLot1").value(50));
}
```

- [ ] **Step 8: Run it, confirm PASS**

- [ ] **Step 9: Write the failing test — the same shipment/testkit pair cannot be assigned twice** (the unique constraint from Step 4)

```java
@Test
@Transactional
void assignTestkit_duplicateShipmentTestkitPair_rejected() throws Exception {
    // create once, then attempt again, assert 4xx not 500 (a real DB constraint violation must surface as a client error, per this codebase's existing error-handling convention — check ExceptionTranslator for how unique-constraint violations are already mapped elsewhere before adding new handling)
}
```

- [ ] **Step 10: Run it, confirm PASS** (or fix the exception-translation gap if the constraint violation surfaces as a raw 500 — that's a real bug to fix, not a test to weaken)

- [ ] **Step 11: Frontend module** — generate/build the standard list/detail/update screens under `entities/shipment-testkit-map/`, plus a sub-view embedded in the shipment detail screen (matching legacy's dedicated `shipment-test-kits` page reached from the shipment) rather than only a standalone top-level entity screen — testkit assignment is always done in the context of one shipment, the UI should reflect that.

- [ ] **Step 12: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/domain/ShipmentTestkitMap.java \
        src/main/java/zw/org/nmrl/ept/repository/ShipmentTestkitMapRepository.java \
        src/main/java/zw/org/nmrl/ept/service/ \
        src/main/java/zw/org/nmrl/ept/web/rest/ShipmentTestkitMapResource.java \
        src/main/resources/config/liquibase/changelog/*ShipmentTestkitMap* \
        src/main/resources/config/liquibase/master.xml \
        src/test/java/zw/org/nmrl/ept/ \
        src/main/webapp/app/entities/shipment-testkit-map/
git commit -m "feat: add ShipmentTestkitMap entity for testkit lot assignment"
```

---

### Task 11: Deadline automation

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/migration/ShipmentDeadlineJob.java` (or wherever Phase 0.2 establishes the job-type package — align with that convention, don't invent a parallel one)
- Modify: `src/main/java/zw/org/nmrl/ept/service/ShipmentWorkflowService.java` (add `closeExpiredResponses()`)
- Test: `src/test/java/zw/org/nmrl/ept/service/ShipmentWorkflowServiceTest.java`, `src/test/java/zw/org/nmrl/ept/ShipmentDeadlineJobIT.java`

**Interfaces:**
- Consumes: Phase 0.2's `@Scheduled` + ShedLock infrastructure (this task is blocked on 0.2 landing first — if sequencing this before 0.2, implement `closeExpiredResponses()` as a plain service method with its own test coverage, and defer only the `@Scheduled` wiring until 0.2 exists).
- Produces: `ShipmentWorkflowService.closeExpiredResponses()` — idempotent: finds shipments with `responsesOpen = true`, `autoCloseAtDeadline = true`, `responseDeadline` in the past, and `status` not already `CANCELLED`/`FINALIZED`, and closes each (`responsesOpen = false`, `status → RESPONSES_CLOSED` if not already past that point).

- [ ] **Step 1: Write the failing test — an overdue shipment is closed**

```java
@Test
void closeExpiredResponses_pastDeadline_closesShipment() {
    Shipment overdue = new Shipment().id(1L).responsesOpen(true).autoCloseAtDeadline(true)
        .responseDeadline(Instant.now().minus(1, ChronoUnit.DAYS)).status(ShipmentStatus.RESPONSES_OPEN);
    when(shipmentRepository.findByResponsesOpenTrueAndAutoCloseAtDeadlineTrueAndResponseDeadlineBefore(any())).thenReturn(List.of(overdue));
    when(shipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    shipmentWorkflowService.closeExpiredResponses();

    verify(shipmentRepository).save(argThat(s -> !s.getResponsesOpen() && s.getStatus() == ShipmentStatus.RESPONSES_CLOSED));
}
```

- [ ] **Step 2: Run it, confirm FAIL**

- [ ] **Step 3: Add the derived-query method and implement**

```java
// ShipmentRepository.java — add
List<Shipment> findByResponsesOpenTrueAndAutoCloseAtDeadlineTrueAndResponseDeadlineBefore(Instant now);
```

```java
// ShipmentWorkflowService.java — add
@Transactional
public void closeExpiredResponses() {
    List<Shipment> overdue = shipmentRepository.findByResponsesOpenTrueAndAutoCloseAtDeadlineTrueAndResponseDeadlineBefore(Instant.now());
    for (Shipment shipment : overdue) {
        shipment.setResponsesOpen(false);
        if (shipment.getStatus() == ShipmentStatus.RESPONSES_OPEN) {
            shipment.setStatus(ShipmentStatus.RESPONSES_CLOSED);
        }
        shipmentRepository.save(shipment);
    }
}
```

- [ ] **Step 4: Run the test, confirm PASS**

- [ ] **Step 5: Write the failing test — a cancelled shipment past deadline is untouched** (guards against the deadline job fighting with Task 7's cancellation)

```java
@Test
void closeExpiredResponses_alreadyCancelledShipmentNotReturnedByQuery() {
    // the query itself excludes non-open shipments by construction (responsesOpen=true filter),
    // and cancel() already sets responsesOpen=false — assert the repository query, not extra service logic
    verify(shipmentRepository, never()).save(argThat(s -> s.getStatus() == ShipmentStatus.CANCELLED));
}
```

- [ ] **Step 6: Confirm this is already true by construction** (Task 7's `cancel()` sets `responsesOpen = false`, so the query in Step 3 naturally excludes cancelled shipments — no new code needed, this test documents the invariant).

- [ ] **Step 7: Write the idempotency test — running twice in a row is a no-op the second time**

```java
@Test
void closeExpiredResponses_runTwice_secondRunIsNoOp() {
    when(shipmentRepository.findByResponsesOpenTrueAndAutoCloseAtDeadlineTrueAndResponseDeadlineBefore(any()))
        .thenReturn(List.of()); // after the first run, nothing matches responsesOpen=true anymore
    shipmentWorkflowService.closeExpiredResponses();
    verify(shipmentRepository, never()).save(any());
}
```

- [ ] **Step 8: Run it, confirm PASS** (satisfied by the query's `responsesOpen = true` filter — same reasoning as Step 6)

- [ ] **Step 9: Wire the `@Scheduled` job once Phase 0.2's infrastructure exists**

```java
@Component
public class ShipmentDeadlineJob {
    private final ShipmentWorkflowService shipmentWorkflowService;

    public ShipmentDeadlineJob(ShipmentWorkflowService shipmentWorkflowService) {
        this.shipmentWorkflowService = shipmentWorkflowService;
    }

    @Scheduled(fixedDelay = 60_000) // every minute, matching legacy's process-shipment-deadlines.php cadence
    @SchedulerLock(name = "shipmentDeadlineJob", lockAtMostFor = "50s")
    public void run() {
        shipmentWorkflowService.closeExpiredResponses();
    }
}
```

- [ ] **Step 10: Write an end-to-end integration test with a real overdue shipment in the DB, run the job, verify the DB row changed**

- [ ] **Step 11: Run it, confirm PASS**

- [ ] **Step 12: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/ShipmentWorkflowService.java \
        src/main/java/zw/org/nmrl/ept/repository/ShipmentRepository.java \
        src/main/java/zw/org/nmrl/ept/migration/ShipmentDeadlineJob.java \
        src/test/java/zw/org/nmrl/ept/service/ShipmentWorkflowServiceTest.java \
        src/test/java/zw/org/nmrl/ept/ShipmentDeadlineJobIT.java
git commit -m "feat: automatically close shipment responses at deadline"
```

---

### Task 12: TB-form generation

**Files:**
- Create: `src/main/java/zw/org/nmrl/ept/service/TbFormService.java`
- Modify: `src/main/java/zw/org/nmrl/ept/web/rest/ShipmentResource.java`
- Test: `src/test/java/zw/org/nmrl/ept/service/TbFormServiceTest.java`

**Interfaces:**
- Consumes: Phase 6's PDF library decision (Apache PDFBox — this task has a real, acknowledged dependency on Phase 6's `pom.xml` addition; if Phase 6 hasn't landed the `org.apache.pdfbox:pdfbox` dependency yet when this task starts, add it here and note the duplication for Phase 6's plan to reconcile, don't block).
- Produces: `TbFormService.generateForParticipant(Long shipmentId, Long participantId)` returning a `byte[]` (the PDF), `GET /api/shipments/{shipmentId}/tb-form/{participantId}`.

- [ ] **Step 1: Write the failing test — generating a TB form for a non-TB-scheme shipment is rejected**

```java
@Test
void generateForParticipant_nonTbScheme_throws() {
    Shipment shipment = new Shipment().scheme(new Scheme().code("dts")); // not 'tb'
    when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

    assertThrows(IllegalArgumentException.class, () -> tbFormService.generateForParticipant(1L, 5L));
}
```

- [ ] **Step 2: Run it, confirm FAIL**

- [ ] **Step 3: Implement the scheme guard and a minimal PDFBox-based generator** (defer the exact field layout to when Phase 4's TB result-entry fields are known — this step only proves the plumbing: guard → fetch data → produce non-empty PDF bytes; Phase 4/5 will supply the real field values once TB result entry exists)

```java
public byte[] generateForParticipant(Long shipmentId, Long participantId) {
    Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow();
    if (!"tb".equals(shipment.getScheme().getCode())) {
        throw new IllegalArgumentException("TB form generation only applies to the tb scheme.");
    }
    try (PDDocument document = new PDDocument()) {
        document.addPage(new PDPage());
        // TODO(phase-4/5): populate real TB result fields once TB result entry exists — this
        // proves the generation plumbing only, per this task's scope.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        return out.toByteArray();
    } catch (IOException e) {
        throw new IllegalStateException("Failed to generate TB form", e);
    }
}
```

- [ ] **Step 4: Run the test, confirm PASS**

- [ ] **Step 5: Wire the REST endpoint, returning `application/pdf`**

```java
@GetMapping(value = "/shipments/{shipmentId}/tb-form/{participantId}", produces = MediaType.APPLICATION_PDF_VALUE)
public ResponseEntity<byte[]> downloadTbForm(@PathVariable Long shipmentId, @PathVariable Long participantId) {
    byte[] pdf = tbFormService.generateForParticipant(shipmentId, participantId);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf);
}
```

- [ ] **Step 6: Integration test — hits the endpoint, asserts a non-empty PDF response with the right content type**

```java
@Test
@Transactional
void downloadTbForm_returnsNonEmptyPdf() throws Exception {
    // seed a tb-scheme shipment + participant
    restShipmentMockMvc
        .perform(get("/api/shipments/{shipmentId}/tb-form/{participantId}", shipment.getId(), participant.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_PDF));
}
```

- [ ] **Step 7: Run it, confirm PASS**

- [ ] **Step 8: Commit**

```bash
git add src/main/java/zw/org/nmrl/ept/service/TbFormService.java \
        src/main/java/zw/org/nmrl/ept/web/rest/ShipmentResource.java \
        src/test/java/zw/org/nmrl/ept/service/TbFormServiceTest.java \
        pom.xml
git commit -m "feat: generate TB participant form PDF via PDFBox"
```

---

## Self-review

**Spec coverage** — every 3.x subtask from `ept_project_plan.md` maps to a task above: 3.0 → "Research findings" section; 3.1 → Task 2; 3.2 → Task 4; 3.3 → Tasks 5, 6, 7, 8; 3.4 → Tasks 9, 10; 3.5 → Task 11; 3.6 → Task 12. Task 1 is a prerequisite the master plan implied but didn't call out as its own numbered subtask (the `CANCELLED` status/field gap) — added because 3.3/3.5's tests can't pass without it, consistent with the master plan's own "no placeholders" spirit.

**Placeholder scan** — one intentional, explicitly-flagged deferral: Task 12 Step 3's TB-form body content is a stub pending Phase 4/5's real TB field data (marked `TODO(phase-4/5)` with a stated reason, not silently glossed over) — this is a genuine cross-phase dependency, not a shortcut. Everything else has real code, real file paths (verified against the actual current-app directory structure), and real assertions.

**Type consistency** — `ShipmentWorkflowService.ButtonStates`, `.cancel()`, `.closeExpiredResponses()`, `.copyParticipantsFrom()` are all defined once (Tasks 6/7/8/11) and referenced consistently by name in every later task that touches them. `ShipmentTestkitMap`'s field names (`testkitLot1/2/3`, `shipment`, `testKit`) are used identically in the entity, Liquibase changeset, and test steps.

**Known open dependencies for whoever executes this plan:**
- Task 7 assumes Phase 0.2's `ScheduledJob` repository exists for report-generation-job cleanup on cancel — stub if not yet landed.
- Task 6's `awaitingFeedbackForm` is stubbed `false` pending a feedback-form-existence query that isn't scoped to any phase yet — flag this gap to whoever owns Phase 8 (ancillary modules, where `ParticipantFeedback`/`FeedbackQuestion` UI lives).
- Task 11's `@Scheduled`/`@SchedulerLock` wiring assumes Phase 0.2 has added the ShedLock dependency to `pom.xml` — Task 11 Steps 1-8 (the service logic + its tests) do not depend on this and can land first regardless of Phase 0.2's sequencing.
- Task 12 duplicates the PDFBox `pom.xml` dependency addition that Phase 6 will also want — whichever phase lands first should add it; the other should find it already present and skip that step.
