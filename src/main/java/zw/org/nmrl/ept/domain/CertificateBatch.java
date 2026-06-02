package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.CertificateBatchStatus;

/**
 * A CertificateBatch.
 */
@Entity
@Table(name = "certificate_batch")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CertificateBatch implements Serializable {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CertificateBatchStatus status;

    @Column(name = "excellence_count")
    private Integer excellenceCount;

    @Column(name = "participation_count")
    private Integer participationCount;

    @Column(name = "skipped_count")
    private Integer skippedCount;

    @Column(name = "download_url")
    private String downloadUrl;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_on")
    private Instant approvedOn;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_certificate_batch__shipments",
        joinColumns = @JoinColumn(name = "certificate_batch_id"),
        inverseJoinColumns = @JoinColumn(name = "shipments_id")
    )
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

    public CertificateBatch id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public CertificateBatch name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CertificateBatchStatus getStatus() {
        return this.status;
    }

    public CertificateBatch status(CertificateBatchStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CertificateBatchStatus status) {
        this.status = status;
    }

    public Integer getExcellenceCount() {
        return this.excellenceCount;
    }

    public CertificateBatch excellenceCount(Integer excellenceCount) {
        this.setExcellenceCount(excellenceCount);
        return this;
    }

    public void setExcellenceCount(Integer excellenceCount) {
        this.excellenceCount = excellenceCount;
    }

    public Integer getParticipationCount() {
        return this.participationCount;
    }

    public CertificateBatch participationCount(Integer participationCount) {
        this.setParticipationCount(participationCount);
        return this;
    }

    public void setParticipationCount(Integer participationCount) {
        this.participationCount = participationCount;
    }

    public Integer getSkippedCount() {
        return this.skippedCount;
    }

    public CertificateBatch skippedCount(Integer skippedCount) {
        this.setSkippedCount(skippedCount);
        return this;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public CertificateBatch downloadUrl(String downloadUrl) {
        this.setDownloadUrl(downloadUrl);
        return this;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public CertificateBatch errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getApprovedBy() {
        return this.approvedBy;
    }

    public CertificateBatch approvedBy(String approvedBy) {
        this.setApprovedBy(approvedBy);
        return this;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedOn() {
        return this.approvedOn;
    }

    public CertificateBatch approvedOn(Instant approvedOn) {
        this.setApprovedOn(approvedOn);
        return this;
    }

    public void setApprovedOn(Instant approvedOn) {
        this.approvedOn = approvedOn;
    }

    public Set<Shipment> getShipmentses() {
        return this.shipmentses;
    }

    public void setShipmentses(Set<Shipment> shipments) {
        this.shipmentses = shipments;
    }

    public CertificateBatch shipmentses(Set<Shipment> shipments) {
        this.setShipmentses(shipments);
        return this;
    }

    public CertificateBatch addShipments(Shipment shipment) {
        this.shipmentses.add(shipment);
        return this;
    }

    public CertificateBatch removeShipments(Shipment shipment) {
        this.shipmentses.remove(shipment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateBatch)) {
            return false;
        }
        return getId() != null && getId().equals(((CertificateBatch) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertificateBatch{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            ", excellenceCount=" + getExcellenceCount() +
            ", participationCount=" + getParticipationCount() +
            ", skippedCount=" + getSkippedCount() +
            ", downloadUrl='" + getDownloadUrl() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", approvedBy='" + getApprovedBy() + "'" +
            ", approvedOn='" + getApprovedOn() + "'" +
            "}";
    }
}
