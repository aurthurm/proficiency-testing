package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.ModeOfReceipt;

/**
 * Spring Data JPA repository for the ModeOfReceipt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ModeOfReceiptRepository extends JpaRepository<ModeOfReceipt, Long> {}
