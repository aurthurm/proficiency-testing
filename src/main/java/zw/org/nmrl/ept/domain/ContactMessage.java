package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Public contact-us submissions (PUB-02).
 */
@Entity
@Table(name = "contact_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContactMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "subject")
    private String subject;

    @Lob
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "is_handled")
    private Boolean isHandled;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ContactMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ContactMessage name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public ContactMessage email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return this.subject;
    }

    public ContactMessage subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return this.message;
    }

    public ContactMessage message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public ContactMessage ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public ContactMessage submittedAt(Instant submittedAt) {
        this.setSubmittedAt(submittedAt);
        return this;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Boolean getIsHandled() {
        return this.isHandled;
    }

    public ContactMessage isHandled(Boolean isHandled) {
        this.setIsHandled(isHandled);
        return this;
    }

    public void setIsHandled(Boolean isHandled) {
        this.isHandled = isHandled;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContactMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((ContactMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContactMessage{" +
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
