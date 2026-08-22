WITH source_users AS (
    SELECT
        d.payload::jsonb ->> 'dm_id' AS source_id,
        'legacy-dm-' || (d.payload::jsonb ->> 'dm_id') AS login,
        NULLIF(lower(trim(d.payload::jsonb ->> 'primary_email')), '') AS requested_email,
        left(d.payload::jsonb ->> 'first_name', 50) AS first_name,
        left(d.payload::jsonb ->> 'last_name', 50) AS last_name,
        d.payload::jsonb ->> 'password' AS password_hash,
        lower(COALESCE(d.payload::jsonb ->> 'status', 'inactive')) = 'active' AS is_active,
        lower(COALESCE(d.payload::jsonb ->> 'login_ban', 'no')) IN ('yes', '1', 'true', 'on') AS is_banned,
        row_number() OVER (
            PARTITION BY lower(trim(COALESCE(d.payload::jsonb ->> 'primary_email', '')))
            ORDER BY (d.payload::jsonb ->> 'dm_id')::bigint
        ) AS email_ordinal
    FROM legacy_record_archive d
    WHERE d.source_table = 'data_manager' AND d.last_seen_batch_id = :batchId
), candidates AS (
    SELECT
        s.*,
        CASE
            WHEN s.requested_email IS NOT NULL
                AND length(s.requested_email) <= 191
                AND s.email_ordinal = 1
                AND NOT EXISTS (
                    SELECT 1 FROM jhi_user existing
                    WHERE lower(existing.email) = s.requested_email AND existing.login <> s.login
                )
            THEN s.requested_email
        END AS email,
        COALESCE(s.password_hash ~ '^[$]2[aby][$][0-9]{2}[$].{53}$', false) AS has_compatible_password
    FROM source_users s
), promoted AS (
    INSERT INTO jhi_user (
        id, login, password_hash, first_name, last_name, email, activated, lang_key,
        created_by, created_date, last_modified_by, last_modified_date
    )
    SELECT
        nextval('sequence_generator'),
        login,
        CASE WHEN has_compatible_password THEN password_hash
            ELSE '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K' END,
        first_name,
        last_name,
        email,
        is_active AND NOT is_banned AND has_compatible_password,
        'en',
        'legacy-migration',
        CURRENT_TIMESTAMP,
        'legacy-migration',
        CURRENT_TIMESTAMP
    FROM candidates
    ON CONFLICT (login) DO UPDATE SET
        password_hash = EXCLUDED.password_hash,
        first_name = EXCLUDED.first_name,
        last_name = EXCLUDED.last_name,
        email = EXCLUDED.email,
        activated = EXCLUDED.activated,
        last_modified_by = EXCLUDED.last_modified_by,
        last_modified_date = EXCLUDED.last_modified_date
    RETURNING id, login
), linked AS (
    UPDATE data_manager d SET
        user_id = p.id,
        force_password_reset = d.force_password_reset OR NOT c.has_compatible_password
    FROM promoted p
    JOIN candidates c ON c.login = p.login
    WHERE d.legacy_source_id = c.source_id
    RETURNING p.id
), authorities AS (
    INSERT INTO jhi_user_authority (user_id, authority_name)
    SELECT id, 'ROLE_USER' FROM linked
    ON CONFLICT DO NOTHING
)
INSERT INTO migration_id_map
    (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'data_manager', c.source_id, 'jhi_user', p.id, :batchId, CURRENT_TIMESTAMP
FROM promoted p JOIN candidates c ON c.login = p.login
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
