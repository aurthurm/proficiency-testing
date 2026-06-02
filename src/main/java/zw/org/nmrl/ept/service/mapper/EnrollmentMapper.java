package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.Enrollment;
import zw.org.nmrl.ept.domain.Participant;
import zw.org.nmrl.ept.domain.Scheme;
import zw.org.nmrl.ept.service.dto.EnrollmentDTO;
import zw.org.nmrl.ept.service.dto.ParticipantDTO;
import zw.org.nmrl.ept.service.dto.SchemeDTO;

/**
 * Mapper for the entity {@link Enrollment} and its DTO {@link EnrollmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnrollmentMapper extends EntityMapper<EnrollmentDTO, Enrollment> {
    @Mapping(target = "participant", source = "participant", qualifiedByName = "participantId")
    @Mapping(target = "scheme", source = "scheme", qualifiedByName = "schemeId")
    EnrollmentDTO toDto(Enrollment s);

    @Named("participantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParticipantDTO toDtoParticipantId(Participant participant);

    @Named("schemeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SchemeDTO toDtoSchemeId(Scheme scheme);
}
