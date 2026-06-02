package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.FinalResult;
import zw.org.nmrl.ept.domain.enumeration.QcStatus;
import zw.org.nmrl.ept.domain.enumeration.ResponseStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ShipmentParticipantMap} entity.
 */
@Schema(description = "Participant's instance of a shipment + evaluation summary.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentParticipantMapDTO implements Serializable {

    private Long id;

    private ResponseStatus responseStatus;

    private LocalDate shipmentReceiptDate;

    private LocalDate shipmentTestDate;

    private Instant shipmentTestReportDate;

    private Instant submittedAt;

    private Instant evaluatedAt;

    private Boolean isExcluded;

    private Boolean isResponseLate;

    private Boolean isPtTestNotPerformed;

    private String ptTestNotPerformedComments;

    private Boolean supervisorApproved;

    private String participantSupervisor;

    private String userComment;

    private Double shipmentScore;

    private Double documentationScore;

    private FinalResult finalResult;

    @Lob
    private String failureReason;

    private String evaluationComment;

    private Boolean isFollowup;

    private Boolean manualOverride;

    private QcStatus qcStatus;

    private LocalDate qcDate;

    private String qcDoneBy;

    private Boolean syncedToMobile;

    private Instant syncedOn;

    private ModeOfReceiptDTO modeOfReceipt;

    private NotTestedReasonDTO notTestedReason;

    @NotNull
    private ShipmentDTO shipment;

    @NotNull
    private ParticipantDTO participant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public LocalDate getShipmentReceiptDate() {
        return shipmentReceiptDate;
    }

    public void setShipmentReceiptDate(LocalDate shipmentReceiptDate) {
        this.shipmentReceiptDate = shipmentReceiptDate;
    }

    public LocalDate getShipmentTestDate() {
        return shipmentTestDate;
    }

    public void setShipmentTestDate(LocalDate shipmentTestDate) {
        this.shipmentTestDate = shipmentTestDate;
    }

    public Instant getShipmentTestReportDate() {
        return shipmentTestReportDate;
    }

    public void setShipmentTestReportDate(Instant shipmentTestReportDate) {
        this.shipmentTestReportDate = shipmentTestReportDate;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(Instant evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public Boolean getIsExcluded() {
        return isExcluded;
    }

    public void setIsExcluded(Boolean isExcluded) {
        this.isExcluded = isExcluded;
    }

    public Boolean getIsResponseLate() {
        return isResponseLate;
    }

    public void setIsResponseLate(Boolean isResponseLate) {
        this.isResponseLate = isResponseLate;
    }

    public Boolean getIsPtTestNotPerformed() {
        return isPtTestNotPerformed;
    }

    public void setIsPtTestNotPerformed(Boolean isPtTestNotPerformed) {
        this.isPtTestNotPerformed = isPtTestNotPerformed;
    }

    public String getPtTestNotPerformedComments() {
        return ptTestNotPerformedComments;
    }

    public void setPtTestNotPerformedComments(String ptTestNotPerformedComments) {
        this.ptTestNotPerformedComments = ptTestNotPerformedComments;
    }

    public Boolean getSupervisorApproved() {
        return supervisorApproved;
    }

    public void setSupervisorApproved(Boolean supervisorApproved) {
        this.supervisorApproved = supervisorApproved;
    }

    public String getParticipantSupervisor() {
        return participantSupervisor;
    }

    public void setParticipantSupervisor(String participantSupervisor) {
        this.participantSupervisor = participantSupervisor;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public Double getShipmentScore() {
        return shipmentScore;
    }

    public void setShipmentScore(Double shipmentScore) {
        this.shipmentScore = shipmentScore;
    }

    public Double getDocumentationScore() {
        return documentationScore;
    }

    public void setDocumentationScore(Double documentationScore) {
        this.documentationScore = documentationScore;
    }

    public FinalResult getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(FinalResult finalResult) {
        this.finalResult = finalResult;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getEvaluationComment() {
        return evaluationComment;
    }

    public void setEvaluationComment(String evaluationComment) {
        this.evaluationComment = evaluationComment;
    }

    public Boolean getIsFollowup() {
        return isFollowup;
    }

    public void setIsFollowup(Boolean isFollowup) {
        this.isFollowup = isFollowup;
    }

    public Boolean getManualOverride() {
        return manualOverride;
    }

    public void setManualOverride(Boolean manualOverride) {
        this.manualOverride = manualOverride;
    }

    public QcStatus getQcStatus() {
        return qcStatus;
    }

    public void setQcStatus(QcStatus qcStatus) {
        this.qcStatus = qcStatus;
    }

    public LocalDate getQcDate() {
        return qcDate;
    }

    public void setQcDate(LocalDate qcDate) {
        this.qcDate = qcDate;
    }

    public String getQcDoneBy() {
        return qcDoneBy;
    }

    public void setQcDoneBy(String qcDoneBy) {
        this.qcDoneBy = qcDoneBy;
    }

    public Boolean getSyncedToMobile() {
        return syncedToMobile;
    }

    public void setSyncedToMobile(Boolean syncedToMobile) {
        this.syncedToMobile = syncedToMobile;
    }

    public Instant getSyncedOn() {
        return syncedOn;
    }

    public void setSyncedOn(Instant syncedOn) {
        this.syncedOn = syncedOn;
    }

    public ModeOfReceiptDTO getModeOfReceipt() {
        return modeOfReceipt;
    }

    public void setModeOfReceipt(ModeOfReceiptDTO modeOfReceipt) {
        this.modeOfReceipt = modeOfReceipt;
    }

    public NotTestedReasonDTO getNotTestedReason() {
        return notTestedReason;
    }

    public void setNotTestedReason(NotTestedReasonDTO notTestedReason) {
        this.notTestedReason = notTestedReason;
    }

    public ShipmentDTO getShipment() {
        return shipment;
    }

    public void setShipment(ShipmentDTO shipment) {
        this.shipment = shipment;
    }

    public ParticipantDTO getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantDTO participant) {
        this.participant = participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentParticipantMapDTO)) {
            return false;
        }

        ShipmentParticipantMapDTO shipmentParticipantMapDTO = (ShipmentParticipantMapDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shipmentParticipantMapDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentParticipantMapDTO{" +
            "id=" + getId() +
            ", responseStatus='" + getResponseStatus() + "'" +
            ", shipmentReceiptDate='" + getShipmentReceiptDate() + "'" +
            ", shipmentTestDate='" + getShipmentTestDate() + "'" +
            ", shipmentTestReportDate='" + getShipmentTestReportDate() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", evaluatedAt='" + getEvaluatedAt() + "'" +
            ", isExcluded='" + getIsExcluded() + "'" +
            ", isResponseLate='" + getIsResponseLate() + "'" +
            ", isPtTestNotPerformed='" + getIsPtTestNotPerformed() + "'" +
            ", ptTestNotPerformedComments='" + getPtTestNotPerformedComments() + "'" +
            ", supervisorApproved='" + getSupervisorApproved() + "'" +
            ", participantSupervisor='" + getParticipantSupervisor() + "'" +
            ", userComment='" + getUserComment() + "'" +
            ", shipmentScore=" + getShipmentScore() +
            ", documentationScore=" + getDocumentationScore() +
            ", finalResult='" + getFinalResult() + "'" +
            ", failureReason='" + getFailureReason() + "'" +
            ", evaluationComment='" + getEvaluationComment() + "'" +
            ", isFollowup='" + getIsFollowup() + "'" +
            ", manualOverride='" + getManualOverride() + "'" +
            ", qcStatus='" + getQcStatus() + "'" +
            ", qcDate='" + getQcDate() + "'" +
            ", qcDoneBy='" + getQcDoneBy() + "'" +
            ", syncedToMobile='" + getSyncedToMobile() + "'" +
            ", syncedOn='" + getSyncedOn() + "'" +
            ", modeOfReceipt=" + getModeOfReceipt() +
            ", notTestedReason=" + getNotTestedReason() +
            ", shipment=" + getShipment() +
            ", participant=" + getParticipant() +
            "}";
    }
}
