package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.EnrollmentStatus;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.Enrollment} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.EnrollmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /enrollments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnrollmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EnrollmentStatus
     */
    public static class EnrollmentStatusFilter extends Filter<EnrollmentStatus> {

        public EnrollmentStatusFilter() {}

        public EnrollmentStatusFilter(EnrollmentStatusFilter filter) {
            super(filter);
        }

        @Override
        public EnrollmentStatusFilter copy() {
            return new EnrollmentStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private EnrollmentStatusFilter status;

    private LocalDateFilter enrolledOn;

    private LocalDateFilter withdrawnOn;

    private LongFilter participantId;

    private LongFilter schemeId;

    private Boolean distinct;

    public EnrollmentCriteria() {}

    public EnrollmentCriteria(EnrollmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(EnrollmentStatusFilter::copy).orElse(null);
        this.enrolledOn = other.optionalEnrolledOn().map(LocalDateFilter::copy).orElse(null);
        this.withdrawnOn = other.optionalWithdrawnOn().map(LocalDateFilter::copy).orElse(null);
        this.participantId = other.optionalParticipantId().map(LongFilter::copy).orElse(null);
        this.schemeId = other.optionalSchemeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EnrollmentCriteria copy() {
        return new EnrollmentCriteria(this);
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

    public EnrollmentStatusFilter getStatus() {
        return status;
    }

    public Optional<EnrollmentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public EnrollmentStatusFilter status() {
        if (status == null) {
            setStatus(new EnrollmentStatusFilter());
        }
        return status;
    }

    public void setStatus(EnrollmentStatusFilter status) {
        this.status = status;
    }

    public LocalDateFilter getEnrolledOn() {
        return enrolledOn;
    }

    public Optional<LocalDateFilter> optionalEnrolledOn() {
        return Optional.ofNullable(enrolledOn);
    }

    public LocalDateFilter enrolledOn() {
        if (enrolledOn == null) {
            setEnrolledOn(new LocalDateFilter());
        }
        return enrolledOn;
    }

    public void setEnrolledOn(LocalDateFilter enrolledOn) {
        this.enrolledOn = enrolledOn;
    }

    public LocalDateFilter getWithdrawnOn() {
        return withdrawnOn;
    }

    public Optional<LocalDateFilter> optionalWithdrawnOn() {
        return Optional.ofNullable(withdrawnOn);
    }

    public LocalDateFilter withdrawnOn() {
        if (withdrawnOn == null) {
            setWithdrawnOn(new LocalDateFilter());
        }
        return withdrawnOn;
    }

    public void setWithdrawnOn(LocalDateFilter withdrawnOn) {
        this.withdrawnOn = withdrawnOn;
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
        final EnrollmentCriteria that = (EnrollmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(enrolledOn, that.enrolledOn) &&
            Objects.equals(withdrawnOn, that.withdrawnOn) &&
            Objects.equals(participantId, that.participantId) &&
            Objects.equals(schemeId, that.schemeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, enrolledOn, withdrawnOn, participantId, schemeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnrollmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalEnrolledOn().map(f -> "enrolledOn=" + f + ", ").orElse("") +
            optionalWithdrawnOn().map(f -> "withdrawnOn=" + f + ", ").orElse("") +
            optionalParticipantId().map(f -> "participantId=" + f + ", ").orElse("") +
            optionalSchemeId().map(f -> "schemeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
