WITH promoted AS (
    INSERT INTO not_tested_reason (id, reason, status, legacy_source_id, legacy_payload)
    SELECT
        nextval('sequence_generator'),
        COALESCE(NULLIF(r.payload::jsonb ->> 'ntr_reason', ''), 'Legacy reason ' || (r.payload::jsonb ->> 'ntr_id')),
        CASE WHEN lower(COALESCE(r.payload::jsonb ->> 'ntr_status', 'active')) = 'active' THEN 'ACTIVE' ELSE 'INACTIVE' END,
        r.payload::jsonb ->> 'ntr_id',
        r.payload
    FROM legacy_record_archive r
    WHERE r.source_table = 'r_response_not_tested_reasons' AND r.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET reason = EXCLUDED.reason, status = EXCLUDED.status, legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'r_response_not_tested_reasons', legacy_source_id, 'not_tested_reason', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET target_id = EXCLUDED.target_id, migration_batch_id = EXCLUDED.migration_batch_id, mapped_at = EXCLUDED.mapped_at
