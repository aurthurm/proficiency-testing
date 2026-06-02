package zw.org.nmrl.ept.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.CustomFieldType;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.CustomFieldDefinition} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomFieldDefinitionDTO implements Serializable {

    private Long id;

    @NotNull
    private String fieldKey;

    @NotNull
    private String label;

    @NotNull
    private CustomFieldType fieldType;

    @Lob
    private String options;

    private Integer displayOrder;

    @NotNull
    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CustomFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(CustomFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
        if (!(o instanceof CustomFieldDefinitionDTO)) {
            return false;
        }

        CustomFieldDefinitionDTO customFieldDefinitionDTO = (CustomFieldDefinitionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customFieldDefinitionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomFieldDefinitionDTO{" +
            "id=" + getId() +
            ", fieldKey='" + getFieldKey() + "'" +
            ", label='" + getLabel() + "'" +
            ", fieldType='" + getFieldType() + "'" +
            ", options='" + getOptions() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
