package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.LoginStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.UserLoginHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserLoginHistoryDTO implements Serializable {

    private Long id;

    private String loginId;

    private String loginContext;

    @NotNull
    private LoginStatus loginStatus;

    @NotNull
    private Instant attemptedAt;

    private String ipAddress;

    private String browser;

    private String operatingSystem;

    private String sessionHash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginContext() {
        return loginContext;
    }

    public void setLoginContext(String loginContext) {
        this.loginContext = loginContext;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Instant getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(Instant attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getSessionHash() {
        return sessionHash;
    }

    public void setSessionHash(String sessionHash) {
        this.sessionHash = sessionHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserLoginHistoryDTO)) {
            return false;
        }

        UserLoginHistoryDTO userLoginHistoryDTO = (UserLoginHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userLoginHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserLoginHistoryDTO{" +
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
