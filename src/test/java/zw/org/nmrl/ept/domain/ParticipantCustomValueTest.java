package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CustomFieldDefinitionTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantCustomValueTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantCustomValueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParticipantCustomValue.class);
        ParticipantCustomValue participantCustomValue1 = getParticipantCustomValueSample1();
        ParticipantCustomValue participantCustomValue2 = new ParticipantCustomValue();
        assertThat(participantCustomValue1).isNotEqualTo(participantCustomValue2);

        participantCustomValue2.setId(participantCustomValue1.getId());
        assertThat(participantCustomValue1).isEqualTo(participantCustomValue2);

        participantCustomValue2 = getParticipantCustomValueSample2();
        assertThat(participantCustomValue1).isNotEqualTo(participantCustomValue2);
    }

    @Test
    void participantTest() {
        ParticipantCustomValue participantCustomValue = getParticipantCustomValueRandomSampleGenerator();
        Participant participantBack = getParticipantRandomSampleGenerator();

        participantCustomValue.setParticipant(participantBack);
        assertThat(participantCustomValue.getParticipant()).isEqualTo(participantBack);

        participantCustomValue.participant(null);
        assertThat(participantCustomValue.getParticipant()).isNull();
    }

    @Test
    void definitionTest() {
        ParticipantCustomValue participantCustomValue = getParticipantCustomValueRandomSampleGenerator();
        CustomFieldDefinition customFieldDefinitionBack = getCustomFieldDefinitionRandomSampleGenerator();

        participantCustomValue.setDefinition(customFieldDefinitionBack);
        assertThat(participantCustomValue.getDefinition()).isEqualTo(customFieldDefinitionBack);

        participantCustomValue.definition(null);
        assertThat(participantCustomValue.getDefinition()).isNull();
    }
}
