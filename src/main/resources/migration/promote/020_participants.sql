WITH promoted AS (
    INSERT INTO participant (
        id, unique_identifier, institute_name, department_name, email, additional_email,
        address, shipping_address, city, state, district, zip, region, phone, mobile,
        affiliation, network_tier, site_type, funding_source, testing_volume, pepfar_id,
        latitude, longitude, lab_director_name, lab_director_email, contact_person_name,
        contact_person_email, contact_person_phone, status, country_id, legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        p.payload::jsonb ->> 'unique_identifier',
        COALESCE(NULLIF(p.payload::jsonb ->> 'institute_name', ''), NULLIF(p.payload::jsonb ->> 'lab_name', ''), p.payload::jsonb ->> 'unique_identifier'),
        p.payload::jsonb ->> 'department_name',
        COALESCE(NULLIF(p.payload::jsonb ->> 'email', ''), 'legacy-' || (p.payload::jsonb ->> 'participant_id') || '@invalid.local'),
        p.payload::jsonb ->> 'additional_email',
        p.payload::jsonb ->> 'address',
        p.payload::jsonb ->> 'shipping_address',
        p.payload::jsonb ->> 'city',
        p.payload::jsonb ->> 'state',
        p.payload::jsonb ->> 'district',
        p.payload::jsonb ->> 'zip',
        p.payload::jsonb ->> 'region',
        p.payload::jsonb ->> 'phone',
        p.payload::jsonb ->> 'mobile',
        COALESCE(NULLIF(p.payload::jsonb ->> 'affiliation', ''), 'UNKNOWN'),
        p.payload::jsonb ->> 'network_tier',
        p.payload::jsonb ->> 'site_type',
        p.payload::jsonb ->> 'funding_source',
        CASE WHEN COALESCE(p.payload::jsonb ->> 'testing_volume', '') ~ '^[0-9]+$' THEN (p.payload::jsonb ->> 'testing_volume')::bigint END,
        p.payload::jsonb ->> 'pepfar_id',
        CASE WHEN COALESCE(p.payload::jsonb ->> 'lat', '') ~ '^-?[0-9]+([.][0-9]+)?$' THEN (p.payload::jsonb ->> 'lat')::double precision END,
        CASE WHEN COALESCE(p.payload::jsonb ->> 'long', '') ~ '^-?[0-9]+([.][0-9]+)?$' THEN (p.payload::jsonb ->> 'long')::double precision END,
        p.payload::jsonb ->> 'lab_director_name',
        p.payload::jsonb ->> 'lab_director_email',
        COALESCE(NULLIF(p.payload::jsonb ->> 'contact_person_name', ''), p.payload::jsonb ->> 'contact_name'),
        p.payload::jsonb ->> 'contact_person_email',
        p.payload::jsonb ->> 'contact_person_telephone',
        CASE lower(COALESCE(p.payload::jsonb ->> 'status', 'inactive')) WHEN 'active' THEN 'ACTIVE' WHEN 'pending' THEN 'PENDING' ELSE 'INACTIVE' END,
        c.id,
        p.payload::jsonb ->> 'participant_id',
        p.payload
    FROM legacy_record_archive p
    LEFT JOIN country c ON c.legacy_source_id = p.payload::jsonb ->> 'country'
    WHERE p.source_table = 'participant' AND p.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        unique_identifier = EXCLUDED.unique_identifier,
        institute_name = EXCLUDED.institute_name,
        department_name = EXCLUDED.department_name,
        email = EXCLUDED.email,
        additional_email = EXCLUDED.additional_email,
        address = EXCLUDED.address,
        shipping_address = EXCLUDED.shipping_address,
        city = EXCLUDED.city,
        state = EXCLUDED.state,
        district = EXCLUDED.district,
        zip = EXCLUDED.zip,
        region = EXCLUDED.region,
        phone = EXCLUDED.phone,
        mobile = EXCLUDED.mobile,
        affiliation = EXCLUDED.affiliation,
        network_tier = EXCLUDED.network_tier,
        site_type = EXCLUDED.site_type,
        funding_source = EXCLUDED.funding_source,
        testing_volume = EXCLUDED.testing_volume,
        pepfar_id = EXCLUDED.pepfar_id,
        latitude = EXCLUDED.latitude,
        longitude = EXCLUDED.longitude,
        lab_director_name = EXCLUDED.lab_director_name,
        lab_director_email = EXCLUDED.lab_director_email,
        contact_person_name = EXCLUDED.contact_person_name,
        contact_person_email = EXCLUDED.contact_person_email,
        contact_person_phone = EXCLUDED.contact_person_phone,
        status = EXCLUDED.status,
        country_id = EXCLUDED.country_id,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map
    (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'participant', legacy_source_id, 'participant', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
