package zw.org.nmrl.ept.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ParticipantFeedback} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantFeedbackDTO implements Serializable {

    private Long id;

    @Lob
    private String answer;

    private Instant submittedAt;

    @NotNull
    private FeedbackQuestionDTO question;

    @NotNull
    private ParticipantDTO participant;

    private ShipmentDTO shipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public FeedbackQuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(FeedbackQuestionDTO question) {
        this.question = question;
    }

    public ParticipantDTO getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantDTO participant) {
        this.participant = participant;
    }

    public ShipmentDTO getShipment() {
        return shipment;
    }

    public void setShipment(ShipmentDTO shipment) {
        this.shipment = shipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantFeedbackDTO)) {
            return false;
        }

        ParticipantFeedbackDTO participantFeedbackDTO = (ParticipantFeedbackDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, participantFeedbackDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantFeedbackDTO{" +
            "id=" + getId() +
            ", answer='" + getAnswer() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", question=" + getQuestion() +
            ", participant=" + getParticipant() +
            ", shipment=" + getShipment() +
            "}";
    }
}
