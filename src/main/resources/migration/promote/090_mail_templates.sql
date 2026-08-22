WITH promoted AS (
    INSERT INTO mail_template (id, code, subject, html_body, status, legacy_source_id, legacy_payload)
    SELECT
        nextval('sequence_generator'),
        COALESCE(NULLIF(r.payload::jsonb ->> 'mail_purpose', ''), 'legacy-' || (r.payload::jsonb ->> 'mail_temp_id')),
        r.payload::jsonb ->> 'mail_subject',
        concat(COALESCE(r.payload::jsonb ->> 'mail_content', ''), COALESCE(r.payload::jsonb ->> 'mail_footer', '')),
        'PUBLISHED',
        r.payload::jsonb ->> 'mail_temp_id',
        r.payload
    FROM legacy_record_archive r
    WHERE r.source_table = 'mail_template' AND r.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET code = EXCLUDED.code, subject = EXCLUDED.subject, html_body = EXCLUDED.html_body, status = EXCLUDED.status, legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'mail_template', legacy_source_id, 'mail_template', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET target_id = EXCLUDED.target_id, migration_batch_id = EXCLUDED.migration_batch_id, mapped_at = EXCLUDED.mapped_at
