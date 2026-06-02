package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.ShipmentStatus;

/**
 * PT panel sent to participants.
 */
@Entity
@Table(name = "shipment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Shipment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotNull
    @Column(name = "shipment_date", nullable = false)
    private LocalDate shipmentDate;

    @NotNull
    @Column(name = "response_deadline", nullable = false)
    private Instant responseDeadline;

    @Column(name = "responses_open")
    private Boolean responsesOpen;

    @Column(name = "auto_close_at_deadline")
    private Boolean autoCloseAtDeadline;

    @Column(name = "allow_editing_response")
    private Boolean allowEditingResponse;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    @Column(name = "coordinator_name")
    private String coordinatorName;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "coordinator_email")
    private String coordinatorEmail;

    @Column(name = "coordinator_phone")
    private String coordinatorPhone;

    @Column(name = "number_of_samples")
    private Integer numberOfSamples;

    @Column(name = "max_score")
    private Integer maxScore;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @Lob
    @Column(name = "attributes")
    private String attributes;

    @Column(name = "reports_generated_at")
    private Instant reportsGeneratedAt;

    @Column(name = "finalized_at")
    private Instant finalizedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shipment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "referenceResultses", "participantResultses", "shipment" }, allowSetters = true)
    private Set<ShipmentSample> sampleses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shipment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "resultses", "capaRecordses", "modeOfReceipt", "notTestedReason", "shipment", "participant" },
        allowSetters = true
    )
    private Set<ShipmentParticipantMap> participantMapses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "shipmentses" }, allowSetters = true)
    private Distribution distribution;

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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "shipmentses")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "shipmentses" }, allowSetters = true)
    private Set<CertificateBatch> certificateBatcheses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Shipment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Shipment code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getShipmentDate() {
        return this.shipmentDate;
    }

    public Shipment shipmentDate(LocalDate shipmentDate) {
        this.setShipmentDate(shipmentDate);
        return this;
    }

    public void setShipmentDate(LocalDate shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public Instant getResponseDeadline() {
        return this.responseDeadline;
    }

    public Shipment responseDeadline(Instant responseDeadline) {
        this.setResponseDeadline(responseDeadline);
        return this;
    }

    public void setResponseDeadline(Instant responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public Boolean getResponsesOpen() {
        return this.responsesOpen;
    }

    public Shipment responsesOpen(Boolean responsesOpen) {
        this.setResponsesOpen(responsesOpen);
        return this;
    }

    public void setResponsesOpen(Boolean responsesOpen) {
        this.responsesOpen = responsesOpen;
    }

    public Boolean getAutoCloseAtDeadline() {
        return this.autoCloseAtDeadline;
    }

    public Shipment autoCloseAtDeadline(Boolean autoCloseAtDeadline) {
        this.setAutoCloseAtDeadline(autoCloseAtDeadline);
        return this;
    }

    public void setAutoCloseAtDeadline(Boolean autoCloseAtDeadline) {
        this.autoCloseAtDeadline = autoCloseAtDeadline;
    }

    public Boolean getAllowEditingResponse() {
        return this.allowEditingResponse;
    }

    public Shipment allowEditingResponse(Boolean allowEditingResponse) {
        this.setAllowEditingResponse(allowEditingResponse);
        return this;
    }

    public void setAllowEditingResponse(Boolean allowEditingResponse) {
        this.allowEditingResponse = allowEditingResponse;
    }

    public String getIssuingAuthority() {
        return this.issuingAuthority;
    }

    public Shipment issuingAuthority(String issuingAuthority) {
        this.setIssuingAuthority(issuingAuthority);
        return this;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getCoordinatorName() {
        return this.coordinatorName;
    }

    public Shipment coordinatorName(String coordinatorName) {
        this.setCoordinatorName(coordinatorName);
        return this;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public String getCoordinatorEmail() {
        return this.coordinatorEmail;
    }

    public Shipment coordinatorEmail(String coordinatorEmail) {
        this.setCoordinatorEmail(coordinatorEmail);
        return this;
    }

    public void setCoordinatorEmail(String coordinatorEmail) {
        this.coordinatorEmail = coordinatorEmail;
    }

    public String getCoordinatorPhone() {
        return this.coordinatorPhone;
    }

    public Shipment coordinatorPhone(String coordinatorPhone) {
        this.setCoordinatorPhone(coordinatorPhone);
        return this;
    }

    public void setCoordinatorPhone(String coordinatorPhone) {
        this.coordinatorPhone = coordinatorPhone;
    }

    public Integer getNumberOfSamples() {
        return this.numberOfSamples;
    }

    public Shipment numberOfSamples(Integer numberOfSamples) {
        this.setNumberOfSamples(numberOfSamples);
        return this;
    }

    public void setNumberOfSamples(Integer numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public Integer getMaxScore() {
        return this.maxScore;
    }

    public Shipment maxScore(Integer maxScore) {
        this.setMaxScore(maxScore);
        return this;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public ShipmentStatus getStatus() {
        return this.status;
    }

    public Shipment status(ShipmentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getAttributes() {
        return this.attributes;
    }

    public Shipment attributes(String attributes) {
        this.setAttributes(attributes);
        return this;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public Instant getReportsGeneratedAt() {
        return this.reportsGeneratedAt;
    }

    public Shipment reportsGeneratedAt(Instant reportsGeneratedAt) {
        this.setReportsGeneratedAt(reportsGeneratedAt);
        return this;
    }

    public void setReportsGeneratedAt(Instant reportsGeneratedAt) {
        this.reportsGeneratedAt = reportsGeneratedAt;
    }

    public Instant getFinalizedAt() {
        return this.finalizedAt;
    }

    public Shipment finalizedAt(Instant finalizedAt) {
        this.setFinalizedAt(finalizedAt);
        return this;
    }

    public void setFinalizedAt(Instant finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public Set<ShipmentSample> getSampleses() {
        return this.sampleses;
    }

    public void setSampleses(Set<ShipmentSample> shipmentSamples) {
        if (this.sampleses != null) {
            this.sampleses.forEach(i -> i.setShipment(null));
        }
        if (shipmentSamples != null) {
            shipmentSamples.forEach(i -> i.setShipment(this));
        }
        this.sampleses = shipmentSamples;
    }

    public Shipment sampleses(Set<ShipmentSample> shipmentSamples) {
        this.setSampleses(shipmentSamples);
        return this;
    }

    public Shipment addSamples(ShipmentSample shipmentSample) {
        this.sampleses.add(shipmentSample);
        shipmentSample.setShipment(this);
        return this;
    }

    public Shipment removeSamples(ShipmentSample shipmentSample) {
        this.sampleses.remove(shipmentSample);
        shipmentSample.setShipment(null);
        return this;
    }

    public Set<ShipmentParticipantMap> getParticipantMapses() {
        return this.participantMapses;
    }

    public void setParticipantMapses(Set<ShipmentParticipantMap> shipmentParticipantMaps) {
        if (this.participantMapses != null) {
            this.participantMapses.forEach(i -> i.setShipment(null));
        }
        if (shipmentParticipantMaps != null) {
            shipmentParticipantMaps.forEach(i -> i.setShipment(this));
        }
        this.participantMapses = shipmentParticipantMaps;
    }

    public Shipment participantMapses(Set<ShipmentParticipantMap> shipmentParticipantMaps) {
        this.setParticipantMapses(shipmentParticipantMaps);
        return this;
    }

    public Shipment addParticipantMaps(ShipmentParticipantMap shipmentParticipantMap) {
        this.participantMapses.add(shipmentParticipantMap);
        shipmentParticipantMap.setShipment(this);
        return this;
    }

    public Shipment removeParticipantMaps(ShipmentParticipantMap shipmentParticipantMap) {
        this.participantMapses.remove(shipmentParticipantMap);
        shipmentParticipantMap.setShipment(null);
        return this;
    }

    public Distribution getDistribution() {
        return this.distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public Shipment distribution(Distribution distribution) {
        this.setDistribution(distribution);
        return this;
    }

    public Scheme getScheme() {
        return this.scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public Shipment scheme(Scheme scheme) {
        this.setScheme(scheme);
        return this;
    }

    public Set<CertificateBatch> getCertificateBatcheses() {
        return this.certificateBatcheses;
    }

    public void setCertificateBatcheses(Set<CertificateBatch> certificateBatches) {
        if (this.certificateBatcheses != null) {
            this.certificateBatcheses.forEach(i -> i.removeShipments(this));
        }
        if (certificateBatches != null) {
            certificateBatches.forEach(i -> i.addShipments(this));
        }
        this.certificateBatcheses = certificateBatches;
    }

    public Shipment certificateBatcheses(Set<CertificateBatch> certificateBatches) {
        this.setCertificateBatcheses(certificateBatches);
        return this;
    }

    public Shipment addCertificateBatches(CertificateBatch certificateBatch) {
        this.certificateBatcheses.add(certificateBatch);
        certificateBatch.getShipmentses().add(this);
        return this;
    }

    public Shipment removeCertificateBatches(CertificateBatch certificateBatch) {
        this.certificateBatcheses.remove(certificateBatch);
        certificateBatch.getShipmentses().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shipment)) {
            return false;
        }
        return getId() != null && getId().equals(((Shipment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shipment{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", shipmentDate='" + getShipmentDate() + "'" +
            ", responseDeadline='" + getResponseDeadline() + "'" +
            ", responsesOpen='" + getResponsesOpen() + "'" +
            ", autoCloseAtDeadline='" + getAutoCloseAtDeadline() + "'" +
            ", allowEditingResponse='" + getAllowEditingResponse() + "'" +
            ", issuingAuthority='" + getIssuingAuthority() + "'" +
            ", coordinatorName='" + getCoordinatorName() + "'" +
            ", coordinatorEmail='" + getCoordinatorEmail() + "'" +
            ", coordinatorPhone='" + getCoordinatorPhone() + "'" +
            ", numberOfSamples=" + getNumberOfSamples() +
            ", maxScore=" + getMaxScore() +
            ", status='" + getStatus() + "'" +
            ", attributes='" + getAttributes() + "'" +
            ", reportsGeneratedAt='" + getReportsGeneratedAt() + "'" +
            ", finalizedAt='" + getFinalizedAt() + "'" +
            "}";
    }
}
