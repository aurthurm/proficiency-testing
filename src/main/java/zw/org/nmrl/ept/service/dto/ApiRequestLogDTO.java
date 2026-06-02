package zw.org.nmrl.ept.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ApiRequestLog} entity.
 */
@Schema(description = "Mobile-app API request log (API-06).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiRequestLogDTO implements Serializable {

    private Long id;

    @NotNull
    private String transactionId;

    private String requestedBy;

    private Instant requestedOn;

    private Integer numberOfRecords;

    private String requestType;

    private String testType;

    private String apiUrl;

    private String dataFormat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(Instant requestedOn) {
        this.requestedOn = requestedOn;
    }

    public Integer getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(Integer numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiRequestLogDTO)) {
            return false;
        }

        ApiRequestLogDTO apiRequestLogDTO = (ApiRequestLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, apiRequestLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiRequestLogDTO{" +
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
