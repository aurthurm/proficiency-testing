package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.HomePageSection;

/**
 * Spring Data JPA repository for the HomePageSection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HomePageSectionRepository extends JpaRepository<HomePageSection, Long> {}
