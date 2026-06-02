package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import zw.org.nmrl.ept.domain.enumeration.ShipmentStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.Shipment} entity.
 */
@Schema(description = "PT panel sent to participants.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private LocalDate shipmentDate;

    @NotNull
    private Instant responseDeadline;

    private Boolean responsesOpen;

    private Boolean autoCloseAtDeadline;

    private Boolean allowEditingResponse;

    private String issuingAuthority;

    private String coordinatorName;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String coordinatorEmail;

    private String coordinatorPhone;

    private Integer numberOfSamples;

    private Integer maxScore;

    @NotNull
    private ShipmentStatus status;

    @Lob
    private String attributes;

    private Instant reportsGeneratedAt;

    private Instant finalizedAt;

    private DistributionDTO distribution;

    @NotNull
    private SchemeDTO scheme;

    private Set<CertificateBatchDTO> certificateBatcheses = new HashSet<>();

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

    public LocalDate getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDate shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public Instant getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(Instant responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public Boolean getResponsesOpen() {
        return responsesOpen;
    }

    public void setResponsesOpen(Boolean responsesOpen) {
        this.responsesOpen = responsesOpen;
    }

    public Boolean getAutoCloseAtDeadline() {
        return autoCloseAtDeadline;
    }

    public void setAutoCloseAtDeadline(Boolean autoCloseAtDeadline) {
        this.autoCloseAtDeadline = autoCloseAtDeadline;
    }

    public Boolean getAllowEditingResponse() {
        return allowEditingResponse;
    }

    public void setAllowEditingResponse(Boolean allowEditingResponse) {
        this.allowEditingResponse = allowEditingResponse;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public String getCoordinatorEmail() {
        return coordinatorEmail;
    }

    public void setCoordinatorEmail(String coordinatorEmail) {
        this.coordinatorEmail = coordinatorEmail;
    }

    public String getCoordinatorPhone() {
        return coordinatorPhone;
    }

    public void setCoordinatorPhone(String coordinatorPhone) {
        this.coordinatorPhone = coordinatorPhone;
    }

    public Integer getNumberOfSamples() {
        return numberOfSamples;
    }

    public void setNumberOfSamples(Integer numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public Instant getReportsGeneratedAt() {
        return reportsGeneratedAt;
    }

    public void setReportsGeneratedAt(Instant reportsGeneratedAt) {
        this.reportsGeneratedAt = reportsGeneratedAt;
    }

    public Instant getFinalizedAt() {
        return finalizedAt;
    }

    public void setFinalizedAt(Instant finalizedAt) {
        this.finalizedAt = finalizedAt;
    }

    public DistributionDTO getDistribution() {
        return distribution;
    }

    public void setDistribution(DistributionDTO distribution) {
        this.distribution = distribution;
    }

    public SchemeDTO getScheme() {
        return scheme;
    }

    public void setScheme(SchemeDTO scheme) {
        this.scheme = scheme;
    }

    public Set<CertificateBatchDTO> getCertificateBatcheses() {
        return certificateBatcheses;
    }

    public void setCertificateBatcheses(Set<CertificateBatchDTO> certificateBatcheses) {
        this.certificateBatcheses = certificateBatcheses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentDTO)) {
            return false;
        }

        ShipmentDTO shipmentDTO = (ShipmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shipmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", shipmentDate='" + getShipmentDate() + "'" +
            ", responseDeadline='" + getResponseDeadline() + "'" +
            ", responsesOpen='" + getResponsesOpen() + "'" +
            ", autoCloseAtDeadline='" + getAutoCloseAtDeadline() + "'" +
            ", allowEditingResponse='" + getAllowEditingResponse() + "'" +
            ", issuingAuthority='" + getIssuingAuthority() + "'" +
            ", coordinatorName='" + getCoordinatorName() + "'" +
            ", coordinatorEmail='" + getCoordinatorEmail() + "'" +
            ", coordinatorPhone='" + getCoordinatorPhone() + "'" +
            ", numberOfSamples=" + getNumberOfSamples() +
            ", maxScore=" + getMaxScore() +
            ", status='" + getStatus() + "'" +
            ", attributes='" + getAttributes() + "'" +
            ", reportsGeneratedAt='" + getReportsGeneratedAt() + "'" +
            ", finalizedAt='" + getFinalizedAt() + "'" +
            ", distribution=" + getDistribution() +
            ", scheme=" + getScheme() +
            ", certificateBatcheses=" + getCertificateBatcheses() +
            "}";
    }
}
