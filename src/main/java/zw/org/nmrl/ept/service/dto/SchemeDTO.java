package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.ResultModality;
import zw.org.nmrl.ept.domain.enumeration.SchemeType;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.Scheme} entity.
 */
@Schema(description = "A PT test discipline (DTS, VL, EID, ...).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SchemeDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private SchemeType schemeType;

    @NotNull
    private ResultModality modality;

    @NotNull
    private Status status;

    private CertificateTemplateDTO certificateTemplate;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SchemeType getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(SchemeType schemeType) {
        this.schemeType = schemeType;
    }

    public ResultModality getModality() {
        return modality;
    }

    public void setModality(ResultModality modality) {
        this.modality = modality;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CertificateTemplateDTO getCertificateTemplate() {
        return certificateTemplate;
    }

    public void setCertificateTemplate(CertificateTemplateDTO certificateTemplate) {
        this.certificateTemplate = certificateTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SchemeDTO)) {
            return false;
        }

        SchemeDTO schemeDTO = (SchemeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, schemeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SchemeDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", schemeType='" + getSchemeType() + "'" +
            ", modality='" + getModality() + "'" +
            ", status='" + getStatus() + "'" +
            ", certificateTemplate=" + getCertificateTemplate() +
            "}";
    }
}
