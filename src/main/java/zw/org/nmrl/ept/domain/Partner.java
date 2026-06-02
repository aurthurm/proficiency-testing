package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A Partner.
 */
@Entity
@Table(name = "partner")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Partner implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "link")
    private String link;

    @Column(name = "logo_ref")
    private String logoRef;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Partner id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Partner name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return this.link;
    }

    public Partner link(String link) {
        this.setLink(link);
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLogoRef() {
        return this.logoRef;
    }

    public Partner logoRef(String logoRef) {
        this.setLogoRef(logoRef);
        return this;
    }

    public void setLogoRef(String logoRef) {
        this.logoRef = logoRef;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public Partner sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public ContentStatus getStatus() {
        return this.status;
    }

    public Partner status(ContentStatus status) {
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
        if (!(o instanceof Partner)) {
            return false;
        }
        return getId() != null && getId().equals(((Partner) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Partner{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", link='" + getLink() + "'" +
            ", logoRef='" + getLogoRef() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
