package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ParticipantCustomValue.
 */
@Entity
@Table(name = "participant_custom_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantCustomValue implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private String value;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "shipmentMapses", "country", "enrollmentses", "customValueses", "dataManagerses" }, allowSetters = true)
    private Participant participant;

    @ManyToOne(optional = false)
    @NotNull
    private CustomFieldDefinition definition;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ParticipantCustomValue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public ParticipantCustomValue value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ParticipantCustomValue participant(Participant participant) {
        this.setParticipant(participant);
        return this;
    }

    public CustomFieldDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(CustomFieldDefinition customFieldDefinition) {
        this.definition = customFieldDefinition;
    }

    public ParticipantCustomValue definition(CustomFieldDefinition customFieldDefinition) {
        this.setDefinition(customFieldDefinition);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantCustomValue)) {
            return false;
        }
        return getId() != null && getId().equals(((ParticipantCustomValue) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantCustomValue{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            "}";
    }
}
