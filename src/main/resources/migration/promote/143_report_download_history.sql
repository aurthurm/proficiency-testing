WITH promoted AS (
    INSERT INTO legacy_report_download (
        id, report_type, downloaded_on, downloaded_by, shipment_id,
        legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        a.payload::jsonb ->> 'report_type',
        CASE WHEN COALESCE(a.payload::jsonb ->> 'downloaded_on', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}'
            THEN (a.payload::jsonb ->> 'downloaded_on')::timestamp ELSE CURRENT_TIMESTAMP END,
        COALESCE(NULLIF(a.payload::jsonb ->> 'downloaded_by', ''), 'unknown'),
        shipment.id,
        a.payload::jsonb ->> 'id',
        a.payload
    FROM legacy_record_archive a
    LEFT JOIN shipment ON shipment.legacy_source_id = a.payload::jsonb ->> 'shipment_id'
    WHERE a.source_table = 'track_report_downloaded_history' AND a.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        report_type = EXCLUDED.report_type,
        downloaded_on = EXCLUDED.downloaded_on,
        downloaded_by = EXCLUDED.downloaded_by,
        shipment_id = EXCLUDED.shipment_id,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'track_report_downloaded_history', legacy_source_id, 'legacy_report_download', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
