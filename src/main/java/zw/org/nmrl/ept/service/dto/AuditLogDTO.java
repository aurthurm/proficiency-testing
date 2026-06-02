package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.AuditAction;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.AuditLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLogDTO implements Serializable {

    private Long id;

    @NotNull
    private AuditAction action;

    @NotNull
    private String statement;

    private String performedBy;

    private String performedByRole;

    @NotNull
    private Instant performedOn;

    private String ipAddress;

    private String userAgent;

    private String sessionHash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getPerformedByRole() {
        return performedByRole;
    }

    public void setPerformedByRole(String performedByRole) {
        this.performedByRole = performedByRole;
    }

    public Instant getPerformedOn() {
        return performedOn;
    }

    public void setPerformedOn(Instant performedOn) {
        this.performedOn = performedOn;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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
        if (!(o instanceof AuditLogDTO)) {
            return false;
        }

        AuditLogDTO auditLogDTO = (AuditLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLogDTO{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", statement='" + getStatement() + "'" +
            ", performedBy='" + getPerformedBy() + "'" +
            ", performedByRole='" + getPerformedByRole() + "'" +
            ", performedOn='" + getPerformedOn() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", userAgent='" + getUserAgent() + "'" +
            ", sessionHash='" + getSessionHash() + "'" +
            "}";
    }
}
