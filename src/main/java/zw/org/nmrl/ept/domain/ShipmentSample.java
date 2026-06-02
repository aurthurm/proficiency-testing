package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A single sample/control within a shipment panel (shared structure).
 */
@Entity
@Table(name = "shipment_sample")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentSample implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_control")
    private Boolean isControl;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "sample_score")
    private Double sampleScore;

    @Column(name = "preparation_date")
    private LocalDate preparationDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sample")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "assay", "sample" }, allowSetters = true)
    private Set<SampleReferenceResult> referenceResultses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sample")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "assay", "testKit", "sample", "shipmentParticipantMap" }, allowSetters = true)
    private Set<ParticipantResult> participantResultses = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "sampleses", "participantMapses", "distribution", "scheme", "certificateBatcheses" },
        allowSetters = true
    )
    private Shipment shipment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShipmentSample id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public ShipmentSample label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public ShipmentSample displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsControl() {
        return this.isControl;
    }

    public ShipmentSample isControl(Boolean isControl) {
        this.setIsControl(isControl);
        return this;
    }

    public void setIsControl(Boolean isControl) {
        this.isControl = isControl;
    }

    public Boolean getIsMandatory() {
        return this.isMandatory;
    }

    public ShipmentSample isMandatory(Boolean isMandatory) {
        this.setIsMandatory(isMandatory);
        return this;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public Double getSampleScore() {
        return this.sampleScore;
    }

    public ShipmentSample sampleScore(Double sampleScore) {
        this.setSampleScore(sampleScore);
        return this;
    }

    public void setSampleScore(Double sampleScore) {
        this.sampleScore = sampleScore;
    }

    public LocalDate getPreparationDate() {
        return this.preparationDate;
    }

    public ShipmentSample preparationDate(LocalDate preparationDate) {
        this.setPreparationDate(preparationDate);
        return this;
    }

    public void setPreparationDate(LocalDate preparationDate) {
        this.preparationDate = preparationDate;
    }

    public Set<SampleReferenceResult> getReferenceResultses() {
        return this.referenceResultses;
    }

    public void setReferenceResultses(Set<SampleReferenceResult> sampleReferenceResults) {
        if (this.referenceResultses != null) {
            this.referenceResultses.forEach(i -> i.setSample(null));
        }
        if (sampleReferenceResults != null) {
            sampleReferenceResults.forEach(i -> i.setSample(this));
        }
        this.referenceResultses = sampleReferenceResults;
    }

    public ShipmentSample referenceResultses(Set<SampleReferenceResult> sampleReferenceResults) {
        this.setReferenceResultses(sampleReferenceResults);
        return this;
    }

    public ShipmentSample addReferenceResults(SampleReferenceResult sampleReferenceResult) {
        this.referenceResultses.add(sampleReferenceResult);
        sampleReferenceResult.setSample(this);
        return this;
    }

    public ShipmentSample removeReferenceResults(SampleReferenceResult sampleReferenceResult) {
        this.referenceResultses.remove(sampleReferenceResult);
        sampleReferenceResult.setSample(null);
        return this;
    }

    public Set<ParticipantResult> getParticipantResultses() {
        return this.participantResultses;
    }

    public void setParticipantResultses(Set<ParticipantResult> participantResults) {
        if (this.participantResultses != null) {
            this.participantResultses.forEach(i -> i.setSample(null));
        }
        if (participantResults != null) {
            participantResults.forEach(i -> i.setSample(this));
        }
        this.participantResultses = participantResults;
    }

    public ShipmentSample participantResultses(Set<ParticipantResult> participantResults) {
        this.setParticipantResultses(participantResults);
        return this;
    }

    public ShipmentSample addParticipantResults(ParticipantResult participantResult) {
        this.participantResultses.add(participantResult);
        participantResult.setSample(this);
        return this;
    }

    public ShipmentSample removeParticipantResults(ParticipantResult participantResult) {
        this.participantResultses.remove(participantResult);
        participantResult.setSample(null);
        return this;
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public ShipmentSample shipment(Shipment shipment) {
        this.setShipment(shipment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentSample)) {
            return false;
        }
        return getId() != null && getId().equals(((ShipmentSample) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentSample{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", isControl='" + getIsControl() + "'" +
            ", isMandatory='" + getIsMandatory() + "'" +
            ", sampleScore=" + getSampleScore() +
            ", preparationDate='" + getPreparationDate() + "'" +
            "}";
    }
}
