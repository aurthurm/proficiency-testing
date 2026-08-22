package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.EnrollmentStatus;

/**
 * Participant enrolled into a scheme/programme (programme-level membership).
 */
@Entity
@Table(name = "enrollment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Enrollment extends LegacyCompatibleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    @NotNull
    @Column(name = "enrolled_on", nullable = false)
    private LocalDate enrolledOn;

    @Column(name = "withdrawn_on")
    private LocalDate withdrawnOn;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "shipmentMapses", "country", "enrollmentses", "customValueses", "dataManagerses" }, allowSetters = true)
    private Participant participant;

    @ManyToOne(optional = false)
    @NotNull
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
    private Scheme scheme;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Enrollment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnrollmentStatus getStatus() {
        return this.status;
    }

    public Enrollment status(EnrollmentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDate getEnrolledOn() {
        return this.enrolledOn;
    }

    public Enrollment enrolledOn(LocalDate enrolledOn) {
        this.setEnrolledOn(enrolledOn);
        return this;
    }

    public void setEnrolledOn(LocalDate enrolledOn) {
        this.enrolledOn = enrolledOn;
    }

    public LocalDate getWithdrawnOn() {
        return this.withdrawnOn;
    }

    public Enrollment withdrawnOn(LocalDate withdrawnOn) {
        this.setWithdrawnOn(withdrawnOn);
        return this;
    }

    public void setWithdrawnOn(LocalDate withdrawnOn) {
        this.withdrawnOn = withdrawnOn;
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Enrollment participant(Participant participant) {
        this.setParticipant(participant);
        return this;
    }

    public Scheme getScheme() {
        return this.scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public Enrollment scheme(Scheme scheme) {
        this.setScheme(scheme);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrollment)) {
            return false;
        }
        return getId() != null && getId().equals(((Enrollment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Enrollment{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", enrolledOn='" + getEnrolledOn() + "'" +
            ", withdrawnOn='" + getWithdrawnOn() + "'" +
            "}";
    }
}
