package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ParticipantMessage.
 */
@Entity
@Table(name = "participant_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Lob
    @Column(name = "body")
    private String body;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "sent_at")
    private Instant sentAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "shipmentMapses", "country", "enrollmentses", "customValueses", "dataManagerses" }, allowSetters = true)
    private Participant participant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ParticipantMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return this.subject;
    }

    public ParticipantMessage subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return this.body;
    }

    public ParticipantMessage body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getIsRead() {
        return this.isRead;
    }

    public ParticipantMessage isRead(Boolean isRead) {
        this.setIsRead(isRead);
        return this;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public ParticipantMessage sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ParticipantMessage participant(Participant participant) {
        this.setParticipant(participant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((ParticipantMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantMessage{" +
            "id=" + getId() +
            ", subject='" + getSubject() + "'" +
            ", body='" + getBody() + "'" +
            ", isRead='" + getIsRead() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            "}";
    }
}
