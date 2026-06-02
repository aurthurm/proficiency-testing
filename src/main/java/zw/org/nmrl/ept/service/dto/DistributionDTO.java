package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.DistributionStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.Distribution} entity.
 */
@Schema(description = "PT survey grouping shipments.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DistributionDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private LocalDate distributionDate;

    @NotNull
    private DistributionStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDistributionDate() {
        return distributionDate;
    }

    public void setDistributionDate(LocalDate distributionDate) {
        this.distributionDate = distributionDate;
    }

    public DistributionStatus getStatus() {
        return status;
    }

    public void setStatus(DistributionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DistributionDTO)) {
            return false;
        }

        DistributionDTO distributionDTO = (DistributionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, distributionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DistributionDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", distributionDate='" + getDistributionDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
