package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.SampleReferenceResult} entity.
 */
@Schema(description = "Expected result for a sample. One row per (sample, assay/algorithm).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SampleReferenceResultDTO implements Serializable {

    private Long id;

    private String qualitativeResult;

    private Double quantitativeValue;

    private String unit;

    private Double lowerLimit;

    private Double upperLimit;

    private Boolean isControlExpected;

    private AssayDTO assay;

    @NotNull
    private ShipmentSampleDTO sample;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQualitativeResult() {
        return qualitativeResult;
    }

    public void setQualitativeResult(String qualitativeResult) {
        this.qualitativeResult = qualitativeResult;
    }

    public Double getQuantitativeValue() {
        return quantitativeValue;
    }

    public void setQuantitativeValue(Double quantitativeValue) {
        this.quantitativeValue = quantitativeValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Boolean getIsControlExpected() {
        return isControlExpected;
    }

    public void setIsControlExpected(Boolean isControlExpected) {
        this.isControlExpected = isControlExpected;
    }

    public AssayDTO getAssay() {
        return assay;
    }

    public void setAssay(AssayDTO assay) {
        this.assay = assay;
    }

    public ShipmentSampleDTO getSample() {
        return sample;
    }

    public void setSample(ShipmentSampleDTO sample) {
        this.sample = sample;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SampleReferenceResultDTO)) {
            return false;
        }

        SampleReferenceResultDTO sampleReferenceResultDTO = (SampleReferenceResultDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sampleReferenceResultDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SampleReferenceResultDTO{" +
            "id=" + getId() +
            ", qualitativeResult='" + getQualitativeResult() + "'" +
            ", quantitativeValue=" + getQuantitativeValue() +
            ", unit='" + getUnit() + "'" +
            ", lowerLimit=" + getLowerLimit() +
            ", upperLimit=" + getUpperLimit() +
            ", isControlExpected='" + getIsControlExpected() + "'" +
            ", assay=" + getAssay() +
            ", sample=" + getSample() +
            "}";
    }
}
