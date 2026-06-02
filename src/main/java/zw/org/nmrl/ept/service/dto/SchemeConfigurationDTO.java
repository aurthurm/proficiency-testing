package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.SchemeConfiguration} entity.
 */
@Schema(description = "Versioned scoring/configuration for a scheme (ADM-14).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SchemeConfigurationDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer version;

    @NotNull
    private LocalDate effectiveDate;

    @NotNull
    private Double passingScore;

    private Double documentationWeight;

    private Boolean allowLateResponse;

    @Lob
    private String optionalFields;

    @Lob
    private String scoringRules;

    private Boolean isActive;

    @NotNull
    private SchemeDTO scheme;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Double getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Double passingScore) {
        this.passingScore = passingScore;
    }

    public Double getDocumentationWeight() {
        return documentationWeight;
    }

    public void setDocumentationWeight(Double documentationWeight) {
        this.documentationWeight = documentationWeight;
    }

    public Boolean getAllowLateResponse() {
        return allowLateResponse;
    }

    public void setAllowLateResponse(Boolean allowLateResponse) {
        this.allowLateResponse = allowLateResponse;
    }

    public String getOptionalFields() {
        return optionalFields;
    }

    public void setOptionalFields(String optionalFields) {
        this.optionalFields = optionalFields;
    }

    public String getScoringRules() {
        return scoringRules;
    }

    public void setScoringRules(String scoringRules) {
        this.scoringRules = scoringRules;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public SchemeDTO getScheme() {
        return scheme;
    }

    public void setScheme(SchemeDTO scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SchemeConfigurationDTO)) {
            return false;
        }

        SchemeConfigurationDTO schemeConfigurationDTO = (SchemeConfigurationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, schemeConfigurationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SchemeConfigurationDTO{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", effectiveDate='" + getEffectiveDate() + "'" +
            ", passingScore=" + getPassingScore() +
            ", documentationWeight=" + getDocumentationWeight() +
            ", allowLateResponse='" + getAllowLateResponse() + "'" +
            ", optionalFields='" + getOptionalFields() + "'" +
            ", scoringRules='" + getScoringRules() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", scheme=" + getScheme() +
            "}";
    }
}
