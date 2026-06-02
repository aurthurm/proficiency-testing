package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.CapaRecord} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.CapaRecordResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /capa-records?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CapaRecordCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter rootCause;

    private StringFilter actionTaken;

    private LocalDateFilter actionDate;

    private StatusFilter status;

    private LocalDateFilter followUpDate;

    private LongFilter correctiveActionId;

    private LongFilter shipmentParticipantMapId;

    private Boolean distinct;

    public CapaRecordCriteria() {}

    public CapaRecordCriteria(CapaRecordCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.rootCause = other.optionalRootCause().map(StringFilter::copy).orElse(null);
        this.actionTaken = other.optionalActionTaken().map(StringFilter::copy).orElse(null);
        this.actionDate = other.optionalActionDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StatusFilter::copy).orElse(null);
        this.followUpDate = other.optionalFollowUpDate().map(LocalDateFilter::copy).orElse(null);
        this.correctiveActionId = other.optionalCorrectiveActionId().map(LongFilter::copy).orElse(null);
        this.shipmentParticipantMapId = other.optionalShipmentParticipantMapId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CapaRecordCriteria copy() {
        return new CapaRecordCriteria(this);
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

    public StringFilter getRootCause() {
        return rootCause;
    }

    public Optional<StringFilter> optionalRootCause() {
        return Optional.ofNullable(rootCause);
    }

    public StringFilter rootCause() {
        if (rootCause == null) {
            setRootCause(new StringFilter());
        }
        return rootCause;
    }

    public void setRootCause(StringFilter rootCause) {
        this.rootCause = rootCause;
    }

    public StringFilter getActionTaken() {
        return actionTaken;
    }

    public Optional<StringFilter> optionalActionTaken() {
        return Optional.ofNullable(actionTaken);
    }

    public StringFilter actionTaken() {
        if (actionTaken == null) {
            setActionTaken(new StringFilter());
        }
        return actionTaken;
    }

    public void setActionTaken(StringFilter actionTaken) {
        this.actionTaken = actionTaken;
    }

    public LocalDateFilter getActionDate() {
        return actionDate;
    }

    public Optional<LocalDateFilter> optionalActionDate() {
        return Optional.ofNullable(actionDate);
    }

    public LocalDateFilter actionDate() {
        if (actionDate == null) {
            setActionDate(new LocalDateFilter());
        }
        return actionDate;
    }

    public void setActionDate(LocalDateFilter actionDate) {
        this.actionDate = actionDate;
    }

    public StatusFilter getStatus() {
        return status;
    }

    public Optional<StatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StatusFilter status() {
        if (status == null) {
            setStatus(new StatusFilter());
        }
        return status;
    }

    public void setStatus(StatusFilter status) {
        this.status = status;
    }

    public LocalDateFilter getFollowUpDate() {
        return followUpDate;
    }

    public Optional<LocalDateFilter> optionalFollowUpDate() {
        return Optional.ofNullable(followUpDate);
    }

    public LocalDateFilter followUpDate() {
        if (followUpDate == null) {
            setFollowUpDate(new LocalDateFilter());
        }
        return followUpDate;
    }

    public void setFollowUpDate(LocalDateFilter followUpDate) {
        this.followUpDate = followUpDate;
    }

    public LongFilter getCorrectiveActionId() {
        return correctiveActionId;
    }

    public Optional<LongFilter> optionalCorrectiveActionId() {
        return Optional.ofNullable(correctiveActionId);
    }

    public LongFilter correctiveActionId() {
        if (correctiveActionId == null) {
            setCorrectiveActionId(new LongFilter());
        }
        return correctiveActionId;
    }

    public void setCorrectiveActionId(LongFilter correctiveActionId) {
        this.correctiveActionId = correctiveActionId;
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
        final CapaRecordCriteria that = (CapaRecordCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(rootCause, that.rootCause) &&
            Objects.equals(actionTaken, that.actionTaken) &&
            Objects.equals(actionDate, that.actionDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(followUpDate, that.followUpDate) &&
            Objects.equals(correctiveActionId, that.correctiveActionId) &&
            Objects.equals(shipmentParticipantMapId, that.shipmentParticipantMapId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            rootCause,
            actionTaken,
            actionDate,
            status,
            followUpDate,
            correctiveActionId,
            shipmentParticipantMapId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CapaRecordCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRootCause().map(f -> "rootCause=" + f + ", ").orElse("") +
            optionalActionTaken().map(f -> "actionTaken=" + f + ", ").orElse("") +
            optionalActionDate().map(f -> "actionDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalFollowUpDate().map(f -> "followUpDate=" + f + ", ").orElse("") +
            optionalCorrectiveActionId().map(f -> "correctiveActionId=" + f + ", ").orElse("") +
            optionalShipmentParticipantMapId().map(f -> "shipmentParticipantMapId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
