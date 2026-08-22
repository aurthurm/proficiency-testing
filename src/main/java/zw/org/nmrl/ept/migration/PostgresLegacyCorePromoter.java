package zw.org.nmrl.ept.migration;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "application.migration", name = "enabled", havingValue = "true")
public class PostgresLegacyCorePromoter implements LegacyCorePromoter {

    private static final Logger LOG = LoggerFactory.getLogger(PostgresLegacyCorePromoter.class);
    private static final List<String> PROMOTION_SCRIPTS = List.of(
        "migration/promote/001_countries.sql",
        "migration/promote/010_schemes.sql",
        "migration/promote/020_participants.sql",
        "migration/promote/030_data_managers.sql",
        "migration/promote/031_data_manager_users.sql",
        "migration/promote/032_system_admin_users.sql",
        "migration/promote/033_participant_managers.sql",
        "migration/promote/034_ptcc_countries.sql",
        "migration/promote/040_modes_of_receipt.sql",
        "migration/promote/041_not_tested_reasons.sql",
        "migration/promote/050_distributions.sql",
        "migration/promote/060_enrollments.sql",
        "migration/promote/070_shipments.sql",
        "migration/promote/080_shipment_participant_maps.sql",
        "migration/promote/090_mail_templates.sql",
        "migration/promote/100_global_config.sql",
        "migration/promote/101_system_config.sql",
        "migration/promote/110_scheme_config.sql"
    );

    private final MigrationTarget migrationTarget;
    private final NamedParameterJdbcTemplate target;

    public PostgresLegacyCorePromoter(MigrationTarget migrationTarget) {
        this.migrationTarget = migrationTarget;
        this.target = new NamedParameterJdbcTemplate(migrationTarget.jdbc());
    }

    @Override
    public void promote(UUID batchId) {
        migrationTarget.write(() -> {
            Map<String, Object> parameters = Map.of("batchId", batchId);
            for (String path : PROMOTION_SCRIPTS) {
                try {
                    ClassPathResource resource = new ClassPathResource(path);
                    String sql = resource.getContentAsString(StandardCharsets.UTF_8);
                    int mappings = target.update(sql, parameters);
                    LOG.info("Applied legacy promotion {} and recorded {} identity mappings", path, mappings);
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to apply legacy promotion script " + path, e);
                }
            }
        });
    }
}
