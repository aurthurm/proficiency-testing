package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.Partner} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String link;

    private String logoRef;

    private Integer sortOrder;

    @NotNull
    private ContentStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLogoRef() {
        return logoRef;
    }

    public void setLogoRef(String logoRef) {
        this.logoRef = logoRef;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
        if (!(o instanceof PartnerDTO)) {
            return false;
        }

        PartnerDTO partnerDTO = (PartnerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partnerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", link='" + getLink() + "'" +
            ", logoRef='" + getLogoRef() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
