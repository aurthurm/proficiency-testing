package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ShipmentSample} entity.
 */
@Schema(description = "A single sample/control within a shipment panel (shared structure).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentSampleDTO implements Serializable {

    private Long id;

    @NotNull
    private String label;

    private Integer displayOrder;

    private Boolean isControl;

    private Boolean isMandatory;

    private Double sampleScore;

    private LocalDate preparationDate;

    @NotNull
    private ShipmentDTO shipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsControl() {
        return isControl;
    }

    public void setIsControl(Boolean isControl) {
        this.isControl = isControl;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public Double getSampleScore() {
        return sampleScore;
    }

    public void setSampleScore(Double sampleScore) {
        this.sampleScore = sampleScore;
    }

    public LocalDate getPreparationDate() {
        return preparationDate;
    }

    public void setPreparationDate(LocalDate preparationDate) {
        this.preparationDate = preparationDate;
    }

    public ShipmentDTO getShipment() {
        return shipment;
    }

    public void setShipment(ShipmentDTO shipment) {
        this.shipment = shipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentSampleDTO)) {
            return false;
        }

        ShipmentSampleDTO shipmentSampleDTO = (ShipmentSampleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shipmentSampleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentSampleDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", isControl='" + getIsControl() + "'" +
            ", isMandatory='" + getIsMandatory() + "'" +
            ", sampleScore=" + getSampleScore() +
            ", preparationDate='" + getPreparationDate() + "'" +
            ", shipment=" + getShipment() +
            "}";
    }
}
