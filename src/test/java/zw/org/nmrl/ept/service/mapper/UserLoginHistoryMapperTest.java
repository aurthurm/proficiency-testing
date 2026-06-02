package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.UserLoginHistoryAsserts.*;
import static zw.org.nmrl.ept.domain.UserLoginHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserLoginHistoryMapperTest {

    private UserLoginHistoryMapper userLoginHistoryMapper;

    @BeforeEach
    void setUp() {
        userLoginHistoryMapper = new UserLoginHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserLoginHistorySample1();
        var actual = userLoginHistoryMapper.toEntity(userLoginHistoryMapper.toDto(expected));
        assertUserLoginHistoryAllPropertiesEquals(expected, actual);
    }
}
