package zw.org.nmrl.ept.migration;

import java.util.UUID;

/** Promotes archived rows into the typed operational model after lossless capture. */
public interface LegacyCorePromoter {
    void promote(UUID batchId);
}
