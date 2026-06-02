package zw.org.nmrl.ept.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.HomePageSection} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HomePageSectionDTO implements Serializable {

    private Long id;

    @NotNull
    private String section;

    private String type;

    private String title;

    @Lob
    private String text;

    private String link;

    private String fileRef;

    private String icon;

    private Integer displayOrder;

    @NotNull
    private ContentStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFileRef() {
        return fileRef;
    }

    public void setFileRef(String fileRef) {
        this.fileRef = fileRef;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
        if (!(o instanceof HomePageSectionDTO)) {
            return false;
        }

        HomePageSectionDTO homePageSectionDTO = (HomePageSectionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, homePageSectionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HomePageSectionDTO{" +
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
