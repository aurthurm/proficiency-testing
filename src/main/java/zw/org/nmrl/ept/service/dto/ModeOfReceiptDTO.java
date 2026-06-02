package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ModeOfReceipt} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ModeOfReceiptDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(o instanceof ModeOfReceiptDTO)) {
            return false;
        }

        ModeOfReceiptDTO modeOfReceiptDTO = (ModeOfReceiptDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, modeOfReceiptDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ModeOfReceiptDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
