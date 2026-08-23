# ePT Parity Project Plan

> **For agentic workers:** This is a master roadmap, not a bite-sized TDD plan. Each phase below must get its own detailed implementation plan (via `superpowers:writing-plans`, executed with `superpowers:subagent-driven-development` or `superpowers:executing-plans`) before implementation starts. This document is the thing to check off, re-read, and update as phases land — treat it as living, not archival.

**Goal:** Bring `proficiency-testing` (Java/Spring Boot 4 + React, this repo) to full functional and UI parity with the legacy PHP application at `/home/administrator/Documents/Development/ept` ("ePT" — the NMRL Zimbabwe proficiency-testing / EQA laboratory system), so it can replace the legacy system in production.

**Starting point:** The data migration pipeline (legacy MySQL → this app's PostgreSQL schema) is already built, tested end to end against a real legacy snapshot, and documented — see [Source references](#source-references) below. That work is **done and out of scope here**. This plan covers everything else: business logic, workflows, UI, background processing, and the API surface.

**Current state, in one sentence each:**

- **Legacy app (`/home/administrator/Documents/Development/ept`)**: a complete, working Zend Framework 1 application — 4 modules (participant/data-manager web UI, admin backend, mobile JSON API, reporting), 7 scheme-specific scoring engines plus one generic config-driven engine, PDF-template-overlay certificate generation via an async job queue, a 4-tier role hierarchy, and 11 scheduled jobs.
- **This app (`proficiency-testing`)**: a complete JHipster-generated CRUD skeleton across 38 domain entities (full resource/service/mapper/React-module stack for each) — but **zero hand-written business logic anywhere**. No scoring, no PDF/certificate generation, no bulk import, no shipment/enrollment workflow, no scheme-specific result entry, no email/notification logic beyond JHipster's stock account emails, no scheduling beyond JHipster's stock stale-account cleanup, and no RBAC beyond stock `ROLE_ADMIN`/`ROLE_USER`. The data model, notably, already anticipates most of this (e.g. `ParticipantResult.zScore`/`calculatedScore`, `SchemeConfiguration.passingScore`/`documentationWeight`) — the target shape exists, the computation doesn't.

This is genuinely a from-scratch business-logic build on top of solid scaffolding, not a partial-parity patch job. Size the schedule accordingly.

**Scope decision — Zimbabwe first:** the legacy app's most complex piece of logic (DTS scoring) supports 10 country/protocol-specific algorithm variants, and the legacy scheme catalog has 13 schemes total. Checking the real migrated Zimbabwe production data settles this concretely rather than guessing: `scheme_list.status` shows only **9 schemes are active** (`dts`, `eid`, `HBV RDT`, `HCV RDT`, `mRDT`, `recency`, `SYPH RDT`, `tb`, `vl`) — `covid19`, `dbs`, `HBV` (legacy), and `SYP` (legacy) are inactive and not part of the current program. And `scheme_config`'s real `dts` row is hardcoded to `dtsSchemeType: "updated-3-tests"` and carries no `allowedAlgorithms` list at all — confirmed against every real migrated DTS shipment's `shipment_attributes`, none of which carry an algorithm override. **Correction (found while writing Phase 5's detailed plan, see Findings below):** RTRI is gated by a per-shipment `enableRtri` attribute, not `scheme_config.rtriEnabled` as first assumed here — real sampled shipments had neither key set, so actual RTRI usage needs a fuller query before golden-output testing can claim coverage of it. **This build targets Zimbabwe's actual active configuration only**: the 9 active schemes, and within DTS, only the "Updated 3 Tests" algorithm + RTRI recency logic. The other 9 DTS algorithm variants and the 4 inactive schemes are explicitly out of scope for this build — but Phase 4/5's architecture (a pluggable per-scheme evaluator/dispatcher) is designed so any of them could be added later without a rewrite, if NMRL ever takes on hosting other countries' programs.

**Delivery principle:** build and ship module by module, in small independently-testable increments — not phase-sized big-bang merges. Phase 0 and Phase 3 below are already split into lettered/numbered subtasks (0.1, 0.2, ... / 3.0, 3.1, ...) each with its own tests and exit criteria; treat that granularity as the model for every phase, splitting further where a phase's task list below still reads as more than one independently-shippable, independently-testable unit of work. A phase is not "in progress" if nothing in it can pass its own tests yet — get the smallest vertical slice green end to end before widening scope.

---

## Source references

Read these before starting any phase below — they are authoritative, not summarized-away:

**Legacy app docs** (in `/home/administrator/Documents/Development/ept/docs/`):

- `ARCHITECTURE.md` — overall Zend Framework 1 structure, module layout, controller/service/model conventions.
- `SchemeArchitecture.md` — the scoring engine, per-scheme algorithm details, shipment lifecycle diagrams, and an "Adding a New Scheme" checklist. **This is the single most important document for Phase 5.**
- `AdminModuleGuide.md` — the 4-tier role hierarchy and step-by-step business workflows (enrollment → shipment → result entry → evaluation → finalize → certificate). **The single most important document for Phase 3.**

**This repo's migration docs** (in `docs/migration/`):

- `README.md` — the migration process and data-preservation contract.
- `DRY_RUN.md` — the end-to-end dry-run log (archive, typed promotion, file migration all verified working).
- `KNOWN_GAPS.md` — legacy data-quality gaps found and accepted as-is; also useful as a map of what real production data actually looks like.

**Consequence for this plan:** because the migration already works, every phase below can and should be tested against **real migrated data**, not synthetic fixtures. Stand up a target database from a legacy snapshot (per `DRY_RUN.md`) as the standard dev/test seed, rather than hand-crafting test data from scratch.

---

## Gap analysis summary

| Area                                                                                | Legacy status                                                                                                     | Current app status                                                                                                                         | Complexity                                                                                                                                                                       | Phase                     |
| ----------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------- |
| Auth / roles / sessions                                                             | 4-tier hierarchy, session-based, flat privilege strings, CSRF, login hardening, impersonation                     | Stock `ROLE_ADMIN`/`ROLE_USER` only, no ownership scoping                                                                                  | High (foundational)                                                                                                                                                              | 0                         |
| Background job queue / scheduling                                                   | `scheduled_jobs` table + Crunz scheduler, 11 jobs                                                                 | Stock 1 `@Scheduled` job only                                                                                                              | Medium (foundational)                                                                                                                                                            | 0                         |
| Email / notifications                                                               | Template + queue + send job; FCM push is vestigial/unused                                                         | Stock account emails only                                                                                                                  | Medium                                                                                                                                                                           | 0 / 7                     |
| File storage integration                                                            | `public/uploads`, `public/files`, `downloads/reports`                                                             | File migration already lands legacy files locally (see migration docs) — needs a serving/generation-target story                           | Low–Medium                                                                                                                                                                       | 0                         |
| Reference data (countries, test kits, assays, modes of receipt, not-tested reasons) | CRUD + a few config screens                                                                                       | Full CRUD scaffold exists                                                                                                                  | Low                                                                                                                                                                              | 1                         |
| Global / scheme configuration                                                       | `global_config`, `scheme_config` JSON, per-scheme settings screens                                                | `GlobalConfiguration`/`SchemeConfiguration` entities exist, no config-editing workflow                                                     | Low–Medium                                                                                                                                                                       | 1                         |
| Participants & data managers                                                        | Full CRUD + custom fields + PTCC/country assignment + bulk import                                                 | CRUD scaffold exists, no bulk import, no assignment workflow                                                                               | Medium                                                                                                                                                                           | 2                         |
| Bulk import (Excel)                                                                 | PhpSpreadsheet-based importers throughout                                                                         | No Excel library, no importer code at all                                                                                                  | Medium                                                                                                                                                                           | 2                         |
| Distributions / shipments / enrollment                                              | `ShipmentController.php` (28 actions, confirmed by deep-read), full lifecycle incl. deadlines, testkit assignment | CRUD scaffold on `Distribution`/`Shipment`/`ShipmentParticipantMap`/`Enrollment`, no workflow — also missing a `CANCELLED` status entirely | **High** — split into 7 independently-testable subtasks (3.0–3.6)                                                                                                                | 3                         |
| Result entry (per scheme)                                                           | 7 bespoke scheme controllers + generic `CustomTest` engine, plus mobile-API mirror                                | Nothing — no result-entry UI or API at all                                                                                                 | **High**, narrowed to Zimbabwe's 9 active schemes (was 13)                                                                                                                       | 4                         |
| Scoring / evaluation engine                                                         | Universal formula + categorical gates + 7 scheme model classes (DTS has 10 algorithm variants) + generic engine   | Nothing — fields exist, no computation                                                                                                     | **High**, narrowed to Zimbabwe's 9 active schemes + 1 of 10 DTS algorithms (confirmed via real `scheme_config`/shipment data) — was **Very high** across the full legacy catalog | 5                         |
| Certificates & reports                                                              | FPDI PDF overlay, async job queue, finalize gate, Excel/Word export, charts, 10+ report types                     | Nothing — no PDF library at all                                                                                                            | **High** — libraries decided (PDFBox/POI/Chart.js, see Architectural decisions)                                                                                                  | 6                         |
| CAPA (corrective/preventive actions)                                                | Failure-reason codes feeding CAPA controllers/reports                                                             | `CapaRecord`/`CorrectiveAction` entities exist, no logic                                                                                   | Medium                                                                                                                                                                           | 5 / 6                     |
| Mobile API                                                                          | Second real client (`application/modules/api`) mirroring web result-entry                                         | No API design has accounted for a second client yet                                                                                        | Resolved — unified API decided (see Architectural decisions)                                                                                                                     | 0, then woven through 3–5 |
| Ancillary (announcements, partners, help/feedback, audit log, dashboards)           | Present, generally simple CRUD-plus-display                                                                       | Entities exist for most, no UI/logic                                                                                                       | Low                                                                                                                                                                              | 8                         |

---

## Phase 0 — Cross-cutting foundations

Everything downstream depends on these decisions being made once, correctly, rather than re-litigated per phase.

### 0.1 Role model & authorization

**Legacy reference:** `docs/AdminModuleGuide.md` (role hierarchy), `SystemAdminsController.php:11` (flat `privileges` string check pattern), `library/Pt/Plugins/PreSetter.php` (front-controller access-control plugin), `ImpersonateController.php`.

**Decision (resolved):** the legacy model is PT Administrator → PTCC → Data Manager → Participant, with per-data-manager _ownership_ scoping (a data manager only sees participants they're mapped to via `participant_manager_map`). Use Spring Security's built-in `RoleHierarchyImpl` (core Spring Security, no extra dependency) to express `ROLE_PT_ADMIN > ROLE_PTCC > ROLE_DATA_MANAGER` inheritance, so a coarse `@PreAuthorize("hasRole('DATA_MANAGER')")` check is satisfied by anyone at or above that tier without duplicating grants. Enforce participant-_ownership_ (as opposed to tier) with service-layer query scoping — filter by mapped participant IDs in the repository/service, not a custom `PermissionEvaluator` — simpler to reason about and avoids per-row permission-check overhead.

**Tasks:**

- [ ] Define `AuthoritiesConstants` for `ROLE_PT_ADMIN`, `ROLE_PTCC`, `ROLE_DATA_MANAGER` (`ROLE_ADMIN`/`ROLE_USER` stay for JHipster's own account management, don't conflate them with the PT domain roles) and a `RoleHierarchy` bean expressing the tier inheritance above.
- [ ] Service-layer participant-ownership scoping: every read/write path a data manager or PTCC hits must filter through their `participant_manager_map`/country assignment, enforced in the service layer, covered by the negative-case test below.
- [ ] Normalize the legacy flat `privileges` string on `system_admin` into fine-grained `Authority` rows using JHipster's existing many-to-many `Authority`↔`User` model — don't carry the string-blob pattern forward, and don't invent a parallel permissions table when one already exists.
- [ ] CSRF: **resolved as not applicable.** This app's `SecurityConfiguration` already runs `oauth2ResourceServer(...).jwt(...)` — stateless bearer-token auth, no server-side session/cookie — so CSRF (a session-based attack vector) doesn't apply the way it did to legacy's `Zend_Session`-based auth. Record this as a deliberate, reviewed decision rather than a silently-dropped legacy control.
- [ ] Login hardening parity: attempt counting, temp/permanent ban, forced password reset flag — port `global_config.max_attempts_for_temp_ban`/`max_attempts_for_perm_ban` equivalents onto `UserLoginHistory`.
- [ ] Admin impersonation of a data manager (`ImpersonateController.php` equivalent) — needed for support workflows; scope carefully (audit-logged, time-limited).

**Tests:** integration tests proving a data manager cannot read/write another data manager's participants via the API even with a valid token; integration tests for each of the 3 PT-domain roles hitting a representative protected endpoint.

**Exit criteria:** a data manager, a PTCC, and a PT admin test account each exist in a seeded dev environment, each sees exactly the scope the legacy app would show them for the same seeded data.

### 0.2 Background job / scheduling infrastructure

**Legacy reference:** `scheduled-jobs/ScheduledTasks.php` (Crunz scheduler config), `Application_Model_DbTable_ScheduledJobs` (`scheduled_jobs` table — **this table already exists in this app's schema** as the `ScheduledJob` entity), `Evaluation.php:queueReportsGeneration()`/`addShipmentEvaluationToQueue()`/`getJobProgress()`.

Almost every later phase (evaluation, report generation, deadline processing, email sending) needs an async job pattern. Build the primitive once.

**Tasks:**

- [ ] Design the job-queue contract: job type enum, payload (JSON), status (`PENDING`/`PROCESSING`/`COMPLETED`/`FAILED`), progress tracking — map onto the existing `ScheduledJob` entity rather than inventing a parallel table.
- [ ] Build the job processor as a Spring `@Scheduled` poller against `ScheduledJob` rows (the direct, faithful port of legacy's `execute-job-queue.php` pattern), guarded by **ShedLock** (`net.javacrumbs.shedlock`, Apache 2.0, free/open source, the standard Spring Boot library for making `@Scheduled` tasks safe against duplicate/concurrent execution). Decided now rather than left open: even a nominally single-instance deployment benefits from ShedLock as cheap insurance against double-processing during rolling restarts, and it means the app can scale to multiple instances later without a redesign.
- [ ] Port `reset-stale-jobs.php` equivalent (recover stuck `PROCESSING` jobs after a timeout — the `processing_started_at`/`last_heartbeat` columns already present on `shipment` in the legacy schema show this heartbeat pattern extends beyond the generic job queue into shipment-level long-running operations too; keep the recovery logic generic enough to cover both).
- [ ] DB-backup/binlog-purge/housekeeping-style ops jobs (`db-tools backup`, `db-tools purge-binlogs`, `housekeeping.php`, `backup-config.php`): **resolved as out of scope for the application.** These are infra/ops concerns, not business logic — handle via standard PostgreSQL tooling (`pg_dump`/`pg_basebackup` on a systemd timer or cron, or the hosting provider's managed backup feature) instead of porting PHP scripts into the app.

**Tests:** a job enqueued via the service layer transitions PENDING → PROCESSING → COMPLETED and is idempotent on reprocessing; a job stuck in PROCESSING past its timeout is recovered by the stale-job sweep.

**Exit criteria:** a trivial "no-op" job type can be enqueued and observed completing end to end — this becomes the scaffold every later async feature (evaluation, report generation, email sending) builds on.

### 0.3 Email / notification infrastructure

**Legacy reference:** `application/services/Common.php:parseRecipients()`, `MailTemplateController.php`, `mail_template` table (already migrated as `MailTemplate`), `scheduled-jobs/send-emails.php`.

**Note:** FCM/push-notification config keys exist in legacy `global_config` but **no code path uses them** (confirmed by grep across the whole legacy codebase) — treat push notifications as **out of scope**, not a parity gap.

**Tasks:**

- [ ] Template rendering service: given a `MailTemplate` + a data context (participant, shipment, etc.), produce a rendered subject/body. Match the legacy template placeholder syntax so migrated `mail_template` rows work unmodified.
- [ ] Outbound queue: use the Phase 0.2 job infrastructure — enqueue an `EmailMessage` row (entity already exists) rather than sending synchronously, matching the legacy async pattern.
- [ ] Send job (poller consuming `EmailMessage` rows via Spring's `JavaMailSender`, already a dependency from JHipster's stock mail setup), guarded by the same ShedLock pattern as Phase 0.2.
- [ ] IMAP bounce processing (`process-bounces.php`, feeds `system_config.bounce_last_uid`): **resolved as descoped from initial phases** — it's a real legacy feature but a significant, separate subsystem, and does not block the Phase 0 exit. If picked up later, use **Jakarta Mail** (`jakarta.mail`, formerly JavaMail, free/open source, the standard Java library for IMAP/SMTP) rather than a bespoke client.

**Tests:** rendering a known `MailTemplate` + context produces the expected subject/body; an enqueued `EmailMessage` transitions to sent.

**Exit criteria:** one real legacy email type (e.g. activation or password-reset — already partially covered by JHipster stock, extend rather than replace) round-trips through the template + queue + send path.

### 0.4 File storage integration

**Legacy reference:** `public/uploads` (bulk-import files, logos), `public/files` (import templates), `downloads/reports` (generated certificates/reports, 12G in the migrated snapshot) — already pulled and verified by the file-migration pipeline (`docs/migration/DRY_RUN.md`).

**Decision (resolved):** local filesystem storage as the default (matches the already-proven migration target layout — zero new infra, free), with the `FileStoreService` abstraction below designed so a swap to S3-compatible object storage is a drop-in replacement later if storage needs outgrow a single disk/volume. If/when that swap is needed, recommend **MinIO** (free, open source, self-hostable, S3-API-compatible) over a proprietary cloud object store, to avoid vendor lock-in.

**Tasks:**

- [ ] Build a thin `FileStoreService` (or confirm one already exists from the migration work — check for `LegacyFileStore` referenced in earlier migration configuration) behind the local-filesystem decision above, that later phases depend on for both reading migrated legacy files and writing newly generated ones (bulk-import uploads, certificate templates, generated PDFs).

**Exit criteria:** a file can be written and read back through the chosen store from a trivial test, and the migrated legacy files (already on disk per the migration dry run) are reachable through the same abstraction.

### 0.5 API design for two clients

**Legacy reference:** `application/modules/api/*` — a second, independent JSON client (mobile app) of the same service layer as the web UI, with its own login and largely mirrored per-scheme get/save endpoints.

**Decision (resolved):** **one unified REST API**, versioned under `/api/**` (matching this app's existing JHipster convention), documented via **springdoc-openapi** (already a Spring Boot 3+/JHipster default dependency, free/open source, industry standard for generating an OpenAPI 3 spec straight from the controllers) — serving both the React web app and any surviving mobile client from the same endpoints. The legacy split (a separate `application/modules/api`) reads as a historical artifact (mobile API bolted on later, duplicating web logic) rather than a deliberate design worth preserving.

**Tasks:**

- [ ] Confirm during Phase 9 whether a real mobile client of the legacy API still exists and is in active use — this doesn't change the unified-API decision above, but determines whether Phase 9 needs to validate against an actual mobile app or can close this item as descoped.
- [ ] Hold every later phase to the unified-API decision — don't let Phase 4's result-entry endpoints and Phase 3's shipment endpoints diverge in shape by accident.

**Exit criteria:** the unified-API decision above is the one every phase builds against — no phase should need to revisit this.

---

## Phase 1 — Reference data & configuration

Lowest complexity, good confidence-building start, mostly UI/workflow polish on already-scaffolded entities.

**Legacy reference:** `SchemeConfigController.php`, `library/Pt/Commons/SchemeConfig.php`, per-scheme `*SettingsController.php` (Dts/Eid/Recency/Tb/Vl), `GlobalConfigController.php`, `HomeConfigController.php`, `TestkitController.php`, `TestPlatformController.php`.

**Scope:** `Country`, `TestKit`, `Assay`, `ModeOfReceipt`, `NotTestedReason`, `GlobalConfiguration`, `SchemeConfiguration`.

**Tasks:**

- [ ] `SchemeConfiguration` editing UI: legacy stores per-scheme JSON config (`scheme_config.value` — `passPercentage`, `documentationScore`, `allowedAlgorithms`, `sampleRehydrateDays`, `dtsEnforceAlgorithmCheck`, etc., already migrated). Build a structured editor rather than a raw-JSON textarea — the shape is scheme-dependent (see `SchemeArchitecture.md` for the field list per scheme) and this config directly drives Phase 5 scoring, so getting the editing UX right here avoids rework later.
- [ ] `GlobalConfiguration` editing UI for the general settings equivalent to legacy `global_config` (institute details, mail settings, login-hardening thresholds from Phase 0.1, participant login prefix/password length, etc.).
- [ ] Reference-data CRUD polish (Country/TestKit/Assay/ModeOfReceipt/NotTestedReason) — these are close to done already; verify the generated screens are actually usable (search/filter, pagination) rather than assuming generated-quality is sufficient.

**Tests:** editing a `SchemeConfiguration` for a known scheme (e.g. `eid`, the simplest) produces a config object Phase 5's EID evaluator can consume without translation.

**Exit criteria:** every reference-data entity has a working, reviewed admin screen; scheme configuration for at least one scheme is editable end to end and matches the legacy JSON shape for that scheme.

---

## Phase 2 — Participants & data managers

**Legacy reference:** `ParticipantsController.php` / `application/services/Participants.php`, `DataManagersController.php` / `application/services/DataManagers.php`, PhpSpreadsheet bulk-import usage throughout both.

**Tasks:**

- [ ] Participant CRUD + custom fields (`ParticipantCustomValue`/`CustomFieldDefinition` — already have entities) + country/PTCC assignment.
- [ ] Data manager CRUD + participant-manager mapping (many-to-many, a participant can be its own data manager per legacy design — preserve this).
- [ ] Bulk import: pick a Java Excel library (Apache POI is the standard choice — add to `pom.xml`), build an importer matching the legacy bulk-import Excel formats (templates for these already sit in the migrated `public/files` — `Participant-Bulk-Import-Excel-Format-v2.xlsx`, `PTCC_Bulk_Import_Excel_Format.xlsx` — use them as the literal target format, don't redesign the spreadsheet layout).
- [ ] Bulk export mirroring the import format.
- [ ] Auth flows scoped to this phase: participant/data-manager login, email verification, password reset — these depend on Phase 0.1's role model and Phase 0.3's email infrastructure; sequence accordingly.

**Tests:** import a real legacy-format bulk-import file (use one of the actual migrated `.xlsx` files as a fixture) and verify the resulting participants match; round-trip export→import is lossless.

**Exit criteria:** a data manager can be created, mapped to participants, log in, and see only their mapped participants (proving Phase 0.1's scoping works in practice, not just in isolated tests).

---

## Phase 3 — Distributions, shipments & enrollment workflow

**This is the largest single legacy controller** (`ShipmentController.php`, 33K, **28 actions** — confirmed by the mandatory 3.0 deep-read below; an earlier file-size-based estimate of "~90 actions" was wrong, see Findings below) — split into independently-testable subtasks below to reduce risk, rather than one large undifferentiated phase. Sequencing is grounded in the real legacy schema (columns confirmed directly against the migrated database, not assumed):

- `distributions` (`distribution_id`, `distribution_code`, `distribution_date`, `status`, audit columns) has **no foreign key to shipment or enrollment** — it's a standalone container, safe to build and ship first in isolation.
- `enrollments` (`list_name`+`participant_id` composite PK, `scheme_id`, `enrolled_on`, `status`) has **no foreign key to `shipment_id` either** — a participant is enrolled into a scheme/round independently of any specific shipment row, so enrollment can be built and tested as its own vertical slice before shipment logic exists.
- `shipment` (`shipment_id`, `shipment_code`, `scheme_type`, `distribution_id`, `response_deadline`, `auto_close_at_deadline`, `response_switch`, `allow_editing_response`, `shipment_attributes` (JSON), `status`, plus a full timeline: `evaluated_at`/`reports_generated_at`/`finalized_at`/`results_approved_on`/`cancelled_at`/`cancellation_reason`/`processing_started_at`/`last_heartbeat`) is the real complexity — build it after distributions/enrollment are solid.
- `shipment_participant_map` and `shipment_testkit_map` (`shipment_id`+`testkit_id` composite PK, `scheme_type`, `testkit_1`/`testkit_2`/`testkit_3` lot quantities) are separate join tables — separate subtasks.

**Legacy reference:** `application/modules/admin/controllers/DistributionsController.php` + `application/services/Distribution.php`, `ShipmentController.php` + `application/services/Shipments.php` (**4978 lines, only partially read during research — see 3.0 below**), `docs/SchemeArchitecture.md` for the shipment lifecycle diagram.

### 3.0 Prerequisite: deep-read `Shipments.php`

Not a feature, a blocking research task. `Shipments.php` was only partially read during the initial gap-analysis pass (the `eid`/`dts` result-update branching was read in detail; the rest wasn't). Do this before writing 3.3–3.5's detailed implementation plans — the full shipment state machine (every legal status value and transition, not just the 3 observed in the current small migrated sample — `evaluated`, `finalized`, `reports generated` — plus the schema-implied `pending`/`cancelled`) needs to come from the actual code, not inference from a data sample.

- [ ] Full read-through of `Shipments.php`, documenting: every shipment status value and legal transition, what `report_in_queue`/`tb_form_generated`/`collect_feedback` actually gate, and whether TB-form generation (3.6 below) is really a shipment-workflow concern or belongs in Phase 6 as a report-generation feature.

**Exit criteria:** a written state-machine diagram (statuses + legal transitions + what triggers each) exists and is used as the literal spec for 3.3–3.5, not re-derived ad hoc during implementation.

### 3.1 Distributions

**Tasks:**

- [ ] Distribution CRUD (create/edit the PT-round container) — `distribution_code`, `distribution_date`, `status`.

**Tests:** create, edit, and list distributions end to end via the API and UI; a distribution with no shipments yet still lists/displays correctly.

**Exit criteria:** an admin can create and manage distributions independently of any shipment or enrollment work — ships and is testable on its own.

### 3.2 Enrollment

**Tasks:**

- [ ] Enrollment: participant + scheme + round (`list_name`), with duplicate-enrollment prevention (not present in current CRUD scaffold at all) — enforce the same effective uniqueness the legacy composite PK (`list_name`, `participant_id`) implies.

**Tests:** enrolling the same participant into the same scheme/round twice is rejected; enrolling into a scheme the participant is already active in elsewhere in the current round is not blocked (confirm this against the legacy behavior during 3.0's read-through, don't assume).

**Exit criteria:** enrollment works end to end against real participants and schemes (Phase 1/2 output), independent of shipment creation — ships and is testable on its own.

### 3.3 Shipment core lifecycle

**Tasks:**

- [ ] Shipment creation within a distribution (per scheme) — `scheme_type`, `response_deadline`, `auto_close_at_deadline`, `number_of_samples`/`number_of_controls`, `shipment_attributes`.
- [ ] Add the missing `CANCELLED` value to the current app's `ShipmentStatus`/`DistributionStatus` enums (confirmed absent entirely by 3.0's deep-read — a real gap, not a design choice) before building the state machine below.
- [ ] Status state-machine per 3.0's documented spec — legacy derives shipment state from a combination of milestone timestamps (`evaluated_at`/`reports_generated_at`/`finalized_at`), an ephemeral disabled-action status list (`draft`/`ready`/`queued`/`processing`/`pending`), and a separate permanent `cancelled_at` lock, with a 15-minute stuck-`queued` recovery override and a feedback-form-existence gate on Finalize — implement only the transitions 3.0 confirmed are real, not a guessed superset.
- [ ] Clone-from-previous-shipment — legacy has this as a distinct action; confirm during 3.0 whether it's worth preserving as-is or is a symptom of a UX gap (e.g. "new round from template") worth actually fixing in the rewrite rather than porting verbatim.

**Tests:** a shipment can be created, transitions through its documented states, and rejects illegal transitions (e.g. finalizing before evaluation).

**Exit criteria:** shipment core lifecycle works end to end against a real distribution and scheme — ships and is testable before testkit/participant mapping exists.

### 3.4 Shipment-participant mapping & testkit assignment

**Tasks:**

- [ ] `ShipmentParticipantMap` workflow — enroll/unenroll a participant into a specific shipment.
- [ ] Testkit assignment (`ShipmentTestkitMap`-equivalent — `testkit_id` + up to 3 lot quantities per shipment/scheme combination).

**Tests:** assigning/unassigning a participant and a testkit lot to a shipment is reflected correctly and is reversible while the shipment is still open.

**Exit criteria:** a shipment can be fully populated with participants and testkits, ready to ship — builds directly on 3.3, independently testable from it.

### 3.5 Deadline automation

**Tasks:**

- [ ] Deadline processing job (Phase 0.2 job infra + this domain logic): `response_switch` toggling, cutoff-timezone handling (`global_config.cutoff_timezone`) at `response_deadline`, matching legacy's `process-shipment-deadlines.php` — idempotent, skips already-finalized shipments (confirm this exact guard during 3.0).

**Tests:** a shipment past its deadline is automatically closed by the scheduled job (not by a manual action), late result entry is blocked afterward, and re-running the job against an already-closed shipment is a no-op.

**Exit criteria:** the full happy path — distribution → enrollment → shipment → participant/testkit assignment → automatic deadline close — runs end to end without manual intervention at the deadline step. This is the prerequisite for every phase after it; don't let it slip into "mostly done."

### 3.6 TB-form generation

**Resolved by 3.0's deep-read:** TB-form generation lives directly in `ShipmentController.php` — it's a shipment-workflow action, not a report-generation concern. Build it here, not in Phase 6.

- [ ] TB-form PDF generation as a shipment action, per the detailed Phase 3 plan.

---

## Phase 4 — Result entry (per scheme)

**Legacy reference:** `{Dts,Vl,Tb,Eid,Recency,Covid19,Dbs,CustomTest}Controller.php` + matching `application/views/scripts/{scheme}/response.phtml`; mobile mirror in `application/modules/api/controllers/ShipmentsController.php` (`dtsAction`/`saveDtsAction`, etc.).

**Scope — Zimbabwe's active schemes only** (confirmed against real `scheme_list.status`, not assumed): `dts`, `eid`, `HBV RDT`, `HCV RDT`, `mRDT`, `recency`, `SYPH RDT`, `tb`, `vl`. `covid19`, `dbs`, `HBV` (legacy), and `SYP` (legacy) are inactive in production and **out of scope** for this phase — of the two remaining bespoke legacy Model classes (`Covid19.php`, and DBS which shares patterns with the qualitative engine), neither is needed. Build the result-entry engine so a new scheme (active or reactivated-legacy) can be added later without an architecture change, but don't build the inactive schemes' bespoke logic now.

**Sequencing rationale:** build the generic config-driven engine first — it covers 4 of the 9 active schemes (`HBV RDT`, `HCV RDT`, `mRDT`, `SYPH RDT`, all `is_user_configured='yes'` in the real data) with one implementation. Then do the 5 bespoke active schemes in ascending complexity (per the research pass's size-based ranking, with the inactive `covid19`/`dbs` entries dropped from the original 7): EID → Recency → VL → TB → DTS.

**Tasks:**

- [ ] Generic `CustomTest`-equivalent result-entry engine, driven by `SchemeConfiguration`'s `user_test_config`-equivalent JSON (built in Phase 1) — result form fields, validation, and storage should all derive from config, not per-scheme code. Covers `HBV RDT`, `HCV RDT`, `mRDT`, `SYPH RDT` in one implementation.
- [ ] EID result entry (simplest bespoke scheme — good second target after the generic engine).
- [ ] Recency result entry.
- [ ] VL result entry — note this is **quantitative**, not qualitative like the others; the entry form and validation differ (numeric value entry vs. categorical response selection).
- [ ] TB result entry.
- [ ] DTS result entry — **most complex of the active schemes**, do last, after the team has built up pattern familiarity from the simpler ones. Result entry itself (as opposed to scoring, Phase 5) is mostly about capturing the right fields — since Zimbabwe's real `scheme_config` hardcodes `dtsSchemeType: "updated-3-tests"` with no per-shipment algorithm override (confirmed against real shipment data), the entry form only needs to support the "Updated 3 Tests" + RTRI field set, not all 10 legacy algorithm variants' field sets. Cross-reference `docs/SchemeArchitecture.md`'s "Adding a New Scheme" checklist for what "done" looks like.
- [ ] Result-entry API parity per the unified-API decision in Phase 0.5.

**Tests:** for each scheme, entering a known set of legacy-equivalent responses produces a stored `ParticipantResult` (or equivalent) with all fields populated correctly — this phase does NOT test scoring correctness (that's Phase 5), only that data capture is complete and faithful to the legacy field set.

**Exit criteria:** every scheme has a working result-entry screen (web) capturing the same fields the legacy scheme-specific `.phtml` form captures, verified field-by-field against the legacy view source.

---

## Phase 5 — Scoring / evaluation engine

**The hardest and highest-risk phase in this plan.** Treat it as its own workstream with mandatory correctness verification, not manual translate-and-trust.

**Scope — Zimbabwe's active configuration only** (see the scope decision at the top of this document): the 9 active schemes (`dts`, `eid`, `HBV RDT`, `HCV RDT`, `mRDT`, `recency`, `SYPH RDT`, `tb`, `vl`), and within DTS, only the algorithm Zimbabwe actually runs.

**Legacy reference:** `docs/SchemeArchitecture.md` (authoritative formula + diagrams), `application/services/Evaluation.php` (4919 lines, orchestration), `application/models/Dts.php` (3413 lines total — only the `algoUpdatedThreeTests` + RTRI paths are in scope, see below), `Vl.php` (1938), `Tb.php` (2270), `Recency.php` (1059), `CustomTest.php` (1376, generic engine), `Eid.php` (473), `application/services/QuantitativeCalculations.php` (94 lines — small, self-contained, **recommend porting near-verbatim** for VL's peer-group statistics). `Covid19.php` is not needed (scheme inactive).

**Universal formula (qualitative schemes):**

```
Response Score = (correct-response points / max possible points) × (100 − Documentation%)
Documentation Score = Σ documentation-item scores (each item worth documentationScore/totalItems, capped by scheme_config.documentationScore)
Final Score = Response Score + Documentation Score
Pass = Final Score >= scheme_config.passPercentage
       AND algorithm-correctness check passes
       AND testkit-lot validity check passes
       AND testkit-expiry check passes
       AND mandatory-result-field check passes
       AND last-date check passes
```

Any categorical gate failing forces overall Fail regardless of numeric score. Result codes: `1`=Pass, `2`=Fail, `3`=Excluded.

**VL (quantitative) is a different paradigm entirely** — peer-group statistics (mean, median, standard deviation, quantile, z-score, coefficient of variation) computed across all participants' reported values for a shipment, not compared against one fixed correct answer.

**DTS in the legacy app dispatches to 10 country/protocol-specific implementations** (`algoUpdatedThreeTests`, `algoVietnam` + 3 Vietnam sub-checks, `algoRTRI`, `algoSerial`, `algoParallel`, `algoSierraLeone`, `algoCoteDivoire`, `algoMyanmar`, `algoMalawi`, `algoGhana`/`algoGhanaSyphilis`), selected via `scheme_config.allowedAlgorithms` + per-shipment attributes. **Confirmed against the real Zimbabwe production data, this build only needs one**: `scheme_config`'s real `dts` row carries no `allowedAlgorithms` list, just `dtsSchemeType: "updated-3-tests"` — and every real migrated DTS shipment's `shipment_attributes` (`{"sampleType": "dried", "screeningTest": "no"}`, checked across the sample) carries no algorithm override either. RTRI itself is gated by a per-shipment `enableRtri` attribute (not the `scheme_config.rtriEnabled` flag this document assumed until the detailed Phase 5 plan corrected it) — real sampled shipments had neither key set, so confirm actual RTRI coverage in the migrated data before treating golden-output tests for it as representative. **Scope: implement `algoUpdatedThreeTests` + the RTRI recency-testing logic only.** Design `evaluateAlgorithm()`'s equivalent as a pluggable dispatcher (strategy pattern, one implementation per algorithm) so the other 9 variants are a later addition, not a rewrite, if NMRL ever hosts another country's program — but do not implement them now.

**Tasks:**

- [ ] Build the universal scoring formula + categorical-gates infrastructure first (shared by every scheme).
- [ ] Port `QuantitativeCalculations.php` for VL — smallest, cleanest, do it early to validate the porting methodology.
- [ ] Generic `CustomTest` scoring engine (pairs with Phase 4's generic result-entry engine) — covers `HBV RDT`, `HCV RDT`, `mRDT`, `SYPH RDT` in one implementation.
- [ ] Per-scheme evaluators in ascending complexity, matching Phase 4's sequencing: EID → Recency → VL (statistics layered on top of the quantitative-calculations port) → TB → **DTS last**.
- [ ] DTS: implement the dispatcher (pluggable, one-algorithm-per-implementation shape) and, within it, only `algoUpdatedThreeTests` + RTRI — reviewable and independently testable as its own unit.
- [ ] CAPA integration: port the failure-reason code lookup (`getDtsCorrectiveActions()`-equivalent) that feeds `CorrectiveAction`/`CapaRecord`, scoped to the failure reasons `algoUpdatedThreeTests`/RTRI actually produce.

**Tests — this is the load-bearing part of the whole phase, do not skip or shortcut:**

- [ ] Build a **golden-output diff harness**: for a representative sample of real migrated shipments (pull from `legacy_record_archive` via the already-working migration pipeline, or run a live legacy instance's evaluation for comparison shipments), capture the legacy-computed `shipment_score`/`documentation_score`/`final_result`/`failure_reason` as the expected output, run the same input through the new evaluator, and diff.
- [ ] Cover all 9 active schemes with at least one golden-output case each — a passing case and a failing case per scheme at minimum. Within DTS, cover `algoUpdatedThreeTests` with and without RTRI, since that's the full real-world variation in scope.
- [ ] Do not consider any scheme's scoring "done" until its golden-output cases pass; do not consider the phase done until all 9 active schemes have golden-output coverage.

**Exit criteria:** every scheme's evaluator reproduces the legacy system's scored output for a representative sample of real historical shipments, with zero unexplained discrepancies. Any intentional behavior change from legacy (bug fixes, etc.) must be explicitly called out and approved, not silently introduced as a side effect of reimplementation.

---

## Phase 6 — Certificates & reports

**Legacy reference:** `library/Pt/Reports/{FpdiReport,IndividualPdf,SummaryPdf}.php`, `application/services/CertificateTemplates.php`, `Evaluation.php`'s job-queueing methods, `reports/FinalizeController.php`, the `reports` module's controllers (Annual, Detailed, Distribution, ParticipantPerformance, ParticipantTrends, Shipments, plus disease-specific reports), `library/Pt/Reports/ChartRenderer/*`.

**Decisions (resolved):**

- **PDF:** `org.apache.pdfbox:pdfbox` (Apache PDFBox — Apache 2.0, free, open source, maintained by the Apache Software Foundation) — the standard Java library for exactly this use case: load an existing PDF template, overlay dynamic text/images at coordinates, matching legacy's FPDI approach. iText 7 is explicitly ruled out (AGPL license requires either open-sourcing this app under AGPL or buying a commercial license — neither fits); OpenPDF is a viable but less-actively-maintained fallback if PDFBox ever proves insufficient.
- **Word export:** `org.apache.poi:poi-ooxml` (Apache POI) — the same library Phase 2 already adds for Excel bulk import/export. POI covers both `.xlsx` and `.docx`, so Word export costs nothing extra once Excel support exists; keep it in scope on that basis (confirm real usage with the team, but default to build since the incremental cost is near zero).
- **Charts:** client-side only, via **Chart.js** + **`react-chartjs-2`** (both MIT-licensed, free, extremely widely used, and match the legacy app's own `ChartJsNode.php` convention so terminology/config carries over conceptually) for every in-app report visual — do not port legacy's server-rendered JPGraph. If a specific PDF report genuinely needs an embedded chart image, render it server-side with **JFreeChart** (LGPL, free, long-standing industry-standard Java charting library) and embed the resulting image via PDFBox — but treat that as the exception, not the default.

**Tasks:**

- [ ] Certificate template upload/management (`CertificateTemplate` entity exists) — template validation equivalent to legacy's `pdftk`-based check (PDFBox can validate/inspect a PDF template directly, no external `pdftk` process dependency needed).
- [ ] Individual certificate PDF generation (per-participant, overlay name/scheme/score/dates/watermark onto the uploaded template) via PDFBox.
- [ ] Summary/batch report PDF generation via PDFBox.
- [ ] Wire generation into the Phase 0.2 async job infrastructure — this mirrors legacy's `queueReportsGeneration()`/job-queue pattern exactly, don't make it synchronous.
- [ ] Finalize gate: a one-way switch (mirrors legacy `FinalizeController`) after which participants can download results/certificates; evaluation and report-regeneration remain repeatable and invisible to participants until finalized.
- [ ] Excel/Word export via Apache POI (per the decision above).
- [ ] Chart rendering for report visuals via Chart.js/react-chartjs-2 in the React app (per the decision above); JFreeChart only for the rare PDF-embedded-chart case.
- [ ] Report types: prioritize by actual usage if that data is available (check `AuditLog`/access patterns from the legacy system if possible); otherwise sequence Annual/Detailed/Distribution/Shipments before the disease-specific and participant-trend reports — and prioritize reports for the 9 active schemes (per Phase 4/5's scope decision) before any report type that only makes sense for an inactive scheme.

**Tests:** generated PDFs are byte-reasonable (not byte-identical — font rendering will differ) but contain the correct data at the correct positions for a known template + known scored shipment; the finalize gate correctly blocks/unblocks participant visibility.

**Exit criteria:** for at least one scheme with a real certificate template (from the migrated file store), the full pipeline — evaluate → generate certificate → finalize → participant can download — works end to end.

---

## Phase 7 — Notifications & scheduled jobs (completion)

Builds on Phase 0.3's infrastructure to reach full parity with the legacy job list.

**Legacy reference:** the scheduled-jobs table in the research report — `process-shipment-deadlines.php` (covered in Phase 3), `check-participant-emails.php` (email syntax+MX validation), `send-reports-mail.php`, `message-notifications.php`.

**Tasks:**

- [ ] Participant/data-manager email validation job (syntax + MX check, batched with retry-after-window).
- [ ] Report-ready notification emails (ties Phase 6's generation completion to Phase 0.3's email queue).
- [ ] Message/announcement notification job, if `ContactMessage`/`Announcement` entities need push-on-create behavior.
- [ ] Revisit the Phase 0.3 deferred decision on IMAP bounce processing — implement or formally descope.

**Exit criteria:** the full set of in-scope scheduled jobs runs on their intended cadence in a deployed environment and their effects are observable (email validation flags bad addresses, report-ready emails arrive after Phase 6 generation completes).

---

## Phase 8 — Ancillary modules & dashboards

Lower risk, lower complexity, do last so it doesn't distract from the high-value phases above.

**Legacy reference:** `AnnouncementController`, `PartnersController`, `HelpController`, `FeedbackResponsesController`, `AuditLogController`, `LogViewerController`, `JobTrackingController`, `ApiHistoryController`, `AlertsController`.

**Tasks:**

- [ ] Announcements, partners, help/feedback screens (entities exist: `Announcement`, `Partner`, `ContactMessage`, `FeedbackQuestion`, `ParticipantFeedback`).
- [ ] Audit log viewer (`AuditLog` entity exists) — surfacing what's already captured, not building new capture logic (confirm capture already happens via JHipster's auditing support, extend if not).
- [ ] Job tracking UI (surfaces Phase 0.2's job queue — `ScheduledJob`/progress).
- [ ] API request log viewer (`ApiRequestLog` entity exists).
- [ ] Dashboards: build on top of whatever data is available by this phase (participant counts, shipment status breakdowns, pass/fail rates by scheme) — this is new value-add UI, not a strict legacy port, since the legacy app's own dashboard sophistication wasn't deeply characterized in the research pass. Scope based on actual stakeholder need at the time, not assumed legacy parity.

**Exit criteria:** each ancillary entity has a working admin screen; the audit/job-tracking screens give operators the same visibility legacy's equivalent tools provided.

---

## Phase 9 — Mobile API finalization & cutover

**Tasks:**

- [ ] Revisit the Phase 0.5 API decision now that every feature is built — confirm the actual API surface matches what any real remaining mobile client needs (or confirm no such client exists and this is fully descoped).
- [ ] Run a full production-representative migration per `docs/migration/DRY_RUN.md`, this time promoting through every phase's now-complete business logic, not just the data layer.
- [ ] Parallel-run / UAT: have real data managers use the new system against migrated production-shadow data alongside the legacy system for a defined period, comparing outputs (especially Phase 5's scoring and Phase 6's certificates).
- [ ] Cutover plan: follow `docs/migration/README.md`'s "Recommended cutover process" (clone production, fresh migration run, reconcile, maintenance-window switch, keep legacy read-only as fallback).

**Exit criteria:** UAT sign-off from real users comparing new-system output to legacy-system output on the same real data, then a scheduled cutover per the existing migration runbook.

---

## Testing & verification strategy (cross-cutting)

- **Every phase gets integration tests** exercising the REST API end to end (the pattern already exists — `*ResourceIT.java` files per entity — extend this pattern for business-logic endpoints, don't just rely on the generated CRUD tests).
- **Phase 5 specifically requires golden-output diff testing against real legacy-computed results** — this is not optional and is called out again here because it's the single highest-risk area in the whole plan. A scoring bug that silently ships is worse than a missing feature.
- **Seed dev/test environments from real migrated data** (per the migration docs), not hand-built fixtures, wherever the data isn't sensitive — this catches shape mismatches the migration's own reconciliation might not (e.g. a JSON config field the scoring engine expects in a slightly different form than what got archived).
- **Before starting each phase**, write its own detailed implementation plan via `superpowers:writing-plans` (bite-sized TDD tasks, file-level detail) — this document intentionally stays at the roadmap/checklist level; don't try to make it double as that plan.
- **After each phase**, update this document: check off completed tasks, record any new decisions made (add to the "Architectural decisions (resolved)" table), and note any scope changes discovered along the way (the plan will be wrong in places — that's expected, fix it here as you learn).

---

## Architectural decisions (resolved)

Every decision that would otherwise have blocked downstream work has been resolved below, using free/open-source, actively-maintained, industry-standard choices in every case. Each is detailed in its phase section; this is the at-a-glance index. Treat these as settled unless new information genuinely warrants revisiting one — don't re-litigate per phase.

| #   | Decision                             | Choice                                                                                                                                                                                                                                                    | Why                                                                                                                        |
| --- | ------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| 0.1 | PT-domain role/permission model      | Spring Security `RoleHierarchy` (`ROLE_PT_ADMIN` > `ROLE_PTCC` > `ROLE_DATA_MANAGER`) for tier inheritance + service-layer query scoping for participant ownership; normalize legacy's flat `privileges` string into JHipster's existing `Authority` rows | Core Spring Security, no new dependency; avoids the complexity of a custom `PermissionEvaluator` for the ownership case    |
| 0.1 | CSRF                                 | Not applicable — this app is stateless JWT bearer-auth, not session-based                                                                                                                                                                                 | CSRF only matters for cookie/session auth, which this app doesn't use                                                      |
| 0.2 | Job scheduling                       | Spring `@Scheduled` + **ShedLock** (`net.javacrumbs.shedlock`, Apache 2.0)                                                                                                                                                                                | Free, the de facto standard for safe scheduled tasks in Spring Boot; cheap insurance even for a single-instance deployment |
| 0.2 | DB backup / housekeeping ops scripts | **Out of scope for the app** — standard `pg_dump`/`pg_basebackup` via systemd timer/cron, or the host's managed backup feature                                                                                                                            | Infra concern, not business logic; don't port PHP ops scripts into the application                                         |
| 0.3 | IMAP bounce processing               | **Descoped from initial phases**; if built later, use **Jakarta Mail**                                                                                                                                                                                    | Significant separate subsystem, not required for MVP parity                                                                |
| 0.4 | File storage                         | Local filesystem (matches the proven migration target layout); **MinIO** if/when object storage is needed later                                                                                                                                           | Free, zero new infra now; MinIO avoids cloud vendor lock-in if storage needs grow                                          |
| 0.5 | API surface                          | One unified REST API under `/api/**`, documented via **springdoc-openapi**                                                                                                                                                                                | Legacy's separate mobile-API module looks like a historical artifact, not a design worth preserving                        |
| 6   | PDF generation                       | **Apache PDFBox** (`org.apache.pdfbox:pdfbox`)                                                                                                                                                                                                            | Apache 2.0, free, standard Java library for template-overlay PDF generation; iText 7's AGPL license is a poor fit          |
| 6   | Excel / Word export                  | **Apache POI** (`org.apache.poi:poi-ooxml`) for both                                                                                                                                                                                                      | One free, open-source, Apache-2.0 library covers `.xlsx` and `.docx` — no second library needed for Word                   |
| 6   | Charts                               | **Chart.js** + `react-chartjs-2` client-side; **JFreeChart** only for the rare PDF-embedded-chart case                                                                                                                                                    | MIT-licensed, free, matches legacy's own Chart.js convention; avoids porting legacy's server-side JPGraph rendering        |

---

## Progress tracking

**Planning status:** all 10 phases have a detailed, bite-sized TDD implementation plan written (2026-08-23), each produced via `superpowers:writing-plans` with fresh supplementary reads of the relevant legacy source and the current app's real entity state. Each plan is a single file covering all of that phase's subtasks (Phase 0's file covers 0.1–0.5 as separate Task blocks; Phase 3's file covers 3.0–3.6 the same way) — see [Findings from the detailed phase plans](#findings-from-the-detailed-phase-plans-2026-08-23) below for what they corrected in this document.

**Implementation status:** nothing has been implemented yet — update the Status column below as work actually lands, following each phase's own plan file task-by-task.

| Phase                                                                            | Status                        | Detailed plan                                                                     |
| -------------------------------------------------------------------------------- | ----------------------------- | --------------------------------------------------------------------------------- |
| 0 — Cross-cutting foundations (0.1–0.5)                                          | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-0-cross-cutting-foundations.md`          |
| 1 — Reference data & configuration                                               | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-1-reference-data-configuration.md`       |
| 2 — Participants & data managers                                                 | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-2-participants-data-managers.md`         |
| 3 — Distributions, shipments & enrollment (3.0–3.6)                              | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-3-distributions-shipments-enrollment.md` |
| 4 — Result entry (9 active schemes)                                              | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-4-result-entry.md`                       |
| 5 — Scoring / evaluation engine (9 active schemes, DTS = updated-3-tests + RTRI) | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-5-scoring-evaluation-engine.md`          |
| 6 — Certificates & reports                                                       | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-6-certificates-reports.md`               |
| 7 — Notifications & scheduled jobs                                               | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-7-notifications-scheduled-jobs.md`       |
| 8 — Ancillary modules & dashboards                                               | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-8-ancillary-modules-dashboards.md`       |
| 9 — Mobile API finalization & cutover                                            | Plan written, not implemented | `docs/superpowers/plans/2026-08-23-phase-9-mobile-api-cutover.md`                 |

---

## Findings from the detailed phase plans (2026-08-23)

Writing all 10 phases' detailed plans surfaced real corrections to this document and to each other — recorded here so they aren't lost in 10 separate files. Two claims in this document were factually wrong and are now fixed in place (the `ShipmentController.php` action count, and the RTRI gating field); everything else below is additive detail worth knowing before implementing.

- **`ShipmentController.php` has 28 actions, not ~90** — the original estimate was file-size-based, not a real count. Fixed above.
- **RTRI is gated by a per-shipment `enableRtri` attribute, not `scheme_config.rtriEnabled`** — fixed above in both the scope-decision paragraph and Phase 5. Real sampled shipments had neither key set; confirm actual RTRI usage in the full migrated dataset before treating golden-output DTS+RTRI test cases as representative.
- **The current app's `ShipmentStatus`/`DistributionStatus` enums are missing `CANCELLED` entirely** — a real gap, now Task 1 of the Phase 3 plan (added to 3.3 above too).
- **TB-form generation is confirmed a shipment-workflow action** (lives in `ShipmentController.php`), not a report-generation concern — 3.6 above updated from "deferred" to resolved.
- **No `ShipmentTestkitMap`-equivalent entity exists in the current app** — built from scratch in Phase 3's plan (Task 10).
- **A legacy `r_possibleresult` table (per-scheme response-option/scoring catalog) has no equivalent entity anywhere in the current app** — the Phase 1 plan adds a new `PossibleResult` entity for it (its own Task 1/2), since Phase 4 (result entry) and Phase 5 (scoring) for the 4 generic-engine active schemes depend on it existing. Cross-check this against Phase 4's `resultAttributes` JSONB column design (Phase 4's plan takes a JSONB-per-entity approach for scheme-specific _captured_ fields, distinct from Phase 1's `PossibleResult` catalog of _valid_ answers) — the two are complementary, not conflicting, but read both plans together before implementing either.
- **Legacy has two separate config-editing controllers, not one**: `SchemeConfigController.php` (6 hardcoded schemes) and `CustomTestController.php` (4 generic schemes) — both covered in the Phase 1 plan.
- **Real bulk-import bug, ported forward as a mandatory guard**: legacy code (`Participants.php:1759-1798`) documents a real production incident where Excel silently drops leading zeros from numeric-typed PT-ID cells, causing 459 wrong site IDs in a past enrollment. The Phase 2 plan's Task 4 makes the guard against this mandatory, not optional.
- **Certificates are fillable AcroForms, not coordinate-overlay PDFs.** `CertificateTemplates.php` validates templates via `pdftk dump_data_fields`, requiring a named form field (`participant_name`/`labname`/etc.) — filled by field name (PDFBox `PDAcroForm`), not overlaid at coordinates. The coordinate-overlay mechanism described in this document's original Phase 6 section is actually how the separate Individual/Summary _report_ renderers work (`IndividualPdf.php`/`SummaryPdf.php`/`FpdiReport.php`), not certificates. The Phase 6 plan also found no real certificate-template files or AcroForm-fill call sites anywhere in the legacy codebase — that feature may be unused/unfinished; the plan recommends building the report renderers (which are demonstrably used — they're the source of the 12G of real generated reports already migrated) before investing in certificate generation.
- **The Individual/Summary report renderers hardcode layout per country** (same `if`/`elseif`-branching pattern as DTS scoring) — the Phase 6 plan scopes this to `layout == "zimbabwe"` only, consistent with this document's Zimbabwe-first principle.
- **Legacy's "Announcement" is not a bulletin** — `Announcement.php:composeNewAnnouncement()` is a targeted non-participation follow-up mailer (queries `shipment_participant_map` for non-responders on a specific shipment, composes and queues a message to them), confirmed independently by both the Phase 7 and Phase 8 plans. The current app's `Announcement` entity really is a static bulletin — kept as-is in Phase 8's plan; the real targeted-messaging feature is built separately in Phase 7's plan (Task 5), since it depends on Phase 3's shipment-participation data.
- **`scheduled-jobs/message-notifications.php` is not a real notification job** — it's a hardcoded WhatsApp Cloud API test script (mostly commented-out sample code). Dropped from scope in the Phase 7 plan, treated like this document's existing FCM-is-vestigial finding.
- **`check-participant-emails.php` lives at `bin/`, not `scheduled-jobs/`** (cron `15 */4 * * *`, batch 500, 30-day recheck window) — corrected in the Phase 7 plan.
- **`send-reports-mail.php` is not on a cron schedule at all** — it's a job-queue job type enqueued by `Shipments.php` when a report generates, drained by the existing job poller. The Phase 7 plan builds it that way, not as a new cron trigger.
- **`JobTrackingController` has a real `cancelJobAction`**, not read-only — the current `ScheduledJob` entity already anticipates this via `JobStatus.CANCELLED`, built in Phase 8's plan.
- **Audit-log capture doesn't exist anywhere in the current codebase yet** (confirmed by grep) — the Phase 8 plan builds the capture service from scratch, not as an extension of something that already exists.
- **The domain model already anticipates Phase 0 more than expected**: `JobStatus`/`JobType`/`DataManagerRole`/`AuditAction` enums already cover most of what Phase 0 needs, and a **read-only `LegacyFileStore` already exists** from the migration work — the Phase 0 plan builds a separate write-capable `FileStoreService` alongside it rather than modifying it. Legacy mail templates use `##PLACEHOLDER##` syntax, not Mustache — matched exactly in the Phase 0 plan's template renderer.
- **The Phase 5 golden-output harness is simpler than this document assumed**: legacy-computed `shipmentScore`/`documentationScore`/`finalResult`/`failureReason` are already sitting in the migrated `ShipmentParticipantMap` rows (the existing promotion SQL copied them straight from legacy) — the harness just reads them back before any new evaluator overwrites them, no separate `legacy_record_archive` extraction or live legacy instance needed.
- **No evidence of real mobile-API traffic**: Phase 9's plan queried `track_api_requests` (written by `ApiServices::insertApiTrackerData()`) against this session's legacy MySQL snapshot and got **zero rows** — a strong signal the Phase 0.5 unified-API decision's "confirm during Phase 9" checkpoint may resolve to "no real client ever existed." Must be re-verified against the actual production snapshot before final sign-off, not treated as conclusive from this sample alone.
