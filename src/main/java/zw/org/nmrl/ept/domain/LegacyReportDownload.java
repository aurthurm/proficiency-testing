package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/** Audit history for reports downloaded from the legacy ePT installation. */
@Entity
@Table(name = "legacy_report_download")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LegacyReportDownload extends LegacyCompatibleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "report_type", length = 100)
    private String reportType;

    @NotNull
    @Column(name = "downloaded_on", nullable = false)
    private Instant downloadedOn;

    @NotNull
    @Column(name = "downloaded_by", nullable = false, length = 256)
    private String downloadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Shipment shipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Instant getDownloadedOn() {
        return downloadedOn;
    }

    public void setDownloadedOn(Instant downloadedOn) {
        this.downloadedOn = downloadedOn;
    }

    public String getDownloadedBy() {
        return downloadedBy;
    }

    public void setDownloadedBy(String downloadedBy) {
        this.downloadedBy = downloadedBy;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
}
