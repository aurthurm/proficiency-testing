WITH promoted AS (
    INSERT INTO data_manager (
        id, first_name, last_name, institute, primary_email, secondary_email, phone, mobile,
        language, role, status, qc_access, view_only_access, enable_test_response_date,
        enable_mode_of_receipt, force_profile_check, country_id, legacy_password_hash,
        force_password_reset, last_login, login_ban, legacy_auth_token, api_token_generated_at,
        legacy_source_id, legacy_payload
    )
    SELECT
        nextval('sequence_generator'),
        d.payload::jsonb ->> 'first_name',
        d.payload::jsonb ->> 'last_name',
        d.payload::jsonb ->> 'institute',
        d.payload::jsonb ->> 'primary_email',
        d.payload::jsonb ->> 'secondary_email',
        d.payload::jsonb ->> 'phone',
        d.payload::jsonb ->> 'mobile',
        COALESCE(NULLIF(d.payload::jsonb ->> 'language', ''), 'en_US'),
        CASE WHEN lower(COALESCE(d.payload::jsonb ->> 'ptcc', 'no')) IN ('yes', '1', 'true') THEN 'PTCC' ELSE 'MANAGER' END,
        CASE lower(COALESCE(d.payload::jsonb ->> 'status', 'inactive')) WHEN 'active' THEN 'ACTIVE' WHEN 'pending' THEN 'PENDING' ELSE 'INACTIVE' END,
        lower(COALESCE(d.payload::jsonb ->> 'qc_access', 'no')) IN ('yes', '1', 'true', 'on'),
        lower(COALESCE(d.payload::jsonb ->> 'view_only_access', 'no')) IN ('yes', '1', 'true', 'on'),
        lower(COALESCE(d.payload::jsonb ->> 'enable_adding_test_response_date', 'no')) IN ('yes', '1', 'true', 'on'),
        lower(COALESCE(d.payload::jsonb ->> 'enable_choosing_mode_of_receipt', 'no')) IN ('yes', '1', 'true', 'on'),
        lower(COALESCE(d.payload::jsonb ->> 'force_profile_check', 'no')) IN ('yes', '1', 'true', 'on'),
        c.id,
        d.payload::jsonb ->> 'password',
        lower(COALESCE(d.payload::jsonb ->> 'force_password_reset', '0')) IN ('yes', '1', 'true', 'on'),
        CASE WHEN COALESCE(d.payload::jsonb ->> 'last_login', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}' THEN (d.payload::jsonb ->> 'last_login')::timestamp END,
        lower(COALESCE(d.payload::jsonb ->> 'login_ban', 'no')) IN ('yes', '1', 'true', 'on'),
        d.payload::jsonb ->> 'auth_token',
        CASE WHEN COALESCE(d.payload::jsonb ->> 'api_token_generated_datetime', '') ~ '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}' THEN (d.payload::jsonb ->> 'api_token_generated_datetime')::timestamp END,
        d.payload::jsonb ->> 'dm_id',
        d.payload
    FROM legacy_record_archive d
    LEFT JOIN country c ON c.legacy_source_id = d.payload::jsonb ->> 'country_id'
    WHERE d.source_table = 'data_manager' AND d.last_seen_batch_id = :batchId
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        first_name = EXCLUDED.first_name,
        last_name = EXCLUDED.last_name,
        institute = EXCLUDED.institute,
        primary_email = EXCLUDED.primary_email,
        secondary_email = EXCLUDED.secondary_email,
        phone = EXCLUDED.phone,
        mobile = EXCLUDED.mobile,
        language = EXCLUDED.language,
        role = EXCLUDED.role,
        status = EXCLUDED.status,
        qc_access = EXCLUDED.qc_access,
        view_only_access = EXCLUDED.view_only_access,
        enable_test_response_date = EXCLUDED.enable_test_response_date,
        enable_mode_of_receipt = EXCLUDED.enable_mode_of_receipt,
        force_profile_check = EXCLUDED.force_profile_check,
        country_id = EXCLUDED.country_id,
        legacy_password_hash = EXCLUDED.legacy_password_hash,
        force_password_reset = EXCLUDED.force_password_reset,
        last_login = EXCLUDED.last_login,
        login_ban = EXCLUDED.login_ban,
        legacy_auth_token = EXCLUDED.legacy_auth_token,
        api_token_generated_at = EXCLUDED.api_token_generated_at,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map
    (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT 'data_manager', legacy_source_id, 'data_manager', id, :batchId, CURRENT_TIMESTAMP FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
