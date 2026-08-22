WITH promoted AS (
    INSERT INTO test_kit (id, name, manufacturer, status, scheme_id, legacy_source_id, legacy_payload)
    SELECT
        nextval('sequence_generator'),
        COALESCE(NULLIF(a.payload::jsonb ->> 'TestKit_Name', ''), a.payload::jsonb ->> 'TestKitName_ID'),
        a.payload::jsonb ->> 'TestKit_Manufacturer',
        CASE
            WHEN lower(COALESCE(a.payload::jsonb ->> 'testkit_status', '')) = 'inactive' THEN 'INACTIVE'
            WHEN COALESCE(a.payload::jsonb ->> 'Approval', '0') IN ('1', 'true') THEN 'ACTIVE'
            ELSE 'INACTIVE'
        END,
        preferred_scheme.id,
        a.payload::jsonb ->> 'TestKitName_ID',
        a.payload
    FROM legacy_record_archive a
    LEFT JOIN LATERAL (
        SELECT s.id
        FROM legacy_record_archive map
        JOIN scheme s ON lower(s.legacy_source_id) = lower(map.payload::jsonb ->> 'scheme_id')
        WHERE map.source_table = 'scheme_testkit_map'
          AND map.last_seen_batch_id = :batchId
          AND map.payload::jsonb ->> 'testkit_id' = a.payload::jsonb ->> 'TestKitName_ID'
        ORDER BY s.legacy_source_id
        LIMIT 1
    ) preferred_scheme ON true
    WHERE a.source_table = 'r_testkitnames' AND a.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        name = EXCLUDED.name,
        manufacturer = EXCLUDED.manufacturer,
        status = EXCLUDED.status,
        scheme_id = EXCLUDED.scheme_id,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
), scheme_links AS (
    INSERT INTO rel_test_kit__schemes (test_kit_id, scheme_id)
    SELECT DISTINCT t.id, s.id
    FROM legacy_record_archive map
    JOIN test_kit t ON t.legacy_source_id = map.payload::jsonb ->> 'testkit_id'
    JOIN scheme s ON lower(s.legacy_source_id) = lower(map.payload::jsonb ->> 'scheme_id')
    WHERE map.source_table = 'scheme_testkit_map' AND map.last_seen_batch_id = :batchId
    ON CONFLICT DO NOTHING
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'r_testkitnames', legacy_source_id, 'test_kit', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
