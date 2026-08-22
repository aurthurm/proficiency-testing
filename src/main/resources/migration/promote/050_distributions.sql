WITH promoted AS (
    INSERT INTO distribution (id, code, distribution_date, status, legacy_source_id, legacy_payload)
    SELECT
        nextval('sequence_generator'),
        r.payload::jsonb ->> 'distribution_code',
        CASE WHEN COALESCE(r.payload::jsonb ->> 'distribution_date', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}$'
            THEN (r.payload::jsonb ->> 'distribution_date')::date END,
        CASE lower(COALESCE(r.payload::jsonb ->> 'status', 'draft'))
            WHEN 'open' THEN 'OPEN'
            WHEN 'shipped' THEN 'SHIPPED'
            WHEN 'closed' THEN 'CLOSED'
            ELSE 'DRAFT'
        END,
        r.payload::jsonb ->> 'distribution_id',
        r.payload
    FROM legacy_record_archive r
    WHERE r.source_table = 'distributions' AND r.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        code = EXCLUDED.code,
        distribution_date = EXCLUDED.distribution_date,
        status = EXCLUDED.status,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'distributions', legacy_source_id, 'distribution', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET target_id = EXCLUDED.target_id, migration_batch_id = EXCLUDED.migration_batch_id, mapped_at = EXCLUDED.mapped_at
