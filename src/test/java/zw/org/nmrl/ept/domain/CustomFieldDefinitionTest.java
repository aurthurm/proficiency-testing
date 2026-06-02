package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CustomFieldDefinitionTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CustomFieldDefinitionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomFieldDefinition.class);
        CustomFieldDefinition customFieldDefinition1 = getCustomFieldDefinitionSample1();
        CustomFieldDefinition customFieldDefinition2 = new CustomFieldDefinition();
        assertThat(customFieldDefinition1).isNotEqualTo(customFieldDefinition2);

        customFieldDefinition2.setId(customFieldDefinition1.getId());
        assertThat(customFieldDefinition1).isEqualTo(customFieldDefinition2);

        customFieldDefinition2 = getCustomFieldDefinitionSample2();
        assertThat(customFieldDefinition1).isNotEqualTo(customFieldDefinition2);
    }
}
