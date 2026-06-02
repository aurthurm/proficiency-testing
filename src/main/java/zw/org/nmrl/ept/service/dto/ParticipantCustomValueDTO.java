package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ParticipantCustomValue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantCustomValueDTO implements Serializable {

    private Long id;

    private String value;

    @NotNull
    private ParticipantDTO participant;

    @NotNull
    private CustomFieldDefinitionDTO definition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ParticipantDTO getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantDTO participant) {
        this.participant = participant;
    }

    public CustomFieldDefinitionDTO getDefinition() {
        return definition;
    }

    public void setDefinition(CustomFieldDefinitionDTO definition) {
        this.definition = definition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantCustomValueDTO)) {
            return false;
        }

        ParticipantCustomValueDTO participantCustomValueDTO = (ParticipantCustomValueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, participantCustomValueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantCustomValueDTO{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", participant=" + getParticipant() +
            ", definition=" + getDefinition() +
            "}";
    }
}
