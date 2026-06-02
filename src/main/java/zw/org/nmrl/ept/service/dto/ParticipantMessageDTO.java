package zw.org.nmrl.ept.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ParticipantMessage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantMessageDTO implements Serializable {

    private Long id;

    private String subject;

    @Lob
    private String body;

    private Boolean isRead;

    private Instant sentAt;

    @NotNull
    private ParticipantDTO participant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public ParticipantDTO getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantDTO participant) {
        this.participant = participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantMessageDTO)) {
            return false;
        }

        ParticipantMessageDTO participantMessageDTO = (ParticipantMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, participantMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantMessageDTO{" +
            "id=" + getId() +
            ", subject='" + getSubject() + "'" +
            ", body='" + getBody() + "'" +
            ", isRead='" + getIsRead() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", participant=" + getParticipant() +
            "}";
    }
}
