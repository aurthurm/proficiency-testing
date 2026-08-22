WITH source_users AS (
    SELECT
        a.payload::jsonb ->> 'admin_id' AS source_id,
        'legacy-admin-' || (a.payload::jsonb ->> 'admin_id') AS login,
        NULLIF(lower(trim(a.payload::jsonb ->> 'primary_email')), '') AS requested_email,
        left(a.payload::jsonb ->> 'first_name', 50) AS first_name,
        left(a.payload::jsonb ->> 'last_name', 50) AS last_name,
        a.payload::jsonb ->> 'password' AS password_hash,
        lower(COALESCE(a.payload::jsonb ->> 'status', 'inactive')) = 'active' AS is_active,
        row_number() OVER (
            PARTITION BY lower(trim(COALESCE(a.payload::jsonb ->> 'primary_email', '')))
            ORDER BY (a.payload::jsonb ->> 'admin_id')::bigint
        ) AS email_ordinal
    FROM legacy_record_archive a
    WHERE a.source_table = 'system_admin' AND a.last_seen_batch_id = :batchId
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
        is_active AND has_compatible_password,
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
), authorities AS (
    INSERT INTO jhi_user_authority (user_id, authority_name)
    SELECT p.id, role.name
    FROM promoted p CROSS JOIN (VALUES ('ROLE_USER'), ('ROLE_ADMIN')) role(name)
    ON CONFLICT DO NOTHING
)
INSERT INTO migration_id_map
    (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'system_admin', c.source_id, 'jhi_user', p.id, :batchId, CURRENT_TIMESTAMP
FROM promoted p JOIN candidates c ON c.login = p.login
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
