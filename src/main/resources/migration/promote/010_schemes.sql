WITH promoted AS (
    INSERT INTO scheme (id, code, name, scheme_type, modality, status, legacy_source_id, legacy_payload)
    SELECT
        nextval('sequence_generator'),
        upper(r.payload::jsonb ->> 'scheme_id'),
        COALESCE(NULLIF(r.payload::jsonb ->> 'scheme_name', ''), upper(r.payload::jsonb ->> 'scheme_id')),
        CASE upper(r.payload::jsonb ->> 'scheme_id')
            WHEN 'DTS' THEN 'DTS'
            WHEN 'VL' THEN 'VL'
            WHEN 'EID' THEN 'EID'
            WHEN 'COVID19' THEN 'COVID19'
            WHEN 'COVID-19' THEN 'COVID19'
            WHEN 'TB' THEN 'TB'
            WHEN 'RECENCY' THEN 'RECENCY'
            ELSE 'GENERIC'
        END,
        CASE
            WHEN upper(r.payload::jsonb ->> 'scheme_id') = 'VL' THEN 'QUANTITATIVE'
            ELSE 'QUALITATIVE'
        END,
        CASE WHEN lower(COALESCE(r.payload::jsonb ->> 'status', 'active')) = 'active' THEN 'ACTIVE' ELSE 'INACTIVE' END,
        r.payload::jsonb ->> 'scheme_id',
        r.payload
    FROM legacy_record_archive r
    WHERE r.source_table = 'scheme_list' AND r.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        code = EXCLUDED.code,
        name = EXCLUDED.name,
        scheme_type = EXCLUDED.scheme_type,
        modality = EXCLUDED.modality,
        status = EXCLUDED.status,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map
    (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'scheme_list', legacy_source_id, 'scheme', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
