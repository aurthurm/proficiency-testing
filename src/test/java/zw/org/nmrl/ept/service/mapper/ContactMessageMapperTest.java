package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ContactMessageAsserts.*;
import static zw.org.nmrl.ept.domain.ContactMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactMessageMapperTest {

    private ContactMessageMapper contactMessageMapper;

    @BeforeEach
    void setUp() {
        contactMessageMapper = new ContactMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getContactMessageSample1();
        var actual = contactMessageMapper.toEntity(contactMessageMapper.toDto(expected));
        assertContactMessageAllPropertiesEquals(expected, actual);
    }
}
