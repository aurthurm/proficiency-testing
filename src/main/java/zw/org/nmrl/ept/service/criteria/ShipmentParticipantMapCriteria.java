package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.FinalResult;
import zw.org.nmrl.ept.domain.enumeration.QcStatus;
import zw.org.nmrl.ept.domain.enumeration.ResponseStatus;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.ShipmentParticipantMap} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.ShipmentParticipantMapResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /shipment-participant-maps?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentParticipantMapCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ResponseStatus
     */
    public static class ResponseStatusFilter extends Filter<ResponseStatus> {

        public ResponseStatusFilter() {}

        public ResponseStatusFilter(ResponseStatusFilter filter) {
            super(filter);
        }

        @Override
        public ResponseStatusFilter copy() {
            return new ResponseStatusFilter(this);
        }
    }

    /**
     * Class for filtering FinalResult
     */
    public static class FinalResultFilter extends Filter<FinalResult> {

        public FinalResultFilter() {}

        public FinalResultFilter(FinalResultFilter filter) {
            super(filter);
        }

        @Override
        public FinalResultFilter copy() {
            return new FinalResultFilter(this);
        }
    }

    /**
     * Class for filtering QcStatus
     */
    public static class QcStatusFilter extends Filter<QcStatus> {

        public QcStatusFilter() {}

        public QcStatusFilter(QcStatusFilter filter) {
            super(filter);
        }

        @Override
        public QcStatusFilter copy() {
            return new QcStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ResponseStatusFilter responseStatus;

    private LocalDateFilter shipmentReceiptDate;

    private LocalDateFilter shipmentTestDate;

    private InstantFilter shipmentTestReportDate;

    private InstantFilter submittedAt;

    private InstantFilter evaluatedAt;

    private BooleanFilter isExcluded;

    private BooleanFilter isResponseLate;

    private BooleanFilter isPtTestNotPerformed;

    private StringFilter ptTestNotPerformedComments;

    private BooleanFilter supervisorApproved;

    private StringFilter participantSupervisor;

    private StringFilter userComment;

    private DoubleFilter shipmentScore;

    private DoubleFilter documentationScore;

    private FinalResultFilter finalResult;

    private StringFilter evaluationComment;

    private BooleanFilter isFollowup;

    private BooleanFilter manualOverride;

    private QcStatusFilter qcStatus;

    private LocalDateFilter qcDate;

    private StringFilter qcDoneBy;

    private BooleanFilter syncedToMobile;

    private InstantFilter syncedOn;

    private LongFilter resultsId;

    private LongFilter capaRecordsId;

    private LongFilter modeOfReceiptId;

    private LongFilter notTestedReasonId;

    private LongFilter shipmentId;

    private LongFilter participantId;

    private Boolean distinct;

    public ShipmentParticipantMapCriteria() {}

    public ShipmentParticipantMapCriteria(ShipmentParticipantMapCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.responseStatus = other.optionalResponseStatus().map(ResponseStatusFilter::copy).orElse(null);
        this.shipmentReceiptDate = other.optionalShipmentReceiptDate().map(LocalDateFilter::copy).orElse(null);
        this.shipmentTestDate = other.optionalShipmentTestDate().map(LocalDateFilter::copy).orElse(null);
        this.shipmentTestReportDate = other.optionalShipmentTestReportDate().map(InstantFilter::copy).orElse(null);
        this.submittedAt = other.optionalSubmittedAt().map(InstantFilter::copy).orElse(null);
        this.evaluatedAt = other.optionalEvaluatedAt().map(InstantFilter::copy).orElse(null);
        this.isExcluded = other.optionalIsExcluded().map(BooleanFilter::copy).orElse(null);
        this.isResponseLate = other.optionalIsResponseLate().map(BooleanFilter::copy).orElse(null);
        this.isPtTestNotPerformed = other.optionalIsPtTestNotPerformed().map(BooleanFilter::copy).orElse(null);
        this.ptTestNotPerformedComments = other.optionalPtTestNotPerformedComments().map(StringFilter::copy).orElse(null);
        this.supervisorApproved = other.optionalSupervisorApproved().map(BooleanFilter::copy).orElse(null);
        this.participantSupervisor = other.optionalParticipantSupervisor().map(StringFilter::copy).orElse(null);
        this.userComment = other.optionalUserComment().map(StringFilter::copy).orElse(null);
        this.shipmentScore = other.optionalShipmentScore().map(DoubleFilter::copy).orElse(null);
        this.documentationScore = other.optionalDocumentationScore().map(DoubleFilter::copy).orElse(null);
        this.finalResult = other.optionalFinalResult().map(FinalResultFilter::copy).orElse(null);
        this.evaluationComment = other.optionalEvaluationComment().map(StringFilter::copy).orElse(null);
        this.isFollowup = other.optionalIsFollowup().map(BooleanFilter::copy).orElse(null);
        this.manualOverride = other.optionalManualOverride().map(BooleanFilter::copy).orElse(null);
        this.qcStatus = other.optionalQcStatus().map(QcStatusFilter::copy).orElse(null);
        this.qcDate = other.optionalQcDate().map(LocalDateFilter::copy).orElse(null);
        this.qcDoneBy = other.optionalQcDoneBy().map(StringFilter::copy).orElse(null);
        this.syncedToMobile = other.optionalSyncedToMobile().map(BooleanFilter::copy).orElse(null);
        this.syncedOn = other.optionalSyncedOn().map(InstantFilter::copy).orElse(null);
        this.resultsId = other.optionalResultsId().map(LongFilter::copy).orElse(null);
        this.capaRecordsId = other.optionalCapaRecordsId().map(LongFilter::copy).orElse(null);
        this.modeOfReceiptId = other.optionalModeOfReceiptId().map(LongFilter::copy).orElse(null);
        this.notTestedReasonId = other.optionalNotTestedReasonId().map(LongFilter::copy).orElse(null);
        this.shipmentId = other.optionalShipmentId().map(LongFilter::copy).orElse(null);
        this.participantId = other.optionalParticipantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ShipmentParticipantMapCriteria copy() {
        return new ShipmentParticipantMapCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ResponseStatusFilter getResponseStatus() {
        return responseStatus;
    }

    public Optional<ResponseStatusFilter> optionalResponseStatus() {
        return Optional.ofNullable(responseStatus);
    }

    public ResponseStatusFilter responseStatus() {
        if (responseStatus == null) {
            setResponseStatus(new ResponseStatusFilter());
        }
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatusFilter responseStatus) {
        this.responseStatus = responseStatus;
    }

    public LocalDateFilter getShipmentReceiptDate() {
        return shipmentReceiptDate;
    }

    public Optional<LocalDateFilter> optionalShipmentReceiptDate() {
        return Optional.ofNullable(shipmentReceiptDate);
    }

    public LocalDateFilter shipmentReceiptDate() {
        if (shipmentReceiptDate == null) {
            setShipmentReceiptDate(new LocalDateFilter());
        }
        return shipmentReceiptDate;
    }

    public void setShipmentReceiptDate(LocalDateFilter shipmentReceiptDate) {
        this.shipmentReceiptDate = shipmentReceiptDate;
    }

    public LocalDateFilter getShipmentTestDate() {
        return shipmentTestDate;
    }

    public Optional<LocalDateFilter> optionalShipmentTestDate() {
        return Optional.ofNullable(shipmentTestDate);
    }

    public LocalDateFilter shipmentTestDate() {
        if (shipmentTestDate == null) {
            setShipmentTestDate(new LocalDateFilter());
        }
        return shipmentTestDate;
    }

    public void setShipmentTestDate(LocalDateFilter shipmentTestDate) {
        this.shipmentTestDate = shipmentTestDate;
    }

    public InstantFilter getShipmentTestReportDate() {
        return shipmentTestReportDate;
    }

    public Optional<InstantFilter> optionalShipmentTestReportDate() {
        return Optional.ofNullable(shipmentTestReportDate);
    }

    public InstantFilter shipmentTestReportDate() {
        if (shipmentTestReportDate == null) {
            setShipmentTestReportDate(new InstantFilter());
        }
        return shipmentTestReportDate;
    }

    public void setShipmentTestReportDate(InstantFilter shipmentTestReportDate) {
        this.shipmentTestReportDate = shipmentTestReportDate;
    }

    public InstantFilter getSubmittedAt() {
        return submittedAt;
    }

    public Optional<InstantFilter> optionalSubmittedAt() {
        return Optional.ofNullable(submittedAt);
    }

    public InstantFilter submittedAt() {
        if (submittedAt == null) {
            setSubmittedAt(new InstantFilter());
        }
        return submittedAt;
    }

    public void setSubmittedAt(InstantFilter submittedAt) {
        this.submittedAt = submittedAt;
    }

    public InstantFilter getEvaluatedAt() {
        return evaluatedAt;
    }

    public Optional<InstantFilter> optionalEvaluatedAt() {
        return Optional.ofNullable(evaluatedAt);
    }

    public InstantFilter evaluatedAt() {
        if (evaluatedAt == null) {
            setEvaluatedAt(new InstantFilter());
        }
        return evaluatedAt;
    }

    public void setEvaluatedAt(InstantFilter evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public BooleanFilter getIsExcluded() {
        return isExcluded;
    }

    public Optional<BooleanFilter> optionalIsExcluded() {
        return Optional.ofNullable(isExcluded);
    }

    public BooleanFilter isExcluded() {
        if (isExcluded == null) {
            setIsExcluded(new BooleanFilter());
        }
        return isExcluded;
    }

    public void setIsExcluded(BooleanFilter isExcluded) {
        this.isExcluded = isExcluded;
    }

    public BooleanFilter getIsResponseLate() {
        return isResponseLate;
    }

    public Optional<BooleanFilter> optionalIsResponseLate() {
        return Optional.ofNullable(isResponseLate);
    }

    public BooleanFilter isResponseLate() {
        if (isResponseLate == null) {
            setIsResponseLate(new BooleanFilter());
        }
        return isResponseLate;
    }

    public void setIsResponseLate(BooleanFilter isResponseLate) {
        this.isResponseLate = isResponseLate;
    }

    public BooleanFilter getIsPtTestNotPerformed() {
        return isPtTestNotPerformed;
    }

    public Optional<BooleanFilter> optionalIsPtTestNotPerformed() {
        return Optional.ofNullable(isPtTestNotPerformed);
    }

    public BooleanFilter isPtTestNotPerformed() {
        if (isPtTestNotPerformed == null) {
            setIsPtTestNotPerformed(new BooleanFilter());
        }
        return isPtTestNotPerformed;
    }

    public void setIsPtTestNotPerformed(BooleanFilter isPtTestNotPerformed) {
        this.isPtTestNotPerformed = isPtTestNotPerformed;
    }

    public StringFilter getPtTestNotPerformedComments() {
        return ptTestNotPerformedComments;
    }

    public Optional<StringFilter> optionalPtTestNotPerformedComments() {
        return Optional.ofNullable(ptTestNotPerformedComments);
    }

    public StringFilter ptTestNotPerformedComments() {
        if (ptTestNotPerformedComments == null) {
            setPtTestNotPerformedComments(new StringFilter());
        }
        return ptTestNotPerformedComments;
    }

    public void setPtTestNotPerformedComments(StringFilter ptTestNotPerformedComments) {
        this.ptTestNotPerformedComments = ptTestNotPerformedComments;
    }

    public BooleanFilter getSupervisorApproved() {
        return supervisorApproved;
    }

    public Optional<BooleanFilter> optionalSupervisorApproved() {
        return Optional.ofNullable(supervisorApproved);
    }

    public BooleanFilter supervisorApproved() {
        if (supervisorApproved == null) {
            setSupervisorApproved(new BooleanFilter());
        }
        return supervisorApproved;
    }

    public void setSupervisorApproved(BooleanFilter supervisorApproved) {
        this.supervisorApproved = supervisorApproved;
    }

    public StringFilter getParticipantSupervisor() {
        return participantSupervisor;
    }

    public Optional<StringFilter> optionalParticipantSupervisor() {
        return Optional.ofNullable(participantSupervisor);
    }

    public StringFilter participantSupervisor() {
        if (participantSupervisor == null) {
            setParticipantSupervisor(new StringFilter());
        }
        return participantSupervisor;
    }

    public void setParticipantSupervisor(StringFilter participantSupervisor) {
        this.participantSupervisor = participantSupervisor;
    }

    public StringFilter getUserComment() {
        return userComment;
    }

    public Optional<StringFilter> optionalUserComment() {
        return Optional.ofNullable(userComment);
    }

    public StringFilter userComment() {
        if (userComment == null) {
            setUserComment(new StringFilter());
        }
        return userComment;
    }

    public void setUserComment(StringFilter userComment) {
        this.userComment = userComment;
    }

    public DoubleFilter getShipmentScore() {
        return shipmentScore;
    }

    public Optional<DoubleFilter> optionalShipmentScore() {
        return Optional.ofNullable(shipmentScore);
    }

    public DoubleFilter shipmentScore() {
        if (shipmentScore == null) {
            setShipmentScore(new DoubleFilter());
        }
        return shipmentScore;
    }

    public void setShipmentScore(DoubleFilter shipmentScore) {
        this.shipmentScore = shipmentScore;
    }

    public DoubleFilter getDocumentationScore() {
        return documentationScore;
    }

    public Optional<DoubleFilter> optionalDocumentationScore() {
        return Optional.ofNullable(documentationScore);
    }

    public DoubleFilter documentationScore() {
        if (documentationScore == null) {
            setDocumentationScore(new DoubleFilter());
        }
        return documentationScore;
    }

    public void setDocumentationScore(DoubleFilter documentationScore) {
        this.documentationScore = documentationScore;
    }

    public FinalResultFilter getFinalResult() {
        return finalResult;
    }

    public Optional<FinalResultFilter> optionalFinalResult() {
        return Optional.ofNullable(finalResult);
    }

    public FinalResultFilter finalResult() {
        if (finalResult == null) {
            setFinalResult(new FinalResultFilter());
        }
        return finalResult;
    }

    public void setFinalResult(FinalResultFilter finalResult) {
        this.finalResult = finalResult;
    }

    public StringFilter getEvaluationComment() {
        return evaluationComment;
    }

    public Optional<StringFilter> optionalEvaluationComment() {
        return Optional.ofNullable(evaluationComment);
    }

    public StringFilter evaluationComment() {
        if (evaluationComment == null) {
            setEvaluationComment(new StringFilter());
        }
        return evaluationComment;
    }

    public void setEvaluationComment(StringFilter evaluationComment) {
        this.evaluationComment = evaluationComment;
    }

    public BooleanFilter getIsFollowup() {
        return isFollowup;
    }

    public Optional<BooleanFilter> optionalIsFollowup() {
        return Optional.ofNullable(isFollowup);
    }

    public BooleanFilter isFollowup() {
        if (isFollowup == null) {
            setIsFollowup(new BooleanFilter());
        }
        return isFollowup;
    }

    public void setIsFollowup(BooleanFilter isFollowup) {
        this.isFollowup = isFollowup;
    }

    public BooleanFilter getManualOverride() {
        return manualOverride;
    }

    public Optional<BooleanFilter> optionalManualOverride() {
        return Optional.ofNullable(manualOverride);
    }

    public BooleanFilter manualOverride() {
        if (manualOverride == null) {
            setManualOverride(new BooleanFilter());
        }
        return manualOverride;
    }

    public void setManualOverride(BooleanFilter manualOverride) {
        this.manualOverride = manualOverride;
    }

    public QcStatusFilter getQcStatus() {
        return qcStatus;
    }

    public Optional<QcStatusFilter> optionalQcStatus() {
        return Optional.ofNullable(qcStatus);
    }

    public QcStatusFilter qcStatus() {
        if (qcStatus == null) {
            setQcStatus(new QcStatusFilter());
        }
        return qcStatus;
    }

    public void setQcStatus(QcStatusFilter qcStatus) {
        this.qcStatus = qcStatus;
    }

    public LocalDateFilter getQcDate() {
        return qcDate;
    }

    public Optional<LocalDateFilter> optionalQcDate() {
        return Optional.ofNullable(qcDate);
    }

    public LocalDateFilter qcDate() {
        if (qcDate == null) {
            setQcDate(new LocalDateFilter());
        }
        return qcDate;
    }

    public void setQcDate(LocalDateFilter qcDate) {
        this.qcDate = qcDate;
    }

    public StringFilter getQcDoneBy() {
        return qcDoneBy;
    }

    public Optional<StringFilter> optionalQcDoneBy() {
        return Optional.ofNullable(qcDoneBy);
    }

    public StringFilter qcDoneBy() {
        if (qcDoneBy == null) {
            setQcDoneBy(new StringFilter());
        }
        return qcDoneBy;
    }

    public void setQcDoneBy(StringFilter qcDoneBy) {
        this.qcDoneBy = qcDoneBy;
    }

    public BooleanFilter getSyncedToMobile() {
        return syncedToMobile;
    }

    public Optional<BooleanFilter> optionalSyncedToMobile() {
        return Optional.ofNullable(syncedToMobile);
    }

    public BooleanFilter syncedToMobile() {
        if (syncedToMobile == null) {
            setSyncedToMobile(new BooleanFilter());
        }
        return syncedToMobile;
    }

    public void setSyncedToMobile(BooleanFilter syncedToMobile) {
        this.syncedToMobile = syncedToMobile;
    }

    public InstantFilter getSyncedOn() {
        return syncedOn;
    }

    public Optional<InstantFilter> optionalSyncedOn() {
        return Optional.ofNullable(syncedOn);
    }

    public InstantFilter syncedOn() {
        if (syncedOn == null) {
            setSyncedOn(new InstantFilter());
        }
        return syncedOn;
    }

    public void setSyncedOn(InstantFilter syncedOn) {
        this.syncedOn = syncedOn;
    }

    public LongFilter getResultsId() {
        return resultsId;
    }

    public Optional<LongFilter> optionalResultsId() {
        return Optional.ofNullable(resultsId);
    }

    public LongFilter resultsId() {
        if (resultsId == null) {
            setResultsId(new LongFilter());
        }
        return resultsId;
    }

    public void setResultsId(LongFilter resultsId) {
        this.resultsId = resultsId;
    }

    public LongFilter getCapaRecordsId() {
        return capaRecordsId;
    }

    public Optional<LongFilter> optionalCapaRecordsId() {
        return Optional.ofNullable(capaRecordsId);
    }

    public LongFilter capaRecordsId() {
        if (capaRecordsId == null) {
            setCapaRecordsId(new LongFilter());
        }
        return capaRecordsId;
    }

    public void setCapaRecordsId(LongFilter capaRecordsId) {
        this.capaRecordsId = capaRecordsId;
    }

    public LongFilter getModeOfReceiptId() {
        return modeOfReceiptId;
    }

    public Optional<LongFilter> optionalModeOfReceiptId() {
        return Optional.ofNullable(modeOfReceiptId);
    }

    public LongFilter modeOfReceiptId() {
        if (modeOfReceiptId == null) {
            setModeOfReceiptId(new LongFilter());
        }
        return modeOfReceiptId;
    }

    public void setModeOfReceiptId(LongFilter modeOfReceiptId) {
        this.modeOfReceiptId = modeOfReceiptId;
    }

    public LongFilter getNotTestedReasonId() {
        return notTestedReasonId;
    }

    public Optional<LongFilter> optionalNotTestedReasonId() {
        return Optional.ofNullable(notTestedReasonId);
    }

    public LongFilter notTestedReasonId() {
        if (notTestedReasonId == null) {
            setNotTestedReasonId(new LongFilter());
        }
        return notTestedReasonId;
    }

    public void setNotTestedReasonId(LongFilter notTestedReasonId) {
        this.notTestedReasonId = notTestedReasonId;
    }

    public LongFilter getShipmentId() {
        return shipmentId;
    }

    public Optional<LongFilter> optionalShipmentId() {
        return Optional.ofNullable(shipmentId);
    }

    public LongFilter shipmentId() {
        if (shipmentId == null) {
            setShipmentId(new LongFilter());
        }
        return shipmentId;
    }

    public void setShipmentId(LongFilter shipmentId) {
        this.shipmentId = shipmentId;
    }

    public LongFilter getParticipantId() {
        return participantId;
    }

    public Optional<LongFilter> optionalParticipantId() {
        return Optional.ofNullable(participantId);
    }

    public LongFilter participantId() {
        if (participantId == null) {
            setParticipantId(new LongFilter());
        }
        return participantId;
    }

    public void setParticipantId(LongFilter participantId) {
        this.participantId = participantId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ShipmentParticipantMapCriteria that = (ShipmentParticipantMapCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(responseStatus, that.responseStatus) &&
            Objects.equals(shipmentReceiptDate, that.shipmentReceiptDate) &&
            Objects.equals(shipmentTestDate, that.shipmentTestDate) &&
            Objects.equals(shipmentTestReportDate, that.shipmentTestReportDate) &&
            Objects.equals(submittedAt, that.submittedAt) &&
            Objects.equals(evaluatedAt, that.evaluatedAt) &&
            Objects.equals(isExcluded, that.isExcluded) &&
            Objects.equals(isResponseLate, that.isResponseLate) &&
            Objects.equals(isPtTestNotPerformed, that.isPtTestNotPerformed) &&
            Objects.equals(ptTestNotPerformedComments, that.ptTestNotPerformedComments) &&
            Objects.equals(supervisorApproved, that.supervisorApproved) &&
            Objects.equals(participantSupervisor, that.participantSupervisor) &&
            Objects.equals(userComment, that.userComment) &&
            Objects.equals(shipmentScore, that.shipmentScore) &&
            Objects.equals(documentationScore, that.documentationScore) &&
            Objects.equals(finalResult, that.finalResult) &&
            Objects.equals(evaluationComment, that.evaluationComment) &&
            Objects.equals(isFollowup, that.isFollowup) &&
            Objects.equals(manualOverride, that.manualOverride) &&
            Objects.equals(qcStatus, that.qcStatus) &&
            Objects.equals(qcDate, that.qcDate) &&
            Objects.equals(qcDoneBy, that.qcDoneBy) &&
            Objects.equals(syncedToMobile, that.syncedToMobile) &&
            Objects.equals(syncedOn, that.syncedOn) &&
            Objects.equals(resultsId, that.resultsId) &&
            Objects.equals(capaRecordsId, that.capaRecordsId) &&
            Objects.equals(modeOfReceiptId, that.modeOfReceiptId) &&
            Objects.equals(notTestedReasonId, that.notTestedReasonId) &&
            Objects.equals(shipmentId, that.shipmentId) &&
            Objects.equals(participantId, that.participantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            responseStatus,
            shipmentReceiptDate,
            shipmentTestDate,
            shipmentTestReportDate,
            submittedAt,
            evaluatedAt,
            isExcluded,
            isResponseLate,
            isPtTestNotPerformed,
            ptTestNotPerformedComments,
            supervisorApproved,
            participantSupervisor,
            userComment,
            shipmentScore,
            documentationScore,
            finalResult,
            evaluationComment,
            isFollowup,
            manualOverride,
            qcStatus,
            qcDate,
            qcDoneBy,
            syncedToMobile,
            syncedOn,
            resultsId,
            capaRecordsId,
            modeOfReceiptId,
            notTestedReasonId,
            shipmentId,
            participantId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentParticipantMapCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalResponseStatus().map(f -> "responseStatus=" + f + ", ").orElse("") +
            optionalShipmentReceiptDate().map(f -> "shipmentReceiptDate=" + f + ", ").orElse("") +
            optionalShipmentTestDate().map(f -> "shipmentTestDate=" + f + ", ").orElse("") +
            optionalShipmentTestReportDate().map(f -> "shipmentTestReportDate=" + f + ", ").orElse("") +
            optionalSubmittedAt().map(f -> "submittedAt=" + f + ", ").orElse("") +
            optionalEvaluatedAt().map(f -> "evaluatedAt=" + f + ", ").orElse("") +
            optionalIsExcluded().map(f -> "isExcluded=" + f + ", ").orElse("") +
            optionalIsResponseLate().map(f -> "isResponseLate=" + f + ", ").orElse("") +
            optionalIsPtTestNotPerformed().map(f -> "isPtTestNotPerformed=" + f + ", ").orElse("") +
            optionalPtTestNotPerformedComments().map(f -> "ptTestNotPerformedComments=" + f + ", ").orElse("") +
            optionalSupervisorApproved().map(f -> "supervisorApproved=" + f + ", ").orElse("") +
            optionalParticipantSupervisor().map(f -> "participantSupervisor=" + f + ", ").orElse("") +
            optionalUserComment().map(f -> "userComment=" + f + ", ").orElse("") +
            optionalShipmentScore().map(f -> "shipmentScore=" + f + ", ").orElse("") +
            optionalDocumentationScore().map(f -> "documentationScore=" + f + ", ").orElse("") +
            optionalFinalResult().map(f -> "finalResult=" + f + ", ").orElse("") +
            optionalEvaluationComment().map(f -> "evaluationComment=" + f + ", ").orElse("") +
            optionalIsFollowup().map(f -> "isFollowup=" + f + ", ").orElse("") +
            optionalManualOverride().map(f -> "manualOverride=" + f + ", ").orElse("") +
            optionalQcStatus().map(f -> "qcStatus=" + f + ", ").orElse("") +
            optionalQcDate().map(f -> "qcDate=" + f + ", ").orElse("") +
            optionalQcDoneBy().map(f -> "qcDoneBy=" + f + ", ").orElse("") +
            optionalSyncedToMobile().map(f -> "syncedToMobile=" + f + ", ").orElse("") +
            optionalSyncedOn().map(f -> "syncedOn=" + f + ", ").orElse("") +
            optionalResultsId().map(f -> "resultsId=" + f + ", ").orElse("") +
            optionalCapaRecordsId().map(f -> "capaRecordsId=" + f + ", ").orElse("") +
            optionalModeOfReceiptId().map(f -> "modeOfReceiptId=" + f + ", ").orElse("") +
            optionalNotTestedReasonId().map(f -> "notTestedReasonId=" + f + ", ").orElse("") +
            optionalShipmentId().map(f -> "shipmentId=" + f + ", ").orElse("") +
            optionalParticipantId().map(f -> "participantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
