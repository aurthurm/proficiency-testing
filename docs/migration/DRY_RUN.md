# End-to-end dry run log

This records the first full, real execution of the migration process
documented in [`README.md`](./README.md): archive, typed promotion, and file
migration, run in sequence against a real legacy MySQL snapshot and a
disposable PostgreSQL target. It exists so the process is reproducible and so
the result is auditable, separate from [`KNOWN_GAPS.md`](./KNOWN_GAPS.md),
which covers the bugs fixed and the legacy data-quality gaps found along the
way in more depth.

**Outcome: the full pipeline (archive → typed promotion → file migration)
works end to end.** All bugs found while proving this out are fixed on
`main`. The only gaps remaining are in the legacy source data itself, not in
the migration code — see `KNOWN_GAPS.md`.

## Scope of this dry run

- **Source**: a local copy of the legacy MySQL database (`ept`, user `lims`),
  loaded from a legacy snapshot for testing.
- **Target**: a fresh PostgreSQL database (`proficiencytesting_migration`) on
  the local native PostgreSQL instance, created empty so Liquibase built the
  schema from scratch, per the recommended process.
- **Files**: the legacy application's real file store, pulled from the live
  legacy server (`eptsrv`, `/var/www/ept`) over SSH/rsync into a local
  staging directory, then migrated from there.
- Not in scope (production-only, not dry-runnable): cloning the actual
  production database/files, and the real maintenance-window cutover.

## Environment setup

The build requires JDK 21 explicitly — see the "Build environment" note
below if `./mvnw` fails with `release version 21 not supported`.

```bash
export JAVA_HOME=/usr/lib/jvm/jdk-21.0.8-oracle-x64
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -Pprod clean package -DskipTests
```

Target database, created empty:

```bash
psql -h 127.0.0.1 -p 5432 -U <superuser> -c "ALTER ROLE nmrl CREATEDB;"
psql -h 127.0.0.1 -p 5432 -U nmrl -d postgres -c "CREATE DATABASE proficiencytesting_migration OWNER nmrl;"
```

Common environment for every run below:

```bash
export EPT_MIGRATION_ENABLED=true
export EPT_MIGRATION_FAIL_ON_ERROR=true
export EPT_LEGACY_JDBC_URL='jdbc:mysql://127.0.0.1:3306/ept?useUnicode=true&characterEncoding=utf8&useSSL=false'
export EPT_LEGACY_DB_USER=lims
export EPT_LEGACY_DB_PASSWORD=password
export SPRING_DATASOURCE_URL='jdbc:postgresql://127.0.0.1:5432/proficiencytesting_migration'
export SPRING_DATASOURCE_USERNAME=nmrl
export SPRING_DATASOURCE_PASSWORD=password
export JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET='<base64 secret, see application-secret-samples.yml>'
```

Invocation (same for every pass, only the `EPT_MIGRATION_*` flags below change):

```bash
java -jar target/proficiency-testing-0.0.1-SNAPSHOT.jar \
  --spring.main.web-application-type=none \
  --spring.cloud.consul.enabled=false \
  --spring.docker.compose.enabled=false
```

## Step 1 — Archive-only pass

```bash
export EPT_MIGRATION_PROMOTE_CORE=false
```

Result: `migration_batch.status = COMPLETED`, 108/108 tables, 1,316,476 rows
archived, 0 errors. Verified against every acceptance query in `README.md`
(table-level row/status mismatches, archive-count reconciliation,
`migration_error`) — all returned zero rows.

## Step 2 — Typed promotion pass

```bash
export EPT_MIGRATION_PROMOTE_CORE=true
```

Result: all 17 typed domain tables populated (`participant`, `scheme`,
`enrollment`, `data_manager`, `shipment_participant_map`,
`legacy_scheme_result`, etc.). Reconciliation flagged 4 tables with archived
rows that can't resolve to a typed record — verified as legacy
referential-integrity gaps in the source data, not defects. See
`KNOWN_GAPS.md` for the row-level breakdown and the decision to migrate them
as-is.

Note on `migration_batch.status = FAILED` here: each table's promotion
commits in its own transaction as it completes, so this status does not mean
data was rolled back — it means the final reconciliation step, which runs
after all promotable rows are already committed, found rows it could not
resolve. Confirmed directly: every typed table's row count matches its
reconciliation "resolved" count exactly.

## Step 3 — File migration pass

Legacy file store pulled from the live server first:

```bash
rsync -az -e "ssh -p 1622" nmrl@<host>:/var/www/ept/public/uploads/    <local>/uploads/
rsync -az -e "ssh -p 1622" nmrl@<host>:/var/www/ept/public/files/      <local>/files/
rsync -az -e "ssh -p 1622" nmrl@<host>:/var/www/ept/public/temporary/  <local>/temporary/
rsync -az -e "ssh -p 1622" nmrl@<host>:/var/www/ept/downloads/reports/ <local>/downloads/reports/
```

| Directory | Size | Files |
| --- | --- | --- |
| `public/uploads` | 40M | 129 |
| `public/files` | 8.3M | 11 |
| `public/temporary` | 4.8M | 5 |
| `downloads/reports` | 12G | 32,009 |
| **Total** | **12G** | **32,154** |

Then:

```bash
export EPT_MIGRATION_FILES_ENABLED=true
export EPT_LEGACY_FILES_ROOT='<local staging root>'
export EPT_TARGET_FILES_ROOT='<local target root>'
export EPT_MIGRATION_FILES_OVERWRITE=false
```

Result: 32,154 / 32,154 files migrated (7,491 `UNCHANGED` on the resumed
rerun after an interrupted first attempt, 24,663 newly `COPIED` — the copy
step commits per file, so the interruption lost no progress). 0 checksum
mismatches, 0 failures. Local target directory size and file count both
matched the source exactly.

`public/temporary` holds transient, timestamped scratch exports rather than
canonical legacy data — it was included in this dry run for completeness but
is not required in a future run.

## Reproducing this

1. Point `EPT_LEGACY_JDBC_URL` at a legacy MySQL snapshot and run Step 1.
   Check the acceptance queries in `README.md`.
2. Set `EPT_MIGRATION_PROMOTE_CORE=true` and rerun. A `FAILED` status here is
   expected if the source data has the same known gaps as `KNOWN_GAPS.md` —
   check `migration_error` for anything *not* already listed there before
   treating a `FAILED` batch as acceptable.
3. Stage the legacy file tree locally and run with
   `EPT_MIGRATION_FILES_ENABLED=true`.
4. This process is one-shot and idempotent per step — reruns skip unchanged
   files and upsert already-promoted rows rather than duplicating them, so it
   is safe to resume any step after an interruption.
