package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.LoginStatus;

/**
 * A UserLoginHistory.
 */
@Entity
@Table(name = "user_login_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserLoginHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "login_context")
    private String loginContext;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "login_status", nullable = false)
    private LoginStatus loginStatus;

    @NotNull
    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "browser")
    private String browser;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "session_hash")
    private String sessionHash;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserLoginHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return this.loginId;
    }

    public UserLoginHistory loginId(String loginId) {
        this.setLoginId(loginId);
        return this;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginContext() {
        return this.loginContext;
    }

    public UserLoginHistory loginContext(String loginContext) {
        this.setLoginContext(loginContext);
        return this;
    }

    public void setLoginContext(String loginContext) {
        this.loginContext = loginContext;
    }

    public LoginStatus getLoginStatus() {
        return this.loginStatus;
    }

    public UserLoginHistory loginStatus(LoginStatus loginStatus) {
        this.setLoginStatus(loginStatus);
        return this;
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Instant getAttemptedAt() {
        return this.attemptedAt;
    }

    public UserLoginHistory attemptedAt(Instant attemptedAt) {
        this.setAttemptedAt(attemptedAt);
        return this;
    }

    public void setAttemptedAt(Instant attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public UserLoginHistory ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBrowser() {
        return this.browser;
    }

    public UserLoginHistory browser(String browser) {
        this.setBrowser(browser);
        return this;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOperatingSystem() {
        return this.operatingSystem;
    }

    public UserLoginHistory operatingSystem(String operatingSystem) {
        this.setOperatingSystem(operatingSystem);
        return this;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getSessionHash() {
        return this.sessionHash;
    }

    public UserLoginHistory sessionHash(String sessionHash) {
        this.setSessionHash(sessionHash);
        return this;
    }

    public void setSessionHash(String sessionHash) {
        this.sessionHash = sessionHash;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserLoginHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((UserLoginHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserLoginHistory{" +
            "id=" + getId() +
            ", loginId='" + getLoginId() + "'" +
            ", loginContext='" + getLoginContext() + "'" +
            ", loginStatus='" + getLoginStatus() + "'" +
            ", attemptedAt='" + getAttemptedAt() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", browser='" + getBrowser() + "'" +
            ", operatingSystem='" + getOperatingSystem() + "'" +
            ", sessionHash='" + getSessionHash() + "'" +
            "}";
    }
}
