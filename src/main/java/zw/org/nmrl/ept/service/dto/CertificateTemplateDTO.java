package zw.org.nmrl.ept.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.CertificateType;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.CertificateTemplate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CertificateTemplateDTO implements Serializable {

    private Long id;

    @NotNull
    private CertificateType certificateType;

    private String fileRef;

    @Lob
    private String detectedFields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public String getFileRef() {
        return fileRef;
    }

    public void setFileRef(String fileRef) {
        this.fileRef = fileRef;
    }

    public String getDetectedFields() {
        return detectedFields;
    }

    public void setDetectedFields(String detectedFields) {
        this.detectedFields = detectedFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateTemplateDTO)) {
            return false;
        }

        CertificateTemplateDTO certificateTemplateDTO = (CertificateTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, certificateTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertificateTemplateDTO{" +
            "id=" + getId() +
            ", certificateType='" + getCertificateType() + "'" +
            ", fileRef='" + getFileRef() + "'" +
            ", detectedFields='" + getDetectedFields() + "'" +
            "}";
    }
}
