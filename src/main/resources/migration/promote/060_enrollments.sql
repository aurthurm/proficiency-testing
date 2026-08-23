WITH promoted AS (
    INSERT INTO enrollment (id, status, enrolled_on, withdrawn_on, participant_id, scheme_id, legacy_source_id, legacy_payload)
    SELECT
        nextval('sequence_generator'),
        CASE lower(COALESCE(e.payload::jsonb ->> 'status', 'enrolled'))
            WHEN 'suspended' THEN 'SUSPENDED'
            WHEN 'withdrawn' THEN 'WITHDRAWN'
            ELSE 'ENROLLED'
        END,
        COALESCE(
            CASE WHEN substring(COALESCE(e.payload::jsonb ->> 'enrolled_on', '') from 1 for 10) ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}$'
                THEN substring(e.payload::jsonb ->> 'enrolled_on' from 1 for 10)::date END,
            CURRENT_DATE
        ),
        NULL,
        p.id,
        s.id,
        (e.payload::jsonb ->> 'list_name') || ':' || (e.payload::jsonb ->> 'participant_id'),
        e.payload
    FROM legacy_record_archive e
    JOIN participant p ON p.legacy_source_id = e.payload::jsonb ->> 'participant_id'
    JOIN scheme s ON s.legacy_source_id = e.payload::jsonb ->> 'scheme_id'
    WHERE e.source_table = 'enrollments' AND e.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        status = EXCLUDED.status,
        enrolled_on = EXCLUDED.enrolled_on,
        participant_id = EXCLUDED.participant_id,
        scheme_id = EXCLUDED.scheme_id,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'enrollments', legacy_source_id, 'enrollment', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET target_id = EXCLUDED.target_id, migration_batch_id = EXCLUDED.migration_batch_id, mapped_at = EXCLUDED.mapped_at
