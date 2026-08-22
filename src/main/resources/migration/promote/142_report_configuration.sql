WITH source_config AS (
    SELECT a.*
    FROM legacy_record_archive a
    WHERE a.source_table = 'report_config' AND a.last_seen_batch_id = :batchId
), promoted AS (
    INSERT INTO report_configuration (
        id, report_header, logo, logo_right, layout, format, top_margin,
        institute_address_position, scheme_id, legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        max(payload::jsonb ->> 'value') FILTER (WHERE payload::jsonb ->> 'name' = 'report-header'),
        max(payload::jsonb ->> 'value') FILTER (WHERE payload::jsonb ->> 'name' = 'logo'),
        max(payload::jsonb ->> 'value') FILTER (WHERE payload::jsonb ->> 'name' = 'logo-right'),
        max(payload::jsonb ->> 'value') FILTER (WHERE payload::jsonb ->> 'name' = 'report-layout'),
        max(payload::jsonb ->> 'value') FILTER (WHERE payload::jsonb ->> 'name' = 'report-format'),
        CASE WHEN COALESCE(max(payload::jsonb ->> 'value') FILTER (WHERE payload::jsonb ->> 'name' = 'template-top-margin'), '') ~ '^[0-9]+$'
            THEN (max(payload::jsonb ->> 'value') FILTER (WHERE payload::jsonb ->> 'name' = 'template-top-margin'))::integer END,
        max(payload::jsonb ->> 'value') FILTER (WHERE payload::jsonb ->> 'name' = 'institute-address-postition'),
        NULL,
        'global',
        jsonb_object_agg(payload::jsonb ->> 'name', payload::jsonb -> 'value')::text
    FROM source_config
    HAVING COUNT(*) > 0
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        report_header = EXCLUDED.report_header,
        logo = EXCLUDED.logo,
        logo_right = EXCLUDED.logo_right,
        layout = EXCLUDED.layout,
        format = EXCLUDED.format,
        top_margin = EXCLUDED.top_margin,
        institute_address_position = EXCLUDED.institute_address_position,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'report_config', source.payload::jsonb ->> 'name', 'report_configuration', promoted.id, :batchId, CURRENT_TIMESTAMP
FROM source_config source CROSS JOIN promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
