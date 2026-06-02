package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Mobile-app API request log (API-06).
 */
@Entity
@Table(name = "api_request_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiRequestLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "requested_on")
    private Instant requestedOn;

    @Column(name = "number_of_records")
    private Integer numberOfRecords;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "test_type")
    private String testType;

    @Column(name = "api_url")
    private String apiUrl;

    @Column(name = "data_format")
    private String dataFormat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ApiRequestLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public ApiRequestLog transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRequestedBy() {
        return this.requestedBy;
    }

    public ApiRequestLog requestedBy(String requestedBy) {
        this.setRequestedBy(requestedBy);
        return this;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedOn() {
        return this.requestedOn;
    }

    public ApiRequestLog requestedOn(Instant requestedOn) {
        this.setRequestedOn(requestedOn);
        return this;
    }

    public void setRequestedOn(Instant requestedOn) {
        this.requestedOn = requestedOn;
    }

    public Integer getNumberOfRecords() {
        return this.numberOfRecords;
    }

    public ApiRequestLog numberOfRecords(Integer numberOfRecords) {
        this.setNumberOfRecords(numberOfRecords);
        return this;
    }

    public void setNumberOfRecords(Integer numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public ApiRequestLog requestType(String requestType) {
        this.setRequestType(requestType);
        return this;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getTestType() {
        return this.testType;
    }

    public ApiRequestLog testType(String testType) {
        this.setTestType(testType);
        return this;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public ApiRequestLog apiUrl(String apiUrl) {
        this.setApiUrl(apiUrl);
        return this;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getDataFormat() {
        return this.dataFormat;
    }

    public ApiRequestLog dataFormat(String dataFormat) {
        this.setDataFormat(dataFormat);
        return this;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiRequestLog)) {
            return false;
        }
        return getId() != null && getId().equals(((ApiRequestLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiRequestLog{" +
            "id=" + getId() +
            ", transactionId='" + getTransactionId() + "'" +
            ", requestedBy='" + getRequestedBy() + "'" +
            ", requestedOn='" + getRequestedOn() + "'" +
            ", numberOfRecords=" + getNumberOfRecords() +
            ", requestType='" + getRequestType() + "'" +
            ", testType='" + getTestType() + "'" +
            ", apiUrl='" + getApiUrl() + "'" +
            ", dataFormat='" + getDataFormat() + "'" +
            "}";
    }
}
