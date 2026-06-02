package zw.org.nmrl.ept.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import zw.org.nmrl.ept.domain.DataManager;

public interface DataManagerRepositoryWithBagRelationships {
    Optional<DataManager> fetchBagRelationships(Optional<DataManager> dataManager);

    List<DataManager> fetchBagRelationships(List<DataManager> dataManagers);

    Page<DataManager> fetchBagRelationships(Page<DataManager> dataManagers);
}
