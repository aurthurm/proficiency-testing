package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import zw.org.nmrl.ept.domain.enumeration.DataManagerRole;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.DataManager} entity.
 */
@Schema(description = "DM / PTCC participant-side login. Wraps the built-in JHipster User.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataManagerDTO implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private String institute;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String primaryEmail;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String secondaryEmail;

    private String phone;

    private String mobile;

    private String language;

    @NotNull
    private DataManagerRole role;

    @NotNull
    private Status status;

    private Boolean qcAccess;

    private Boolean viewOnlyAccess;

    private Boolean enableTestResponseDate;

    private Boolean enableModeOfReceipt;

    private Boolean forceProfileCheck;

    private UserDTO user;

    private CountryDTO country;

    private Set<ParticipantDTO> participantses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public DataManagerRole getRole() {
        return role;
    }

    public void setRole(DataManagerRole role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getQcAccess() {
        return qcAccess;
    }

    public void setQcAccess(Boolean qcAccess) {
        this.qcAccess = qcAccess;
    }

    public Boolean getViewOnlyAccess() {
        return viewOnlyAccess;
    }

    public void setViewOnlyAccess(Boolean viewOnlyAccess) {
        this.viewOnlyAccess = viewOnlyAccess;
    }

    public Boolean getEnableTestResponseDate() {
        return enableTestResponseDate;
    }

    public void setEnableTestResponseDate(Boolean enableTestResponseDate) {
        this.enableTestResponseDate = enableTestResponseDate;
    }

    public Boolean getEnableModeOfReceipt() {
        return enableModeOfReceipt;
    }

    public void setEnableModeOfReceipt(Boolean enableModeOfReceipt) {
        this.enableModeOfReceipt = enableModeOfReceipt;
    }

    public Boolean getForceProfileCheck() {
        return forceProfileCheck;
    }

    public void setForceProfileCheck(Boolean forceProfileCheck) {
        this.forceProfileCheck = forceProfileCheck;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public Set<ParticipantDTO> getParticipantses() {
        return participantses;
    }

    public void setParticipantses(Set<ParticipantDTO> participantses) {
        this.participantses = participantses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataManagerDTO)) {
            return false;
        }

        DataManagerDTO dataManagerDTO = (DataManagerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dataManagerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataManagerDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", institute='" + getInstitute() + "'" +
            ", primaryEmail='" + getPrimaryEmail() + "'" +
            ", secondaryEmail='" + getSecondaryEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", mobile='" + getMobile() + "'" +
            ", language='" + getLanguage() + "'" +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            ", qcAccess='" + getQcAccess() + "'" +
            ", viewOnlyAccess='" + getViewOnlyAccess() + "'" +
            ", enableTestResponseDate='" + getEnableTestResponseDate() + "'" +
            ", enableModeOfReceipt='" + getEnableModeOfReceipt() + "'" +
            ", forceProfileCheck='" + getForceProfileCheck() + "'" +
            ", user=" + getUser() +
            ", country=" + getCountry() +
            ", participantses=" + getParticipantses() +
            "}";
    }
}
