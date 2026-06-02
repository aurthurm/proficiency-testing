package zw.org.nmrl.ept.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.MailTemplate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MailTemplateDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    private String subject;

    @Lob
    private String htmlBody;

    private ContentStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MailTemplateDTO)) {
            return false;
        }

        MailTemplateDTO mailTemplateDTO = (MailTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mailTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MailTemplateDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", subject='" + getSubject() + "'" +
            ", htmlBody='" + getHtmlBody() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
