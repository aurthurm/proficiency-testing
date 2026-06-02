package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.CustomFieldDefinitionAsserts.*;
import static zw.org.nmrl.ept.domain.CustomFieldDefinitionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomFieldDefinitionMapperTest {

    private CustomFieldDefinitionMapper customFieldDefinitionMapper;

    @BeforeEach
    void setUp() {
        customFieldDefinitionMapper = new CustomFieldDefinitionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCustomFieldDefinitionSample1();
        var actual = customFieldDefinitionMapper.toEntity(customFieldDefinitionMapper.toDto(expected));
        assertCustomFieldDefinitionAllPropertiesEquals(expected, actual);
    }
}
