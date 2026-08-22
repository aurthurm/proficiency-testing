WITH promoted AS (
    INSERT INTO shipment (
        id, code, shipment_date, response_deadline, responses_open, auto_close_at_deadline,
        allow_editing_response, issuing_authority, coordinator_name, coordinator_email,
        coordinator_phone, number_of_samples, max_score, status, attributes,
        reports_generated_at, finalized_at, distribution_id, scheme_id, legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        r.payload::jsonb ->> 'shipment_code',
        COALESCE(NULLIF(r.payload::jsonb ->> 'shipment_date', '')::date, d.distribution_date),
        COALESCE(
            NULLIF(r.payload::jsonb ->> 'response_deadline', '')::timestamp,
            COALESCE(NULLIF(r.payload::jsonb ->> 'shipment_date', '')::date, d.distribution_date)::timestamp + interval '30 days'
        ),
        lower(COALESCE(r.payload::jsonb ->> 'response_switch', 'off')) IN ('on', 'yes', '1', 'true'),
        lower(COALESCE(r.payload::jsonb ->> 'auto_close_at_deadline', 'yes')) IN ('on', 'yes', '1', 'true'),
        lower(COALESCE(r.payload::jsonb ->> 'allow_editing_response', 'yes')) IN ('on', 'yes', '1', 'true'),
        r.payload::jsonb ->> 'issuing_authority',
        r.payload::jsonb ->> 'pt_co_ordinator_name',
        r.payload::jsonb ->> 'pt_co_ordinator_email',
        r.payload::jsonb ->> 'pt_co_ordinator_phone',
        NULLIF(r.payload::jsonb ->> 'number_of_samples', '')::integer,
        NULLIF(r.payload::jsonb ->> 'max_score', '')::integer,
        CASE lower(COALESCE(r.payload::jsonb ->> 'status', 'draft'))
            WHEN 'shipped' THEN 'SHIPPED'
            WHEN 'ready' THEN 'READY'
            WHEN 'queued' THEN 'QUEUED'
            WHEN 'response' THEN 'RESPONSES_OPEN'
            WHEN 'responses_open' THEN 'RESPONSES_OPEN'
            WHEN 'closed' THEN 'RESPONSES_CLOSED'
            WHEN 'evaluating' THEN 'EVALUATING'
            WHEN 'evaluated' THEN 'EVALUATED'
            WHEN 'reports_generated' THEN 'REPORTS_GENERATED'
            WHEN 'finalized' THEN 'FINALIZED'
            ELSE 'DRAFT'
        END,
        r.payload::jsonb ->> 'shipment_attributes',
        CASE WHEN COALESCE(r.payload::jsonb ->> 'reports_generated_at', '') ~ '^[1-9][0-9]{3}-' THEN (r.payload::jsonb ->> 'reports_generated_at')::timestamp END,
        CASE WHEN COALESCE(r.payload::jsonb ->> 'finalized_at', '') ~ '^[1-9][0-9]{3}-' THEN (r.payload::jsonb ->> 'finalized_at')::timestamp END,
        d.id,
        s.id,
        r.payload::jsonb ->> 'shipment_id',
        r.payload
    FROM legacy_record_archive r
    JOIN distribution d ON d.legacy_source_id = r.payload::jsonb ->> 'distribution_id'
    JOIN scheme s ON lower(s.legacy_source_id) = lower(r.payload::jsonb ->> 'scheme_type')
    WHERE r.source_table = 'shipment' AND r.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        code = EXCLUDED.code,
        shipment_date = EXCLUDED.shipment_date,
        response_deadline = EXCLUDED.response_deadline,
        responses_open = EXCLUDED.responses_open,
        auto_close_at_deadline = EXCLUDED.auto_close_at_deadline,
        allow_editing_response = EXCLUDED.allow_editing_response,
        issuing_authority = EXCLUDED.issuing_authority,
        coordinator_name = EXCLUDED.coordinator_name,
        coordinator_email = EXCLUDED.coordinator_email,
        coordinator_phone = EXCLUDED.coordinator_phone,
        number_of_samples = EXCLUDED.number_of_samples,
        max_score = EXCLUDED.max_score,
        status = EXCLUDED.status,
        attributes = EXCLUDED.attributes,
        reports_generated_at = EXCLUDED.reports_generated_at,
        finalized_at = EXCLUDED.finalized_at,
        distribution_id = EXCLUDED.distribution_id,
        scheme_id = EXCLUDED.scheme_id,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'shipment', legacy_source_id, 'shipment', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET target_id = EXCLUDED.target_id, migration_batch_id = EXCLUDED.migration_batch_id, mapped_at = EXCLUDED.mapped_at
