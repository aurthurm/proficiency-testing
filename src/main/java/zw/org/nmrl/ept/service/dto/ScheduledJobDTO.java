package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.JobStatus;
import zw.org.nmrl.ept.domain.enumeration.JobType;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ScheduledJob} entity.
 */
@Schema(description = "Background job queue (SCH-01..07).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduledJobDTO implements Serializable {

    private Long id;

    @NotNull
    private JobType jobType;

    @NotNull
    private JobStatus status;

    private String requestedBy;

    private Instant requestedOn;

    private Instant startedAt;

    private Instant lastHeartbeat;

    private Instant completedAt;

    private Integer progressCompleted;

    private Integer progressTotal;

    @Lob
    private String summary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(Instant requestedOn) {
        this.requestedOn = requestedOn;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Instant lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getProgressCompleted() {
        return progressCompleted;
    }

    public void setProgressCompleted(Integer progressCompleted) {
        this.progressCompleted = progressCompleted;
    }

    public Integer getProgressTotal() {
        return progressTotal;
    }

    public void setProgressTotal(Integer progressTotal) {
        this.progressTotal = progressTotal;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduledJobDTO)) {
            return false;
        }

        ScheduledJobDTO scheduledJobDTO = (ScheduledJobDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scheduledJobDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduledJobDTO{" +
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
