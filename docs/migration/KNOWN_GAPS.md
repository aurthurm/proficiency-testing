# Known gaps from the first end-to-end migration run

This records findings from actually running the documented migration process
(`docs/migration/README.md`) for the first time, end to end, against a real
legacy MySQL snapshot (`ept`) and a disposable PostgreSQL target. Several
issues only surface when the process is actually executed rather than read.

## Bugs fixed to make the documented process work

The documented invocation (`--spring.main.web-application-type=none`) had
apparently never been run end to end before. Fixing it required:

1. **`Instant` bound directly to Postgres params.** pgjdbc cannot infer a SQL
   type for a raw `java.time.Instant`. Fixed at 8 call sites across
   `LegacyBatchMigrationService`, `LegacyFileMigrationService`, and
   `LegacyMigrationReconciliationService` by wrapping with `Timestamp.from(...)`.
2. **`String.format` modernizer violations**, only enforced at the Maven
   `package` phase (`-Pprod`), so never caught by `compile`/`test`.
3. **`CacheConfiguration` required `ServerProperties`**, which Spring Boot
   only registers for a real web application. Made lazy via `ObjectProvider`.
4. **Migration mode silently disabled the app's real Postgres datasource.**
   `LegacyMigrationConfiguration.legacySourceDataSource` is typed `DataSource`,
   which satisfies Spring Boot's `@ConditionalOnMissingBean(DataSource.class)`
   guard on its own datasource auto-configuration — so enabling migration mode
   suppressed the *entire* main datasource (Liquibase, JPA/Hibernate,
   JdbcTemplate, everything). Fixed by explicitly redefining the primary
   `dataSource` bean inside `LegacyMigrationConfiguration`, scoped to the same
   `application.migration.enabled=true` condition.
5. **Web-only Spring Security beans required in headless mode.**
   `AuthenticationManagerBuilder` / `HttpSecurity` don't exist without a
   servlet container, but all 43 `@RestController` classes still get
   instantiated as plain beans regardless of `web-application-type`. Added
   `MigrationWebLayerExcludeFilter` to exclude `zw.org.nmrl.ept.web.rest.*`
   from component scanning when migration mode is enabled, and gated
   `SecurityConfiguration.filterChain` the same way (kept `passwordEncoder`
   unconditional).
6. **`enrollment` typed-promotion SQL used the wrong legacy identity column.**
   `migration/promote/060_enrollments.sql` used `enrollment_id` as
   `legacy_source_id`, but that legacy column is a shared per-round batch
   code, not a row identifier (e.g. `i0AAWImN` is shared by every participant
   enrolled in round `2022-B(1)`) — the real legacy primary key is the
   composite `(list_name, participant_id)`. Caused
   `ON CONFLICT DO UPDATE command cannot affect row a second time` during
   promotion. Fixed by deriving `legacy_source_id` from the composite key.

## Data-quality gaps — accepted as-is

After the fixes above, an archive-only pass and a typed-promotion pass both
ran clean against the full `ept` database (108/108 tables, 1,316,476 rows
archived, 0 archive errors). The typed-promotion pass's reconciliation step
then found 4 tables where not every archived row could resolve to a typed
domain row. Each was individually verified against the live legacy database
and traced to real referential gaps in the legacy data itself — not bugs in
the promotion SQL or the reconciliation logic:

| Legacy table | Archived rows | Resolved | Gap | Cause |
| --- | --- | --- | --- | --- |
| `enrollments` | 36,972 | 31,262 | 5,710 | Legacy row has a blank `scheme_id` — no scheme to link to |
| `participant_manager_map` | 35,420 | 25,815 | 9,605 | Dangling `dm_id`/`participant_id` — legacy MySQL never enforced these as foreign keys |
| `scheme_config` | 14 | 12 | 2 | Orphaned config rows (`HCV`, `MAL`) left over from schemes since renamed to `HCV RDT`/`mRDT` in `scheme_list` |
| `shipment_participant_map` | 81,590 | 81,512 | 78 | Same dangling-reference pattern (not individually row-level verified, but consistent with the above) |

**Decision:** keep migrating these as they are. No source-side cleanup and no
promotion-SQL relaxation. The affected rows remain fully preserved —
losslessly, byte-for-byte — in `legacy_record_archive` per the
data-preservation contract in `README.md`; they simply have no valid typed
target to link to, because the legacy data itself never had one. This is
reconciliation working as designed, not a defect.

Practical implication: a batch run against data with gaps like these will
report `migration_batch.status = FAILED` (or `COMPLETED_WITH_ERRORS`) under
the strict acceptance rule in `README.md` ("Only a batch whose status is
COMPLETED and error count is zero is eligible for cutover"). That rule is
about production cutover readiness, not about whether the archive pass
succeeded — the archive-only pass is unaffected by any of this and is safe to
treat as authoritative on its own.

## Finding the affected rows again

```sql
-- enrollments with no scheme
SELECT * FROM legacy_record_archive
WHERE source_table = 'enrollments'
  AND (payload::jsonb ->> 'scheme_id') IS NULL OR (payload::jsonb ->> 'scheme_id') = '';
```

```sql
-- scheme_config rows with no matching scheme
SELECT a.*
FROM legacy_record_archive a
LEFT JOIN scheme s ON lower(s.legacy_source_id) = lower(a.payload::jsonb ->> 'scheme_config_name')
WHERE a.source_table = 'scheme_config' AND s.id IS NULL;
```

For `participant_manager_map` / `shipment_participant_map`, the dangling
references live in the legacy MySQL database itself (compare
`dm_id`/`participant_id` against `data_manager`/`participant` directly on the
source), since the archive payload alone doesn't distinguish "orphaned" from
"not yet promoted."

## File migration

Also run end to end, against the legacy server's real file store
(`eptsrv:/var/www/ept`), not just the database:

- `public/uploads` (40M / 129 files), `public/files` (8.3M / 11 files),
  `public/temporary` (4.8M / 5 files), and `downloads/reports` (12G / 32,009
  files) were pulled via `rsync` into a local staging root and used as
  `EPT_LEGACY_FILES_ROOT`.
- Result: all 32,154 files migrated, 0 checksum mismatches, 0 failures.

Note: `public/temporary` holds transient, timestamped scratch exports (not
canonical data) — it was included at the operator's request, but is not
something a future re-run strictly needs to include.
