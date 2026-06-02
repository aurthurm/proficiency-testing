package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.CertificateType;

/**
 * A CertificateTemplate.
 */
@Entity
@Table(name = "certificate_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CertificateTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @Column(name = "file_ref")
    private String fileRef;

    @Lob
    @Column(name = "detected_fields")
    private String detectedFields;

    @JsonIgnoreProperties(
        value = {
            "certificateTemplate",
            "configurationses",
            "correctiveActionses",
            "assayses",
            "testKitses",
            "shipmentses",
            "enrollmentses",
        },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "certificateTemplate")
    private Scheme scheme;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CertificateTemplate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CertificateType getCertificateType() {
        return this.certificateType;
    }

    public CertificateTemplate certificateType(CertificateType certificateType) {
        this.setCertificateType(certificateType);
        return this;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public String getFileRef() {
        return this.fileRef;
    }

    public CertificateTemplate fileRef(String fileRef) {
        this.setFileRef(fileRef);
        return this;
    }

    public void setFileRef(String fileRef) {
        this.fileRef = fileRef;
    }

    public String getDetectedFields() {
        return this.detectedFields;
    }

    public CertificateTemplate detectedFields(String detectedFields) {
        this.setDetectedFields(detectedFields);
        return this;
    }

    public void setDetectedFields(String detectedFields) {
        this.detectedFields = detectedFields;
    }

    public Scheme getScheme() {
        return this.scheme;
    }

    public void setScheme(Scheme scheme) {
        if (this.scheme != null) {
            this.scheme.setCertificateTemplate(null);
        }
        if (scheme != null) {
            scheme.setCertificateTemplate(this);
        }
        this.scheme = scheme;
    }

    public CertificateTemplate scheme(Scheme scheme) {
        this.setScheme(scheme);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateTemplate)) {
            return false;
        }
        return getId() != null && getId().equals(((CertificateTemplate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertificateTemplate{" +
            "id=" + getId() +
            ", certificateType='" + getCertificateType() + "'" +
            ", fileRef='" + getFileRef() + "'" +
            ", detectedFields='" + getDetectedFields() + "'" +
            "}";
    }
}
