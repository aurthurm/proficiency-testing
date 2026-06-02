package zw.org.nmrl.ept.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;
import zw.org.nmrl.ept.domain.enumeration.DistributionStatus;

/**
 * Criteria class for the {@link zw.org.nmrl.ept.domain.Distribution} entity. This class is used
 * in {@link zw.org.nmrl.ept.web.rest.DistributionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /distributions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DistributionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DistributionStatus
     */
    public static class DistributionStatusFilter extends Filter<DistributionStatus> {

        public DistributionStatusFilter() {}

        public DistributionStatusFilter(DistributionStatusFilter filter) {
            super(filter);
        }

        @Override
        public DistributionStatusFilter copy() {
            return new DistributionStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private LocalDateFilter distributionDate;

    private DistributionStatusFilter status;

    private LongFilter shipmentsId;

    private Boolean distinct;

    public DistributionCriteria() {}

    public DistributionCriteria(DistributionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.distributionDate = other.optionalDistributionDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(DistributionStatusFilter::copy).orElse(null);
        this.shipmentsId = other.optionalShipmentsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DistributionCriteria copy() {
        return new DistributionCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public LocalDateFilter getDistributionDate() {
        return distributionDate;
    }

    public Optional<LocalDateFilter> optionalDistributionDate() {
        return Optional.ofNullable(distributionDate);
    }

    public LocalDateFilter distributionDate() {
        if (distributionDate == null) {
            setDistributionDate(new LocalDateFilter());
        }
        return distributionDate;
    }

    public void setDistributionDate(LocalDateFilter distributionDate) {
        this.distributionDate = distributionDate;
    }

    public DistributionStatusFilter getStatus() {
        return status;
    }

    public Optional<DistributionStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public DistributionStatusFilter status() {
        if (status == null) {
            setStatus(new DistributionStatusFilter());
        }
        return status;
    }

    public void setStatus(DistributionStatusFilter status) {
        this.status = status;
    }

    public LongFilter getShipmentsId() {
        return shipmentsId;
    }

    public Optional<LongFilter> optionalShipmentsId() {
        return Optional.ofNullable(shipmentsId);
    }

    public LongFilter shipmentsId() {
        if (shipmentsId == null) {
            setShipmentsId(new LongFilter());
        }
        return shipmentsId;
    }

    public void setShipmentsId(LongFilter shipmentsId) {
        this.shipmentsId = shipmentsId;
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
        final DistributionCriteria that = (DistributionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(distributionDate, that.distributionDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(shipmentsId, that.shipmentsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, distributionDate, status, shipmentsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DistributionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalDistributionDate().map(f -> "distributionDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalShipmentsId().map(f -> "shipmentsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
