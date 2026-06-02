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
import zw.org.nmrl.ept.domain.enumeration.FinalResult;
import zw.org.nmrl.ept.domain.enumeration.QcStatus;
import zw.org.nmrl.ept.domain.enumeration.ResponseStatus;

/**
 * Participant's instance of a shipment + evaluation summary.
 */
@Entity
@Table(name = "shipment_participant_map")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentParticipantMap implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_status")
    private ResponseStatus responseStatus;

    @Column(name = "shipment_receipt_date")
    private LocalDate shipmentReceiptDate;

    @Column(name = "shipment_test_date")
    private LocalDate shipmentTestDate;

    @Column(name = "shipment_test_report_date")
    private Instant shipmentTestReportDate;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "evaluated_at")
    private Instant evaluatedAt;

    @Column(name = "is_excluded")
    private Boolean isExcluded;

    @Column(name = "is_response_late")
    private Boolean isResponseLate;

    @Column(name = "is_pt_test_not_performed")
    private Boolean isPtTestNotPerformed;

    @Column(name = "pt_test_not_performed_comments")
    private String ptTestNotPerformedComments;

    @Column(name = "supervisor_approved")
    private Boolean supervisorApproved;

    @Column(name = "participant_supervisor")
    private String participantSupervisor;

    @Column(name = "user_comment")
    private String userComment;

    @Column(name = "shipment_score")
    private Double shipmentScore;

    @Column(name = "documentation_score")
    private Double documentationScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_result")
    private FinalResult finalResult;

    @Lob
    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "evaluation_comment")
    private String evaluationComment;

    @Column(name = "is_followup")
    private Boolean isFollowup;

    @Column(name = "manual_override")
    private Boolean manualOverride;

    @Enumerated(EnumType.STRING)
    @Column(name = "qc_status")
    private QcStatus qcStatus;

    @Column(name = "qc_date")
    private LocalDate qcDate;

    @Column(name = "qc_done_by")
    private String qcDoneBy;

    @Column(name = "synced_to_mobile")
    private Boolean syncedToMobile;

    @Column(name = "synced_on")
    private Instant syncedOn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shipmentParticipantMap")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "assay", "testKit", "sample", "shipmentParticipantMap" }, allowSetters = true)
    private Set<ParticipantResult> resultses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shipmentParticipantMap")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "correctiveAction", "shipmentParticipantMap" }, allowSetters = true)
    private Set<CapaRecord> capaRecordses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private ModeOfReceipt modeOfReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    private NotTestedReason notTestedReason;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "sampleses", "participantMapses", "distribution", "scheme", "certificateBatcheses" },
        allowSetters = true
    )
    private Shipment shipment;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "shipmentMapses", "country", "enrollmentses", "customValueses", "dataManagerses" }, allowSetters = true)
    private Participant participant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShipmentParticipantMap id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public ShipmentParticipantMap responseStatus(ResponseStatus responseStatus) {
        this.setResponseStatus(responseStatus);
        return this;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public LocalDate getShipmentReceiptDate() {
        return this.shipmentReceiptDate;
    }

    public ShipmentParticipantMap shipmentReceiptDate(LocalDate shipmentReceiptDate) {
        this.setShipmentReceiptDate(shipmentReceiptDate);
        return this;
    }

    public void setShipmentReceiptDate(LocalDate shipmentReceiptDate) {
        this.shipmentReceiptDate = shipmentReceiptDate;
    }

    public LocalDate getShipmentTestDate() {
        return this.shipmentTestDate;
    }

    public ShipmentParticipantMap shipmentTestDate(LocalDate shipmentTestDate) {
        this.setShipmentTestDate(shipmentTestDate);
        return this;
    }

    public void setShipmentTestDate(LocalDate shipmentTestDate) {
        this.shipmentTestDate = shipmentTestDate;
    }

    public Instant getShipmentTestReportDate() {
        return this.shipmentTestReportDate;
    }

    public ShipmentParticipantMap shipmentTestReportDate(Instant shipmentTestReportDate) {
        this.setShipmentTestReportDate(shipmentTestReportDate);
        return this;
    }

    public void setShipmentTestReportDate(Instant shipmentTestReportDate) {
        this.shipmentTestReportDate = shipmentTestReportDate;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public ShipmentParticipantMap submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getEvaluatedAt() {
        return this.evaluatedAt;
    }

    public ShipmentParticipantMap evaluatedAt(Instant evaluatedAt) {
        this.setEvaluatedAt(evaluatedAt);
        return this;
    }

    public void setEvaluatedAt(Instant evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public Boolean getIsExcluded() {
        return this.isExcluded;
    }

    public ShipmentParticipantMap isExcluded(Boolean isExcluded) {
        this.setIsExcluded(isExcluded);
        return this;
    }

    public void setIsExcluded(Boolean isExcluded) {
        this.isExcluded = isExcluded;
    }

    public Boolean getIsResponseLate() {
        return this.isResponseLate;
    }

    public ShipmentParticipantMap isResponseLate(Boolean isResponseLate) {
        this.setIsResponseLate(isResponseLate);
        return this;
    }

    public void setIsResponseLate(Boolean isResponseLate) {
        this.isResponseLate = isResponseLate;
    }

    public Boolean getIsPtTestNotPerformed() {
        return this.isPtTestNotPerformed;
    }

    public ShipmentParticipantMap isPtTestNotPerformed(Boolean isPtTestNotPerformed) {
        this.setIsPtTestNotPerformed(isPtTestNotPerformed);
        return this;
    }

    public void setIsPtTestNotPerformed(Boolean isPtTestNotPerformed) {
        this.isPtTestNotPerformed = isPtTestNotPerformed;
    }

    public String getPtTestNotPerformedComments() {
        return this.ptTestNotPerformedComments;
    }

    public ShipmentParticipantMap ptTestNotPerformedComments(String ptTestNotPerformedComments) {
        this.setPtTestNotPerformedComments(ptTestNotPerformedComments);
        return this;
    }

    public void setPtTestNotPerformedComments(String ptTestNotPerformedComments) {
        this.ptTestNotPerformedComments = ptTestNotPerformedComments;
    }

    public Boolean getSupervisorApproved() {
        return this.supervisorApproved;
    }

    public ShipmentParticipantMap supervisorApproved(Boolean supervisorApproved) {
        this.setSupervisorApproved(supervisorApproved);
        return this;
    }

    public void setSupervisorApproved(Boolean supervisorApproved) {
        this.supervisorApproved = supervisorApproved;
    }

    public String getParticipantSupervisor() {
        return this.participantSupervisor;
    }

    public ShipmentParticipantMap participantSupervisor(String participantSupervisor) {
        this.setParticipantSupervisor(participantSupervisor);
        return this;
    }

    public void setParticipantSupervisor(String participantSupervisor) {
        this.participantSupervisor = participantSupervisor;
    }

    public String getUserComment() {
        return this.userComment;
    }

    public ShipmentParticipantMap userComment(String userComment) {
        this.setUserComment(userComment);
        return this;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public Double getShipmentScore() {
        return this.shipmentScore;
    }

    public ShipmentParticipantMap shipmentScore(Double shipmentScore) {
        this.setShipmentScore(shipmentScore);
        return this;
    }

    public void setShipmentScore(Double shipmentScore) {
        this.shipmentScore = shipmentScore;
    }

    public Double getDocumentationScore() {
        return this.documentationScore;
    }

    public ShipmentParticipantMap documentationScore(Double documentationScore) {
        this.setDocumentationScore(documentationScore);
        return this;
    }

    public void setDocumentationScore(Double documentationScore) {
        this.documentationScore = documentationScore;
    }

    public FinalResult getFinalResult() {
        return this.finalResult;
    }

    public ShipmentParticipantMap finalResult(FinalResult finalResult) {
        this.setFinalResult(finalResult);
        return this;
    }

    public void setFinalResult(FinalResult finalResult) {
        this.finalResult = finalResult;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public ShipmentParticipantMap failureReason(String failureReason) {
        this.setFailureReason(failureReason);
        return this;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getEvaluationComment() {
        return this.evaluationComment;
    }

    public ShipmentParticipantMap evaluationComment(String evaluationComment) {
        this.setEvaluationComment(evaluationComment);
        return this;
    }

    public void setEvaluationComment(String evaluationComment) {
        this.evaluationComment = evaluationComment;
    }

    public Boolean getIsFollowup() {
        return this.isFollowup;
    }

    public ShipmentParticipantMap isFollowup(Boolean isFollowup) {
        this.setIsFollowup(isFollowup);
        return this;
    }

    public void setIsFollowup(Boolean isFollowup) {
        this.isFollowup = isFollowup;
    }

    public Boolean getManualOverride() {
        return this.manualOverride;
    }

    public ShipmentParticipantMap manualOverride(Boolean manualOverride) {
        this.setManualOverride(manualOverride);
        return this;
    }

    public void setManualOverride(Boolean manualOverride) {
        this.manualOverride = manualOverride;
    }

    public QcStatus getQcStatus() {
        return this.qcStatus;
    }

    public ShipmentParticipantMap qcStatus(QcStatus qcStatus) {
        this.setQcStatus(qcStatus);
        return this;
    }

    public void setQcStatus(QcStatus qcStatus) {
        this.qcStatus = qcStatus;
    }

    public LocalDate getQcDate() {
        return this.qcDate;
    }

    public ShipmentParticipantMap qcDate(LocalDate qcDate) {
        this.setQcDate(qcDate);
        return this;
    }

    public void setQcDate(LocalDate qcDate) {
        this.qcDate = qcDate;
    }

    public String getQcDoneBy() {
        return this.qcDoneBy;
    }

    public ShipmentParticipantMap qcDoneBy(String qcDoneBy) {
        this.setQcDoneBy(qcDoneBy);
        return this;
    }

    public void setQcDoneBy(String qcDoneBy) {
        this.qcDoneBy = qcDoneBy;
    }

    public Boolean getSyncedToMobile() {
        return this.syncedToMobile;
    }

    public ShipmentParticipantMap syncedToMobile(Boolean syncedToMobile) {
        this.setSyncedToMobile(syncedToMobile);
        return this;
    }

    public void setSyncedToMobile(Boolean syncedToMobile) {
        this.syncedToMobile = syncedToMobile;
    }

    public Instant getSyncedOn() {
        return this.syncedOn;
    }

    public ShipmentParticipantMap syncedOn(Instant syncedOn) {
        this.setSyncedOn(syncedOn);
        return this;
    }

    public void setSyncedOn(Instant syncedOn) {
        this.syncedOn = syncedOn;
    }

    public Set<ParticipantResult> getResultses() {
        return this.resultses;
    }

    public void setResultses(Set<ParticipantResult> participantResults) {
        if (this.resultses != null) {
            this.resultses.forEach(i -> i.setShipmentParticipantMap(null));
        }
        if (participantResults != null) {
            participantResults.forEach(i -> i.setShipmentParticipantMap(this));
        }
        this.resultses = participantResults;
    }

    public ShipmentParticipantMap resultses(Set<ParticipantResult> participantResults) {
        this.setResultses(participantResults);
        return this;
    }

    public ShipmentParticipantMap addResults(ParticipantResult participantResult) {
        this.resultses.add(participantResult);
        participantResult.setShipmentParticipantMap(this);
        return this;
    }

    public ShipmentParticipantMap removeResults(ParticipantResult participantResult) {
        this.resultses.remove(participantResult);
        participantResult.setShipmentParticipantMap(null);
        return this;
    }

    public Set<CapaRecord> getCapaRecordses() {
        return this.capaRecordses;
    }

    public void setCapaRecordses(Set<CapaRecord> capaRecords) {
        if (this.capaRecordses != null) {
            this.capaRecordses.forEach(i -> i.setShipmentParticipantMap(null));
        }
        if (capaRecords != null) {
            capaRecords.forEach(i -> i.setShipmentParticipantMap(this));
        }
        this.capaRecordses = capaRecords;
    }

    public ShipmentParticipantMap capaRecordses(Set<CapaRecord> capaRecords) {
        this.setCapaRecordses(capaRecords);
        return this;
    }

    public ShipmentParticipantMap addCapaRecords(CapaRecord capaRecord) {
        this.capaRecordses.add(capaRecord);
        capaRecord.setShipmentParticipantMap(this);
        return this;
    }

    public ShipmentParticipantMap removeCapaRecords(CapaRecord capaRecord) {
        this.capaRecordses.remove(capaRecord);
        capaRecord.setShipmentParticipantMap(null);
        return this;
    }

    public ModeOfReceipt getModeOfReceipt() {
        return this.modeOfReceipt;
    }

    public void setModeOfReceipt(ModeOfReceipt modeOfReceipt) {
        this.modeOfReceipt = modeOfReceipt;
    }

    public ShipmentParticipantMap modeOfReceipt(ModeOfReceipt modeOfReceipt) {
        this.setModeOfReceipt(modeOfReceipt);
        return this;
    }

    public NotTestedReason getNotTestedReason() {
        return this.notTestedReason;
    }

    public void setNotTestedReason(NotTestedReason notTestedReason) {
        this.notTestedReason = notTestedReason;
    }

    public ShipmentParticipantMap notTestedReason(NotTestedReason notTestedReason) {
        this.setNotTestedReason(notTestedReason);
        return this;
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public ShipmentParticipantMap shipment(Shipment shipment) {
        this.setShipment(shipment);
        return this;
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ShipmentParticipantMap participant(Participant participant) {
        this.setParticipant(participant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentParticipantMap)) {
            return false;
        }
        return getId() != null && getId().equals(((ShipmentParticipantMap) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentParticipantMap{" +
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
            "}";
    }
}
