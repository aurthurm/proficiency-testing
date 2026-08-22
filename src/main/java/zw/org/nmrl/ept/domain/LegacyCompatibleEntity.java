package zw.org.nmrl.ept.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Links a normalized entity back to the exact source row retained in
 * {@code legacy_record_archive}. The payload is deliberately kept on the
 * operational row as well so unmapped legacy fields remain available during
 * phased domain-model expansion.
 */
@MappedSuperclass
public abstract class LegacyCompatibleEntity implements Serializable {

    @Column(name = "legacy_source_id", length = 255, unique = true)
    private String legacySourceId;

    @Column(name = "legacy_payload", columnDefinition = "text")
    private String legacyPayload;

    public String getLegacySourceId() {
        return legacySourceId;
    }

    public void setLegacySourceId(String legacySourceId) {
        this.legacySourceId = legacySourceId;
    }

    public String getLegacyPayload() {
        return legacyPayload;
    }

    public void setLegacyPayload(String legacyPayload) {
        this.legacyPayload = legacyPayload;
    }
}
