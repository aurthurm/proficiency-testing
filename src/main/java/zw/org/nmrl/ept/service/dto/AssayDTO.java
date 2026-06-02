package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.AssayType;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.Assay} entity.
 */
@Schema(description = "Scheme-specific dimension: DTS algorithm, VL/EID/TB assay, COVID gene, etc.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssayDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private AssayType assayType;

    private String manufacturer;

    @NotNull
    private Status status;

    private SchemeDTO scheme;

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

    public AssayType getAssayType() {
        return assayType;
    }

    public void setAssayType(AssayType assayType) {
        this.assayType = assayType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        if (!(o instanceof AssayDTO)) {
            return false;
        }

        AssayDTO assayDTO = (AssayDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assayDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssayDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", assayType='" + getAssayType() + "'" +
            ", manufacturer='" + getManufacturer() + "'" +
            ", status='" + getStatus() + "'" +
            ", scheme=" + getScheme() +
            "}";
    }
}
