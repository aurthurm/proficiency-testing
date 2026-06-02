package zw.org.nmrl.ept.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.Announcement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AnnouncementDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @Lob
    private String body;

    @NotNull
    private ContentStatus status;

    private Instant publishedFrom;

    private Instant publishedTo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public Instant getPublishedFrom() {
        return publishedFrom;
    }

    public void setPublishedFrom(Instant publishedFrom) {
        this.publishedFrom = publishedFrom;
    }

    public Instant getPublishedTo() {
        return publishedTo;
    }

    public void setPublishedTo(Instant publishedTo) {
        this.publishedTo = publishedTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnnouncementDTO)) {
            return false;
        }

        AnnouncementDTO announcementDTO = (AnnouncementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, announcementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AnnouncementDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", body='" + getBody() + "'" +
            ", status='" + getStatus() + "'" +
            ", publishedFrom='" + getPublishedFrom() + "'" +
            ", publishedTo='" + getPublishedTo() + "'" +
            "}";
    }
}
