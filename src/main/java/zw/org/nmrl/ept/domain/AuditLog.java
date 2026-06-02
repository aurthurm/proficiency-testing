package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.AuditAction;

/**
 * A AuditLog.
 */
@Entity
@Table(name = "audit_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuditAction action;

    @NotNull
    @Column(name = "statement", nullable = false)
    private String statement;

    @Column(name = "performed_by")
    private String performedBy;

    @Column(name = "performed_by_role")
    private String performedByRole;

    @NotNull
    @Column(name = "performed_on", nullable = false)
    private Instant performedOn;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "session_hash")
    private String sessionHash;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditAction getAction() {
        return this.action;
    }

    public AuditLog action(AuditAction action) {
        this.setAction(action);
        return this;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getStatement() {
        return this.statement;
    }

    public AuditLog statement(String statement) {
        this.setStatement(statement);
        return this;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getPerformedBy() {
        return this.performedBy;
    }

    public AuditLog performedBy(String performedBy) {
        this.setPerformedBy(performedBy);
        return this;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getPerformedByRole() {
        return this.performedByRole;
    }

    public AuditLog performedByRole(String performedByRole) {
        this.setPerformedByRole(performedByRole);
        return this;
    }

    public void setPerformedByRole(String performedByRole) {
        this.performedByRole = performedByRole;
    }

    public Instant getPerformedOn() {
        return this.performedOn;
    }

    public AuditLog performedOn(Instant performedOn) {
        this.setPerformedOn(performedOn);
        return this;
    }

    public void setPerformedOn(Instant performedOn) {
        this.performedOn = performedOn;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public AuditLog ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public AuditLog userAgent(String userAgent) {
        this.setUserAgent(userAgent);
        return this;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSessionHash() {
        return this.sessionHash;
    }

    public AuditLog sessionHash(String sessionHash) {
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
        if (!(o instanceof AuditLog)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLog{" +
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
