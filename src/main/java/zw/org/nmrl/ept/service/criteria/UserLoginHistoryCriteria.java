package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.LoginStatus;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.UserLoginHistory} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.UserLoginHistoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-login-histories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserLoginHistoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LoginStatus
     */
    public static class LoginStatusFilter extends Filter<LoginStatus> {

        public LoginStatusFilter() {}

        public LoginStatusFilter(LoginStatusFilter filter) {
            super(filter);
        }

        @Override
        public LoginStatusFilter copy() {
            return new LoginStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter loginId;

    private StringFilter loginContext;

    private LoginStatusFilter loginStatus;

    private InstantFilter attemptedAt;

    private StringFilter ipAddress;

    private StringFilter browser;

    private StringFilter operatingSystem;

    private StringFilter sessionHash;

    private Boolean distinct;

    public UserLoginHistoryCriteria() {}

    public UserLoginHistoryCriteria(UserLoginHistoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.loginId = other.optionalLoginId().map(StringFilter::copy).orElse(null);
        this.loginContext = other.optionalLoginContext().map(StringFilter::copy).orElse(null);
        this.loginStatus = other.optionalLoginStatus().map(LoginStatusFilter::copy).orElse(null);
        this.attemptedAt = other.optionalAttemptedAt().map(InstantFilter::copy).orElse(null);
        this.ipAddress = other.optionalIpAddress().map(StringFilter::copy).orElse(null);
        this.browser = other.optionalBrowser().map(StringFilter::copy).orElse(null);
        this.operatingSystem = other.optionalOperatingSystem().map(StringFilter::copy).orElse(null);
        this.sessionHash = other.optionalSessionHash().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public UserLoginHistoryCriteria copy() {
        return new UserLoginHistoryCriteria(this);
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

    public StringFilter getLoginId() {
        return loginId;
    }

    public Optional<StringFilter> optionalLoginId() {
        return Optional.ofNullable(loginId);
    }

    public StringFilter loginId() {
        if (loginId == null) {
            setLoginId(new StringFilter());
        }
        return loginId;
    }

    public void setLoginId(StringFilter loginId) {
        this.loginId = loginId;
    }

    public StringFilter getLoginContext() {
        return loginContext;
    }

    public Optional<StringFilter> optionalLoginContext() {
        return Optional.ofNullable(loginContext);
    }

    public StringFilter loginContext() {
        if (loginContext == null) {
            setLoginContext(new StringFilter());
        }
        return loginContext;
    }

    public void setLoginContext(StringFilter loginContext) {
        this.loginContext = loginContext;
    }

    public LoginStatusFilter getLoginStatus() {
        return loginStatus;
    }

    public Optional<LoginStatusFilter> optionalLoginStatus() {
        return Optional.ofNullable(loginStatus);
    }

    public LoginStatusFilter loginStatus() {
        if (loginStatus == null) {
            setLoginStatus(new LoginStatusFilter());
        }
        return loginStatus;
    }

    public void setLoginStatus(LoginStatusFilter loginStatus) {
        this.loginStatus = loginStatus;
    }

    public InstantFilter getAttemptedAt() {
        return attemptedAt;
    }

    public Optional<InstantFilter> optionalAttemptedAt() {
        return Optional.ofNullable(attemptedAt);
    }

    public InstantFilter attemptedAt() {
        if (attemptedAt == null) {
            setAttemptedAt(new InstantFilter());
        }
        return attemptedAt;
    }

    public void setAttemptedAt(InstantFilter attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public StringFilter getIpAddress() {
        return ipAddress;
    }

    public Optional<StringFilter> optionalIpAddress() {
        return Optional.ofNullable(ipAddress);
    }

    public StringFilter ipAddress() {
        if (ipAddress == null) {
            setIpAddress(new StringFilter());
        }
        return ipAddress;
    }

    public void setIpAddress(StringFilter ipAddress) {
        this.ipAddress = ipAddress;
    }

    public StringFilter getBrowser() {
        return browser;
    }

    public Optional<StringFilter> optionalBrowser() {
        return Optional.ofNullable(browser);
    }

    public StringFilter browser() {
        if (browser == null) {
            setBrowser(new StringFilter());
        }
        return browser;
    }

    public void setBrowser(StringFilter browser) {
        this.browser = browser;
    }

    public StringFilter getOperatingSystem() {
        return operatingSystem;
    }

    public Optional<StringFilter> optionalOperatingSystem() {
        return Optional.ofNullable(operatingSystem);
    }

    public StringFilter operatingSystem() {
        if (operatingSystem == null) {
            setOperatingSystem(new StringFilter());
        }
        return operatingSystem;
    }

    public void setOperatingSystem(StringFilter operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public StringFilter getSessionHash() {
        return sessionHash;
    }

    public Optional<StringFilter> optionalSessionHash() {
        return Optional.ofNullable(sessionHash);
    }

    public StringFilter sessionHash() {
        if (sessionHash == null) {
            setSessionHash(new StringFilter());
        }
        return sessionHash;
    }

    public void setSessionHash(StringFilter sessionHash) {
        this.sessionHash = sessionHash;
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
        final UserLoginHistoryCriteria that = (UserLoginHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(loginId, that.loginId) &&
            Objects.equals(loginContext, that.loginContext) &&
            Objects.equals(loginStatus, that.loginStatus) &&
            Objects.equals(attemptedAt, that.attemptedAt) &&
            Objects.equals(ipAddress, that.ipAddress) &&
            Objects.equals(browser, that.browser) &&
            Objects.equals(operatingSystem, that.operatingSystem) &&
            Objects.equals(sessionHash, that.sessionHash) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            loginId,
            loginContext,
            loginStatus,
            attemptedAt,
            ipAddress,
            browser,
            operatingSystem,
            sessionHash,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserLoginHistoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLoginId().map(f -> "loginId=" + f + ", ").orElse("") +
            optionalLoginContext().map(f -> "loginContext=" + f + ", ").orElse("") +
            optionalLoginStatus().map(f -> "loginStatus=" + f + ", ").orElse("") +
            optionalAttemptedAt().map(f -> "attemptedAt=" + f + ", ").orElse("") +
            optionalIpAddress().map(f -> "ipAddress=" + f + ", ").orElse("") +
            optionalBrowser().map(f -> "browser=" + f + ", ").orElse("") +
            optionalOperatingSystem().map(f -> "operatingSystem=" + f + ", ").orElse("") +
            optionalSessionHash().map(f -> "sessionHash=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
