package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.CapaRecord} entity.
 */
@Schema(description = "A logged corrective/preventive action against a participant's shipment.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CapaRecordDTO implements Serializable {

    private Long id;

    private String rootCause;

    private String actionTaken;

    private LocalDate actionDate;

    private Status status;

    private LocalDate followUpDate;

    private CorrectiveActionDTO correctiveAction;

    @NotNull
    private ShipmentParticipantMapDTO shipmentParticipantMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public LocalDate getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDate actionDate) {
        this.actionDate = actionDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    public CorrectiveActionDTO getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(CorrectiveActionDTO correctiveAction) {
        this.correctiveAction = correctiveAction;
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
        if (!(o instanceof CapaRecordDTO)) {
            return false;
        }

        CapaRecordDTO capaRecordDTO = (CapaRecordDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, capaRecordDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CapaRecordDTO{" +
            "id=" + getId() +
            ", rootCause='" + getRootCause() + "'" +
            ", actionTaken='" + getActionTaken() + "'" +
            ", actionDate='" + getActionDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", followUpDate='" + getFollowUpDate() + "'" +
            ", correctiveAction=" + getCorrectiveAction() +
            ", shipmentParticipantMap=" + getShipmentParticipantMap() +
            "}";
    }
}
