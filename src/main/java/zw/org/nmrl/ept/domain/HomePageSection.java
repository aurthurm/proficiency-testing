package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A HomePageSection.
 */
@Entity
@Table(name = "home_page_section")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HomePageSection implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "section", nullable = false)
    private String section;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "text")
    private String text;

    @Column(name = "link")
    private String link;

    @Column(name = "file_ref")
    private String fileRef;

    @Column(name = "icon")
    private String icon;

    @Column(name = "display_order")
    private Integer displayOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HomePageSection id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSection() {
        return this.section;
    }

    public HomePageSection section(String section) {
        this.setSection(section);
        return this;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getType() {
        return this.type;
    }

    public HomePageSection type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public HomePageSection title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return this.text;
    }

    public HomePageSection text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return this.link;
    }

    public HomePageSection link(String link) {
        this.setLink(link);
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFileRef() {
        return this.fileRef;
    }

    public HomePageSection fileRef(String fileRef) {
        this.setFileRef(fileRef);
        return this;
    }

    public void setFileRef(String fileRef) {
        this.fileRef = fileRef;
    }

    public String getIcon() {
        return this.icon;
    }

    public HomePageSection icon(String icon) {
        this.setIcon(icon);
        return this;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public HomePageSection displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public ContentStatus getStatus() {
        return this.status;
    }

    public HomePageSection status(ContentStatus status) {
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
        if (!(o instanceof HomePageSection)) {
            return false;
        }
        return getId() != null && getId().equals(((HomePageSection) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HomePageSection{" +
            "id=" + getId() +
            ", section='" + getSection() + "'" +
            ", type='" + getType() + "'" +
            ", title='" + getTitle() + "'" +
            ", text='" + getText() + "'" +
            ", link='" + getLink() + "'" +
            ", fileRef='" + getFileRef() + "'" +
            ", icon='" + getIcon() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
