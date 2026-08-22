WITH promoted AS (
    INSERT INTO global_configuration (id, config_key, config_value, description)
    SELECT
        nextval('sequence_generator'),
        a.payload::jsonb ->> 'config',
        a.payload::jsonb ->> 'value',
        a.payload::jsonb ->> 'display_name'
    FROM legacy_record_archive a
    WHERE a.source_table = 'system_config' AND a.last_seen_batch_id = :batchId
    ON CONFLICT (config_key) DO UPDATE SET
        config_value = EXCLUDED.config_value,
        description = EXCLUDED.description
    RETURNING id, config_key
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'system_config', config_key, 'global_configuration', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
