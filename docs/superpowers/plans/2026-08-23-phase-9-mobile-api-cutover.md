# Phase 9 — Mobile API Finalization & Cutover Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Close out the ePT parity project: confirm the Phase 0.5 unified-API decision against real mobile-client usage, run a full production-representative migration through every phase's now-complete business logic, execute a structured parallel-run/UAT comparing the new system against legacy on real data, and cut over via a rehearsed, documented runbook.

**Architecture:** This phase produces process artifacts (checklists, comparison scripts, a runbook), not application code — it consumes and re-runs the already-built migration pipeline (`docs/migration/`) and reuses Phase 5's golden-output diff methodology and Phase 6's certificate-generation exit criteria as the concrete verification mechanisms for UAT, rather than inventing a parallel one.

**Tech Stack:** Bash, SQL (MySQL for legacy source, PostgreSQL for target), the existing migration JAR (`proficiency-testing-0.0.1-SNAPSHOT.jar` run in `--spring.main.web-application-type=none` mode).

**Spec:** `/home/administrator/Documents/Development/proficiency-testing/ept_project_plan.md` (Phase 9 section), `docs/migration/README.md`, `docs/migration/DRY_RUN.md`, `docs/migration/KNOWN_GAPS.md`.

## Global Constraints

- Legacy source: MySQL, app: `ept` — do not write to it at any point (the migrator is read-only against MySQL by design, per `README.md`: "The migrator never writes to MySQL").
- Target: PostgreSQL, created empty per migration and populated by Liquibase — never run the first migration of a production snapshot against the only production copy (`README.md` step 1).
- Migration mode invocation is one-shot and headless: `--spring.main.web-application-type=none --spring.cloud.consul.enabled=false --spring.docker.compose.enabled=false`.
- `EPT_MIGRATION_FAIL_ON_ERROR=true` for every run in this phase — never relax this to make a batch "pass" artificially.
- A `migration_batch.status = FAILED` caused only by the 4 known data gaps in `KNOWN_GAPS.md` is expected and acceptable; any *other* row in `migration_error` is not — every task below that runs a migration must check this distinction explicitly, not just check the batch status.
- JDK 21 required to build: `export JAVA_HOME=/usr/lib/jvm/jdk-21.0.8-oracle-x64; export PATH="$JAVA_HOME/bin:$PATH"` (see `DRY_RUN.md`'s "Build environment" note).

---

### Task 1: Mobile client existence audit

**Files:**

- Create: `docs/cutover/mobile-client-audit.md`

**Interfaces:**

- Consumes: read-only SQL access to the production (or latest available) legacy MySQL snapshot.
- Produces: a documented decision (`REAL_TRAFFIC` or `NO_TRAFFIC`) that Task 5's runbook and `ept_project_plan.md`'s Phase 0.5 checkbox depend on.

- [ ] **Step 1: Query the legacy request-tracking table**

The legacy app already tracks every mobile-API call in `track_api_requests` (written by `Application_Service_ApiServices::insertApiTrackerData()` — confirmed in `/home/administrator/Documents/Development/ept/application/services/ApiServices.php`, called from the `api` module's controllers). Run against the legacy MySQL snapshot you're about to cut over from (not just a stale dev copy):

```sql
SELECT COUNT(*) AS total_requests,
       MIN(requested_on) AS earliest_request,
       MAX(requested_on) AS latest_request
FROM track_api_requests;

SELECT request_type, COUNT(*) AS n
FROM track_api_requests
GROUP BY request_type
ORDER BY n DESC;
```

- [ ] **Step 2: Record the finding**

On the dev/test snapshot used throughout this project, both queries returned **zero rows** — `track_api_requests` exists but has never recorded a single mobile-API call on that snapshot. This is a real, verified finding from this project's own snapshot, not a guess — but it does not by itself prove production has no mobile client, since dev/test snapshots can lag or diverge from production traffic. Re-run Step 1 against the actual production (or most-recent) snapshot before treating this as final.

Write `docs/cutover/mobile-client-audit.md` containing:
- The exact query results from Step 1, with the date the query was run and which database it ran against.
- The decision: `NO_TRAFFIC` if `total_requests` is zero or negligible (no more than a handful of historical test calls, all with `requested_on` well in the past) and no other evidence of a distributed mobile app exists (app-store listing, installed-device reports, or direct confirmation from the team); `REAL_TRAFFIC` otherwise, with the `request_type` breakdown attached.

- [ ] **Step 3: Act on the decision**

If `NO_TRAFFIC`: update `ept_project_plan.md`'s Phase 0.5 task list — check off "Confirm during Phase 9 whether a real mobile client of the legacy API still exists" and add a one-line note referencing `docs/cutover/mobile-client-audit.md`. No further mobile-specific work is needed; the already-built unified `/api/**` surface is sufficient.

If `REAL_TRAFFIC`: this blocks the rest of this phase — flag it to the team before proceeding to Task 2, since it means Phase 0.5's unified-API design needs validation against real mobile request/response shapes (captured `api_url`/`data_format` values from `track_api_requests`) before cutover, which is new scope this plan doesn't cover.

- [ ] **Step 4: Commit**

```bash
git add docs/cutover/mobile-client-audit.md ept_project_plan.md
git commit -m "docs: record mobile-client audit finding for Phase 9 cutover"
```

---

### Task 2: Production-representative migration re-run checklist

**Files:**

- Create: `docs/cutover/production-representative-migration-checklist.md`

**Interfaces:**

- Consumes: the already-built migration pipeline (unchanged from `docs/migration/`), and Phase 5's golden-output diff harness + Phase 6's certificate-generation exit-criteria test (both already built by their own phases — this task does not redefine them, it points to them).
- Produces: a verified, freshly-promoted target database that Task 3/4's UAT and Task 5's cutover rehearsal both run against.

This re-runs the exact process already proven in `docs/migration/DRY_RUN.md`, but against a full production-representative snapshot (not the smaller dev copy used there), and adds a business-logic smoke-test step that `DRY_RUN.md` couldn't include (business logic didn't exist yet when it was written).

- [ ] **Step 1: Build and prepare**

```bash
export JAVA_HOME=/usr/lib/jvm/jdk-21.0.8-oracle-x64
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -Pprod clean package -DskipTests
psql -h <target-host> -p 5432 -U <superuser> -c "ALTER ROLE <app-role> CREATEDB;"
psql -h <target-host> -p 5432 -U <app-role> -d postgres -c "CREATE DATABASE proficiencytesting_cutover OWNER <app-role>;"
```

- [ ] **Step 2: Archive-only pass**

```bash
export EPT_MIGRATION_ENABLED=true
export EPT_MIGRATION_PROMOTE_CORE=false
export EPT_MIGRATION_FAIL_ON_ERROR=true
export EPT_LEGACY_JDBC_URL='jdbc:mysql://<legacy-clone-host>:3306/ept?useUnicode=true&characterEncoding=utf8&useSSL=true&useCursorFetch=true'
export EPT_LEGACY_DB_USER='migration_reader'
export EPT_LEGACY_DB_PASSWORD='<from secret manager>'
export SPRING_DATASOURCE_URL='jdbc:postgresql://<target-host>:5432/proficiencytesting_cutover'
export SPRING_DATASOURCE_USERNAME=<app-role>
export SPRING_DATASOURCE_PASSWORD='<from secret manager>'
export JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET='<production secret>'

java -jar target/proficiency-testing-0.0.1-SNAPSHOT.jar \
  --spring.main.web-application-type=none \
  --spring.cloud.consul.enabled=false \
  --spring.docker.compose.enabled=false
```

- [ ] **Step 3: Run the acceptance queries**

Run all four queries from `docs/migration/README.md`'s "Acceptance queries" section against `proficiencytesting_cutover`, substituting this run's `batch_id` (from `SELECT id FROM migration_batch ORDER BY started_at DESC LIMIT 1;`). Expected: **zero rows from every query.** If any query returns rows, stop — do not proceed to Step 4 until resolved.

- [ ] **Step 4: Typed promotion pass**

```bash
export EPT_MIGRATION_PROMOTE_CORE=true
java -jar target/proficiency-testing-0.0.1-SNAPSHOT.jar \
  --spring.main.web-application-type=none \
  --spring.cloud.consul.enabled=false \
  --spring.docker.compose.enabled=false
```

- [ ] **Step 5: Verify the promotion result against `KNOWN_GAPS.md`**

```sql
SELECT source_table, error_message
FROM migration_error
WHERE batch_id = (SELECT id FROM migration_batch ORDER BY started_at DESC LIMIT 1)
  AND stage = 'RECONCILE';
```

Every row returned must match one of the four known gap categories in `docs/migration/KNOWN_GAPS.md` (`enrollments` blank `scheme_id`, `participant_manager_map` dangling references, `scheme_config` orphaned rows, `shipment_participant_map` dangling references). Any error row that does **not** match one of these four categories is a new, unexpected finding — stop and investigate before proceeding.

- [ ] **Step 6: Business-logic smoke test against the migrated data**

Everything through Phase 8 is now built, so this migrated dataset is the first real chance to prove the business logic actually works against production-shaped data, not just synthetic fixtures:

- Re-run Phase 5's golden-output diff harness (built in Phase 5's own plan) pointed at `proficiencytesting_cutover` instead of its original dev target — this is the same harness, same golden cases, different target database. Record the pass/fail count.
- Re-run Phase 6's certificate-generation exit-criteria test ("evaluate → generate certificate → finalize → participant can download") against at least one migrated shipment with a real migrated certificate template. Record pass/fail.
- If either fails against this dataset but passed against the dev dataset used in their own phases, the discrepancy is specific to production-shaped data (e.g. a data shape Phase 5/6's dev fixtures didn't cover) — file it as a defect against that phase, don't patch around it here.

- [ ] **Step 7: File migration pass**

```bash
export EPT_MIGRATION_FILES_ENABLED=true
export EPT_LEGACY_FILES_ROOT='<local staging root, rsynced from the production file tree per DRY_RUN.md Step 3's commands>'
export EPT_TARGET_FILES_ROOT='<production target file root>'
export EPT_MIGRATION_FILES_OVERWRITE=false

java -jar target/proficiency-testing-0.0.1-SNAPSHOT.jar \
  --spring.main.web-application-type=none \
  --spring.cloud.consul.enabled=false \
  --spring.docker.compose.enabled=false
```

Verify: `SELECT COUNT(*) FROM migration_file_result WHERE status NOT IN ('COPIED','UNCHANGED');` returns 0, and `SELECT COUNT(*) FROM migration_file_result WHERE source_checksum IS DISTINCT FROM target_checksum;` returns 0.

- [ ] **Step 8: Write the checklist doc and commit**

Write `docs/cutover/production-representative-migration-checklist.md` capturing the exact commands above (with real values filled in for this run, secrets redacted) and the Step 3/5/6/7 results, so Task 5's actual cutover rehearsal has a proven, copy-pasteable procedure rather than re-deriving one.

```bash
git add docs/cutover/production-representative-migration-checklist.md
git commit -m "docs: verify migration + business logic against production-representative snapshot"
```

---

### Task 3: UAT comparison harness

**Files:**

- Create: `scripts/cutover/compare_shipment.sql`
- Create: `docs/cutover/uat-comparison-methodology.md`

**Interfaces:**

- Consumes: a shipment ID present in both the still-running legacy MySQL database and the migrated PostgreSQL target from Task 2.
- Produces: a `PASS`/`FAIL`/`DELTA` comparison result per shipment that Task 4's UAT acceptance criteria consume.

This extends Phase 5's golden-output methodology (already proven against archived/historical data) to live comparison against the still-running legacy system during the parallel-run window — same principle, applied to shipments still being actively worked in both systems.

- [ ] **Step 1: Write the legacy-side query**

```sql
-- Run against the live legacy MySQL database
SELECT shipment_id, shipment_code, scheme_type, status,
       max_score, average_score
FROM shipment
WHERE shipment_id = :shipment_id;
```

- [ ] **Step 2: Write the new-system-side query**

```sql
-- Run against the target PostgreSQL database (proficiencytesting_cutover
-- or whichever target Task 2's checklist is being kept in sync against)
SELECT s.id, s.legacy_source_id, s.status,
       s.max_score, s.average_score
FROM shipment s
WHERE s.legacy_source_id = :shipment_id;
```

(`legacy_source_id` is the migration's standard cross-reference column — see `docs/migration/README.md`'s typed-promotion coverage table; every promoted table carries it.)

- [ ] **Step 3: Write the comparison script**

Create `scripts/cutover/compare_shipment.sql` as a single script parameterized on `:shipment_id` that runs both Step 1 and Step 2 queries (via `dblink` if run from one Postgres session against both databases, or as two separate scripts invoked by a wrapping shell script — pick whichever this deployment's tooling already supports; don't introduce a new cross-database dependency just for this) and asserts:

- `status` maps correctly between legacy's string values and the new system's status enum, per the state-machine mapping Phase 3.0 already documented (`docs/superpowers/plans/2026-08-23-phase-3-distributions-shipments-enrollment.md`'s state-machine spec — reuse it, don't redefine a second mapping here).
- `max_score` and `average_score` match within a defined epsilon (0 for exact-integer schemes; document per-scheme tolerance if VL's statistical rounding introduces float drift — confirm the actual tolerance needed empirically during Task 4's execution, don't assume 0 for every scheme).

For per-scheme response-level detail beyond these shipment-level aggregates (i.e. comparing individual test results, not just the rollup), extend this script using the exact field mapping Phase 5's plan defines for each scheme's evaluator (`docs/superpowers/plans/2026-08-23-phase-5-scoring-evaluation-engine.md`) — that plan already had to nail down every legacy `response_result_*` column's meaning to build the evaluator; reuse that mapping rather than re-deriving it here.

- [ ] **Step 4: Document the methodology**

Write `docs/cutover/uat-comparison-methodology.md`: how to invoke `compare_shipment.sql`, what a `PASS`/`FAIL`/`DELTA` result means, and the escalation path for a `FAIL` (file a defect against the relevant phase's plan, referencing the specific shipment ID and both systems' raw output).

- [ ] **Step 5: Commit**

```bash
git add scripts/cutover/compare_shipment.sql docs/cutover/uat-comparison-methodology.md
git commit -m "feat: add UAT shipment comparison harness for parallel-run"
```

---

### Task 4: UAT plan and acceptance criteria

**Files:**

- Create: `docs/cutover/uat-plan.md`

**Interfaces:**

- Consumes: Task 3's comparison harness, Task 2's verified migrated target.
- Produces: a signed-off UAT record that Task 5's cutover go/no-go decision depends on.

- [ ] **Step 1: Define the parallel-run window and sample**

Write into `docs/cutover/uat-plan.md`: the window duration (recommend at least one full result-entry-to-finalize cycle for at least one active scheme, so the comparison covers the full workflow, not just a snapshot), which real data managers participate, and which shipments are in scope (recommend: every shipment newly created during the window, across all 9 active schemes at least once, per `ept_project_plan.md`'s Zimbabwe-scope decision — don't sample only the easy schemes).

- [ ] **Step 2: Define the acceptance criteria table**

Write this exact table into the plan (fill in the "Owner" column with real names before starting UAT — leaving it blank is not acceptable, this is a real sign-off document, not a template to leave incomplete):

| Criterion | Measurement method | Pass threshold | Owner |
|---|---|---|---|
| Scoring parity | Task 3's `compare_shipment.sql` run against every in-scope shipment | Zero unexplained `FAIL` results (every `FAIL` traced to a filed, resolved defect) | |
| Certificate generation | Manual visual review of every generated certificate against its legacy equivalent for the same shipment | 100% reviewed and approved by at least 2 reviewers | |
| Workflow correctness | Data managers complete the full enrollment→shipment→result-entry→evaluation→finalize cycle in the new system without falling back to legacy | Zero blocking defects (P1/P2) open at window close | |
| Data manager sign-off | Structured feedback form (see Step 3) from every participating data manager | 100% response rate, no unresolved "would not use" responses | |

- [ ] **Step 3: Define the feedback form**

Write a short structured feedback form (as a markdown template within the same doc) with fields: data manager name, shipments worked during the window, any workflow step that took longer or was more confusing than legacy, any defect IDs filed, and a final yes/no "ready to fully switch to the new system."

- [ ] **Step 4: Commit**

```bash
git add docs/cutover/uat-plan.md
git commit -m "docs: define Phase 9 UAT plan and acceptance criteria"
```

---

### Task 5: Cutover runbook

**Files:**

- Create: `docs/cutover/cutover-runbook.md`

**Interfaces:**

- Consumes: Task 1's mobile-client decision, Task 2's proven migration checklist, Task 4's UAT sign-off.
- Produces: the literal, rehearsed sequence of commands used for the real production cutover.

- [ ] **Step 1: Write the pre-cutover checklist**

Into `docs/cutover/cutover-runbook.md`, list in order: confirm Task 1's audit is `NO_TRAFFIC` (or its `REAL_TRAFFIC` follow-up work is complete), confirm Task 4's UAT acceptance table is fully green with all owners signed off, announce the maintenance window to users, freeze legacy writes (or accept the brief window where the final snapshot may miss last-minute writes — document which policy this deployment chooses).

- [ ] **Step 2: Write the exact migration commands**

Copy Task 2's proven checklist commands verbatim (Steps 1–7 of that task) into this runbook as the literal commands to run during the maintenance window — don't paraphrase them a second time, reference and reuse the exact command blocks from `docs/cutover/production-representative-migration-checklist.md`, since that's the one proven-to-work version and duplicating it risks drift.

- [ ] **Step 3: Write the traffic-switch step**

This step is genuinely deployment-specific and not something this plan can specify without knowing the production reverse-proxy/load-balancer/DNS topology — that topology hasn't been established anywhere in this project's research so far. Concrete instruction, not a placeholder: **before the real cutover**, identify the exact mechanism this deployment uses (nginx/ingress config reload, DNS record change, load-balancer target-group swap, etc.), rehearse it once against a non-production environment, and paste the exact rehearsed command sequence into this section of the runbook. Do not leave this section as a description of options — it must contain the literal commands for this deployment before the runbook is considered complete.

- [ ] **Step 4: Write the rollback procedure**

Per `docs/migration/README.md`'s restart/rollback behavior: legacy is never written to by the migrator and stays untouched, so rollback is simply reversing Step 3's traffic switch (point traffic back at legacy) and discarding the new target database attempt (`README.md`: "A failed target can be discarded and rebuilt from the unchanged MySQL/file snapshots"). Write this as an explicit numbered sequence: (1) reverse the traffic-switch command from Step 3, (2) confirm legacy is serving traffic and functioning, (3) mark the cutover attempt as rolled back, (4) drop or archive the abandoned target database, (5) schedule a retry using this same runbook once the blocking issue is resolved.

- [ ] **Step 5: Write the post-cutover verification checklist**

List the concrete checks to run immediately after the traffic switch: re-run the four acceptance queries from `docs/migration/README.md` one final time against the now-live target, confirm at least one real user can log in and complete one read and one write action end to end, confirm Task 3's comparison harness shows no new `FAIL` results on the first few post-cutover shipments.

- [ ] **Step 6: Commit**

```bash
git add docs/cutover/cutover-runbook.md
git commit -m "docs: add Phase 9 cutover runbook with rollback procedure"
```

---

## Self-review notes

- **Spec coverage:** `ept_project_plan.md` Phase 9 lists 4 bullets — "revisit Phase 0.5 decision" → Task 1; "run a full production-representative migration...promoting through every phase's now-complete business logic" → Task 2; "parallel-run/UAT...comparing outputs" → Tasks 3+4; "cutover plan...per README's recommended cutover process" → Task 5. All four covered.
- **Placeholder scan:** the one genuinely deployment-dependent unknown (Task 5, Step 3's traffic-switch mechanism) is handled with a concrete resolution instruction (rehearse, then paste the literal command) rather than vague "handle appropriately" language — this is a real, currently-unknowable fact about a not-yet-chosen production topology, not a skipped design decision.
- **Type/naming consistency:** `legacy_source_id` (Task 3) matches the column name used throughout `docs/migration/README.md`'s typed-promotion table; `proficiencytesting_cutover` database name is used consistently across Task 2 and referenced (not redefined) in Task 5; file paths for Tasks 1–5's artifacts are all under `docs/cutover/` except the SQL script under `scripts/cutover/`, consistent throughout.
