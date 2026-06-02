package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.TestKit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestKitDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

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
        if (!(o instanceof TestKitDTO)) {
            return false;
        }

        TestKitDTO testKitDTO = (TestKitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, testKitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestKitDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", manufacturer='" + getManufacturer() + "'" +
            ", status='" + getStatus() + "'" +
            ", scheme=" + getScheme() +
            "}";
    }
}
