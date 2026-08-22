WITH source_templates AS (
    SELECT
        a.*,
        a.payload::jsonb ->> 'ct_id' AS source_id,
        a.payload::jsonb ->> 'scheme_type' AS scheme_code
    FROM legacy_record_archive a
    WHERE a.source_table = 'certificate_templates' AND a.last_seen_batch_id = :batchId
), expanded AS (
    SELECT source.*, 'PARTICIPATION' AS certificate_type,
        source.payload::jsonb ->> 'participation_certificate' AS file_ref
    FROM source_templates source
    UNION ALL
    SELECT source.*, 'EXCELLENCE' AS certificate_type,
        source.payload::jsonb ->> 'excellence_certificate' AS file_ref
    FROM source_templates source
), promoted AS (
    INSERT INTO certificate_template (
        id, certificate_type, file_ref, detected_fields, legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        certificate_type,
        file_ref,
        NULL,
        source_id || ':' || certificate_type,
        payload
    FROM expanded
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        file_ref = EXCLUDED.file_ref,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
), linked AS (
    UPDATE scheme s SET certificate_template_id = p.id
    FROM promoted p
    JOIN source_templates source
      ON p.legacy_source_id = source.source_id || ':PARTICIPATION'
    WHERE lower(s.legacy_source_id) = lower(source.scheme_code)
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT
    'certificate_templates',
    legacy_source_id,
    'certificate_template',
    id,
    :batchId,
    CURRENT_TIMESTAMP
FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
