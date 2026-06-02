package zw.org.nmrl.ept.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import zw.org.nmrl.ept.domain.CertificateBatch;

public interface CertificateBatchRepositoryWithBagRelationships {
    Optional<CertificateBatch> fetchBagRelationships(Optional<CertificateBatch> certificateBatch);

    List<CertificateBatch> fetchBagRelationships(List<CertificateBatch> certificateBatches);

    Page<CertificateBatch> fetchBagRelationships(Page<CertificateBatch> certificateBatches);
}
