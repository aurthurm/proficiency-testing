package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.EmailStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.EmailMessage} entity.
 */
@Schema(description = "Outbound email queue with retry/failure tracking.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmailMessageDTO implements Serializable {

    private Long id;

    private String fromEmail;

    private String fromName;

    private String replyTo;

    @NotNull
    private String toEmail;

    private String cc;

    private String bcc;

    private String subject;

    @Lob
    private String body;

    private String attachmentRef;

    @NotNull
    private EmailStatus status;

    private String failureType;

    private String failureReason;

    private Instant queuedOn;

    private Instant sentAt;

    private Integer retryCount;

    private MailTemplateDTO template;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
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

    public String getAttachmentRef() {
        return attachmentRef;
    }

    public void setAttachmentRef(String attachmentRef) {
        this.attachmentRef = attachmentRef;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public String getFailureType() {
        return failureType;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getQueuedOn() {
        return queuedOn;
    }

    public void setQueuedOn(Instant queuedOn) {
        this.queuedOn = queuedOn;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public MailTemplateDTO getTemplate() {
        return template;
    }

    public void setTemplate(MailTemplateDTO template) {
        this.template = template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmailMessageDTO)) {
            return false;
        }

        EmailMessageDTO emailMessageDTO = (EmailMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, emailMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmailMessageDTO{" +
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
            ", template=" + getTemplate() +
            "}";
    }
}
