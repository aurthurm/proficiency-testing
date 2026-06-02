package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.ShipmentStatus;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.Shipment} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.ShipmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /shipments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ShipmentStatus
     */
    public static class ShipmentStatusFilter extends Filter<ShipmentStatus> {

        public ShipmentStatusFilter() {}

        public ShipmentStatusFilter(ShipmentStatusFilter filter) {
            super(filter);
        }

        @Override
        public ShipmentStatusFilter copy() {
            return new ShipmentStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private LocalDateFilter shipmentDate;

    private InstantFilter responseDeadline;

    private BooleanFilter responsesOpen;

    private BooleanFilter autoCloseAtDeadline;

    private BooleanFilter allowEditingResponse;

    private StringFilter issuingAuthority;

    private StringFilter coordinatorName;

    private StringFilter coordinatorEmail;

    private StringFilter coordinatorPhone;

    private IntegerFilter numberOfSamples;

    private IntegerFilter maxScore;

    private ShipmentStatusFilter status;

    private InstantFilter reportsGeneratedAt;

    private InstantFilter finalizedAt;

    private LongFilter samplesId;

    private LongFilter participantMapsId;

    private LongFilter distributionId;

    private LongFilter schemeId;

    private LongFilter certificateBatchesId;

    private Boolean distinct;

    public ShipmentCriteria() {}

    public ShipmentCriteria(ShipmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.shipmentDate = other.optionalShipmentDate().map(LocalDateFilter::copy).orElse(null);
        this.responseDeadline = other.optionalResponseDeadline().map(InstantFilter::copy).orElse(null);
        this.responsesOpen = other.optionalResponsesOpen().map(BooleanFilter::copy).orElse(null);
        this.autoCloseAtDeadline = other.optionalAutoCloseAtDeadline().map(BooleanFilter::copy).orElse(null);
        this.allowEditingResponse = other.optionalAllowEditingResponse().map(BooleanFilter::copy).orElse(null);
        this.issuingAuthority = other.optionalIssuingAuthority().map(StringFilter::copy).orElse(null);
        this.coordinatorName = other.optionalCoordinatorName().map(StringFilter::copy).orElse(null);
        this.coordinatorEmail = other.optionalCoordinatorEmail().map(StringFilter::copy).orElse(null);
        this.coordinatorPhone = other.optionalCoordinatorPhone().map(StringFilter::copy).orElse(null);
        this.numberOfSamples = other.optionalNumberOfSamples().map(IntegerFilter::copy).orElse(null);
        this.maxScore = other.optionalMaxScore().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ShipmentStatusFilter::copy).orElse(null);
        this.reportsGeneratedAt = other.optionalReportsGeneratedAt().map(InstantFilter::copy).orElse(null);
        this.finalizedAt = other.optionalFinalizedAt().map(InstantFilter::copy).orElse(null);
        this.samplesId = other.optionalSamplesId().map(LongFilter::copy).orElse(null);
        this.participantMapsId = other.optionalParticipantMapsId().map(LongFilter::copy).orElse(null);
        this.distributionId = other.optionalDistributionId().map(LongFilter::copy).orElse(null);
        this.schemeId = other.optionalSchemeId().map(LongFilter::copy).orElse(null);
        this.certificateBatchesId = other.optionalCertificateBatchesId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ShipmentCriteria copy() {
        return new ShipmentCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public LocalDateFilter getShipmentDate() {
        return shipmentDate;
    }

    public Optional<LocalDateFilter> optionalShipmentDate() {
        return Optional.ofNullable(shipmentDate);
    }

    public LocalDateFilter shipmentDate() {
        if (shipmentDate == null) {
            setShipmentDate(new LocalDateFilter());
        }
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateFilter shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public InstantFilter getResponseDeadline() {
        return responseDeadline;
    }

    public Optional<InstantFilter> optionalResponseDeadline() {
        return Optional.ofNullable(responseDeadline);
    }

    public InstantFilter responseDeadline() {
        if (responseDeadline == null) {
            setResponseDeadline(new InstantFilter());
        }
        return responseDeadline;
    }

    public void setResponseDeadline(InstantFilter responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public BooleanFilter getResponsesOpen() {
        return responsesOpen;
    }

    public Optional<BooleanFilter> optionalResponsesOpen() {
        return Optional.ofNullable(responsesOpen);
    }

    public BooleanFilter responsesOpen() {
        if (responsesOpen == null) {
            setResponsesOpen(new BooleanFilter());
        }
        return responsesOpen;
    }

    public void setResponsesOpen(BooleanFilter responsesOpen) {
        this.responsesOpen = responsesOpen;
    }

    public BooleanFilter getAutoCloseAtDeadline() {
        return autoCloseAtDeadline;
    }

    public Optional<BooleanFilter> optionalAutoCloseAtDeadline() {
        return Optional.ofNullable(autoCloseAtDeadline);
    }

    public BooleanFilter autoCloseAtDeadline() {
        if (autoCloseAtDeadline == null) {
            setAutoCloseAtDeadline(new BooleanFilter());
        }
        return autoCloseAtDeadline;
    }

    public void setAutoCloseAtDeadline(BooleanFilter autoCloseAtDeadline) {
        this.autoCloseAtDeadline = autoCloseAtDeadline;
    }

    public BooleanFilter getAllowEditingResponse() {
        return allowEditingResponse;
    }

    public Optional<BooleanFilter> optionalAllowEditingResponse() {
        return Optional.ofNullable(allowEditingResponse);
    }

    public BooleanFilter allowEditingResponse() {
        if (allowEditingResponse == null) {
            setAllowEditingResponse(new BooleanFilter());
        }
        return allowEditingResponse;
    }

    public void setAllowEditingResponse(BooleanFilter allowEditingResponse) {
        this.allowEditingResponse = allowEditingResponse;
    }

    public StringFilter getIssuingAuthority() {
        return issuingAuthority;
    }

    public Optional<StringFilter> optionalIssuingAuthority() {
        return Optional.ofNullable(issuingAuthority);
    }

    public StringFilter issuingAuthority() {
        if (issuingAuthority == null) {
            setIssuingAuthority(new StringFilter());
        }
        return issuingAuthority;
    }

    public void setIssuingAuthority(StringFilter issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public StringFilter getCoordinatorName() {
        return coordinatorName;
    }

    public Optional<StringFilter> optionalCoordinatorName() {
        return Optional.ofNullable(coordinatorName);
    }

    public StringFilter coordinatorName() {
        if (coordinatorName == null) {
            setCoordinatorName(new StringFilter());
        }
        return coordinatorName;
    }

    public void setCoordinatorName(StringFilter coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public StringFilter getCoordinatorEmail() {
        return coordinatorEmail;
    }

    public Optional<StringFilter> optionalCoordinatorEmail() {
        return Optional.ofNullable(coordinatorEmail);
    }

    public StringFilter coordinatorEmail() {
        if (coordinatorEmail == null) {
            setCoordinatorEmail(new StringFilter());
        }
        return coordinatorEmail;
    }

    public void setCoordinatorEmail(StringFilter coordinatorEmail) {
        this.coordinatorEmail = coordinatorEmail;
    }

    public StringFilter getCoordinatorPhone() {
        return coordinatorPhone;
    }

    public Optional<StringFilter> optionalCoordinatorPhone() {
        return Optional.ofNullable(coordinatorPhone);
    }

    public StringFilter coordinatorPhone() {
        if (coordinatorPhone == null) {
            setCoordinatorPhone(new StringFilter());
        }
        return coordinatorPhone;
    }

    public void setCoordinatorPhone(StringFilter coordinatorPhone) {
        this.coordinatorPhone = coordinatorPhone;
    }

    public IntegerFilter getNumberOfSamples() {
        return numberOfSamples;
    }

    public Optional<IntegerFilter> optionalNumberOfSamples() {
        return Optional.ofNullable(numberOfSamples);
    }

    public IntegerFilter numberOfSamples() {
        if (numberOfSamples == null) {
            setNumberOfSamples(new IntegerFilter());
        }
        return numberOfSamples;
    }

    public void setNumberOfSamples(IntegerFilter numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public IntegerFilter getMaxScore() {
        return maxScore;
    }

    public Optional<IntegerFilter> optionalMaxScore() {
        return Optional.ofNullable(maxScore);
    }

    public IntegerFilter maxScore() {
        if (maxScore == null) {
            setMaxScore(new IntegerFilter());
        }
        return maxScore;
    }

    public void setMaxScore(IntegerFilter maxScore) {
        this.maxScore = maxScore;
    }

    public ShipmentStatusFilter getStatus() {
        return status;
    }

    public Optional<ShipmentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ShipmentStatusFilter status() {
        if (status == null) {
            setStatus(new ShipmentStatusFilter());
        }
        return status;
    }

    public void setStatus(ShipmentStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getReportsGeneratedAt() {
        return reportsGeneratedAt;
    }

    public Optional<InstantFilter> optionalReportsGeneratedAt() {
        return Optional.ofNullable(reportsGeneratedAt);
    }

    public InstantFilter reportsGeneratedAt() {
        if (reportsGeneratedAt == null) {
            setReportsGeneratedAt(new InstantFilter());
        }
        return reportsGeneratedAt;
    }

    public void setReportsGeneratedAt(InstantFilter reportsGeneratedAt) {
        this.reportsGeneratedAt = reportsGeneratedAt;
    }

    public InstantFilter getFinalizedAt() {
        return finalizedAt;
    }

    public Optional<InstantFilter> optionalFinalizedAt() {
        return Optional.ofNullable(finalizedAt);
    }

    public InstantFilter finalizedAt() {
        if (finalizedAt == null) {
            setFinalizedAt(new InstantFilter());
        }
        return finalizedAt;
    }

    public void setFinalizedAt(InstantFilter finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public LongFilter getSamplesId() {
        return samplesId;
    }

    public Optional<LongFilter> optionalSamplesId() {
        return Optional.ofNullable(samplesId);
    }

    public LongFilter samplesId() {
        if (samplesId == null) {
            setSamplesId(new LongFilter());
        }
        return samplesId;
    }

    public void setSamplesId(LongFilter samplesId) {
        this.samplesId = samplesId;
    }

    public LongFilter getParticipantMapsId() {
        return participantMapsId;
    }

    public Optional<LongFilter> optionalParticipantMapsId() {
        return Optional.ofNullable(participantMapsId);
    }

    public LongFilter participantMapsId() {
        if (participantMapsId == null) {
            setParticipantMapsId(new LongFilter());
        }
        return participantMapsId;
    }

    public void setParticipantMapsId(LongFilter participantMapsId) {
        this.participantMapsId = participantMapsId;
    }

    public LongFilter getDistributionId() {
        return distributionId;
    }

    public Optional<LongFilter> optionalDistributionId() {
        return Optional.ofNullable(distributionId);
    }

    public LongFilter distributionId() {
        if (distributionId == null) {
            setDistributionId(new LongFilter());
        }
        return distributionId;
    }

    public void setDistributionId(LongFilter distributionId) {
        this.distributionId = distributionId;
    }

    public LongFilter getSchemeId() {
        return schemeId;
    }

    public Optional<LongFilter> optionalSchemeId() {
        return Optional.ofNullable(schemeId);
    }

    public LongFilter schemeId() {
        if (schemeId == null) {
            setSchemeId(new LongFilter());
        }
        return schemeId;
    }

    public void setSchemeId(LongFilter schemeId) {
        this.schemeId = schemeId;
    }

    public LongFilter getCertificateBatchesId() {
        return certificateBatchesId;
    }

    public Optional<LongFilter> optionalCertificateBatchesId() {
        return Optional.ofNullable(certificateBatchesId);
    }

    public LongFilter certificateBatchesId() {
        if (certificateBatchesId == null) {
            setCertificateBatchesId(new LongFilter());
        }
        return certificateBatchesId;
    }

    public void setCertificateBatchesId(LongFilter certificateBatchesId) {
        this.certificateBatchesId = certificateBatchesId;
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
        final ShipmentCriteria that = (ShipmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(shipmentDate, that.shipmentDate) &&
            Objects.equals(responseDeadline, that.responseDeadline) &&
            Objects.equals(responsesOpen, that.responsesOpen) &&
            Objects.equals(autoCloseAtDeadline, that.autoCloseAtDeadline) &&
            Objects.equals(allowEditingResponse, that.allowEditingResponse) &&
            Objects.equals(issuingAuthority, that.issuingAuthority) &&
            Objects.equals(coordinatorName, that.coordinatorName) &&
            Objects.equals(coordinatorEmail, that.coordinatorEmail) &&
            Objects.equals(coordinatorPhone, that.coordinatorPhone) &&
            Objects.equals(numberOfSamples, that.numberOfSamples) &&
            Objects.equals(maxScore, that.maxScore) &&
            Objects.equals(status, that.status) &&
            Objects.equals(reportsGeneratedAt, that.reportsGeneratedAt) &&
            Objects.equals(finalizedAt, that.finalizedAt) &&
            Objects.equals(samplesId, that.samplesId) &&
            Objects.equals(participantMapsId, that.participantMapsId) &&
            Objects.equals(distributionId, that.distributionId) &&
            Objects.equals(schemeId, that.schemeId) &&
            Objects.equals(certificateBatchesId, that.certificateBatchesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            shipmentDate,
            responseDeadline,
            responsesOpen,
            autoCloseAtDeadline,
            allowEditingResponse,
            issuingAuthority,
            coordinatorName,
            coordinatorEmail,
            coordinatorPhone,
            numberOfSamples,
            maxScore,
            status,
            reportsGeneratedAt,
            finalizedAt,
            samplesId,
            participantMapsId,
            distributionId,
            schemeId,
            certificateBatchesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalShipmentDate().map(f -> "shipmentDate=" + f + ", ").orElse("") +
            optionalResponseDeadline().map(f -> "responseDeadline=" + f + ", ").orElse("") +
            optionalResponsesOpen().map(f -> "responsesOpen=" + f + ", ").orElse("") +
            optionalAutoCloseAtDeadline().map(f -> "autoCloseAtDeadline=" + f + ", ").orElse("") +
            optionalAllowEditingResponse().map(f -> "allowEditingResponse=" + f + ", ").orElse("") +
            optionalIssuingAuthority().map(f -> "issuingAuthority=" + f + ", ").orElse("") +
            optionalCoordinatorName().map(f -> "coordinatorName=" + f + ", ").orElse("") +
            optionalCoordinatorEmail().map(f -> "coordinatorEmail=" + f + ", ").orElse("") +
            optionalCoordinatorPhone().map(f -> "coordinatorPhone=" + f + ", ").orElse("") +
            optionalNumberOfSamples().map(f -> "numberOfSamples=" + f + ", ").orElse("") +
            optionalMaxScore().map(f -> "maxScore=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalReportsGeneratedAt().map(f -> "reportsGeneratedAt=" + f + ", ").orElse("") +
            optionalFinalizedAt().map(f -> "finalizedAt=" + f + ", ").orElse("") +
            optionalSamplesId().map(f -> "samplesId=" + f + ", ").orElse("") +
            optionalParticipantMapsId().map(f -> "participantMapsId=" + f + ", ").orElse("") +
            optionalDistributionId().map(f -> "distributionId=" + f + ", ").orElse("") +
            optionalSchemeId().map(f -> "schemeId=" + f + ", ").orElse("") +
            optionalCertificateBatchesId().map(f -> "certificateBatchesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
