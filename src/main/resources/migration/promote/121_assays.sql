WITH source_assays AS (
    SELECT
        a.*,
        CASE a.source_table
            WHEN 'r_dbs_eia' THEN a.payload::jsonb ->> 'eia_id'
            WHEN 'r_dbs_wb' THEN a.payload::jsonb ->> 'wb_id'
            WHEN 'r_covid19_gene_types' THEN a.payload::jsonb ->> 'gene_id'
            WHEN 'r_test_type_covid19' THEN a.payload::jsonb ->> 'test_type_id'
            ELSE a.payload::jsonb ->> 'id'
        END AS source_id,
        CASE a.source_table
            WHEN 'r_dbs_eia' THEN a.payload::jsonb ->> 'eia_name'
            WHEN 'r_dbs_wb' THEN a.payload::jsonb ->> 'wb_name'
            WHEN 'r_covid19_gene_types' THEN a.payload::jsonb ->> 'gene_name'
            WHEN 'r_test_type_covid19' THEN a.payload::jsonb ->> 'test_type_name'
            ELSE a.payload::jsonb ->> 'name'
        END AS assay_name,
        CASE a.source_table
            WHEN 'r_dbs_eia' THEN 'DBS_EIA'
            WHEN 'r_dbs_wb' THEN 'DBS_WB'
            WHEN 'r_eid_detection_assay' THEN 'EID_DETECTION_ASSAY'
            WHEN 'r_eid_extraction_assay' THEN 'EID_EXTRACTION_ASSAY'
            WHEN 'r_recency_assay' THEN 'RECENCY_ASSAY'
            WHEN 'r_tb_assay' THEN 'TB_ASSAY'
            WHEN 'r_vl_assay' THEN 'VL_ASSAY'
            WHEN 'r_covid19_gene_types' THEN 'COVID19_GENE'
            WHEN 'r_test_type_covid19' THEN 'COVID19_TEST_TYPE'
        END AS assay_type,
        CASE a.source_table
            WHEN 'r_dbs_eia' THEN 'dbs'
            WHEN 'r_dbs_wb' THEN 'dbs'
            WHEN 'r_eid_detection_assay' THEN 'eid'
            WHEN 'r_eid_extraction_assay' THEN 'eid'
            WHEN 'r_recency_assay' THEN 'recency'
            WHEN 'r_tb_assay' THEN 'tb'
            WHEN 'r_vl_assay' THEN 'vl'
            ELSE COALESCE(a.payload::jsonb ->> 'scheme_type', 'covid19')
        END AS scheme_code
    FROM legacy_record_archive a
    WHERE a.last_seen_batch_id = :batchId
      AND a.source_table IN (
        'r_dbs_eia', 'r_dbs_wb', 'r_eid_detection_assay', 'r_eid_extraction_assay',
        'r_recency_assay', 'r_tb_assay', 'r_vl_assay', 'r_covid19_gene_types', 'r_test_type_covid19'
      )
), promoted AS (
    INSERT INTO assay (id, name, assay_type, manufacturer, status, scheme_id, legacy_source_id, legacy_payload)
    SELECT
        nextval('sequence_generator'),
        COALESCE(NULLIF(source.assay_name, ''), source.source_id),
        source.assay_type,
        source.payload::jsonb ->> 'test_type_manufacturer',
        CASE WHEN lower(COALESCE(
            source.payload::jsonb ->> 'status',
            source.payload::jsonb ->> 'gene_status',
            'active'
        )) = 'active' THEN 'ACTIVE' ELSE 'INACTIVE' END,
        s.id,
        source.source_table || ':' || source.source_id,
        source.payload
    FROM source_assays source
    LEFT JOIN scheme s ON lower(s.legacy_source_id) = lower(source.scheme_code)
    ON CONFLICT (legacy_source_id) DO UPDATE SET
        name = EXCLUDED.name,
        assay_type = EXCLUDED.assay_type,
        manufacturer = EXCLUDED.manufacturer,
        status = EXCLUDED.status,
        scheme_id = EXCLUDED.scheme_id,
        legacy_payload = EXCLUDED.legacy_payload
    RETURNING id, legacy_source_id
)
INSERT INTO migration_id_map (source_table, source_id, target_table, target_id, migration_batch_id, mapped_at)
SELECT
    split_part(legacy_source_id, ':', 1),
    substring(legacy_source_id from position(':' in legacy_source_id) + 1),
    'assay',
    id,
    :batchId,
    CURRENT_TIMESTAMP
FROM promoted
ON CONFLICT (source_table, source_id, target_table) DO UPDATE SET
    target_id = EXCLUDED.target_id,
    migration_batch_id = EXCLUDED.migration_batch_id,
    mapped_at = EXCLUDED.mapped_at
