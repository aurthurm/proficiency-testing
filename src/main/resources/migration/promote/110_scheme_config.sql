WITH promoted AS (
    INSERT INTO scheme_configuration (
        id, version, effective_date, passing_score, documentation_weight,
        allow_late_response, optional_fields, scoring_rules, is_active,
        scheme_id, legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        1,
        DATE '1970-01-01',
        CASE
            WHEN COALESCE((a.payload::jsonb ->> 'scheme_config_value')::jsonb ->> 'passPercentage', '') ~ '^[0-9]+([.][0-9]+)?$'
            THEN ((a.payload::jsonb ->> 'scheme_config_value')::jsonb ->> 'passPercentage')::double precision
            ELSE 0
        END,
        CASE
            WHEN COALESCE((a.payload::jsonb ->> 'scheme_config_value')::jsonb ->> 'documentationScore', '') ~ '^[0-9]+([.][0-9]+)?$'
            THEN ((a.payload::jsonb ->> 'scheme_config_value')::jsonb ->> 'documentationScore')::double precision
        END,
        true,
        NULL,
        a.payload::jsonb ->> 'scheme_config_value',
        true,
        s.id,
        a.payload::jsonb ->> 'scheme_config_name',
        a.payload
    FROM legacy_record_archive a
    JOIN scheme s ON lower(s.legacy_source_id) = lower(a.payload::jsonb ->> 'scheme_config_name')
    WHERE a.source_table = 'scheme_config' AND a.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        passing_score = EXCLUDED.passing_score,
        documentation_weight = EXCLUDED.documentation_weight,
        scoring_rules = EXCLUDED.scoring_rules,
        scheme_id = EXCLUDED.scheme_id,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'scheme_config', legacy_source_id, 'scheme_configuration', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
