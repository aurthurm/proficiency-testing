package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.EmailStatus;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.EmailMessage} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.EmailMessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /email-messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmailMessageCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EmailStatus
     */
    public static class EmailStatusFilter extends Filter<EmailStatus> {

        public EmailStatusFilter() {}

        public EmailStatusFilter(EmailStatusFilter filter) {
            super(filter);
        }

        @Override
        public EmailStatusFilter copy() {
            return new EmailStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fromEmail;

    private StringFilter fromName;

    private StringFilter replyTo;

    private StringFilter toEmail;

    private StringFilter cc;

    private StringFilter bcc;

    private StringFilter subject;

    private StringFilter attachmentRef;

    private EmailStatusFilter status;

    private StringFilter failureType;

    private StringFilter failureReason;

    private InstantFilter queuedOn;

    private InstantFilter sentAt;

    private IntegerFilter retryCount;

    private LongFilter templateId;

    private Boolean distinct;

    public EmailMessageCriteria() {}

    public EmailMessageCriteria(EmailMessageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.fromEmail = other.optionalFromEmail().map(StringFilter::copy).orElse(null);
        this.fromName = other.optionalFromName().map(StringFilter::copy).orElse(null);
        this.replyTo = other.optionalReplyTo().map(StringFilter::copy).orElse(null);
        this.toEmail = other.optionalToEmail().map(StringFilter::copy).orElse(null);
        this.cc = other.optionalCc().map(StringFilter::copy).orElse(null);
        this.bcc = other.optionalBcc().map(StringFilter::copy).orElse(null);
        this.subject = other.optionalSubject().map(StringFilter::copy).orElse(null);
        this.attachmentRef = other.optionalAttachmentRef().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(EmailStatusFilter::copy).orElse(null);
        this.failureType = other.optionalFailureType().map(StringFilter::copy).orElse(null);
        this.failureReason = other.optionalFailureReason().map(StringFilter::copy).orElse(null);
        this.queuedOn = other.optionalQueuedOn().map(InstantFilter::copy).orElse(null);
        this.sentAt = other.optionalSentAt().map(InstantFilter::copy).orElse(null);
        this.retryCount = other.optionalRetryCount().map(IntegerFilter::copy).orElse(null);
        this.templateId = other.optionalTemplateId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EmailMessageCriteria copy() {
        return new EmailMessageCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFromEmail() {
        return fromEmail;
    }

    public Optional<StringFilter> optionalFromEmail() {
        return Optional.ofNullable(fromEmail);
    }

    public StringFilter fromEmail() {
        if (fromEmail == null) {
            setFromEmail(new StringFilter());
        }
        return fromEmail;
    }

    public void setFromEmail(StringFilter fromEmail) {
        this.fromEmail = fromEmail;
    }

    public StringFilter getFromName() {
        return fromName;
    }

    public Optional<StringFilter> optionalFromName() {
        return Optional.ofNullable(fromName);
    }

    public StringFilter fromName() {
        if (fromName == null) {
            setFromName(new StringFilter());
        }
        return fromName;
    }

    public void setFromName(StringFilter fromName) {
        this.fromName = fromName;
    }

    public StringFilter getReplyTo() {
        return replyTo;
    }

    public Optional<StringFilter> optionalReplyTo() {
        return Optional.ofNullable(replyTo);
    }

    public StringFilter replyTo() {
        if (replyTo == null) {
            setReplyTo(new StringFilter());
        }
        return replyTo;
    }

    public void setReplyTo(StringFilter replyTo) {
        this.replyTo = replyTo;
    }

    public StringFilter getToEmail() {
        return toEmail;
    }

    public Optional<StringFilter> optionalToEmail() {
        return Optional.ofNullable(toEmail);
    }

    public StringFilter toEmail() {
        if (toEmail == null) {
            setToEmail(new StringFilter());
        }
        return toEmail;
    }

    public void setToEmail(StringFilter toEmail) {
        this.toEmail = toEmail;
    }

    public StringFilter getCc() {
        return cc;
    }

    public Optional<StringFilter> optionalCc() {
        return Optional.ofNullable(cc);
    }

    public StringFilter cc() {
        if (cc == null) {
            setCc(new StringFilter());
        }
        return cc;
    }

    public void setCc(StringFilter cc) {
        this.cc = cc;
    }

    public StringFilter getBcc() {
        return bcc;
    }

    public Optional<StringFilter> optionalBcc() {
        return Optional.ofNullable(bcc);
    }

    public StringFilter bcc() {
        if (bcc == null) {
            setBcc(new StringFilter());
        }
        return bcc;
    }

    public void setBcc(StringFilter bcc) {
        this.bcc = bcc;
    }

    public StringFilter getSubject() {
        return subject;
    }

    public Optional<StringFilter> optionalSubject() {
        return Optional.ofNullable(subject);
    }

    public StringFilter subject() {
        if (subject == null) {
            setSubject(new StringFilter());
        }
        return subject;
    }

    public void setSubject(StringFilter subject) {
        this.subject = subject;
    }

    public StringFilter getAttachmentRef() {
        return attachmentRef;
    }

    public Optional<StringFilter> optionalAttachmentRef() {
        return Optional.ofNullable(attachmentRef);
    }

    public StringFilter attachmentRef() {
        if (attachmentRef == null) {
            setAttachmentRef(new StringFilter());
        }
        return attachmentRef;
    }

    public void setAttachmentRef(StringFilter attachmentRef) {
        this.attachmentRef = attachmentRef;
    }

    public EmailStatusFilter getStatus() {
        return status;
    }

    public Optional<EmailStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public EmailStatusFilter status() {
        if (status == null) {
            setStatus(new EmailStatusFilter());
        }
        return status;
    }

    public void setStatus(EmailStatusFilter status) {
        this.status = status;
    }

    public StringFilter getFailureType() {
        return failureType;
    }

    public Optional<StringFilter> optionalFailureType() {
        return Optional.ofNullable(failureType);
    }

    public StringFilter failureType() {
        if (failureType == null) {
            setFailureType(new StringFilter());
        }
        return failureType;
    }

    public void setFailureType(StringFilter failureType) {
        this.failureType = failureType;
    }

    public StringFilter getFailureReason() {
        return failureReason;
    }

    public Optional<StringFilter> optionalFailureReason() {
        return Optional.ofNullable(failureReason);
    }

    public StringFilter failureReason() {
        if (failureReason == null) {
            setFailureReason(new StringFilter());
        }
        return failureReason;
    }

    public void setFailureReason(StringFilter failureReason) {
        this.failureReason = failureReason;
    }

    public InstantFilter getQueuedOn() {
        return queuedOn;
    }

    public Optional<InstantFilter> optionalQueuedOn() {
        return Optional.ofNullable(queuedOn);
    }

    public InstantFilter queuedOn() {
        if (queuedOn == null) {
            setQueuedOn(new InstantFilter());
        }
        return queuedOn;
    }

    public void setQueuedOn(InstantFilter queuedOn) {
        this.queuedOn = queuedOn;
    }

    public InstantFilter getSentAt() {
        return sentAt;
    }

    public Optional<InstantFilter> optionalSentAt() {
        return Optional.ofNullable(sentAt);
    }

    public InstantFilter sentAt() {
        if (sentAt == null) {
            setSentAt(new InstantFilter());
        }
        return sentAt;
    }

    public void setSentAt(InstantFilter sentAt) {
        this.sentAt = sentAt;
    }

    public IntegerFilter getRetryCount() {
        return retryCount;
    }

    public Optional<IntegerFilter> optionalRetryCount() {
        return Optional.ofNullable(retryCount);
    }

    public IntegerFilter retryCount() {
        if (retryCount == null) {
            setRetryCount(new IntegerFilter());
        }
        return retryCount;
    }

    public void setRetryCount(IntegerFilter retryCount) {
        this.retryCount = retryCount;
    }

    public LongFilter getTemplateId() {
        return templateId;
    }

    public Optional<LongFilter> optionalTemplateId() {
        return Optional.ofNullable(templateId);
    }

    public LongFilter templateId() {
        if (templateId == null) {
            setTemplateId(new LongFilter());
        }
        return templateId;
    }

    public void setTemplateId(LongFilter templateId) {
        this.templateId = templateId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmailMessageCriteria that = (EmailMessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fromEmail, that.fromEmail) &&
            Objects.equals(fromName, that.fromName) &&
            Objects.equals(replyTo, that.replyTo) &&
            Objects.equals(toEmail, that.toEmail) &&
            Objects.equals(cc, that.cc) &&
            Objects.equals(bcc, that.bcc) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(attachmentRef, that.attachmentRef) &&
            Objects.equals(status, that.status) &&
            Objects.equals(failureType, that.failureType) &&
            Objects.equals(failureReason, that.failureReason) &&
            Objects.equals(queuedOn, that.queuedOn) &&
            Objects.equals(sentAt, that.sentAt) &&
            Objects.equals(retryCount, that.retryCount) &&
            Objects.equals(templateId, that.templateId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            fromEmail,
            fromName,
            replyTo,
            toEmail,
            cc,
            bcc,
            subject,
            attachmentRef,
            status,
            failureType,
            failureReason,
            queuedOn,
            sentAt,
            retryCount,
            templateId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmailMessageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFromEmail().map(f -> "fromEmail=" + f + ", ").orElse("") +
            optionalFromName().map(f -> "fromName=" + f + ", ").orElse("") +
            optionalReplyTo().map(f -> "replyTo=" + f + ", ").orElse("") +
            optionalToEmail().map(f -> "toEmail=" + f + ", ").orElse("") +
            optionalCc().map(f -> "cc=" + f + ", ").orElse("") +
            optionalBcc().map(f -> "bcc=" + f + ", ").orElse("") +
            optionalSubject().map(f -> "subject=" + f + ", ").orElse("") +
            optionalAttachmentRef().map(f -> "attachmentRef=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalFailureType().map(f -> "failureType=" + f + ", ").orElse("") +
            optionalFailureReason().map(f -> "failureReason=" + f + ", ").orElse("") +
            optionalQueuedOn().map(f -> "queuedOn=" + f + ", ").orElse("") +
            optionalSentAt().map(f -> "sentAt=" + f + ", ").orElse("") +
            optionalRetryCount().map(f -> "retryCount=" + f + ", ").orElse("") +
            optionalTemplateId().map(f -> "templateId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
