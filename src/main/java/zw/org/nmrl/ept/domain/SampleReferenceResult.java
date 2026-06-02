package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Expected result for a sample. One row per (sample, assay/algorithm).
 */
@Entity
@Table(name = "sample_reference_result")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SampleReferenceResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "qualitative_result")
    private String qualitativeResult;

    @Column(name = "quantitative_value")
    private Double quantitativeValue;

    @Column(name = "unit")
    private String unit;

    @Column(name = "lower_limit")
    private Double lowerLimit;

    @Column(name = "upper_limit")
    private Double upperLimit;

    @Column(name = "is_control_expected")
    private Boolean isControlExpected;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    private Assay assay;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "referenceResultses", "participantResultses", "shipment" }, allowSetters = true)
    private ShipmentSample sample;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SampleReferenceResult id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQualitativeResult() {
        return this.qualitativeResult;
    }

    public SampleReferenceResult qualitativeResult(String qualitativeResult) {
        this.setQualitativeResult(qualitativeResult);
        return this;
    }

    public void setQualitativeResult(String qualitativeResult) {
        this.qualitativeResult = qualitativeResult;
    }

    public Double getQuantitativeValue() {
        return this.quantitativeValue;
    }

    public SampleReferenceResult quantitativeValue(Double quantitativeValue) {
        this.setQuantitativeValue(quantitativeValue);
        return this;
    }

    public void setQuantitativeValue(Double quantitativeValue) {
        this.quantitativeValue = quantitativeValue;
    }

    public String getUnit() {
        return this.unit;
    }

    public SampleReferenceResult unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getLowerLimit() {
        return this.lowerLimit;
    }

    public SampleReferenceResult lowerLimit(Double lowerLimit) {
        this.setLowerLimit(lowerLimit);
        return this;
    }

    public void setLowerLimit(Double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Double getUpperLimit() {
        return this.upperLimit;
    }

    public SampleReferenceResult upperLimit(Double upperLimit) {
        this.setUpperLimit(upperLimit);
        return this;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Boolean getIsControlExpected() {
        return this.isControlExpected;
    }

    public SampleReferenceResult isControlExpected(Boolean isControlExpected) {
        this.setIsControlExpected(isControlExpected);
        return this;
    }

    public void setIsControlExpected(Boolean isControlExpected) {
        this.isControlExpected = isControlExpected;
    }

    public Assay getAssay() {
        return this.assay;
    }

    public void setAssay(Assay assay) {
        this.assay = assay;
    }

    public SampleReferenceResult assay(Assay assay) {
        this.setAssay(assay);
        return this;
    }

    public ShipmentSample getSample() {
        return this.sample;
    }

    public void setSample(ShipmentSample shipmentSample) {
        this.sample = shipmentSample;
    }

    public SampleReferenceResult sample(ShipmentSample shipmentSample) {
        this.setSample(shipmentSample);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SampleReferenceResult)) {
            return false;
        }
        return getId() != null && getId().equals(((SampleReferenceResult) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SampleReferenceResult{" +
            "id=" + getId() +
            ", qualitativeResult='" + getQualitativeResult() + "'" +
            ", quantitativeValue=" + getQuantitativeValue() +
            ", unit='" + getUnit() + "'" +
            ", lowerLimit=" + getLowerLimit() +
            ", upperLimit=" + getUpperLimit() +
            ", isControlExpected='" + getIsControlExpected() + "'" +
            "}";
    }
}
