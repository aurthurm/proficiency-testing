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
 * A ParticipantFeedback.
 */
@Entity
@Table(name = "participant_feedback")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantFeedback implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "answer")
    private String answer;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "answerses" }, allowSetters = true)
    private FeedbackQuestion question;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "shipmentMapses", "country", "enrollmentses", "customValueses", "dataManagerses" }, allowSetters = true)
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "sampleses", "participantMapses", "distribution", "scheme", "certificateBatcheses" },
        allowSetters = true
    )
    private Shipment shipment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ParticipantFeedback id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return this.answer;
    }

    public ParticipantFeedback answer(String answer) {
        this.setAnswer(answer);
        return this;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public ParticipantFeedback submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public FeedbackQuestion getQuestion() {
        return this.question;
    }

    public void setQuestion(FeedbackQuestion feedbackQuestion) {
        this.question = feedbackQuestion;
    }

    public ParticipantFeedback question(FeedbackQuestion feedbackQuestion) {
        this.setQuestion(feedbackQuestion);
        return this;
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ParticipantFeedback participant(Participant participant) {
        this.setParticipant(participant);
        return this;
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public ParticipantFeedback shipment(Shipment shipment) {
        this.setShipment(shipment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantFeedback)) {
            return false;
        }
        return getId() != null && getId().equals(((ParticipantFeedback) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantFeedback{" +
            "id=" + getId() +
            ", answer='" + getAnswer() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            "}";
    }
}
