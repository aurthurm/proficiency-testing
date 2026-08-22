INSERT INTO rel_data_manager__ptcc_countries (data_manager_id, country_id, state, district, mapped_on)
SELECT DISTINCT ON (d.id, c.id)
    d.id,
    c.id,
    a.payload::jsonb ->> 'state',
    a.payload::jsonb ->> 'district',
    CASE WHEN COALESCE(a.payload::jsonb ->> 'mapped_on', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}'
        THEN (a.payload::jsonb ->> 'mapped_on')::timestamp END
FROM legacy_record_archive a
JOIN data_manager d ON d.legacy_source_id = a.payload::jsonb ->> 'ptcc_id'
JOIN country c ON c.legacy_source_id = a.payload::jsonb ->> 'country_id'
WHERE a.source_table = 'ptcc_countries_map' AND a.last_seen_batch_id = :batchId
ORDER BY d.id, c.id, a.source_primary_key_hash
ON CONFLICT (data_manager_id, country_id) DO UPDATE SET
    state = EXCLUDED.state,
    district = EXCLUDED.district,
    mapped_on = EXCLUDED.mapped_on
