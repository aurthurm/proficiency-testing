package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A Announcement.
 */
@Entity
@Table(name = "announcement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Announcement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "body")
    private String body;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status;

    @Column(name = "published_from")
    private Instant publishedFrom;

    @Column(name = "published_to")
    private Instant publishedTo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Announcement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Announcement title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return this.body;
    }

    public Announcement body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ContentStatus getStatus() {
        return this.status;
    }

    public Announcement status(ContentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public Instant getPublishedFrom() {
        return this.publishedFrom;
    }

    public Announcement publishedFrom(Instant publishedFrom) {
        this.setPublishedFrom(publishedFrom);
        return this;
    }

    public void setPublishedFrom(Instant publishedFrom) {
        this.publishedFrom = publishedFrom;
    }

    public Instant getPublishedTo() {
        return this.publishedTo;
    }

    public Announcement publishedTo(Instant publishedTo) {
        this.setPublishedTo(publishedTo);
        return this;
    }

    public void setPublishedTo(Instant publishedTo) {
        this.publishedTo = publishedTo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Announcement)) {
            return false;
        }
        return getId() != null && getId().equals(((Announcement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Announcement{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", body='" + getBody() + "'" +
            ", status='" + getStatus() + "'" +
            ", publishedFrom='" + getPublishedFrom() + "'" +
            ", publishedTo='" + getPublishedTo() + "'" +
            "}";
    }
}
