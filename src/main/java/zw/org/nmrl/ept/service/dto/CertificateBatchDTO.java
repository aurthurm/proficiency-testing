package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import zw.org.nmrl.ept.domain.enumeration.CertificateBatchStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.CertificateBatch} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CertificateBatchDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private CertificateBatchStatus status;

    private Integer excellenceCount;

    private Integer participationCount;

    private Integer skippedCount;

    private String downloadUrl;

    private String errorMessage;

    private String approvedBy;

    private Instant approvedOn;

    private Set<ShipmentDTO> shipmentses = new HashSet<>();

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

    public CertificateBatchStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateBatchStatus status) {
        this.status = status;
    }

    public Integer getExcellenceCount() {
        return excellenceCount;
    }

    public void setExcellenceCount(Integer excellenceCount) {
        this.excellenceCount = excellenceCount;
    }

    public Integer getParticipationCount() {
        return participationCount;
    }

    public void setParticipationCount(Integer participationCount) {
        this.participationCount = participationCount;
    }

    public Integer getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(Instant approvedOn) {
        this.approvedOn = approvedOn;
    }

    public Set<ShipmentDTO> getShipmentses() {
        return shipmentses;
    }

    public void setShipmentses(Set<ShipmentDTO> shipmentses) {
        this.shipmentses = shipmentses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateBatchDTO)) {
            return false;
        }

        CertificateBatchDTO certificateBatchDTO = (CertificateBatchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, certificateBatchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertificateBatchDTO{" +
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
            ", shipmentses=" + getShipmentses() +
            "}";
    }
}
