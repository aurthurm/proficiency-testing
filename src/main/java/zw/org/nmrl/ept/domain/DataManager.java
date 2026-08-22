package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.DataManagerRole;
import zw.org.nmrl.ept.domain.enumeration.Status;

/**
 * DM / PTCC participant-side login. Wraps the built-in JHipster User.
 */
@Entity
@Table(name = "data_manager")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataManager extends LegacyCompatibleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "institute", length = 500)
    private String institute;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "primary_email", nullable = false)
    private String primaryEmail;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "secondary_email")
    private String secondaryEmail;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "language")
    private String language;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private DataManagerRole role;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "qc_access")
    private Boolean qcAccess;

    @Column(name = "view_only_access")
    private Boolean viewOnlyAccess;

    @Column(name = "enable_test_response_date")
    private Boolean enableTestResponseDate;

    @Column(name = "enable_mode_of_receipt")
    private Boolean enableModeOfReceipt;

    @Column(name = "force_profile_check")
    private Boolean forceProfileCheck;

    @Lob
    @Column(name = "legacy_password_hash")
    private String legacyPasswordHash;

    @Column(name = "force_password_reset")
    private Boolean forcePasswordReset;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "login_ban")
    private Boolean loginBan;

    @Column(name = "legacy_auth_token")
    private String legacyAuthToken;

    @Column(name = "api_token_generated_at")
    private Instant apiTokenGeneratedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_data_manager__participants",
        joinColumns = @JoinColumn(name = "data_manager_id"),
        inverseJoinColumns = @JoinColumn(name = "participants_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "shipmentMapses", "country", "enrollmentses", "customValueses", "dataManagerses" }, allowSetters = true)
    private Set<Participant> participantses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DataManager id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public DataManager firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public DataManager lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInstitute() {
        return this.institute;
    }

    public DataManager institute(String institute) {
        this.setInstitute(institute);
        return this;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getPrimaryEmail() {
        return this.primaryEmail;
    }

    public DataManager primaryEmail(String primaryEmail) {
        this.setPrimaryEmail(primaryEmail);
        return this;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getSecondaryEmail() {
        return this.secondaryEmail;
    }

    public DataManager secondaryEmail(String secondaryEmail) {
        this.setSecondaryEmail(secondaryEmail);
        return this;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    public String getPhone() {
        return this.phone;
    }

    public DataManager phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return this.mobile;
    }

    public DataManager mobile(String mobile) {
        this.setMobile(mobile);
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLanguage() {
        return this.language;
    }

    public DataManager language(String language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public DataManagerRole getRole() {
        return this.role;
    }

    public DataManager role(DataManagerRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(DataManagerRole role) {
        this.role = role;
    }

    public Status getStatus() {
        return this.status;
    }

    public DataManager status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getQcAccess() {
        return this.qcAccess;
    }

    public DataManager qcAccess(Boolean qcAccess) {
        this.setQcAccess(qcAccess);
        return this;
    }

    public void setQcAccess(Boolean qcAccess) {
        this.qcAccess = qcAccess;
    }

    public Boolean getViewOnlyAccess() {
        return this.viewOnlyAccess;
    }

    public DataManager viewOnlyAccess(Boolean viewOnlyAccess) {
        this.setViewOnlyAccess(viewOnlyAccess);
        return this;
    }

    public void setViewOnlyAccess(Boolean viewOnlyAccess) {
        this.viewOnlyAccess = viewOnlyAccess;
    }

    public Boolean getEnableTestResponseDate() {
        return this.enableTestResponseDate;
    }

    public DataManager enableTestResponseDate(Boolean enableTestResponseDate) {
        this.setEnableTestResponseDate(enableTestResponseDate);
        return this;
    }

    public void setEnableTestResponseDate(Boolean enableTestResponseDate) {
        this.enableTestResponseDate = enableTestResponseDate;
    }

    public Boolean getEnableModeOfReceipt() {
        return this.enableModeOfReceipt;
    }

    public DataManager enableModeOfReceipt(Boolean enableModeOfReceipt) {
        this.setEnableModeOfReceipt(enableModeOfReceipt);
        return this;
    }

    public void setEnableModeOfReceipt(Boolean enableModeOfReceipt) {
        this.enableModeOfReceipt = enableModeOfReceipt;
    }

    public Boolean getForceProfileCheck() {
        return this.forceProfileCheck;
    }

    public DataManager forceProfileCheck(Boolean forceProfileCheck) {
        this.setForceProfileCheck(forceProfileCheck);
        return this;
    }

    public void setForceProfileCheck(Boolean forceProfileCheck) {
        this.forceProfileCheck = forceProfileCheck;
    }

    public String getLegacyPasswordHash() {
        return legacyPasswordHash;
    }

    public void setLegacyPasswordHash(String legacyPasswordHash) {
        this.legacyPasswordHash = legacyPasswordHash;
    }

    public Boolean getForcePasswordReset() {
        return forcePasswordReset;
    }

    public void setForcePasswordReset(Boolean forcePasswordReset) {
        this.forcePasswordReset = forcePasswordReset;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getLoginBan() {
        return loginBan;
    }

    public void setLoginBan(Boolean loginBan) {
        this.loginBan = loginBan;
    }

    public String getLegacyAuthToken() {
        return legacyAuthToken;
    }

    public void setLegacyAuthToken(String legacyAuthToken) {
        this.legacyAuthToken = legacyAuthToken;
    }

    public Instant getApiTokenGeneratedAt() {
        return apiTokenGeneratedAt;
    }

    public void setApiTokenGeneratedAt(Instant apiTokenGeneratedAt) {
        this.apiTokenGeneratedAt = apiTokenGeneratedAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DataManager user(User user) {
        this.setUser(user);
        return this;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public DataManager country(Country country) {
        this.setCountry(country);
        return this;
    }

    public Set<Participant> getParticipantses() {
        return this.participantses;
    }

    public void setParticipantses(Set<Participant> participants) {
        this.participantses = participants;
    }

    public DataManager participantses(Set<Participant> participants) {
        this.setParticipantses(participants);
        return this;
    }

    public DataManager addParticipants(Participant participant) {
        this.participantses.add(participant);
        return this;
    }

    public DataManager removeParticipants(Participant participant) {
        this.participantses.remove(participant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataManager)) {
            return false;
        }
        return getId() != null && getId().equals(((DataManager) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataManager{" +
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
            "}";
    }
}
