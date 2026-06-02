package zw.org.nmrl.ept.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.ReportConfiguration} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReportConfigurationDTO implements Serializable {

    private Long id;

    private String reportHeader;

    private String logo;

    private String logoRight;

    private String layout;

    private String format;

    private Integer topMargin;

    private String instituteAddressPosition;

    private SchemeDTO scheme;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportHeader() {
        return reportHeader;
    }

    public void setReportHeader(String reportHeader) {
        this.reportHeader = reportHeader;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogoRight() {
        return logoRight;
    }

    public void setLogoRight(String logoRight) {
        this.logoRight = logoRight;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(Integer topMargin) {
        this.topMargin = topMargin;
    }

    public String getInstituteAddressPosition() {
        return instituteAddressPosition;
    }

    public void setInstituteAddressPosition(String instituteAddressPosition) {
        this.instituteAddressPosition = instituteAddressPosition;
    }

    public SchemeDTO getScheme() {
        return scheme;
    }

    public void setScheme(SchemeDTO scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportConfigurationDTO)) {
            return false;
        }

        ReportConfigurationDTO reportConfigurationDTO = (ReportConfigurationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reportConfigurationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReportConfigurationDTO{" +
            "id=" + getId() +
            ", reportHeader='" + getReportHeader() + "'" +
            ", logo='" + getLogo() + "'" +
            ", logoRight='" + getLogoRight() + "'" +
            ", layout='" + getLayout() + "'" +
            ", format='" + getFormat() + "'" +
            ", topMargin=" + getTopMargin() +
            ", instituteAddressPosition='" + getInstituteAddressPosition() + "'" +
            ", scheme=" + getScheme() +
            "}";
    }
}
