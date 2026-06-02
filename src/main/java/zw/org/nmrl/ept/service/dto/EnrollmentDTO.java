package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.EnrollmentStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.Enrollment} entity.
 */
@Schema(description = "Participant enrolled into a scheme/programme (programme-level membership).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnrollmentDTO implements Serializable {

    private Long id;

    @NotNull
    private EnrollmentStatus status;

    @NotNull
    private LocalDate enrolledOn;

    private LocalDate withdrawnOn;

    @NotNull
    private ParticipantDTO participant;

    @NotNull
    private SchemeDTO scheme;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDate getEnrolledOn() {
        return enrolledOn;
    }

    public void setEnrolledOn(LocalDate enrolledOn) {
        this.enrolledOn = enrolledOn;
    }

    public LocalDate getWithdrawnOn() {
        return withdrawnOn;
    }

    public void setWithdrawnOn(LocalDate withdrawnOn) {
        this.withdrawnOn = withdrawnOn;
    }

    public ParticipantDTO getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantDTO participant) {
        this.participant = participant;
    }

    public SchemeDTO getScheme() {
        return scheme;
    }

    public void setScheme(SchemeDTO scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnrollmentDTO)) {
            return false;
        }

        EnrollmentDTO enrollmentDTO = (EnrollmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, enrollmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnrollmentDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", enrolledOn='" + getEnrolledOn() + "'" +
            ", withdrawnOn='" + getWithdrawnOn() + "'" +
            ", participant=" + getParticipant() +
            ", scheme=" + getScheme() +
            "}";
    }
}
