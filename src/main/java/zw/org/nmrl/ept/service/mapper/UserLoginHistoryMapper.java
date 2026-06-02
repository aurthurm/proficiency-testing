package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.UserLoginHistory;
import zw.org.nmrl.ept.service.dto.UserLoginHistoryDTO;

/**
 * Mapper for the entity {@link UserLoginHistory} and its DTO {@link UserLoginHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserLoginHistoryMapper extends EntityMapper<UserLoginHistoryDTO, UserLoginHistory> {}
