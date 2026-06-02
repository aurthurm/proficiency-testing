package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.EmailStatus;

/**
 * Outbound email queue with retry/failure tracking.
 */
@Entity
@Table(name = "email_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmailMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "from_name")
    private String fromName;

    @Column(name = "reply_to")
    private String replyTo;

    @NotNull
    @Column(name = "to_email", nullable = false)
    private String toEmail;

    @Column(name = "cc")
    private String cc;

    @Column(name = "bcc")
    private String bcc;

    @Column(name = "subject")
    private String subject;

    @Lob
    @Column(name = "body")
    private String body;

    @Column(name = "attachment_ref")
    private String attachmentRef;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmailStatus status;

    @Column(name = "failure_type")
    private String failureType;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "queued_on")
    private Instant queuedOn;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private MailTemplate template;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EmailMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromEmail() {
        return this.fromEmail;
    }

    public EmailMessage fromEmail(String fromEmail) {
        this.setFromEmail(fromEmail);
        return this;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromName() {
        return this.fromName;
    }

    public EmailMessage fromName(String fromName) {
        this.setFromName(fromName);
        return this;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getReplyTo() {
        return this.replyTo;
    }

    public EmailMessage replyTo(String replyTo) {
        this.setReplyTo(replyTo);
        return this;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getToEmail() {
        return this.toEmail;
    }

    public EmailMessage toEmail(String toEmail) {
        this.setToEmail(toEmail);
        return this;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getCc() {
        return this.cc;
    }

    public EmailMessage cc(String cc) {
        this.setCc(cc);
        return this;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return this.bcc;
    }

    public EmailMessage bcc(String bcc) {
        this.setBcc(bcc);
        return this;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return this.subject;
    }

    public EmailMessage subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return this.body;
    }

    public EmailMessage body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttachmentRef() {
        return this.attachmentRef;
    }

    public EmailMessage attachmentRef(String attachmentRef) {
        this.setAttachmentRef(attachmentRef);
        return this;
    }

    public void setAttachmentRef(String attachmentRef) {
        this.attachmentRef = attachmentRef;
    }

    public EmailStatus getStatus() {
        return this.status;
    }

    public EmailMessage status(EmailStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public String getFailureType() {
        return this.failureType;
    }

    public EmailMessage failureType(String failureType) {
        this.setFailureType(failureType);
        return this;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public EmailMessage failureReason(String failureReason) {
        this.setFailureReason(failureReason);
        return this;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getQueuedOn() {
        return this.queuedOn;
    }

    public EmailMessage queuedOn(Instant queuedOn) {
        this.setQueuedOn(queuedOn);
        return this;
    }

    public void setQueuedOn(Instant queuedOn) {
        this.queuedOn = queuedOn;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public EmailMessage sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Integer getRetryCount() {
        return this.retryCount;
    }

    public EmailMessage retryCount(Integer retryCount) {
        this.setRetryCount(retryCount);
        return this;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public MailTemplate getTemplate() {
        return this.template;
    }

    public void setTemplate(MailTemplate mailTemplate) {
        this.template = mailTemplate;
    }

    public EmailMessage template(MailTemplate mailTemplate) {
        this.setTemplate(mailTemplate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmailMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((EmailMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmailMessage{" +
            "id=" + getId() +
            ", fromEmail='" + getFromEmail() + "'" +
            ", fromName='" + getFromName() + "'" +
            ", replyTo='" + getReplyTo() + "'" +
            ", toEmail='" + getToEmail() + "'" +
            ", cc='" + getCc() + "'" +
            ", bcc='" + getBcc() + "'" +
            ", subject='" + getSubject() + "'" +
            ", body='" + getBody() + "'" +
            ", attachmentRef='" + getAttachmentRef() + "'" +
            ", status='" + getStatus() + "'" +
            ", failureType='" + getFailureType() + "'" +
            ", failureReason='" + getFailureReason() + "'" +
            ", queuedOn='" + getQueuedOn() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", retryCount=" + getRetryCount() +
            "}";
    }
}
