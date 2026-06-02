package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.AuditAction;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.AuditLog} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.AuditLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /audit-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLogCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AuditAction
     */
    public static class AuditActionFilter extends Filter<AuditAction> {

        public AuditActionFilter() {}

        public AuditActionFilter(AuditActionFilter filter) {
            super(filter);
        }

        @Override
        public AuditActionFilter copy() {
            return new AuditActionFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private AuditActionFilter action;

    private StringFilter statement;

    private StringFilter performedBy;

    private StringFilter performedByRole;

    private InstantFilter performedOn;

    private StringFilter ipAddress;

    private StringFilter userAgent;

    private StringFilter sessionHash;

    private Boolean distinct;

    public AuditLogCriteria() {}

    public AuditLogCriteria(AuditLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.action = other.optionalAction().map(AuditActionFilter::copy).orElse(null);
        this.statement = other.optionalStatement().map(StringFilter::copy).orElse(null);
        this.performedBy = other.optionalPerformedBy().map(StringFilter::copy).orElse(null);
        this.performedByRole = other.optionalPerformedByRole().map(StringFilter::copy).orElse(null);
        this.performedOn = other.optionalPerformedOn().map(InstantFilter::copy).orElse(null);
        this.ipAddress = other.optionalIpAddress().map(StringFilter::copy).orElse(null);
        this.userAgent = other.optionalUserAgent().map(StringFilter::copy).orElse(null);
        this.sessionHash = other.optionalSessionHash().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AuditLogCriteria copy() {
        return new AuditLogCriteria(this);
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

    public AuditActionFilter getAction() {
        return action;
    }

    public Optional<AuditActionFilter> optionalAction() {
        return Optional.ofNullable(action);
    }

    public AuditActionFilter action() {
        if (action == null) {
            setAction(new AuditActionFilter());
        }
        return action;
    }

    public void setAction(AuditActionFilter action) {
        this.action = action;
    }

    public StringFilter getStatement() {
        return statement;
    }

    public Optional<StringFilter> optionalStatement() {
        return Optional.ofNullable(statement);
    }

    public StringFilter statement() {
        if (statement == null) {
            setStatement(new StringFilter());
        }
        return statement;
    }

    public void setStatement(StringFilter statement) {
        this.statement = statement;
    }

    public StringFilter getPerformedBy() {
        return performedBy;
    }

    public Optional<StringFilter> optionalPerformedBy() {
        return Optional.ofNullable(performedBy);
    }

    public StringFilter performedBy() {
        if (performedBy == null) {
            setPerformedBy(new StringFilter());
        }
        return performedBy;
    }

    public void setPerformedBy(StringFilter performedBy) {
        this.performedBy = performedBy;
    }

    public StringFilter getPerformedByRole() {
        return performedByRole;
    }

    public Optional<StringFilter> optionalPerformedByRole() {
        return Optional.ofNullable(performedByRole);
    }

    public StringFilter performedByRole() {
        if (performedByRole == null) {
            setPerformedByRole(new StringFilter());
        }
        return performedByRole;
    }

    public void setPerformedByRole(StringFilter performedByRole) {
        this.performedByRole = performedByRole;
    }

    public InstantFilter getPerformedOn() {
        return performedOn;
    }

    public Optional<InstantFilter> optionalPerformedOn() {
        return Optional.ofNullable(performedOn);
    }

    public InstantFilter performedOn() {
        if (performedOn == null) {
            setPerformedOn(new InstantFilter());
        }
        return performedOn;
    }

    public void setPerformedOn(InstantFilter performedOn) {
        this.performedOn = performedOn;
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

    public StringFilter getUserAgent() {
        return userAgent;
    }

    public Optional<StringFilter> optionalUserAgent() {
        return Optional.ofNullable(userAgent);
    }

    public StringFilter userAgent() {
        if (userAgent == null) {
            setUserAgent(new StringFilter());
        }
        return userAgent;
    }

    public void setUserAgent(StringFilter userAgent) {
        this.userAgent = userAgent;
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
        final AuditLogCriteria that = (AuditLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(action, that.action) &&
            Objects.equals(statement, that.statement) &&
            Objects.equals(performedBy, that.performedBy) &&
            Objects.equals(performedByRole, that.performedByRole) &&
            Objects.equals(performedOn, that.performedOn) &&
            Objects.equals(ipAddress, that.ipAddress) &&
            Objects.equals(userAgent, that.userAgent) &&
            Objects.equals(sessionHash, that.sessionHash) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, action, statement, performedBy, performedByRole, performedOn, ipAddress, userAgent, sessionHash, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAction().map(f -> "action=" + f + ", ").orElse("") +
            optionalStatement().map(f -> "statement=" + f + ", ").orElse("") +
            optionalPerformedBy().map(f -> "performedBy=" + f + ", ").orElse("") +
            optionalPerformedByRole().map(f -> "performedByRole=" + f + ", ").orElse("") +
            optionalPerformedOn().map(f -> "performedOn=" + f + ", ").orElse("") +
            optionalIpAddress().map(f -> "ipAddress=" + f + ", ").orElse("") +
            optionalUserAgent().map(f -> "userAgent=" + f + ", ").orElse("") +
            optionalSessionHash().map(f -> "sessionHash=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
