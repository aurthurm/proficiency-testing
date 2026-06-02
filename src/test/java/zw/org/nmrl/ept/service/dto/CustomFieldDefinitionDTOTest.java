package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CustomFieldDefinitionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomFieldDefinitionDTO.class);
        CustomFieldDefinitionDTO customFieldDefinitionDTO1 = new CustomFieldDefinitionDTO();
        customFieldDefinitionDTO1.setId(1L);
        CustomFieldDefinitionDTO customFieldDefinitionDTO2 = new CustomFieldDefinitionDTO();
        assertThat(customFieldDefinitionDTO1).isNotEqualTo(customFieldDefinitionDTO2);
        customFieldDefinitionDTO2.setId(customFieldDefinitionDTO1.getId());
        assertThat(customFieldDefinitionDTO1).isEqualTo(customFieldDefinitionDTO2);
        customFieldDefinitionDTO2.setId(2L);
        assertThat(customFieldDefinitionDTO1).isNotEqualTo(customFieldDefinitionDTO2);
        customFieldDefinitionDTO1.setId(null);
        assertThat(customFieldDefinitionDTO1).isNotEqualTo(customFieldDefinitionDTO2);
    }
}
