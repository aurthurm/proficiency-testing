WITH scheme_tables AS (
    SELECT
        lower(a.payload::jsonb ->> 'scheme_id') AS scheme_code,
        a.payload::jsonb ->> 'response_table' AS response_table,
        a.payload::jsonb ->> 'reference_result_table' AS reference_table
    FROM legacy_record_archive a
    WHERE a.source_table = 'scheme_list' AND a.last_seen_batch_id = :batchId
), sources AS (
    SELECT a.*, s.scheme_code, 'RESPONSE' AS category
    FROM legacy_record_archive a
    JOIN scheme_tables s ON s.response_table = a.source_table
    WHERE a.last_seen_batch_id = :batchId
    UNION ALL
    SELECT a.*, s.scheme_code, 'REFERENCE' AS category
    FROM legacy_record_archive a
    JOIN scheme_tables s ON s.reference_table = a.source_table
    WHERE a.last_seen_batch_id = :batchId
), promoted AS (
    INSERT INTO legacy_scheme_result (
        id, source_table, source_row_key, result_category, scheme_code,
        legacy_payload, shipment_participant_map_id, shipment_id
    )
    SELECT
        nextval('sequence_generator'),
        source.source_table,
        source.source_primary_key_hash,
        source.category,
        source.scheme_code,
        source.payload,
        map.id,
        COALESCE(shipment.id, map.shipment_id)
    FROM sources source
    LEFT JOIN shipment_participant_map map ON map.legacy_source_id = COALESCE(
        source.payload::jsonb ->> 'shipment_map_id',
        source.payload::jsonb ->> 'map_id'
    )
    LEFT JOIN shipment shipment ON shipment.legacy_source_id = source.payload::jsonb ->> 'shipment_id'
    ON CONFLICT (source_table, source_row_key) DO UPDATE SET
        result_category = EXCLUDED.result_category,
        scheme_code = EXCLUDED.scheme_code,
        legacy_payload = EXCLUDED.legacy_payload,
        shipment_participant_map_id = EXCLUDED.shipment_participant_map_id,
        shipment_id = EXCLUDED.shipment_id
    RETURNING id, source_table, source_row_key
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT source_table, source_row_key, 'legacy_scheme_result', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
