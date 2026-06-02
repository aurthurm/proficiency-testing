package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A FeedbackQuestion.
 */
@Entity
@Table(name = "feedback_question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FeedbackQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(name = "display_order")
    private Integer displayOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "question", "participant", "shipment" }, allowSetters = true)
    private Set<ParticipantFeedback> answerses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FeedbackQuestion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public FeedbackQuestion questionText(String questionText) {
        this.setQuestionText(questionText);
        return this;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public FeedbackQuestion displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public ContentStatus getStatus() {
        return this.status;
    }

    public FeedbackQuestion status(ContentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public Set<ParticipantFeedback> getAnswerses() {
        return this.answerses;
    }

    public void setAnswerses(Set<ParticipantFeedback> participantFeedbacks) {
        if (this.answerses != null) {
            this.answerses.forEach(i -> i.setQuestion(null));
        }
        if (participantFeedbacks != null) {
            participantFeedbacks.forEach(i -> i.setQuestion(this));
        }
        this.answerses = participantFeedbacks;
    }

    public FeedbackQuestion answerses(Set<ParticipantFeedback> participantFeedbacks) {
        this.setAnswerses(participantFeedbacks);
        return this;
    }

    public FeedbackQuestion addAnswers(ParticipantFeedback participantFeedback) {
        this.answerses.add(participantFeedback);
        participantFeedback.setQuestion(this);
        return this;
    }

    public FeedbackQuestion removeAnswers(ParticipantFeedback participantFeedback) {
        this.answerses.remove(participantFeedback);
        participantFeedback.setQuestion(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeedbackQuestion)) {
            return false;
        }
        return getId() != null && getId().equals(((FeedbackQuestion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FeedbackQuestion{" +
            "id=" + getId() +
            ", questionText='" + getQuestionText() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
