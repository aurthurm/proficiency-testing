package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A MailTemplate.
 */
@Entity
@Table(name = "mail_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MailTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "subject")
    private String subject;

    @Lob
    @Column(name = "html_body")
    private String htmlBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContentStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MailTemplate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public MailTemplate code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubject() {
        return this.subject;
    }

    public MailTemplate subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlBody() {
        return this.htmlBody;
    }

    public MailTemplate htmlBody(String htmlBody) {
        this.setHtmlBody(htmlBody);
        return this;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public ContentStatus getStatus() {
        return this.status;
    }

    public MailTemplate status(ContentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MailTemplate)) {
            return false;
        }
        return getId() != null && getId().equals(((MailTemplate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MailTemplate{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", subject='" + getSubject() + "'" +
            ", htmlBody='" + getHtmlBody() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
