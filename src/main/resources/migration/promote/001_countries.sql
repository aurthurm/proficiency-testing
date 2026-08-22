WITH promoted AS (
    INSERT INTO country (id, iso_code, name, region, legacy_source_id, legacy_payload)
    SELECT
        nextval('sequence_generator'),
        COALESCE(NULLIF(r.payload::jsonb ->> 'iso3', ''), NULLIF(r.payload::jsonb ->> 'iso2', ''), 'ZZ'),
        COALESCE(NULLIF(r.payload::jsonb ->> 'iso_name', ''), 'Unknown country ' || (r.payload::jsonb ->> 'id')),
        NULL,
        r.payload::jsonb ->> 'id',
        r.payload
    FROM legacy_record_archive r
    WHERE r.source_table = 'countries' AND r.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        iso_code = EXCLUDED.iso_code,
        name = EXCLUDED.name,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map
    (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'countries', legacy_source_id, 'country', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
