package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.Participant} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.ParticipantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /participants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter uniqueIdentifier;

    private StringFilter instituteName;

    private StringFilter departmentName;

    private StringFilter email;

    private StringFilter additionalEmail;

    private StringFilter address;

    private StringFilter shippingAddress;

    private StringFilter city;

    private StringFilter state;

    private StringFilter district;

    private StringFilter zip;

    private StringFilter region;

    private StringFilter phone;

    private StringFilter mobile;

    private StringFilter affiliation;

    private StringFilter networkTier;

    private StringFilter siteType;

    private StringFilter fundingSource;

    private LongFilter testingVolume;

    private StringFilter pepfarId;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private StringFilter labDirectorName;

    private StringFilter labDirectorEmail;

    private StringFilter contactPersonName;

    private StringFilter contactPersonEmail;

    private StringFilter contactPersonPhone;

    private StatusFilter status;

    private LongFilter shipmentMapsId;

    private LongFilter countryId;

    private LongFilter enrollmentsId;

    private LongFilter customValuesId;

    private LongFilter dataManagersId;

    private Boolean distinct;

    public ParticipantCriteria() {}

    public ParticipantCriteria(ParticipantCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.uniqueIdentifier = other.optionalUniqueIdentifier().map(StringFilter::copy).orElse(null);
        this.instituteName = other.optionalInstituteName().map(StringFilter::copy).orElse(null);
        this.departmentName = other.optionalDepartmentName().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.additionalEmail = other.optionalAdditionalEmail().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.shippingAddress = other.optionalShippingAddress().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.state = other.optionalState().map(StringFilter::copy).orElse(null);
        this.district = other.optionalDistrict().map(StringFilter::copy).orElse(null);
        this.zip = other.optionalZip().map(StringFilter::copy).orElse(null);
        this.region = other.optionalRegion().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.mobile = other.optionalMobile().map(StringFilter::copy).orElse(null);
        this.affiliation = other.optionalAffiliation().map(StringFilter::copy).orElse(null);
        this.networkTier = other.optionalNetworkTier().map(StringFilter::copy).orElse(null);
        this.siteType = other.optionalSiteType().map(StringFilter::copy).orElse(null);
        this.fundingSource = other.optionalFundingSource().map(StringFilter::copy).orElse(null);
        this.testingVolume = other.optionalTestingVolume().map(LongFilter::copy).orElse(null);
        this.pepfarId = other.optionalPepfarId().map(StringFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(DoubleFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(DoubleFilter::copy).orElse(null);
        this.labDirectorName = other.optionalLabDirectorName().map(StringFilter::copy).orElse(null);
        this.labDirectorEmail = other.optionalLabDirectorEmail().map(StringFilter::copy).orElse(null);
        this.contactPersonName = other.optionalContactPersonName().map(StringFilter::copy).orElse(null);
        this.contactPersonEmail = other.optionalContactPersonEmail().map(StringFilter::copy).orElse(null);
        this.contactPersonPhone = other.optionalContactPersonPhone().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StatusFilter::copy).orElse(null);
        this.shipmentMapsId = other.optionalShipmentMapsId().map(LongFilter::copy).orElse(null);
        this.countryId = other.optionalCountryId().map(LongFilter::copy).orElse(null);
        this.enrollmentsId = other.optionalEnrollmentsId().map(LongFilter::copy).orElse(null);
        this.customValuesId = other.optionalCustomValuesId().map(LongFilter::copy).orElse(null);
        this.dataManagersId = other.optionalDataManagersId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ParticipantCriteria copy() {
        return new ParticipantCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public Optional<StringFilter> optionalUniqueIdentifier() {
        return Optional.ofNullable(uniqueIdentifier);
    }

    public StringFilter uniqueIdentifier() {
        if (uniqueIdentifier == null) {
            setUniqueIdentifier(new StringFilter());
        }
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(StringFilter uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public StringFilter getInstituteName() {
        return instituteName;
    }

    public Optional<StringFilter> optionalInstituteName() {
        return Optional.ofNullable(instituteName);
    }

    public StringFilter instituteName() {
        if (instituteName == null) {
            setInstituteName(new StringFilter());
        }
        return instituteName;
    }

    public void setInstituteName(StringFilter instituteName) {
        this.instituteName = instituteName;
    }

    public StringFilter getDepartmentName() {
        return departmentName;
    }

    public Optional<StringFilter> optionalDepartmentName() {
        return Optional.ofNullable(departmentName);
    }

    public StringFilter departmentName() {
        if (departmentName == null) {
            setDepartmentName(new StringFilter());
        }
        return departmentName;
    }

    public void setDepartmentName(StringFilter departmentName) {
        this.departmentName = departmentName;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getAdditionalEmail() {
        return additionalEmail;
    }

    public Optional<StringFilter> optionalAdditionalEmail() {
        return Optional.ofNullable(additionalEmail);
    }

    public StringFilter additionalEmail() {
        if (additionalEmail == null) {
            setAdditionalEmail(new StringFilter());
        }
        return additionalEmail;
    }

    public void setAdditionalEmail(StringFilter additionalEmail) {
        this.additionalEmail = additionalEmail;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public StringFilter getShippingAddress() {
        return shippingAddress;
    }

    public Optional<StringFilter> optionalShippingAddress() {
        return Optional.ofNullable(shippingAddress);
    }

    public StringFilter shippingAddress() {
        if (shippingAddress == null) {
            setShippingAddress(new StringFilter());
        }
        return shippingAddress;
    }

    public void setShippingAddress(StringFilter shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public StringFilter getCity() {
        return city;
    }

    public Optional<StringFilter> optionalCity() {
        return Optional.ofNullable(city);
    }

    public StringFilter city() {
        if (city == null) {
            setCity(new StringFilter());
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getState() {
        return state;
    }

    public Optional<StringFilter> optionalState() {
        return Optional.ofNullable(state);
    }

    public StringFilter state() {
        if (state == null) {
            setState(new StringFilter());
        }
        return state;
    }

    public void setState(StringFilter state) {
        this.state = state;
    }

    public StringFilter getDistrict() {
        return district;
    }

    public Optional<StringFilter> optionalDistrict() {
        return Optional.ofNullable(district);
    }

    public StringFilter district() {
        if (district == null) {
            setDistrict(new StringFilter());
        }
        return district;
    }

    public void setDistrict(StringFilter district) {
        this.district = district;
    }

    public StringFilter getZip() {
        return zip;
    }

    public Optional<StringFilter> optionalZip() {
        return Optional.ofNullable(zip);
    }

    public StringFilter zip() {
        if (zip == null) {
            setZip(new StringFilter());
        }
        return zip;
    }

    public void setZip(StringFilter zip) {
        this.zip = zip;
    }

    public StringFilter getRegion() {
        return region;
    }

    public Optional<StringFilter> optionalRegion() {
        return Optional.ofNullable(region);
    }

    public StringFilter region() {
        if (region == null) {
            setRegion(new StringFilter());
        }
        return region;
    }

    public void setRegion(StringFilter region) {
        this.region = region;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getMobile() {
        return mobile;
    }

    public Optional<StringFilter> optionalMobile() {
        return Optional.ofNullable(mobile);
    }

    public StringFilter mobile() {
        if (mobile == null) {
            setMobile(new StringFilter());
        }
        return mobile;
    }

    public void setMobile(StringFilter mobile) {
        this.mobile = mobile;
    }

    public StringFilter getAffiliation() {
        return affiliation;
    }

    public Optional<StringFilter> optionalAffiliation() {
        return Optional.ofNullable(affiliation);
    }

    public StringFilter affiliation() {
        if (affiliation == null) {
            setAffiliation(new StringFilter());
        }
        return affiliation;
    }

    public void setAffiliation(StringFilter affiliation) {
        this.affiliation = affiliation;
    }

    public StringFilter getNetworkTier() {
        return networkTier;
    }

    public Optional<StringFilter> optionalNetworkTier() {
        return Optional.ofNullable(networkTier);
    }

    public StringFilter networkTier() {
        if (networkTier == null) {
            setNetworkTier(new StringFilter());
        }
        return networkTier;
    }

    public void setNetworkTier(StringFilter networkTier) {
        this.networkTier = networkTier;
    }

    public StringFilter getSiteType() {
        return siteType;
    }

    public Optional<StringFilter> optionalSiteType() {
        return Optional.ofNullable(siteType);
    }

    public StringFilter siteType() {
        if (siteType == null) {
            setSiteType(new StringFilter());
        }
        return siteType;
    }

    public void setSiteType(StringFilter siteType) {
        this.siteType = siteType;
    }

    public StringFilter getFundingSource() {
        return fundingSource;
    }

    public Optional<StringFilter> optionalFundingSource() {
        return Optional.ofNullable(fundingSource);
    }

    public StringFilter fundingSource() {
        if (fundingSource == null) {
            setFundingSource(new StringFilter());
        }
        return fundingSource;
    }

    public void setFundingSource(StringFilter fundingSource) {
        this.fundingSource = fundingSource;
    }

    public LongFilter getTestingVolume() {
        return testingVolume;
    }

    public Optional<LongFilter> optionalTestingVolume() {
        return Optional.ofNullable(testingVolume);
    }

    public LongFilter testingVolume() {
        if (testingVolume == null) {
            setTestingVolume(new LongFilter());
        }
        return testingVolume;
    }

    public void setTestingVolume(LongFilter testingVolume) {
        this.testingVolume = testingVolume;
    }

    public StringFilter getPepfarId() {
        return pepfarId;
    }

    public Optional<StringFilter> optionalPepfarId() {
        return Optional.ofNullable(pepfarId);
    }

    public StringFilter pepfarId() {
        if (pepfarId == null) {
            setPepfarId(new StringFilter());
        }
        return pepfarId;
    }

    public void setPepfarId(StringFilter pepfarId) {
        this.pepfarId = pepfarId;
    }

    public DoubleFilter getLatitude() {
        return latitude;
    }

    public Optional<DoubleFilter> optionalLatitude() {
        return Optional.ofNullable(latitude);
    }

    public DoubleFilter latitude() {
        if (latitude == null) {
            setLatitude(new DoubleFilter());
        }
        return latitude;
    }

    public void setLatitude(DoubleFilter latitude) {
        this.latitude = latitude;
    }

    public DoubleFilter getLongitude() {
        return longitude;
    }

    public Optional<DoubleFilter> optionalLongitude() {
        return Optional.ofNullable(longitude);
    }

    public DoubleFilter longitude() {
        if (longitude == null) {
            setLongitude(new DoubleFilter());
        }
        return longitude;
    }

    public void setLongitude(DoubleFilter longitude) {
        this.longitude = longitude;
    }

    public StringFilter getLabDirectorName() {
        return labDirectorName;
    }

    public Optional<StringFilter> optionalLabDirectorName() {
        return Optional.ofNullable(labDirectorName);
    }

    public StringFilter labDirectorName() {
        if (labDirectorName == null) {
            setLabDirectorName(new StringFilter());
        }
        return labDirectorName;
    }

    public void setLabDirectorName(StringFilter labDirectorName) {
        this.labDirectorName = labDirectorName;
    }

    public StringFilter getLabDirectorEmail() {
        return labDirectorEmail;
    }

    public Optional<StringFilter> optionalLabDirectorEmail() {
        return Optional.ofNullable(labDirectorEmail);
    }

    public StringFilter labDirectorEmail() {
        if (labDirectorEmail == null) {
            setLabDirectorEmail(new StringFilter());
        }
        return labDirectorEmail;
    }

    public void setLabDirectorEmail(StringFilter labDirectorEmail) {
        this.labDirectorEmail = labDirectorEmail;
    }

    public StringFilter getContactPersonName() {
        return contactPersonName;
    }

    public Optional<StringFilter> optionalContactPersonName() {
        return Optional.ofNullable(contactPersonName);
    }

    public StringFilter contactPersonName() {
        if (contactPersonName == null) {
            setContactPersonName(new StringFilter());
        }
        return contactPersonName;
    }

    public void setContactPersonName(StringFilter contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public StringFilter getContactPersonEmail() {
        return contactPersonEmail;
    }

    public Optional<StringFilter> optionalContactPersonEmail() {
        return Optional.ofNullable(contactPersonEmail);
    }

    public StringFilter contactPersonEmail() {
        if (contactPersonEmail == null) {
            setContactPersonEmail(new StringFilter());
        }
        return contactPersonEmail;
    }

    public void setContactPersonEmail(StringFilter contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public StringFilter getContactPersonPhone() {
        return contactPersonPhone;
    }

    public Optional<StringFilter> optionalContactPersonPhone() {
        return Optional.ofNullable(contactPersonPhone);
    }

    public StringFilter contactPersonPhone() {
        if (contactPersonPhone == null) {
            setContactPersonPhone(new StringFilter());
        }
        return contactPersonPhone;
    }

    public void setContactPersonPhone(StringFilter contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public StatusFilter getStatus() {
        return status;
    }

    public Optional<StatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StatusFilter status() {
        if (status == null) {
            setStatus(new StatusFilter());
        }
        return status;
    }

    public void setStatus(StatusFilter status) {
        this.status = status;
    }

    public LongFilter getShipmentMapsId() {
        return shipmentMapsId;
    }

    public Optional<LongFilter> optionalShipmentMapsId() {
        return Optional.ofNullable(shipmentMapsId);
    }

    public LongFilter shipmentMapsId() {
        if (shipmentMapsId == null) {
            setShipmentMapsId(new LongFilter());
        }
        return shipmentMapsId;
    }

    public void setShipmentMapsId(LongFilter shipmentMapsId) {
        this.shipmentMapsId = shipmentMapsId;
    }

    public LongFilter getCountryId() {
        return countryId;
    }

    public Optional<LongFilter> optionalCountryId() {
        return Optional.ofNullable(countryId);
    }

    public LongFilter countryId() {
        if (countryId == null) {
            setCountryId(new LongFilter());
        }
        return countryId;
    }

    public void setCountryId(LongFilter countryId) {
        this.countryId = countryId;
    }

    public LongFilter getEnrollmentsId() {
        return enrollmentsId;
    }

    public Optional<LongFilter> optionalEnrollmentsId() {
        return Optional.ofNullable(enrollmentsId);
    }

    public LongFilter enrollmentsId() {
        if (enrollmentsId == null) {
            setEnrollmentsId(new LongFilter());
        }
        return enrollmentsId;
    }

    public void setEnrollmentsId(LongFilter enrollmentsId) {
        this.enrollmentsId = enrollmentsId;
    }

    public LongFilter getCustomValuesId() {
        return customValuesId;
    }

    public Optional<LongFilter> optionalCustomValuesId() {
        return Optional.ofNullable(customValuesId);
    }

    public LongFilter customValuesId() {
        if (customValuesId == null) {
            setCustomValuesId(new LongFilter());
        }
        return customValuesId;
    }

    public void setCustomValuesId(LongFilter customValuesId) {
        this.customValuesId = customValuesId;
    }

    public LongFilter getDataManagersId() {
        return dataManagersId;
    }

    public Optional<LongFilter> optionalDataManagersId() {
        return Optional.ofNullable(dataManagersId);
    }

    public LongFilter dataManagersId() {
        if (dataManagersId == null) {
            setDataManagersId(new LongFilter());
        }
        return dataManagersId;
    }

    public void setDataManagersId(LongFilter dataManagersId) {
        this.dataManagersId = dataManagersId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ParticipantCriteria that = (ParticipantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(uniqueIdentifier, that.uniqueIdentifier) &&
            Objects.equals(instituteName, that.instituteName) &&
            Objects.equals(departmentName, that.departmentName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(additionalEmail, that.additionalEmail) &&
            Objects.equals(address, that.address) &&
            Objects.equals(shippingAddress, that.shippingAddress) &&
            Objects.equals(city, that.city) &&
            Objects.equals(state, that.state) &&
            Objects.equals(district, that.district) &&
            Objects.equals(zip, that.zip) &&
            Objects.equals(region, that.region) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(mobile, that.mobile) &&
            Objects.equals(affiliation, that.affiliation) &&
            Objects.equals(networkTier, that.networkTier) &&
            Objects.equals(siteType, that.siteType) &&
            Objects.equals(fundingSource, that.fundingSource) &&
            Objects.equals(testingVolume, that.testingVolume) &&
            Objects.equals(pepfarId, that.pepfarId) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(labDirectorName, that.labDirectorName) &&
            Objects.equals(labDirectorEmail, that.labDirectorEmail) &&
            Objects.equals(contactPersonName, that.contactPersonName) &&
            Objects.equals(contactPersonEmail, that.contactPersonEmail) &&
            Objects.equals(contactPersonPhone, that.contactPersonPhone) &&
            Objects.equals(status, that.status) &&
            Objects.equals(shipmentMapsId, that.shipmentMapsId) &&
            Objects.equals(countryId, that.countryId) &&
            Objects.equals(enrollmentsId, that.enrollmentsId) &&
            Objects.equals(customValuesId, that.customValuesId) &&
            Objects.equals(dataManagersId, that.dataManagersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            uniqueIdentifier,
            instituteName,
            departmentName,
            email,
            additionalEmail,
            address,
            shippingAddress,
            city,
            state,
            district,
            zip,
            region,
            phone,
            mobile,
            affiliation,
            networkTier,
            siteType,
            fundingSource,
            testingVolume,
            pepfarId,
            latitude,
            longitude,
            labDirectorName,
            labDirectorEmail,
            contactPersonName,
            contactPersonEmail,
            contactPersonPhone,
            status,
            shipmentMapsId,
            countryId,
            enrollmentsId,
            customValuesId,
            dataManagersId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUniqueIdentifier().map(f -> "uniqueIdentifier=" + f + ", ").orElse("") +
            optionalInstituteName().map(f -> "instituteName=" + f + ", ").orElse("") +
            optionalDepartmentName().map(f -> "departmentName=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalAdditionalEmail().map(f -> "additionalEmail=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalShippingAddress().map(f -> "shippingAddress=" + f + ", ").orElse("") +
            optionalCity().map(f -> "city=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalDistrict().map(f -> "district=" + f + ", ").orElse("") +
            optionalZip().map(f -> "zip=" + f + ", ").orElse("") +
            optionalRegion().map(f -> "region=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalMobile().map(f -> "mobile=" + f + ", ").orElse("") +
            optionalAffiliation().map(f -> "affiliation=" + f + ", ").orElse("") +
            optionalNetworkTier().map(f -> "networkTier=" + f + ", ").orElse("") +
            optionalSiteType().map(f -> "siteType=" + f + ", ").orElse("") +
            optionalFundingSource().map(f -> "fundingSource=" + f + ", ").orElse("") +
            optionalTestingVolume().map(f -> "testingVolume=" + f + ", ").orElse("") +
            optionalPepfarId().map(f -> "pepfarId=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalLabDirectorName().map(f -> "labDirectorName=" + f + ", ").orElse("") +
            optionalLabDirectorEmail().map(f -> "labDirectorEmail=" + f + ", ").orElse("") +
            optionalContactPersonName().map(f -> "contactPersonName=" + f + ", ").orElse("") +
            optionalContactPersonEmail().map(f -> "contactPersonEmail=" + f + ", ").orElse("") +
            optionalContactPersonPhone().map(f -> "contactPersonPhone=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalShipmentMapsId().map(f -> "shipmentMapsId=" + f + ", ").orElse("") +
            optionalCountryId().map(f -> "countryId=" + f + ", ").orElse("") +
            optionalEnrollmentsId().map(f -> "enrollmentsId=" + f + ", ").orElse("") +
            optionalCustomValuesId().map(f -> "customValuesId=" + f + ", ").orElse("") +
            optionalDataManagersId().map(f -> "dataManagersId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
