package zw.org.nmrl.ept.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Proficiency Testing.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    private final Migration migration = new Migration();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Migration getMigration() {
        return migration;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    /**
     * One-off, restartable migration from a legacy ePT MySQL database.
     * Disabled by default so normal application startup never contacts the
     * source database or mutates migration state.
     */
    public static class Migration {

        private boolean enabled;
        private boolean promoteCore;
        private boolean failOnError = true;
        private int batchSize = 500;
        private List<String> includeTables = new ArrayList<>();
        private List<String> supportedSourceVersions = new ArrayList<>(List.of("7.6.20"));
        private List<String> requiredSourceTables = new ArrayList<>(
            List.of(
                "system_config",
                "countries",
                "scheme_list",
                "participant",
                "data_manager",
                "system_admin",
                "participant_manager_map",
                "ptcc_countries_map",
                "global_config",
                "scheme_config",
                "distributions",
                "enrollments",
                "shipment",
                "shipment_participant_map"
            )
        );
        private final Source source = new Source();
        private final Files files = new Files();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isPromoteCore() {
            return promoteCore;
        }

        public void setPromoteCore(boolean promoteCore) {
            this.promoteCore = promoteCore;
        }

        public boolean isFailOnError() {
            return failOnError;
        }

        public void setFailOnError(boolean failOnError) {
            this.failOnError = failOnError;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public List<String> getIncludeTables() {
            return includeTables;
        }

        public void setIncludeTables(List<String> includeTables) {
            this.includeTables = includeTables == null ? new ArrayList<>() : new ArrayList<>(includeTables);
        }

        public List<String> getSupportedSourceVersions() {
            return supportedSourceVersions;
        }

        public void setSupportedSourceVersions(List<String> supportedSourceVersions) {
            this.supportedSourceVersions = supportedSourceVersions == null ? new ArrayList<>() : new ArrayList<>(supportedSourceVersions);
        }

        public List<String> getRequiredSourceTables() {
            return requiredSourceTables;
        }

        public void setRequiredSourceTables(List<String> requiredSourceTables) {
            this.requiredSourceTables = requiredSourceTables == null ? new ArrayList<>() : new ArrayList<>(requiredSourceTables);
        }

        public Source getSource() {
            return source;
        }

        public Files getFiles() {
            return files;
        }

        public static class Source {

            private String url;
            private String username;
            private String password;
            private String driverClassName = "com.mysql.cj.jdbc.Driver";

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getDriverClassName() {
                return driverClassName;
            }

            public void setDriverClassName(String driverClassName) {
                this.driverClassName = driverClassName;
            }
        }

        public static class Files {

            private boolean enabled;
            private String sourceRoot;
            private String targetRoot;
            private boolean overwrite;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getSourceRoot() {
                return sourceRoot;
            }

            public void setSourceRoot(String sourceRoot) {
                this.sourceRoot = sourceRoot;
            }

            public String getTargetRoot() {
                return targetRoot;
            }

            public void setTargetRoot(String targetRoot) {
                this.targetRoot = targetRoot;
            }

            public boolean isOverwrite() {
                return overwrite;
            }

            public void setOverwrite(boolean overwrite) {
                this.overwrite = overwrite;
            }
        }
    }

    // jhipster-needle-application-properties-property-class
}
