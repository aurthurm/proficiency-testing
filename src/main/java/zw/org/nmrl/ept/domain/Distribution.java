package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.DistributionStatus;

/**
 * PT survey grouping shipments.
 */
@Entity
@Table(name = "distribution")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Distribution extends LegacyCompatibleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotNull
    @Column(name = "distribution_date", nullable = false)
    private LocalDate distributionDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DistributionStatus status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "distribution")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "sampleses", "participantMapses", "distribution", "scheme", "certificateBatcheses" },
        allowSetters = true
    )
    private Set<Shipment> shipmentses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Distribution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Distribution code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDistributionDate() {
        return this.distributionDate;
    }

    public Distribution distributionDate(LocalDate distributionDate) {
        this.setDistributionDate(distributionDate);
        return this;
    }

    public void setDistributionDate(LocalDate distributionDate) {
        this.distributionDate = distributionDate;
    }

    public DistributionStatus getStatus() {
        return this.status;
    }

    public Distribution status(DistributionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DistributionStatus status) {
        this.status = status;
    }

    public Set<Shipment> getShipmentses() {
        return this.shipmentses;
    }

    public void setShipmentses(Set<Shipment> shipments) {
        if (this.shipmentses != null) {
            this.shipmentses.forEach(i -> i.setDistribution(null));
        }
        if (shipments != null) {
            shipments.forEach(i -> i.setDistribution(this));
        }
        this.shipmentses = shipments;
    }

    public Distribution shipmentses(Set<Shipment> shipments) {
        this.setShipmentses(shipments);
        return this;
    }

    public Distribution addShipments(Shipment shipment) {
        this.shipmentses.add(shipment);
        shipment.setDistribution(this);
        return this;
    }

    public Distribution removeShipments(Shipment shipment) {
        this.shipmentses.remove(shipment);
        shipment.setDistribution(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Distribution)) {
            return false;
        }
        return getId() != null && getId().equals(((Distribution) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Distribution{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", distributionDate='" + getDistributionDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
