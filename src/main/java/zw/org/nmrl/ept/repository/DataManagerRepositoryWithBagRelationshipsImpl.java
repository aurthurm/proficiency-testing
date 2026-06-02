package zw.org.nmrl.ept.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import zw.org.nmrl.ept.domain.DataManager;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class DataManagerRepositoryWithBagRelationshipsImpl implements DataManagerRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String DATAMANAGERS_PARAMETER = "dataManagers";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<DataManager> fetchBagRelationships(Optional<DataManager> dataManager) {
        return dataManager.map(this::fetchParticipantses);
    }

    @Override
    public Page<DataManager> fetchBagRelationships(Page<DataManager> dataManagers) {
        return new PageImpl<>(
            fetchBagRelationships(dataManagers.getContent()),
            dataManagers.getPageable(),
            dataManagers.getTotalElements()
        );
    }

    @Override
    public List<DataManager> fetchBagRelationships(List<DataManager> dataManagers) {
        return Optional.of(dataManagers).map(this::fetchParticipantses).orElse(List.of());
    }

    DataManager fetchParticipantses(DataManager result) {
        return entityManager
            .createQuery(
                "select dataManager from DataManager dataManager left join fetch dataManager.participantses where dataManager.id = :id",
                DataManager.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<DataManager> fetchParticipantses(List<DataManager> dataManagers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, dataManagers.size()).forEach(index -> order.put(dataManagers.get(index).getId(), index));
        List<DataManager> result = entityManager
            .createQuery(
                "select dataManager from DataManager dataManager left join fetch dataManager.participantses where dataManager in :dataManagers",
                DataManager.class
            )
            .setParameter(DATAMANAGERS_PARAMETER, dataManagers)
            .getResultList();
        result.sort((o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
