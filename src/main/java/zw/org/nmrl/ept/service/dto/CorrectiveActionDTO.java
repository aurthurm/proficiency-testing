package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.CorrectiveAction} entity.
 */
@Schema(description = "Catalogue of predefined failure reasons + recommended corrective actions.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CorrectiveActionDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private String description;

    private SchemeDTO scheme;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(o instanceof CorrectiveActionDTO)) {
            return false;
        }

        CorrectiveActionDTO correctiveActionDTO = (CorrectiveActionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, correctiveActionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CorrectiveActionDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", scheme=" + getScheme() +
            "}";
    }
}
