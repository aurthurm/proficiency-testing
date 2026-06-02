package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.Participant} entity.
 */
@Schema(description = "Testing site / laboratory.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantDTO implements Serializable {

    private Long id;

    @NotNull
    private String uniqueIdentifier;

    @NotNull
    private String instituteName;

    private String departmentName;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String additionalEmail;

    private String address;

    private String shippingAddress;

    private String city;

    private String state;

    private String district;

    private String zip;

    private String region;

    private String phone;

    private String mobile;

    @NotNull
    private String affiliation;

    private String networkTier;

    private String siteType;

    private String fundingSource;

    private Long testingVolume;

    private String pepfarId;

    private Double latitude;

    private Double longitude;

    private String labDirectorName;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String labDirectorEmail;

    private String contactPersonName;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String contactPersonEmail;

    private String contactPersonPhone;

    @NotNull
    private Status status;

    private CountryDTO country;

    private Set<DataManagerDTO> dataManagerses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdditionalEmail() {
        return additionalEmail;
    }

    public void setAdditionalEmail(String additionalEmail) {
        this.additionalEmail = additionalEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getNetworkTier() {
        return networkTier;
    }

    public void setNetworkTier(String networkTier) {
        this.networkTier = networkTier;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public Long getTestingVolume() {
        return testingVolume;
    }

    public void setTestingVolume(Long testingVolume) {
        this.testingVolume = testingVolume;
    }

    public String getPepfarId() {
        return pepfarId;
    }

    public void setPepfarId(String pepfarId) {
        this.pepfarId = pepfarId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLabDirectorName() {
        return labDirectorName;
    }

    public void setLabDirectorName(String labDirectorName) {
        this.labDirectorName = labDirectorName;
    }

    public String getLabDirectorEmail() {
        return labDirectorEmail;
    }

    public void setLabDirectorEmail(String labDirectorEmail) {
        this.labDirectorEmail = labDirectorEmail;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public Set<DataManagerDTO> getDataManagerses() {
        return dataManagerses;
    }

    public void setDataManagerses(Set<DataManagerDTO> dataManagerses) {
        this.dataManagerses = dataManagerses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantDTO)) {
            return false;
        }

        ParticipantDTO participantDTO = (ParticipantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, participantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantDTO{" +
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
            ", country=" + getCountry() +
            ", dataManagerses=" + getDataManagerses() +
            "}";
    }
}
