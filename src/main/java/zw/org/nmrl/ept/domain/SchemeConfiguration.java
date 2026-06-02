package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Versioned scoring/configuration for a scheme (ADM-14).
 */
@Entity
@Table(name = "scheme_configuration")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SchemeConfiguration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version;

    @NotNull
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @NotNull
    @Column(name = "passing_score", nullable = false)
    private Double passingScore;

    @Column(name = "documentation_weight")
    private Double documentationWeight;

    @Column(name = "allow_late_response")
    private Boolean allowLateResponse;

    @Lob
    @Column(name = "optional_fields")
    private String optionalFields;

    @Lob
    @Column(name = "scoring_rules")
    private String scoringRules;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "certificateTemplate",
            "configurationses",
            "correctiveActionses",
            "assayses",
            "testKitses",
            "shipmentses",
            "enrollmentses",
        },
        allowSetters = true
    )
    private Scheme scheme;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SchemeConfiguration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public SchemeConfiguration version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDate getEffectiveDate() {
        return this.effectiveDate;
    }

    public SchemeConfiguration effectiveDate(LocalDate effectiveDate) {
        this.setEffectiveDate(effectiveDate);
        return this;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Double getPassingScore() {
        return this.passingScore;
    }

    public SchemeConfiguration passingScore(Double passingScore) {
        this.setPassingScore(passingScore);
        return this;
    }

    public void setPassingScore(Double passingScore) {
        this.passingScore = passingScore;
    }

    public Double getDocumentationWeight() {
        return this.documentationWeight;
    }

    public SchemeConfiguration documentationWeight(Double documentationWeight) {
        this.setDocumentationWeight(documentationWeight);
        return this;
    }

    public void setDocumentationWeight(Double documentationWeight) {
        this.documentationWeight = documentationWeight;
    }

    public Boolean getAllowLateResponse() {
        return this.allowLateResponse;
    }

    public SchemeConfiguration allowLateResponse(Boolean allowLateResponse) {
        this.setAllowLateResponse(allowLateResponse);
        return this;
    }

    public void setAllowLateResponse(Boolean allowLateResponse) {
        this.allowLateResponse = allowLateResponse;
    }

    public String getOptionalFields() {
        return this.optionalFields;
    }

    public SchemeConfiguration optionalFields(String optionalFields) {
        this.setOptionalFields(optionalFields);
        return this;
    }

    public void setOptionalFields(String optionalFields) {
        this.optionalFields = optionalFields;
    }

    public String getScoringRules() {
        return this.scoringRules;
    }

    public SchemeConfiguration scoringRules(String scoringRules) {
        this.setScoringRules(scoringRules);
        return this;
    }

    public void setScoringRules(String scoringRules) {
        this.scoringRules = scoringRules;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public SchemeConfiguration isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Scheme getScheme() {
        return this.scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public SchemeConfiguration scheme(Scheme scheme) {
        this.setScheme(scheme);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SchemeConfiguration)) {
            return false;
        }
        return getId() != null && getId().equals(((SchemeConfiguration) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SchemeConfiguration{" +
            "id=" + getId() +
            ", version=" + getVersion() +
            ", effectiveDate='" + getEffectiveDate() + "'" +
            ", passingScore=" + getPassingScore() +
            ", documentationWeight=" + getDocumentationWeight() +
            ", allowLateResponse='" + getAllowLateResponse() + "'" +
            ", optionalFields='" + getOptionalFields() + "'" +
            ", scoringRules='" + getScoringRules() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
