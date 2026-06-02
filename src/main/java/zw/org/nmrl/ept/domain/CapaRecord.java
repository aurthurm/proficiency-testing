package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A logged corrective/preventive action against a participant's shipment.
 */
@Entity
@Table(name = "capa_record")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CapaRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "root_cause")
    private String rootCause;

    @Column(name = "action_taken")
    private String actionTaken;

    @Column(name = "action_date")
    private LocalDate actionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    private CorrectiveAction correctiveAction;

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

    public CapaRecord id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRootCause() {
        return this.rootCause;
    }

    public CapaRecord rootCause(String rootCause) {
        this.setRootCause(rootCause);
        return this;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getActionTaken() {
        return this.actionTaken;
    }

    public CapaRecord actionTaken(String actionTaken) {
        this.setActionTaken(actionTaken);
        return this;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public LocalDate getActionDate() {
        return this.actionDate;
    }

    public CapaRecord actionDate(LocalDate actionDate) {
        this.setActionDate(actionDate);
        return this;
    }

    public void setActionDate(LocalDate actionDate) {
        this.actionDate = actionDate;
    }

    public Status getStatus() {
        return this.status;
    }

    public CapaRecord status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getFollowUpDate() {
        return this.followUpDate;
    }

    public CapaRecord followUpDate(LocalDate followUpDate) {
        this.setFollowUpDate(followUpDate);
        return this;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    public CorrectiveAction getCorrectiveAction() {
        return this.correctiveAction;
    }

    public void setCorrectiveAction(CorrectiveAction correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    public CapaRecord correctiveAction(CorrectiveAction correctiveAction) {
        this.setCorrectiveAction(correctiveAction);
        return this;
    }

    public ShipmentParticipantMap getShipmentParticipantMap() {
        return this.shipmentParticipantMap;
    }

    public void setShipmentParticipantMap(ShipmentParticipantMap shipmentParticipantMap) {
        this.shipmentParticipantMap = shipmentParticipantMap;
    }

    public CapaRecord shipmentParticipantMap(ShipmentParticipantMap shipmentParticipantMap) {
        this.setShipmentParticipantMap(shipmentParticipantMap);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CapaRecord)) {
            return false;
        }
        return getId() != null && getId().equals(((CapaRecord) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CapaRecord{" +
            "id=" + getId() +
            ", rootCause='" + getRootCause() + "'" +
            ", actionTaken='" + getActionTaken() + "'" +
            ", actionDate='" + getActionDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", followUpDate='" + getFollowUpDate() + "'" +
            "}";
    }
}
