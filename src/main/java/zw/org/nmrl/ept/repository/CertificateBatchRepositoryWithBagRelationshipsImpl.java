package zw.org.nmrl.ept.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import zw.org.nmrl.ept.domain.CertificateBatch;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CertificateBatchRepositoryWithBagRelationshipsImpl implements CertificateBatchRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String CERTIFICATEBATCHES_PARAMETER = "certificateBatches";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CertificateBatch> fetchBagRelationships(Optional<CertificateBatch> certificateBatch) {
        return certificateBatch.map(this::fetchShipmentses);
    }

    @Override
    public Page<CertificateBatch> fetchBagRelationships(Page<CertificateBatch> certificateBatches) {
        return new PageImpl<>(
            fetchBagRelationships(certificateBatches.getContent()),
            certificateBatches.getPageable(),
            certificateBatches.getTotalElements()
        );
    }

    @Override
    public List<CertificateBatch> fetchBagRelationships(List<CertificateBatch> certificateBatches) {
        return Optional.of(certificateBatches).map(this::fetchShipmentses).orElse(List.of());
    }

    CertificateBatch fetchShipmentses(CertificateBatch result) {
        return entityManager
            .createQuery(
                "select certificateBatch from CertificateBatch certificateBatch left join fetch certificateBatch.shipmentses where certificateBatch.id = :id",
                CertificateBatch.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<CertificateBatch> fetchShipmentses(List<CertificateBatch> certificateBatches) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, certificateBatches.size()).forEach(index -> order.put(certificateBatches.get(index).getId(), index));
        List<CertificateBatch> result = entityManager
            .createQuery(
                "select certificateBatch from CertificateBatch certificateBatch left join fetch certificateBatch.shipmentses where certificateBatch in :certificateBatches",
                CertificateBatch.class
            )
            .setParameter(CERTIFICATEBATCHES_PARAMETER, certificateBatches)
            .getResultList();
        result.sort((o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
