package zw.org.nmrl.ept.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import zw.org.nmrl.ept.domain.CustomFieldDefinition;

/**
 * Spring Data JPA repository for the CustomFieldDefinition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomFieldDefinitionRepository extends JpaRepository<CustomFieldDefinition, Long> {}
