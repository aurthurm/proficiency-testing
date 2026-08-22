WITH promoted AS (
    INSERT INTO shipment_participant_map (
        id, response_status, shipment_receipt_date, shipment_test_date, shipment_test_report_date,
        is_excluded, is_response_late, is_pt_test_not_performed, pt_test_not_performed_comments,
        supervisor_approved, participant_supervisor, user_comment, shipment_score, documentation_score,
        final_result, failure_reason, evaluation_comment, is_followup, manual_override, qc_status,
        qc_date, qc_done_by, synced_to_mobile, synced_on, mode_of_receipt_id,
        not_tested_reason_id, shipment_id, participant_id, legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        CASE lower(COALESCE(r.payload::jsonb ->> 'response_status', 'noresponse'))
            WHEN 'submitted' THEN 'SUBMITTED'
            WHEN 'responded' THEN 'SUBMITTED'
            WHEN 'late' THEN 'LATE'
            WHEN 'nottested' THEN 'NOT_TESTED'
            WHEN 'not_tested' THEN 'NOT_TESTED'
            WHEN 'inprogress' THEN 'IN_PROGRESS'
            WHEN 'in_progress' THEN 'IN_PROGRESS'
            WHEN 'noresponse' THEN 'NO_RESPONSE'
            ELSE 'NOT_STARTED'
        END,
        CASE WHEN COALESCE(r.payload::jsonb ->> 'shipment_receipt_date', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}$'
            THEN (r.payload::jsonb ->> 'shipment_receipt_date')::date END,
        CASE WHEN COALESCE(r.payload::jsonb ->> 'shipment_test_date', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}$'
            THEN (r.payload::jsonb ->> 'shipment_test_date')::date END,
        CASE WHEN COALESCE(r.payload::jsonb ->> 'shipment_test_report_date', '') ~ '^[1-9][0-9]{3}-' THEN (r.payload::jsonb ->> 'shipment_test_report_date')::timestamp END,
        lower(COALESCE(r.payload::jsonb ->> 'is_excluded', 'no')) IN ('yes', '1', 'true', 'on'),
        lower(COALESCE(r.payload::jsonb ->> 'is_response_late', 'no')) IN ('yes', '1', 'true', 'on'),
        lower(COALESCE(r.payload::jsonb ->> 'is_pt_test_not_performed', 'no')) IN ('yes', '1', 'true', 'on'),
        r.payload::jsonb ->> 'pt_test_not_performed_comments',
        lower(COALESCE(r.payload::jsonb ->> 'supervisor_approval', 'no')) IN ('yes', '1', 'true', 'approved', 'on'),
        r.payload::jsonb ->> 'participant_supervisor',
        r.payload::jsonb ->> 'user_comment',
        NULLIF(r.payload::jsonb ->> 'shipment_score', '')::double precision,
        NULLIF(r.payload::jsonb ->> 'documentation_score', '')::double precision,
        CASE COALESCE(NULLIF(r.payload::jsonb ->> 'final_result', ''), '0')
            WHEN '1' THEN 'PASS'
            WHEN '2' THEN 'FAIL'
            WHEN '3' THEN 'EXCLUDED'
            WHEN '4' THEN 'NOT_EVALUATED'
            ELSE 'NOT_EVALUATED'
        END,
        r.payload::jsonb ->> 'failure_reason',
        r.payload::jsonb ->> 'optional_eval_comment',
        lower(COALESCE(r.payload::jsonb ->> 'is_followup', 'no')) IN ('yes', '1', 'true', 'on'),
        lower(COALESCE(r.payload::jsonb ->> 'manual_override', 'no')) IN ('yes', '1', 'true', 'on'),
        CASE WHEN lower(COALESCE(r.payload::jsonb ->> 'qc_done', 'no')) IN ('yes', '1', 'true', 'on') THEN 'PASSED' ELSE 'PENDING' END,
        CASE WHEN COALESCE(r.payload::jsonb ->> 'qc_date', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}$'
            THEN (r.payload::jsonb ->> 'qc_date')::date END,
        r.payload::jsonb ->> 'qc_done_by',
        lower(COALESCE(r.payload::jsonb ->> 'synced', 'no')) IN ('yes', '1', 'true', 'on'),
        CASE WHEN COALESCE(r.payload::jsonb ->> 'synced_on', '') ~ '^[1-9][0-9]{3}-' THEN (r.payload::jsonb ->> 'synced_on')::timestamp END,
        m.id,
        n.id,
        s.id,
        p.id,
        r.payload::jsonb ->> 'map_id',
        r.payload
    FROM legacy_record_archive r
    JOIN shipment s ON s.legacy_source_id = r.payload::jsonb ->> 'shipment_id'
    JOIN participant p ON p.legacy_source_id = r.payload::jsonb ->> 'participant_id'
    LEFT JOIN mode_of_receipt m ON m.legacy_source_id = r.payload::jsonb ->> 'mode_id'
    LEFT JOIN not_tested_reason n ON n.legacy_source_id = r.payload::jsonb ->> 'pt_not_tested_reason'
    WHERE r.source_table = 'shipment_participant_map' AND r.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        response_status = EXCLUDED.response_status,
        shipment_receipt_date = EXCLUDED.shipment_receipt_date,
        shipment_test_date = EXCLUDED.shipment_test_date,
        shipment_test_report_date = EXCLUDED.shipment_test_report_date,
        is_excluded = EXCLUDED.is_excluded,
        is_response_late = EXCLUDED.is_response_late,
        is_pt_test_not_performed = EXCLUDED.is_pt_test_not_performed,
        pt_test_not_performed_comments = EXCLUDED.pt_test_not_performed_comments,
        supervisor_approved = EXCLUDED.supervisor_approved,
        participant_supervisor = EXCLUDED.participant_supervisor,
        user_comment = EXCLUDED.user_comment,
        shipment_score = EXCLUDED.shipment_score,
        documentation_score = EXCLUDED.documentation_score,
        final_result = EXCLUDED.final_result,
        failure_reason = EXCLUDED.failure_reason,
        evaluation_comment = EXCLUDED.evaluation_comment,
        is_followup = EXCLUDED.is_followup,
        manual_override = EXCLUDED.manual_override,
        qc_status = EXCLUDED.qc_status,
        qc_date = EXCLUDED.qc_date,
        qc_done_by = EXCLUDED.qc_done_by,
        synced_to_mobile = EXCLUDED.synced_to_mobile,
        synced_on = EXCLUDED.synced_on,
        mode_of_receipt_id = EXCLUDED.mode_of_receipt_id,
        not_tested_reason_id = EXCLUDED.not_tested_reason_id,
        shipment_id = EXCLUDED.shipment_id,
        participant_id = EXCLUDED.participant_id,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'shipment_participant_map', legacy_source_id, 'shipment_participant_map', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET target_id = EXCLUDED.target_id, migration_batch_id = EXCLUDED.migration_batch_id, mapped_at = EXCLUDED.mapped_at
