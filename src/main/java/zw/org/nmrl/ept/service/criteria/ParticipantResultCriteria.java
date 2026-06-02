package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.ParticipantResult} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.ParticipantResultResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /participant-results?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantResultCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reportedQualitativeResult;

    private DoubleFilter reportedQuantitativeValue;

    private StringFilter unit;

    private StringFilter lotNumber;

    private LocalDateFilter expiryDate;

    private DoubleFilter zScore;

    private DoubleFilter calculatedScore;

    private StringFilter comments;

    private LongFilter assayId;

    private LongFilter testKitId;

    private LongFilter sampleId;

    private LongFilter shipmentParticipantMapId;

    private Boolean distinct;

    public ParticipantResultCriteria() {}

    public ParticipantResultCriteria(ParticipantResultCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reportedQualitativeResult = other.optionalReportedQualitativeResult().map(StringFilter::copy).orElse(null);
        this.reportedQuantitativeValue = other.optionalReportedQuantitativeValue().map(DoubleFilter::copy).orElse(null);
        this.unit = other.optionalUnit().map(StringFilter::copy).orElse(null);
        this.lotNumber = other.optionalLotNumber().map(StringFilter::copy).orElse(null);
        this.expiryDate = other.optionalExpiryDate().map(LocalDateFilter::copy).orElse(null);
        this.zScore = other.optionalzScore().map(DoubleFilter::copy).orElse(null);
        this.calculatedScore = other.optionalCalculatedScore().map(DoubleFilter::copy).orElse(null);
        this.comments = other.optionalComments().map(StringFilter::copy).orElse(null);
        this.assayId = other.optionalAssayId().map(LongFilter::copy).orElse(null);
        this.testKitId = other.optionalTestKitId().map(LongFilter::copy).orElse(null);
        this.sampleId = other.optionalSampleId().map(LongFilter::copy).orElse(null);
        this.shipmentParticipantMapId = other.optionalShipmentParticipantMapId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ParticipantResultCriteria copy() {
        return new ParticipantResultCriteria(this);
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

    public StringFilter getReportedQualitativeResult() {
        return reportedQualitativeResult;
    }

    public Optional<StringFilter> optionalReportedQualitativeResult() {
        return Optional.ofNullable(reportedQualitativeResult);
    }

    public StringFilter reportedQualitativeResult() {
        if (reportedQualitativeResult == null) {
            setReportedQualitativeResult(new StringFilter());
        }
        return reportedQualitativeResult;
    }

    public void setReportedQualitativeResult(StringFilter reportedQualitativeResult) {
        this.reportedQualitativeResult = reportedQualitativeResult;
    }

    public DoubleFilter getReportedQuantitativeValue() {
        return reportedQuantitativeValue;
    }

    public Optional<DoubleFilter> optionalReportedQuantitativeValue() {
        return Optional.ofNullable(reportedQuantitativeValue);
    }

    public DoubleFilter reportedQuantitativeValue() {
        if (reportedQuantitativeValue == null) {
            setReportedQuantitativeValue(new DoubleFilter());
        }
        return reportedQuantitativeValue;
    }

    public void setReportedQuantitativeValue(DoubleFilter reportedQuantitativeValue) {
        this.reportedQuantitativeValue = reportedQuantitativeValue;
    }

    public StringFilter getUnit() {
        return unit;
    }

    public Optional<StringFilter> optionalUnit() {
        return Optional.ofNullable(unit);
    }

    public StringFilter unit() {
        if (unit == null) {
            setUnit(new StringFilter());
        }
        return unit;
    }

    public void setUnit(StringFilter unit) {
        this.unit = unit;
    }

    public StringFilter getLotNumber() {
        return lotNumber;
    }

    public Optional<StringFilter> optionalLotNumber() {
        return Optional.ofNullable(lotNumber);
    }

    public StringFilter lotNumber() {
        if (lotNumber == null) {
            setLotNumber(new StringFilter());
        }
        return lotNumber;
    }

    public void setLotNumber(StringFilter lotNumber) {
        this.lotNumber = lotNumber;
    }

    public LocalDateFilter getExpiryDate() {
        return expiryDate;
    }

    public Optional<LocalDateFilter> optionalExpiryDate() {
        return Optional.ofNullable(expiryDate);
    }

    public LocalDateFilter expiryDate() {
        if (expiryDate == null) {
            setExpiryDate(new LocalDateFilter());
        }
        return expiryDate;
    }

    public void setExpiryDate(LocalDateFilter expiryDate) {
        this.expiryDate = expiryDate;
    }

    public DoubleFilter getzScore() {
        return zScore;
    }

    public Optional<DoubleFilter> optionalzScore() {
        return Optional.ofNullable(zScore);
    }

    public DoubleFilter zScore() {
        if (zScore == null) {
            setzScore(new DoubleFilter());
        }
        return zScore;
    }

    public void setzScore(DoubleFilter zScore) {
        this.zScore = zScore;
    }

    public DoubleFilter getCalculatedScore() {
        return calculatedScore;
    }

    public Optional<DoubleFilter> optionalCalculatedScore() {
        return Optional.ofNullable(calculatedScore);
    }

    public DoubleFilter calculatedScore() {
        if (calculatedScore == null) {
            setCalculatedScore(new DoubleFilter());
        }
        return calculatedScore;
    }

    public void setCalculatedScore(DoubleFilter calculatedScore) {
        this.calculatedScore = calculatedScore;
    }

    public StringFilter getComments() {
        return comments;
    }

    public Optional<StringFilter> optionalComments() {
        return Optional.ofNullable(comments);
    }

    public StringFilter comments() {
        if (comments == null) {
            setComments(new StringFilter());
        }
        return comments;
    }

    public void setComments(StringFilter comments) {
        this.comments = comments;
    }

    public LongFilter getAssayId() {
        return assayId;
    }

    public Optional<LongFilter> optionalAssayId() {
        return Optional.ofNullable(assayId);
    }

    public LongFilter assayId() {
        if (assayId == null) {
            setAssayId(new LongFilter());
        }
        return assayId;
    }

    public void setAssayId(LongFilter assayId) {
        this.assayId = assayId;
    }

    public LongFilter getTestKitId() {
        return testKitId;
    }

    public Optional<LongFilter> optionalTestKitId() {
        return Optional.ofNullable(testKitId);
    }

    public LongFilter testKitId() {
        if (testKitId == null) {
            setTestKitId(new LongFilter());
        }
        return testKitId;
    }

    public void setTestKitId(LongFilter testKitId) {
        this.testKitId = testKitId;
    }

    public LongFilter getSampleId() {
        return sampleId;
    }

    public Optional<LongFilter> optionalSampleId() {
        return Optional.ofNullable(sampleId);
    }

    public LongFilter sampleId() {
        if (sampleId == null) {
            setSampleId(new LongFilter());
        }
        return sampleId;
    }

    public void setSampleId(LongFilter sampleId) {
        this.sampleId = sampleId;
    }

    public LongFilter getShipmentParticipantMapId() {
        return shipmentParticipantMapId;
    }

    public Optional<LongFilter> optionalShipmentParticipantMapId() {
        return Optional.ofNullable(shipmentParticipantMapId);
    }

    public LongFilter shipmentParticipantMapId() {
        if (shipmentParticipantMapId == null) {
            setShipmentParticipantMapId(new LongFilter());
        }
        return shipmentParticipantMapId;
    }

    public void setShipmentParticipantMapId(LongFilter shipmentParticipantMapId) {
        this.shipmentParticipantMapId = shipmentParticipantMapId;
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
        final ParticipantResultCriteria that = (ParticipantResultCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reportedQualitativeResult, that.reportedQualitativeResult) &&
            Objects.equals(reportedQuantitativeValue, that.reportedQuantitativeValue) &&
            Objects.equals(unit, that.unit) &&
            Objects.equals(lotNumber, that.lotNumber) &&
            Objects.equals(expiryDate, that.expiryDate) &&
            Objects.equals(zScore, that.zScore) &&
            Objects.equals(calculatedScore, that.calculatedScore) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(assayId, that.assayId) &&
            Objects.equals(testKitId, that.testKitId) &&
            Objects.equals(sampleId, that.sampleId) &&
            Objects.equals(shipmentParticipantMapId, that.shipmentParticipantMapId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reportedQualitativeResult,
            reportedQuantitativeValue,
            unit,
            lotNumber,
            expiryDate,
            zScore,
            calculatedScore,
            comments,
            assayId,
            testKitId,
            sampleId,
            shipmentParticipantMapId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantResultCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReportedQualitativeResult().map(f -> "reportedQualitativeResult=" + f + ", ").orElse("") +
            optionalReportedQuantitativeValue().map(f -> "reportedQuantitativeValue=" + f + ", ").orElse("") +
            optionalUnit().map(f -> "unit=" + f + ", ").orElse("") +
            optionalLotNumber().map(f -> "lotNumber=" + f + ", ").orElse("") +
            optionalExpiryDate().map(f -> "expiryDate=" + f + ", ").orElse("") +
            optionalzScore().map(f -> "zScore=" + f + ", ").orElse("") +
            optionalCalculatedScore().map(f -> "calculatedScore=" + f + ", ").orElse("") +
            optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
            optionalAssayId().map(f -> "assayId=" + f + ", ").orElse("") +
            optionalTestKitId().map(f -> "testKitId=" + f + ", ").orElse("") +
            optionalSampleId().map(f -> "sampleId=" + f + ", ").orElse("") +
            optionalShipmentParticipantMapId().map(f -> "shipmentParticipantMapId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
