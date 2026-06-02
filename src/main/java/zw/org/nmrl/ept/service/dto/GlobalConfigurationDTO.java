package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.GlobalConfiguration} entity.
 */
@Schema(description = "System-wide configuration (key/value).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GlobalConfigurationDTO implements Serializable {

    private Long id;

    @NotNull
    private String configKey;

    @Lob
    private String configValue;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GlobalConfigurationDTO)) {
            return false;
        }

        GlobalConfigurationDTO globalConfigurationDTO = (GlobalConfigurationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, globalConfigurationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GlobalConfigurationDTO{" +
            "id=" + getId() +
            ", configKey='" + getConfigKey() + "'" +
            ", configValue='" + getConfigValue() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
