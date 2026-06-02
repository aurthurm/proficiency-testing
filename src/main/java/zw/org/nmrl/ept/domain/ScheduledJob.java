package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.domain.enumeration.JobType;

/**
 * Background job queue (SCH-01..07).
 */
@Entity
@Table(name = "scheduled_job")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduledJob implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "requested_on")
    private Instant requestedOn;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "last_heartbeat")
    private Instant lastHeartbeat;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "progress_completed")
    private Integer progressCompleted;

    @Column(name = "progress_total")
    private Integer progressTotal;

    @Lob
    @Column(name = "summary")
    private String summary;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ScheduledJob id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobType getJobType() {
        return this.jobType;
    }

    public ScheduledJob jobType(JobType jobType) {
        this.setJobType(jobType);
        return this;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobStatus getStatus() {
        return this.status;
    }

    public ScheduledJob status(JobStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return this.requestedBy;
    }

    public ScheduledJob requestedBy(String requestedBy) {
        this.setRequestedBy(requestedBy);
        return this;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedOn() {
        return this.requestedOn;
    }

    public ScheduledJob requestedOn(Instant requestedOn) {
        this.setRequestedOn(requestedOn);
        return this;
    }

    public void setRequestedOn(Instant requestedOn) {
        this.requestedOn = requestedOn;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public ScheduledJob startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getLastHeartbeat() {
        return this.lastHeartbeat;
    }

    public ScheduledJob lastHeartbeat(Instant lastHeartbeat) {
        this.setLastHeartbeat(lastHeartbeat);
        return this;
    }

    public void setLastHeartbeat(Instant lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public ScheduledJob completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getProgressCompleted() {
        return this.progressCompleted;
    }

    public ScheduledJob progressCompleted(Integer progressCompleted) {
        this.setProgressCompleted(progressCompleted);
        return this;
    }

    public void setProgressCompleted(Integer progressCompleted) {
        this.progressCompleted = progressCompleted;
    }

    public Integer getProgressTotal() {
        return this.progressTotal;
    }

    public ScheduledJob progressTotal(Integer progressTotal) {
        this.setProgressTotal(progressTotal);
        return this;
    }

    public void setProgressTotal(Integer progressTotal) {
        this.progressTotal = progressTotal;
    }

    public String getSummary() {
        return this.summary;
    }

    public ScheduledJob summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduledJob)) {
            return false;
        }
        return getId() != null && getId().equals(((ScheduledJob) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduledJob{" +
            "id=" + getId() +
            ", jobType='" + getJobType() + "'" +
            ", status='" + getStatus() + "'" +
            ", requestedBy='" + getRequestedBy() + "'" +
            ", requestedOn='" + getRequestedOn() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", lastHeartbeat='" + getLastHeartbeat() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", progressCompleted=" + getProgressCompleted() +
            ", progressTotal=" + getProgressTotal() +
            ", summary='" + getSummary() + "'" +
            "}";
    }
}
