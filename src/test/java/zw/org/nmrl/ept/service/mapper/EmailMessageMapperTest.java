package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.EmailMessageAsserts.*;
import static zw.org.nmrl.ept.domain.EmailMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmailMessageMapperTest {

    private EmailMessageMapper emailMessageMapper;

    @BeforeEach
    void setUp() {
        emailMessageMapper = new EmailMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEmailMessageSample1();
        var actual = emailMessageMapper.toEntity(emailMessageMapper.toDto(expected));
        assertEmailMessageAllPropertiesEquals(expected, actual);
    }
}
