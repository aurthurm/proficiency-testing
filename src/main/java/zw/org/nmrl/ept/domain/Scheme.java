package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.ResultModality;
import zw.org.nmrl.ept.domain.enumeration.SchemeType;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A PT test discipline (DTS, VL, EID, ...).
 */
@Entity
@Table(name = "scheme")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Scheme implements Serializable {

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
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_type", nullable = false)
    private SchemeType schemeType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "modality", nullable = false)
    private ResultModality modality;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private CertificateTemplate certificateTemplate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    private Set<SchemeConfiguration> configurationses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    private Set<CorrectiveAction> correctiveActionses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    private Set<Assay> assayses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "scheme" }, allowSetters = true)
    private Set<TestKit> testKitses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "sampleses", "participantMapses", "distribution", "scheme", "certificateBatcheses" },
        allowSetters = true
    )
    private Set<Shipment> shipmentses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "participant", "scheme" }, allowSetters = true)
    private Set<Enrollment> enrollmentses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Scheme id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Scheme code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Scheme name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SchemeType getSchemeType() {
        return this.schemeType;
    }

    public Scheme schemeType(SchemeType schemeType) {
        this.setSchemeType(schemeType);
        return this;
    }

    public void setSchemeType(SchemeType schemeType) {
        this.schemeType = schemeType;
    }

    public ResultModality getModality() {
        return this.modality;
    }

    public Scheme modality(ResultModality modality) {
        this.setModality(modality);
        return this;
    }

    public void setModality(ResultModality modality) {
        this.modality = modality;
    }

    public Status getStatus() {
        return this.status;
    }

    public Scheme status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CertificateTemplate getCertificateTemplate() {
        return this.certificateTemplate;
    }

    public void setCertificateTemplate(CertificateTemplate certificateTemplate) {
        this.certificateTemplate = certificateTemplate;
    }

    public Scheme certificateTemplate(CertificateTemplate certificateTemplate) {
        this.setCertificateTemplate(certificateTemplate);
        return this;
    }

    public Set<SchemeConfiguration> getConfigurationses() {
        return this.configurationses;
    }

    public void setConfigurationses(Set<SchemeConfiguration> schemeConfigurations) {
        if (this.configurationses != null) {
            this.configurationses.forEach(i -> i.setScheme(null));
        }
        if (schemeConfigurations != null) {
            schemeConfigurations.forEach(i -> i.setScheme(this));
        }
        this.configurationses = schemeConfigurations;
    }

    public Scheme configurationses(Set<SchemeConfiguration> schemeConfigurations) {
        this.setConfigurationses(schemeConfigurations);
        return this;
    }

    public Scheme addConfigurations(SchemeConfiguration schemeConfiguration) {
        this.configurationses.add(schemeConfiguration);
        schemeConfiguration.setScheme(this);
        return this;
    }

    public Scheme removeConfigurations(SchemeConfiguration schemeConfiguration) {
        this.configurationses.remove(schemeConfiguration);
        schemeConfiguration.setScheme(null);
        return this;
    }

    public Set<CorrectiveAction> getCorrectiveActionses() {
        return this.correctiveActionses;
    }

    public void setCorrectiveActionses(Set<CorrectiveAction> correctiveActions) {
        if (this.correctiveActionses != null) {
            this.correctiveActionses.forEach(i -> i.setScheme(null));
        }
        if (correctiveActions != null) {
            correctiveActions.forEach(i -> i.setScheme(this));
        }
        this.correctiveActionses = correctiveActions;
    }

    public Scheme correctiveActionses(Set<CorrectiveAction> correctiveActions) {
        this.setCorrectiveActionses(correctiveActions);
        return this;
    }

    public Scheme addCorrectiveActions(CorrectiveAction correctiveAction) {
        this.correctiveActionses.add(correctiveAction);
        correctiveAction.setScheme(this);
        return this;
    }

    public Scheme removeCorrectiveActions(CorrectiveAction correctiveAction) {
        this.correctiveActionses.remove(correctiveAction);
        correctiveAction.setScheme(null);
        return this;
    }

    public Set<Assay> getAssayses() {
        return this.assayses;
    }

    public void setAssayses(Set<Assay> assays) {
        if (this.assayses != null) {
            this.assayses.forEach(i -> i.setScheme(null));
        }
        if (assays != null) {
            assays.forEach(i -> i.setScheme(this));
        }
        this.assayses = assays;
    }

    public Scheme assayses(Set<Assay> assays) {
        this.setAssayses(assays);
        return this;
    }

    public Scheme addAssays(Assay assay) {
        this.assayses.add(assay);
        assay.setScheme(this);
        return this;
    }

    public Scheme removeAssays(Assay assay) {
        this.assayses.remove(assay);
        assay.setScheme(null);
        return this;
    }

    public Set<TestKit> getTestKitses() {
        return this.testKitses;
    }

    public void setTestKitses(Set<TestKit> testKits) {
        if (this.testKitses != null) {
            this.testKitses.forEach(i -> i.setScheme(null));
        }
        if (testKits != null) {
            testKits.forEach(i -> i.setScheme(this));
        }
        this.testKitses = testKits;
    }

    public Scheme testKitses(Set<TestKit> testKits) {
        this.setTestKitses(testKits);
        return this;
    }

    public Scheme addTestKits(TestKit testKit) {
        this.testKitses.add(testKit);
        testKit.setScheme(this);
        return this;
    }

    public Scheme removeTestKits(TestKit testKit) {
        this.testKitses.remove(testKit);
        testKit.setScheme(null);
        return this;
    }

    public Set<Shipment> getShipmentses() {
        return this.shipmentses;
    }

    public void setShipmentses(Set<Shipment> shipments) {
        if (this.shipmentses != null) {
            this.shipmentses.forEach(i -> i.setScheme(null));
        }
        if (shipments != null) {
            shipments.forEach(i -> i.setScheme(this));
        }
        this.shipmentses = shipments;
    }

    public Scheme shipmentses(Set<Shipment> shipments) {
        this.setShipmentses(shipments);
        return this;
    }

    public Scheme addShipments(Shipment shipment) {
        this.shipmentses.add(shipment);
        shipment.setScheme(this);
        return this;
    }

    public Scheme removeShipments(Shipment shipment) {
        this.shipmentses.remove(shipment);
        shipment.setScheme(null);
        return this;
    }

    public Set<Enrollment> getEnrollmentses() {
        return this.enrollmentses;
    }

    public void setEnrollmentses(Set<Enrollment> enrollments) {
        if (this.enrollmentses != null) {
            this.enrollmentses.forEach(i -> i.setScheme(null));
        }
        if (enrollments != null) {
            enrollments.forEach(i -> i.setScheme(this));
        }
        this.enrollmentses = enrollments;
    }

    public Scheme enrollmentses(Set<Enrollment> enrollments) {
        this.setEnrollmentses(enrollments);
        return this;
    }

    public Scheme addEnrollments(Enrollment enrollment) {
        this.enrollmentses.add(enrollment);
        enrollment.setScheme(this);
        return this;
    }

    public Scheme removeEnrollments(Enrollment enrollment) {
        this.enrollmentses.remove(enrollment);
        enrollment.setScheme(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scheme)) {
            return false;
        }
        return getId() != null && getId().equals(((Scheme) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Scheme{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", schemeType='" + getSchemeType() + "'" +
            ", modality='" + getModality() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
