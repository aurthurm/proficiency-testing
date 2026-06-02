package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.CapaRecord;

/**
 * Spring Data JPA repository for the CapaRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CapaRecordRepository extends JpaRepository<CapaRecord, Long>, JpaSpecificationExecutor<CapaRecord> {}
