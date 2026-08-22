package zw.org.nmrl.ept.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import zw.org.nmrl.ept.domain.enumeration.LegacyResultCategory;

/** Lossless typed envelope for every scheme-specific response and reference-result row. */
@Entity
@Table(
    name = "legacy_scheme_result",
    uniqueConstraints = @UniqueConstraint(columnNames = { "source_table", "source_row_key" })
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LegacySchemeResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "source_table", nullable = false, length = 128)
    private String sourceTable;

    @NotNull
    @Column(name = "source_row_key", nullable = false, length = 64)
    private String sourceRowKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "result_category", nullable = false, length = 32)
    private LegacyResultCategory resultCategory;

    @Column(name = "scheme_code", length = 64)
    private String schemeCode;

    @NotNull
    @Lob
    @Column(name = "legacy_payload", nullable = false)
    private String legacyPayload;

    @ManyToOne(fetch = FetchType.LAZY)
    private ShipmentParticipantMap shipmentParticipantMap;

    @ManyToOne(fetch = FetchType.LAZY)
    private Shipment shipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getSourceRowKey() {
        return sourceRowKey;
    }

    public void setSourceRowKey(String sourceRowKey) {
        this.sourceRowKey = sourceRowKey;
    }

    public LegacyResultCategory getResultCategory() {
        return resultCategory;
    }

    public void setResultCategory(LegacyResultCategory resultCategory) {
        this.resultCategory = resultCategory;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getLegacyPayload() {
        return legacyPayload;
    }

    public void setLegacyPayload(String legacyPayload) {
        this.legacyPayload = legacyPayload;
    }

    public ShipmentParticipantMap getShipmentParticipantMap() {
        return shipmentParticipantMap;
    }

    public void setShipmentParticipantMap(ShipmentParticipantMap shipmentParticipantMap) {
        this.shipmentParticipantMap = shipmentParticipantMap;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
}
