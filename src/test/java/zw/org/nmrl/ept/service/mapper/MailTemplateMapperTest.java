package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.MailTemplateAsserts.*;
import static zw.org.nmrl.ept.domain.MailTemplateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MailTemplateMapperTest {

    private MailTemplateMapper mailTemplateMapper;

    @BeforeEach
    void setUp() {
        mailTemplateMapper = new MailTemplateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMailTemplateSample1();
        var actual = mailTemplateMapper.toEntity(mailTemplateMapper.toDto(expected));
        assertMailTemplateAllPropertiesEquals(expected, actual);
    }
}
