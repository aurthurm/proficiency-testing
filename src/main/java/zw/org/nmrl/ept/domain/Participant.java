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
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * Testing site / laboratory.
 */
@Entity
@Table(name = "participant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Participant extends LegacyCompatibleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "unique_identifier", nullable = false, unique = true)
    private String uniqueIdentifier;

    @NotNull
    @Column(name = "institute_name", nullable = false)
    private String instituteName;

    @Column(name = "department_name")
    private String departmentName;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false)
    private String email;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "additional_email")
    private String additionalEmail;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "shipping_address", length = 1000)
    private String shippingAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "district")
    private String district;

    @Column(name = "zip")
    private String zip;

    @Column(name = "region")
    private String region;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mobile")
    private String mobile;

    @NotNull
    @Column(name = "affiliation", nullable = false)
    private String affiliation;

    @Column(name = "network_tier")
    private String networkTier;

    @Column(name = "site_type")
    private String siteType;

    @Column(name = "funding_source")
    private String fundingSource;

    @Column(name = "testing_volume")
    private Long testingVolume;

    @Column(name = "pepfar_id")
    private String pepfarId;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "lab_director_name")
    private String labDirectorName;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "lab_director_email")
    private String labDirectorEmail;

    @Column(name = "contact_person_name")
    private String contactPersonName;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "contact_person_email")
    private String contactPersonEmail;

    @Column(name = "contact_person_phone")
    private String contactPersonPhone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "participant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "resultses", "capaRecordses", "modeOfReceipt", "notTestedReason", "shipment", "participant" },
        allowSetters = true
    )
    private Set<ShipmentParticipantMap> shipmentMapses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "participant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "participant", "scheme" }, allowSetters = true)
    private Set<Enrollment> enrollmentses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "participant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "participant", "definition" }, allowSetters = true)
    private Set<ParticipantCustomValue> customValueses = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "participantses")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "country", "participantses" }, allowSetters = true)
    private Set<DataManager> dataManagerses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Participant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueIdentifier() {
        return this.uniqueIdentifier;
    }

    public Participant uniqueIdentifier(String uniqueIdentifier) {
        this.setUniqueIdentifier(uniqueIdentifier);
        return this;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getInstituteName() {
        return this.instituteName;
    }

    public Participant instituteName(String instituteName) {
        this.setInstituteName(instituteName);
        return this;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public Participant departmentName(String departmentName) {
        this.setDepartmentName(departmentName);
        return this;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmail() {
        return this.email;
    }

    public Participant email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdditionalEmail() {
        return this.additionalEmail;
    }

    public Participant additionalEmail(String additionalEmail) {
        this.setAdditionalEmail(additionalEmail);
        return this;
    }

    public void setAdditionalEmail(String additionalEmail) {
        this.additionalEmail = additionalEmail;
    }

    public String getAddress() {
        return this.address;
    }

    public Participant address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public Participant shippingAddress(String shippingAddress) {
        this.setShippingAddress(shippingAddress);
        return this;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCity() {
        return this.city;
    }

    public Participant city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public Participant state(String state) {
        this.setState(state);
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return this.district;
    }

    public Participant district(String district) {
        this.setDistrict(district);
        return this;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getZip() {
        return this.zip;
    }

    public Participant zip(String zip) {
        this.setZip(zip);
        return this;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getRegion() {
        return this.region;
    }

    public Participant region(String region) {
        this.setRegion(region);
        return this;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhone() {
        return this.phone;
    }

    public Participant phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return this.mobile;
    }

    public Participant mobile(String mobile) {
        this.setMobile(mobile);
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAffiliation() {
        return this.affiliation;
    }

    public Participant affiliation(String affiliation) {
        this.setAffiliation(affiliation);
        return this;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getNetworkTier() {
        return this.networkTier;
    }

    public Participant networkTier(String networkTier) {
        this.setNetworkTier(networkTier);
        return this;
    }

    public void setNetworkTier(String networkTier) {
        this.networkTier = networkTier;
    }

    public String getSiteType() {
        return this.siteType;
    }

    public Participant siteType(String siteType) {
        this.setSiteType(siteType);
        return this;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getFundingSource() {
        return this.fundingSource;
    }

    public Participant fundingSource(String fundingSource) {
        this.setFundingSource(fundingSource);
        return this;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public Long getTestingVolume() {
        return this.testingVolume;
    }

    public Participant testingVolume(Long testingVolume) {
        this.setTestingVolume(testingVolume);
        return this;
    }

    public void setTestingVolume(Long testingVolume) {
        this.testingVolume = testingVolume;
    }

    public String getPepfarId() {
        return this.pepfarId;
    }

    public Participant pepfarId(String pepfarId) {
        this.setPepfarId(pepfarId);
        return this;
    }

    public void setPepfarId(String pepfarId) {
        this.pepfarId = pepfarId;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Participant latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Participant longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLabDirectorName() {
        return this.labDirectorName;
    }

    public Participant labDirectorName(String labDirectorName) {
        this.setLabDirectorName(labDirectorName);
        return this;
    }

    public void setLabDirectorName(String labDirectorName) {
        this.labDirectorName = labDirectorName;
    }

    public String getLabDirectorEmail() {
        return this.labDirectorEmail;
    }

    public Participant labDirectorEmail(String labDirectorEmail) {
        this.setLabDirectorEmail(labDirectorEmail);
        return this;
    }

    public void setLabDirectorEmail(String labDirectorEmail) {
        this.labDirectorEmail = labDirectorEmail;
    }

    public String getContactPersonName() {
        return this.contactPersonName;
    }

    public Participant contactPersonName(String contactPersonName) {
        this.setContactPersonName(contactPersonName);
        return this;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonEmail() {
        return this.contactPersonEmail;
    }

    public Participant contactPersonEmail(String contactPersonEmail) {
        this.setContactPersonEmail(contactPersonEmail);
        return this;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return this.contactPersonPhone;
    }

    public Participant contactPersonPhone(String contactPersonPhone) {
        this.setContactPersonPhone(contactPersonPhone);
        return this;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public Status getStatus() {
        return this.status;
    }

    public Participant status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<ShipmentParticipantMap> getShipmentMapses() {
        return this.shipmentMapses;
    }

    public void setShipmentMapses(Set<ShipmentParticipantMap> shipmentParticipantMaps) {
        if (this.shipmentMapses != null) {
            this.shipmentMapses.forEach(i -> i.setParticipant(null));
        }
        if (shipmentParticipantMaps != null) {
            shipmentParticipantMaps.forEach(i -> i.setParticipant(this));
        }
        this.shipmentMapses = shipmentParticipantMaps;
    }

    public Participant shipmentMapses(Set<ShipmentParticipantMap> shipmentParticipantMaps) {
        this.setShipmentMapses(shipmentParticipantMaps);
        return this;
    }

    public Participant addShipmentMaps(ShipmentParticipantMap shipmentParticipantMap) {
        this.shipmentMapses.add(shipmentParticipantMap);
        shipmentParticipantMap.setParticipant(this);
        return this;
    }

    public Participant removeShipmentMaps(ShipmentParticipantMap shipmentParticipantMap) {
        this.shipmentMapses.remove(shipmentParticipantMap);
        shipmentParticipantMap.setParticipant(null);
        return this;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Participant country(Country country) {
        this.setCountry(country);
        return this;
    }

    public Set<Enrollment> getEnrollmentses() {
        return this.enrollmentses;
    }

    public void setEnrollmentses(Set<Enrollment> enrollments) {
        if (this.enrollmentses != null) {
            this.enrollmentses.forEach(i -> i.setParticipant(null));
        }
        if (enrollments != null) {
            enrollments.forEach(i -> i.setParticipant(this));
        }
        this.enrollmentses = enrollments;
    }

    public Participant enrollmentses(Set<Enrollment> enrollments) {
        this.setEnrollmentses(enrollments);
        return this;
    }

    public Participant addEnrollments(Enrollment enrollment) {
        this.enrollmentses.add(enrollment);
        enrollment.setParticipant(this);
        return this;
    }

    public Participant removeEnrollments(Enrollment enrollment) {
        this.enrollmentses.remove(enrollment);
        enrollment.setParticipant(null);
        return this;
    }

    public Set<ParticipantCustomValue> getCustomValueses() {
        return this.customValueses;
    }

    public void setCustomValueses(Set<ParticipantCustomValue> participantCustomValues) {
        if (this.customValueses != null) {
            this.customValueses.forEach(i -> i.setParticipant(null));
        }
        if (participantCustomValues != null) {
            participantCustomValues.forEach(i -> i.setParticipant(this));
        }
        this.customValueses = participantCustomValues;
    }

    public Participant customValueses(Set<ParticipantCustomValue> participantCustomValues) {
        this.setCustomValueses(participantCustomValues);
        return this;
    }

    public Participant addCustomValues(ParticipantCustomValue participantCustomValue) {
        this.customValueses.add(participantCustomValue);
        participantCustomValue.setParticipant(this);
        return this;
    }

    public Participant removeCustomValues(ParticipantCustomValue participantCustomValue) {
        this.customValueses.remove(participantCustomValue);
        participantCustomValue.setParticipant(null);
        return this;
    }

    public Set<DataManager> getDataManagerses() {
        return this.dataManagerses;
    }

    public void setDataManagerses(Set<DataManager> dataManagers) {
        if (this.dataManagerses != null) {
            this.dataManagerses.forEach(i -> i.removeParticipants(this));
        }
        if (dataManagers != null) {
            dataManagers.forEach(i -> i.addParticipants(this));
        }
        this.dataManagerses = dataManagers;
    }

    public Participant dataManagerses(Set<DataManager> dataManagers) {
        this.setDataManagerses(dataManagers);
        return this;
    }

    public Participant addDataManagers(DataManager dataManager) {
        this.dataManagerses.add(dataManager);
        dataManager.getParticipantses().add(this);
        return this;
    }

    public Participant removeDataManagers(DataManager dataManager) {
        this.dataManagerses.remove(dataManager);
        dataManager.getParticipantses().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Participant)) {
            return false;
        }
        return getId() != null && getId().equals(((Participant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Participant{" +
            "id=" + getId() +
            ", uniqueIdentifier='" + getUniqueIdentifier() + "'" +
            ", instituteName='" + getInstituteName() + "'" +
            ", departmentName='" + getDepartmentName() + "'" +
            ", email='" + getEmail() + "'" +
            ", additionalEmail='" + getAdditionalEmail() + "'" +
            ", address='" + getAddress() + "'" +
            ", shippingAddress='" + getShippingAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", state='" + getState() + "'" +
            ", district='" + getDistrict() + "'" +
            ", zip='" + getZip() + "'" +
            ", region='" + getRegion() + "'" +
            ", phone='" + getPhone() + "'" +
            ", mobile='" + getMobile() + "'" +
            ", affiliation='" + getAffiliation() + "'" +
            ", networkTier='" + getNetworkTier() + "'" +
            ", siteType='" + getSiteType() + "'" +
            ", fundingSource='" + getFundingSource() + "'" +
            ", testingVolume=" + getTestingVolume() +
            ", pepfarId='" + getPepfarId() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", labDirectorName='" + getLabDirectorName() + "'" +
            ", labDirectorEmail='" + getLabDirectorEmail() + "'" +
            ", contactPersonName='" + getContactPersonName() + "'" +
            ", contactPersonEmail='" + getContactPersonEmail() + "'" +
            ", contactPersonPhone='" + getContactPersonPhone() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
