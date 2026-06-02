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
 * Participant's reported result for a sample.
 */
@Entity
@Table(name = "participant_result")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "reported_qualitative_result")
    private String reportedQualitativeResult;

    @Column(name = "reported_quantitative_value")
    private Double reportedQuantitativeValue;

    @Column(name = "unit")
    private String unit;

    @Column(name = "lot_number")
    private String lotNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "z_score")
    private Double zScore;

    @Column(name = "calculated_score")
    private Double calculatedScore;

    @Column(name = "comments")
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    private Assay assay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    private TestKit testKit;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "referenceResultses", "participantResultses", "shipment" }, allowSetters = true)
    private ShipmentSample sample;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "resultses", "capaRecordses", "modeOfReceipt", "notTestedReason", "shipment", "participant" },
        allowSetters = true
    )
    private ShipmentParticipantMap shipmentParticipantMap;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ParticipantResult id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportedQualitativeResult() {
        return this.reportedQualitativeResult;
    }

    public ParticipantResult reportedQualitativeResult(String reportedQualitativeResult) {
        this.setReportedQualitativeResult(reportedQualitativeResult);
        return this;
    }

    public void setReportedQualitativeResult(String reportedQualitativeResult) {
        this.reportedQualitativeResult = reportedQualitativeResult;
    }

    public Double getReportedQuantitativeValue() {
        return this.reportedQuantitativeValue;
    }

    public ParticipantResult reportedQuantitativeValue(Double reportedQuantitativeValue) {
        this.setReportedQuantitativeValue(reportedQuantitativeValue);
        return this;
    }

    public void setReportedQuantitativeValue(Double reportedQuantitativeValue) {
        this.reportedQuantitativeValue = reportedQuantitativeValue;
    }

    public String getUnit() {
        return this.unit;
    }

    public ParticipantResult unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLotNumber() {
        return this.lotNumber;
    }

    public ParticipantResult lotNumber(String lotNumber) {
        this.setLotNumber(lotNumber);
        return this;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public LocalDate getExpiryDate() {
        return this.expiryDate;
    }

    public ParticipantResult expiryDate(LocalDate expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Double getzScore() {
        return this.zScore;
    }

    public ParticipantResult zScore(Double zScore) {
        this.setzScore(zScore);
        return this;
    }

    public void setzScore(Double zScore) {
        this.zScore = zScore;
    }

    public Double getCalculatedScore() {
        return this.calculatedScore;
    }

    public ParticipantResult calculatedScore(Double calculatedScore) {
        this.setCalculatedScore(calculatedScore);
        return this;
    }

    public void setCalculatedScore(Double calculatedScore) {
        this.calculatedScore = calculatedScore;
    }

    public String getComments() {
        return this.comments;
    }

    public ParticipantResult comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Assay getAssay() {
        return this.assay;
    }

    public void setAssay(Assay assay) {
        this.assay = assay;
    }

    public ParticipantResult assay(Assay assay) {
        this.setAssay(assay);
        return this;
    }

    public TestKit getTestKit() {
        return this.testKit;
    }

    public void setTestKit(TestKit testKit) {
        this.testKit = testKit;
    }

    public ParticipantResult testKit(TestKit testKit) {
        this.setTestKit(testKit);
        return this;
    }

    public ShipmentSample getSample() {
        return this.sample;
    }

    public void setSample(ShipmentSample shipmentSample) {
        this.sample = shipmentSample;
    }

    public ParticipantResult sample(ShipmentSample shipmentSample) {
        this.setSample(shipmentSample);
        return this;
    }

    public ShipmentParticipantMap getShipmentParticipantMap() {
        return this.shipmentParticipantMap;
    }

    public void setShipmentParticipantMap(ShipmentParticipantMap shipmentParticipantMap) {
        this.shipmentParticipantMap = shipmentParticipantMap;
    }

    public ParticipantResult shipmentParticipantMap(ShipmentParticipantMap shipmentParticipantMap) {
        this.setShipmentParticipantMap(shipmentParticipantMap);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantResult)) {
            return false;
        }
        return getId() != null && getId().equals(((ParticipantResult) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantResult{" +
            "id=" + getId() +
            ", reportedQualitativeResult='" + getReportedQualitativeResult() + "'" +
            ", reportedQuantitativeValue=" + getReportedQuantitativeValue() +
            ", unit='" + getUnit() + "'" +
            ", lotNumber='" + getLotNumber() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", zScore=" + getzScore() +
            ", calculatedScore=" + getCalculatedScore() +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
