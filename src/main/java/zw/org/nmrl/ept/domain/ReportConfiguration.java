package zw.org.nmrl.ept.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ReportConfiguration.
 */
@Entity
@Table(name = "report_configuration")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReportConfiguration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "report_header")
    private String reportHeader;

    @Column(name = "logo")
    private String logo;

    @Column(name = "logo_right")
    private String logoRight;

    @Column(name = "layout")
    private String layout;

    @Column(name = "format")
    private String format;

    @Column(name = "top_margin")
    private Integer topMargin;

    @Column(name = "institute_address_position")
    private String instituteAddressPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "certificateTemplate",
            "configurationses",
            "correctiveActionses",
            "assayses",
            "testKitses",
            "shipmentses",
            "enrollmentses",
        },
        allowSetters = true
    )
    private Scheme scheme;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReportConfiguration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportHeader() {
        return this.reportHeader;
    }

    public ReportConfiguration reportHeader(String reportHeader) {
        this.setReportHeader(reportHeader);
        return this;
    }

    public void setReportHeader(String reportHeader) {
        this.reportHeader = reportHeader;
    }

    public String getLogo() {
        return this.logo;
    }

    public ReportConfiguration logo(String logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogoRight() {
        return this.logoRight;
    }

    public ReportConfiguration logoRight(String logoRight) {
        this.setLogoRight(logoRight);
        return this;
    }

    public void setLogoRight(String logoRight) {
        this.logoRight = logoRight;
    }

    public String getLayout() {
        return this.layout;
    }

    public ReportConfiguration layout(String layout) {
        this.setLayout(layout);
        return this;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getFormat() {
        return this.format;
    }

    public ReportConfiguration format(String format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getTopMargin() {
        return this.topMargin;
    }

    public ReportConfiguration topMargin(Integer topMargin) {
        this.setTopMargin(topMargin);
        return this;
    }

    public void setTopMargin(Integer topMargin) {
        this.topMargin = topMargin;
    }

    public String getInstituteAddressPosition() {
        return this.instituteAddressPosition;
    }

    public ReportConfiguration instituteAddressPosition(String instituteAddressPosition) {
        this.setInstituteAddressPosition(instituteAddressPosition);
        return this;
    }

    public void setInstituteAddressPosition(String instituteAddressPosition) {
        this.instituteAddressPosition = instituteAddressPosition;
    }

    public Scheme getScheme() {
        return this.scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public ReportConfiguration scheme(Scheme scheme) {
        this.setScheme(scheme);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportConfiguration)) {
            return false;
        }
        return getId() != null && getId().equals(((ReportConfiguration) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReportConfiguration{" +
            "id=" + getId() +
            ", reportHeader='" + getReportHeader() + "'" +
            ", logo='" + getLogo() + "'" +
            ", logoRight='" + getLogoRight() + "'" +
            ", layout='" + getLayout() + "'" +
            ", format='" + getFormat() + "'" +
            ", topMargin=" + getTopMargin() +
            ", instituteAddressPosition='" + getInstituteAddressPosition() + "'" +
            "}";
    }
}
