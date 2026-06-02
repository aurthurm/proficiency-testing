package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.NotTestedReason} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotTestedReasonDTO implements Serializable {

    private Long id;

    @NotNull
    private String reason;

    @NotNull
    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotTestedReasonDTO)) {
            return false;
        }

        NotTestedReasonDTO notTestedReasonDTO = (NotTestedReasonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notTestedReasonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotTestedReasonDTO{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
