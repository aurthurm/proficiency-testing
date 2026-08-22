WITH promoted AS (
    INSERT INTO mode_of_receipt (id, name, status, legacy_source_id, legacy_payload)
    SELECT nextval('sequence_generator'), COALESCE(NULLIF(r.payload::jsonb ->> 'mode_name', ''), 'Legacy mode ' || (r.payload::jsonb ->> 'mode_id')), 'ACTIVE', r.payload::jsonb ->> 'mode_id', r.payload
    FROM legacy_record_archive r
    WHERE r.source_table = 'r_modes_of_receipt' AND r.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET name = EXCLUDED.name, status = EXCLUDED.status, legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'r_modes_of_receipt', legacy_source_id, 'mode_of_receipt', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET target_id = EXCLUDED.target_id, migration_batch_id = EXCLUDED.migration_batch_id, mapped_at = EXCLUDED.mapped_at
