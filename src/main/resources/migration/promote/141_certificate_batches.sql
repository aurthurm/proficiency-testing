WITH promoted AS (
    INSERT INTO certificate_batch (
        id, name, status, excellence_count, participation_count, skipped_count,
        download_url, error_message, approved_by, approved_on, legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        a.payload::jsonb ->> 'batch_name',
        CASE upper(COALESCE(a.payload::jsonb ->> 'status', 'PENDING'))
            WHEN 'PENDING' THEN 'PENDING'
            WHEN 'GENERATING' THEN 'GENERATING'
            WHEN 'GENERATED' THEN 'GENERATED'
            WHEN 'APPROVED' THEN 'APPROVED'
            WHEN 'DISTRIBUTING' THEN 'DISTRIBUTING'
            WHEN 'DISTRIBUTED' THEN 'DISTRIBUTED'
            ELSE 'FAILED'
        END,
        COALESCE(NULLIF(a.payload::jsonb ->> 'excellence_count', '')::integer, 0),
        COALESCE(NULLIF(a.payload::jsonb ->> 'participation_count', '')::integer, 0),
        COALESCE(NULLIF(a.payload::jsonb ->> 'skipped_count', '')::integer, 0),
        a.payload::jsonb ->> 'download_url',
        a.payload::jsonb ->> 'error_message',
        a.payload::jsonb ->> 'approved_by',
        CASE WHEN COALESCE(a.payload::jsonb ->> 'approved_on', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}'
            THEN (a.payload::jsonb ->> 'approved_on')::timestamp END,
        a.payload::jsonb ->> 'batch_id',
        a.payload
    FROM legacy_record_archive a
    WHERE a.source_table = 'certificate_batches' AND a.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        name = EXCLUDED.name,
        status = EXCLUDED.status,
        excellence_count = EXCLUDED.excellence_count,
        participation_count = EXCLUDED.participation_count,
        skipped_count = EXCLUDED.skipped_count,
        download_url = EXCLUDED.download_url,
        error_message = EXCLUDED.error_message,
        approved_by = EXCLUDED.approved_by,
        approved_on = EXCLUDED.approved_on,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
), shipment_links AS (
    INSERT INTO rel_certificate_batch__shipments (certificate_batch_id, shipments_id)
    SELECT DISTINCT batch.id, shipment.id
    FROM legacy_record_archive source
    JOIN certificate_batch batch ON batch.legacy_source_id = source.payload::jsonb ->> 'batch_id'
    CROSS JOIN LATERAL regexp_split_to_table(
        regexp_replace(COALESCE(source.payload::jsonb ->> 'shipment_ids', ''), '[^0-9,]', '', 'g'),
        ','
    ) shipment_source_id
    JOIN shipment shipment ON shipment.legacy_source_id = shipment_source_id
    WHERE source.source_table = 'certificate_batches'
      AND source.last_seen_batch_id = :batchId
      AND shipment_source_id <> ''
    ON CONFLICT DO NOTHING
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'certificate_batches', legacy_source_id, 'certificate_batch', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
