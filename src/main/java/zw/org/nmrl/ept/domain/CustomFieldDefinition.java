package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.CustomFieldType;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A CustomFieldDefinition.
 */
@Entity
@Table(name = "custom_field_definition")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomFieldDefinition implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "field_key", nullable = false, unique = true)
    private String fieldKey;

    @NotNull
    @Column(name = "label", nullable = false)
    private String label;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private CustomFieldType fieldType;

    @Lob
    @Column(name = "options")
    private String options;

    @Column(name = "display_order")
    private Integer displayOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomFieldDefinition id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldKey() {
        return this.fieldKey;
    }

    public CustomFieldDefinition fieldKey(String fieldKey) {
        this.setFieldKey(fieldKey);
        return this;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getLabel() {
        return this.label;
    }

    public CustomFieldDefinition label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CustomFieldType getFieldType() {
        return this.fieldType;
    }

    public CustomFieldDefinition fieldType(CustomFieldType fieldType) {
        this.setFieldType(fieldType);
        return this;
    }

    public void setFieldType(CustomFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getOptions() {
        return this.options;
    }

    public CustomFieldDefinition options(String options) {
        this.setOptions(options);
        return this;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public CustomFieldDefinition displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Status getStatus() {
        return this.status;
    }

    public CustomFieldDefinition status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomFieldDefinition)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomFieldDefinition) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomFieldDefinition{" +
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
