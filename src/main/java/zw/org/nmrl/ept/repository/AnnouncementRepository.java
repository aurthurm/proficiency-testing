package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.Announcement;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {}
