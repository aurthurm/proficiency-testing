package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ContactMessage} entity.
 */
@Schema(description = "Public contact-us submissions (PUB-02).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContactMessageDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    private String subject;

    @Lob
    private String message;

    private String ipAddress;

    private Instant submittedAt;

    private Boolean isHandled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Boolean getIsHandled() {
        return isHandled;
    }

    public void setIsHandled(Boolean isHandled) {
        this.isHandled = isHandled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContactMessageDTO)) {
            return false;
        }

        ContactMessageDTO contactMessageDTO = (ContactMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, contactMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContactMessageDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", subject='" + getSubject() + "'" +
            ", message='" + getMessage() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", submittedAt='" + getSubmittedAt() + "'" +
            ", isHandled='" + getIsHandled() + "'" +
            "}";
    }
}
