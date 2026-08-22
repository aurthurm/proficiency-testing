INSERT INTO rel_data_manager__participants (data_manager_id, participants_id)
SELECT DISTINCT d.id, p.id
FROM legacy_record_archive a
JOIN data_manager d ON d.legacy_source_id = a.payload::jsonb ->> 'dm_id'
JOIN participant p ON p.legacy_source_id = a.payload::jsonb ->> 'participant_id'
WHERE a.source_table = 'participant_manager_map' AND a.last_seen_batch_id = :batchId
ON CONFLICT DO NOTHING
