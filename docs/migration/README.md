# Legacy ePT migration

This migration moves an ePT MySQL installation into this Java/PostgreSQL application without requiring the two schemas to be identical.

The source baseline used for this implementation is [`deforay/ept`](https://github.com/deforay/ept) commit `f25f00a0fac4901b146221096870526fed241c68` (baseline schema 7.6.1 plus 43 upgrade scripts through 7.6.20). The migrator requires source version `7.6.20` by default and discovers the live schema dynamically. Do not use the baseline `sql/init.sql` table count as a production acceptance number: later upgrade scripts add and remove tables.

## Data-preservation contract

"Complete" has an explicit, testable meaning:

1. Every discovered MySQL table is streamed; no fixed allow-list is required.
2. Every row and column value is stored in `legacy_record_archive.payload` as canonical JSON.
3. Original primary keys, SHA-256 row checksums, row counts, table checksums, and batch identity are retained.
4. Binary values are Base64 encoded. MySQL date/time values retain their wall-clock representation rather than being silently shifted to another timezone.
5. Supported core records are also promoted into typed Java/PostgreSQL domains, with `legacy_source_id`, the complete original payload, and an entry in `migration_id_map`.
6. Legacy uploaded/generated files can be copied separately with relative paths and SHA-256 verification.
7. Reconciliation independently checks discovered-table coverage, committed archive counts, recomputed archive checksums, resolvable typed identity maps, operational relationships, result envelopes, and file coverage/checksums.
8. MySQL zero dates are preserved literally in the archive and converted to `NULL` only in typed date columns that cannot represent them.
9. Every PostgreSQL write is explicitly committed even though the production Hikari pool has auto-commit disabled.

The archive is the lossless system of record for legacy fields that do not yet have typed Java domains. This makes phased domain work possible without discarding data.

## Typed promotion coverage

| Legacy table | Java/PostgreSQL domain | Notes |
| --- | --- | --- |
| `countries` | `Country` | Source identity and full payload retained |
| `scheme_list` | `Scheme` | Scheme code is the stable business key |
| `participant` | `Participant` | Existing domain extended with legacy identity |
| `data_manager` | `DataManager` | Password/token material preserved but not enabled for login |
| `r_modes_of_receipt` | `ModeOfReceipt` | Reference data |
| `r_response_not_tested_reasons` | `NotTestedReason` | Reference data |
| `distributions` | `Distribution` | Linked to typed schemes |
| `enrollments` | `Enrollment` | Participant/scheme relationship |
| `shipment` | `Shipment` | Linked to distribution and scheme |
| `shipment_participant_map` | `ShipmentParticipantMap` | Relationship and operational state |
| `mail_template` | `MailTemplate` | Body and source payload retained |
| `data_manager`, `system_admin` | `jhi_user` | Compatible bcrypt credentials remain usable; incompatible hashes are disabled for reset |
| `participant_manager_map`, `ptcc_countries_map` | Operational relationship tables | Manager/site and PTCC/country assignments rebuilt |
| `global_config`, `system_config`, `scheme_config` | Configuration domains | Global and scoring configuration retained |
| `r_testkitnames` | `TestKit` | Source identity, scheme assignments and full payload retained |
| assay reference tables | `Assay` | DBS, EID, VL, TB, recency and COVID-19 dimensions retained |
| configured response/reference tables | `LegacySchemeResult` | Every scheme-specific result gets a typed, linked lossless envelope |
| certificate/report tables | Certificate and report domains | Templates, batches, shipment links, configuration and download history retained |

Scheme-specific result payloads are not flattened into the smaller generic `ParticipantResult` model. They are retained in `LegacySchemeResult`, linked to the typed shipment/map when possible, so scheme-specific Java models can be introduced later without re-reading MySQL.

## Recommended cutover process

1. Clone the production MySQL database and file roots. Never run the first migration against the only production copy.
2. Bring the clone to the source application's expected schema version using the legacy ePT upgrade process.
3. Start with an empty PostgreSQL database and let Liquibase create the target schema. Migration mode forces Liquibase to run synchronously.
4. Run an archive-only pass first (`EPT_MIGRATION_PROMOTE_CORE=false`) and review the reconciliation tables.
5. Run the typed promotion pass and, if required, the file pass.
6. Re-run from a fresh production snapshot during a maintenance window, reconcile, then switch traffic.
7. Keep the MySQL backup and archived source files read-only until operational acceptance is signed off.

Example archive-only environment (supply secrets through the deployment secret manager):

```bash
export EPT_MIGRATION_ENABLED=true
export EPT_MIGRATION_PROMOTE_CORE=false
export EPT_MIGRATION_FAIL_ON_ERROR=true
export EPT_MIGRATION_BATCH_SIZE=500
export EPT_LEGACY_JDBC_URL='jdbc:mysql://legacy-db:3306/pt?useUnicode=true&characterEncoding=utf8&useSSL=true&useCursorFetch=true'
export EPT_LEGACY_DB_USER='migration_reader'
export EPT_LEGACY_DB_PASSWORD='...'

./mvnw -Pprod clean verify
java -jar target/proficiency-testing-0.0.1-SNAPSHOT.jar \
  --spring.main.web-application-type=none \
  --spring.cloud.consul.enabled=false \
  --spring.docker.compose.enabled=false
```

The process takes a PostgreSQL advisory lock, performs one migration, reconciles it, and exits. A concurrent or unsupported-source run fails before archiving. To enable typed promotion after the archive-only pass, set `EPT_MIGRATION_PROMOTE_CORE=true` and run again against the same target.

For files, add:

```bash
export EPT_MIGRATION_FILES_ENABLED=true
export EPT_LEGACY_FILES_ROOT='/mnt/legacy-ept-data'
export EPT_TARGET_FILES_ROOT='/mnt/java-ept-data'
export EPT_MIGRATION_FILES_OVERWRITE=false
```

After cutover, make the copied tree available to Java with:

```bash
export EPT_LEGACY_FILE_STORE_ENABLED=true
export EPT_LEGACY_FILE_STORE_ROOT='/mnt/java-ept-data'
```

The source database account only needs `SELECT` and `SHOW VIEW`. The application target account must be allowed to write the application and migration tables. Migration is disabled by default.

## Restart and rollback behavior

- Archive upserts use the source table and a hash of the source primary key. Re-running updates the same archived records.
- Typed promotion upserts use `legacy_source_id`. It does not create duplicate domain records.
- File migration leaves identical targets unchanged and refuses to replace differing files unless overwrite is explicitly enabled.
- Each attempt has its own `migration_batch`. `migration_table_result`, `migration_file_result`, `migration_error`, and `migration_id_map` provide an audit trail.
- A PostgreSQL session advisory lock prevents two attempts from overlapping.
- Migration mode is one-shot and closes the application after a clean run; any error produces a failed startup instead of leaving a web server running.
- A failed target can be discarded and rebuilt from the unchanged MySQL/file snapshots. The migrator never writes to MySQL.

## Acceptance queries

No rows should be returned by these queries for an accepted batch:

```sql
SELECT source_table, status, source_rows, archived_rows, stale_rows
FROM migration_table_result
WHERE batch_id = :batch_id
  AND (status <> 'COMPLETED' OR source_rows <> archived_rows OR stale_rows <> 0);

SELECT r.source_table, r.source_rows, COUNT(a.id) AS committed_archive_rows
FROM migration_table_result r
LEFT JOIN legacy_record_archive a
  ON a.source_table = r.source_table AND a.last_seen_batch_id = r.batch_id
WHERE r.batch_id = :batch_id
GROUP BY r.source_table, r.source_rows
HAVING r.source_rows <> COUNT(a.id);

SELECT stage, source_table, error_type, error_message
FROM migration_error
WHERE batch_id = :batch_id;

SELECT relative_path, status, source_checksum, target_checksum
FROM migration_file_result
WHERE batch_id = :batch_id
  AND (status NOT IN ('COPIED', 'UNCHANGED') OR source_checksum <> target_checksum);
```

Only a batch whose `migration_batch.status` is `COMPLETED` and whose `migration_error` count is zero is eligible for cutover. `COMPLETED_WITH_ERRORS` and `FAILED` are never acceptable.
