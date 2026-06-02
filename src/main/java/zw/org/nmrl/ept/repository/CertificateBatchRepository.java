package zw.org.nmrl.ept.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.CertificateBatch;

/**
 * Spring Data JPA repository for the CertificateBatch entity.
 *
 * When extending this class, extend CertificateBatchRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface CertificateBatchRepository extends CertificateBatchRepositoryWithBagRelationships, JpaRepository<CertificateBatch, Long> {
    default Optional<CertificateBatch> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<CertificateBatch> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<CertificateBatch> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
