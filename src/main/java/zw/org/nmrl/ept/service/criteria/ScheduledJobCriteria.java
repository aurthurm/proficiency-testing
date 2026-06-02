package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.domain.enumeration.JobType;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.ScheduledJob} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.ScheduledJobResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /scheduled-jobs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduledJobCriteria implements Serializable, Criteria {

    /**
     * Class for filtering JobType
     */
    public static class JobTypeFilter extends Filter<JobType> {

        public JobTypeFilter() {}

        public JobTypeFilter(JobTypeFilter filter) {
            super(filter);
        }

        @Override
        public JobTypeFilter copy() {
            return new JobTypeFilter(this);
        }
    }

    /**
     * Class for filtering JobStatus
     */
    public static class JobStatusFilter extends Filter<JobStatus> {

        public JobStatusFilter() {}

        public JobStatusFilter(JobStatusFilter filter) {
            super(filter);
        }

        @Override
        public JobStatusFilter copy() {
            return new JobStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private JobTypeFilter jobType;

    private JobStatusFilter status;

    private StringFilter requestedBy;

    private InstantFilter requestedOn;

    private InstantFilter startedAt;

    private InstantFilter lastHeartbeat;

    private InstantFilter completedAt;

    private IntegerFilter progressCompleted;

    private IntegerFilter progressTotal;

    private Boolean distinct;

    public ScheduledJobCriteria() {}

    public ScheduledJobCriteria(ScheduledJobCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.jobType = other.optionalJobType().map(JobTypeFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(JobStatusFilter::copy).orElse(null);
        this.requestedBy = other.optionalRequestedBy().map(StringFilter::copy).orElse(null);
        this.requestedOn = other.optionalRequestedOn().map(InstantFilter::copy).orElse(null);
        this.startedAt = other.optionalStartedAt().map(InstantFilter::copy).orElse(null);
        this.lastHeartbeat = other.optionalLastHeartbeat().map(InstantFilter::copy).orElse(null);
        this.completedAt = other.optionalCompletedAt().map(InstantFilter::copy).orElse(null);
        this.progressCompleted = other.optionalProgressCompleted().map(IntegerFilter::copy).orElse(null);
        this.progressTotal = other.optionalProgressTotal().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ScheduledJobCriteria copy() {
        return new ScheduledJobCriteria(this);
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

    public JobTypeFilter getJobType() {
        return jobType;
    }

    public Optional<JobTypeFilter> optionalJobType() {
        return Optional.ofNullable(jobType);
    }

    public JobTypeFilter jobType() {
        if (jobType == null) {
            setJobType(new JobTypeFilter());
        }
        return jobType;
    }

    public void setJobType(JobTypeFilter jobType) {
        this.jobType = jobType;
    }

    public JobStatusFilter getStatus() {
        return status;
    }

    public Optional<JobStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public JobStatusFilter status() {
        if (status == null) {
            setStatus(new JobStatusFilter());
        }
        return status;
    }

    public void setStatus(JobStatusFilter status) {
        this.status = status;
    }

    public StringFilter getRequestedBy() {
        return requestedBy;
    }

    public Optional<StringFilter> optionalRequestedBy() {
        return Optional.ofNullable(requestedBy);
    }

    public StringFilter requestedBy() {
        if (requestedBy == null) {
            setRequestedBy(new StringFilter());
        }
        return requestedBy;
    }

    public void setRequestedBy(StringFilter requestedBy) {
        this.requestedBy = requestedBy;
    }

    public InstantFilter getRequestedOn() {
        return requestedOn;
    }

    public Optional<InstantFilter> optionalRequestedOn() {
        return Optional.ofNullable(requestedOn);
    }

    public InstantFilter requestedOn() {
        if (requestedOn == null) {
            setRequestedOn(new InstantFilter());
        }
        return requestedOn;
    }

    public void setRequestedOn(InstantFilter requestedOn) {
        this.requestedOn = requestedOn;
    }

    public InstantFilter getStartedAt() {
        return startedAt;
    }

    public Optional<InstantFilter> optionalStartedAt() {
        return Optional.ofNullable(startedAt);
    }

    public InstantFilter startedAt() {
        if (startedAt == null) {
            setStartedAt(new InstantFilter());
        }
        return startedAt;
    }

    public void setStartedAt(InstantFilter startedAt) {
        this.startedAt = startedAt;
    }

    public InstantFilter getLastHeartbeat() {
        return lastHeartbeat;
    }

    public Optional<InstantFilter> optionalLastHeartbeat() {
        return Optional.ofNullable(lastHeartbeat);
    }

    public InstantFilter lastHeartbeat() {
        if (lastHeartbeat == null) {
            setLastHeartbeat(new InstantFilter());
        }
        return lastHeartbeat;
    }

    public void setLastHeartbeat(InstantFilter lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public InstantFilter getCompletedAt() {
        return completedAt;
    }

    public Optional<InstantFilter> optionalCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    public InstantFilter completedAt() {
        if (completedAt == null) {
            setCompletedAt(new InstantFilter());
        }
        return completedAt;
    }

    public void setCompletedAt(InstantFilter completedAt) {
        this.completedAt = completedAt;
    }

    public IntegerFilter getProgressCompleted() {
        return progressCompleted;
    }

    public Optional<IntegerFilter> optionalProgressCompleted() {
        return Optional.ofNullable(progressCompleted);
    }

    public IntegerFilter progressCompleted() {
        if (progressCompleted == null) {
            setProgressCompleted(new IntegerFilter());
        }
        return progressCompleted;
    }

    public void setProgressCompleted(IntegerFilter progressCompleted) {
        this.progressCompleted = progressCompleted;
    }

    public IntegerFilter getProgressTotal() {
        return progressTotal;
    }

    public Optional<IntegerFilter> optionalProgressTotal() {
        return Optional.ofNullable(progressTotal);
    }

    public IntegerFilter progressTotal() {
        if (progressTotal == null) {
            setProgressTotal(new IntegerFilter());
        }
        return progressTotal;
    }

    public void setProgressTotal(IntegerFilter progressTotal) {
        this.progressTotal = progressTotal;
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
        final ScheduledJobCriteria that = (ScheduledJobCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(jobType, that.jobType) &&
            Objects.equals(status, that.status) &&
            Objects.equals(requestedBy, that.requestedBy) &&
            Objects.equals(requestedOn, that.requestedOn) &&
            Objects.equals(startedAt, that.startedAt) &&
            Objects.equals(lastHeartbeat, that.lastHeartbeat) &&
            Objects.equals(completedAt, that.completedAt) &&
            Objects.equals(progressCompleted, that.progressCompleted) &&
            Objects.equals(progressTotal, that.progressTotal) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            jobType,
            status,
            requestedBy,
            requestedOn,
            startedAt,
            lastHeartbeat,
            completedAt,
            progressCompleted,
            progressTotal,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduledJobCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalJobType().map(f -> "jobType=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalRequestedBy().map(f -> "requestedBy=" + f + ", ").orElse("") +
            optionalRequestedOn().map(f -> "requestedOn=" + f + ", ").orElse("") +
            optionalStartedAt().map(f -> "startedAt=" + f + ", ").orElse("") +
            optionalLastHeartbeat().map(f -> "lastHeartbeat=" + f + ", ").orElse("") +
            optionalCompletedAt().map(f -> "completedAt=" + f + ", ").orElse("") +
            optionalProgressCompleted().map(f -> "progressCompleted=" + f + ", ").orElse("") +
            optionalProgressTotal().map(f -> "progressTotal=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
