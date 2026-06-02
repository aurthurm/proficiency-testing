package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ParticipantResult} entity.
 */
@Schema(description = "Participant's reported result for a sample.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantResultDTO implements Serializable {

    private Long id;

    private String reportedQualitativeResult;

    private Double reportedQuantitativeValue;

    private String unit;

    private String lotNumber;

    private LocalDate expiryDate;

    private Double zScore;

    private Double calculatedScore;

    private String comments;

    private AssayDTO assay;

    private TestKitDTO testKit;

    @NotNull
    private ShipmentSampleDTO sample;

    @NotNull
    private ShipmentParticipantMapDTO shipmentParticipantMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportedQualitativeResult() {
        return reportedQualitativeResult;
    }

    public void setReportedQualitativeResult(String reportedQualitativeResult) {
        this.reportedQualitativeResult = reportedQualitativeResult;
    }

    public Double getReportedQuantitativeValue() {
        return reportedQuantitativeValue;
    }

    public void setReportedQuantitativeValue(Double reportedQuantitativeValue) {
        this.reportedQuantitativeValue = reportedQuantitativeValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Double getzScore() {
        return zScore;
    }

    public void setzScore(Double zScore) {
        this.zScore = zScore;
    }

    public Double getCalculatedScore() {
        return calculatedScore;
    }

    public void setCalculatedScore(Double calculatedScore) {
        this.calculatedScore = calculatedScore;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public AssayDTO getAssay() {
        return assay;
    }

    public void setAssay(AssayDTO assay) {
        this.assay = assay;
    }

    public TestKitDTO getTestKit() {
        return testKit;
    }

    public void setTestKit(TestKitDTO testKit) {
        this.testKit = testKit;
    }

    public ShipmentSampleDTO getSample() {
        return sample;
    }

    public void setSample(ShipmentSampleDTO sample) {
        this.sample = sample;
    }

    public ShipmentParticipantMapDTO getShipmentParticipantMap() {
        return shipmentParticipantMap;
    }

    public void setShipmentParticipantMap(ShipmentParticipantMapDTO shipmentParticipantMap) {
        this.shipmentParticipantMap = shipmentParticipantMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantResultDTO)) {
            return false;
        }

        ParticipantResultDTO participantResultDTO = (ParticipantResultDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, participantResultDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantResultDTO{" +
            "id=" + getId() +
            ", reportedQualitativeResult='" + getReportedQualitativeResult() + "'" +
            ", reportedQuantitativeValue=" + getReportedQuantitativeValue() +
            ", unit='" + getUnit() + "'" +
            ", lotNumber='" + getLotNumber() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", zScore=" + getzScore() +
            ", calculatedScore=" + getCalculatedScore() +
            ", comments='" + getComments() + "'" +
            ", assay=" + getAssay() +
            ", testKit=" + getTestKit() +
            ", sample=" + getSample() +
            ", shipmentParticipantMap=" + getShipmentParticipantMap() +
            "}";
    }
}
