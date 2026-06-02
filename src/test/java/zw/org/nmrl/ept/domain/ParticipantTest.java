package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CountryTestSamples.*;
import static zw.org.nmrl.ept.domain.DataManagerTestSamples.*;
import static zw.org.nmrl.ept.domain.EnrollmentTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantCustomValueTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentParticipantMapTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ParticipantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Participant.class);
        Participant participant1 = getParticipantSample1();
        Participant participant2 = new Participant();
        assertThat(participant1).isNotEqualTo(participant2);

        participant2.setId(participant1.getId());
        assertThat(participant1).isEqualTo(participant2);

        participant2 = getParticipantSample2();
        assertThat(participant1).isNotEqualTo(participant2);
    }

    @Test
    void shipmentMapsTest() {
        Participant participant = getParticipantRandomSampleGenerator();
        ShipmentParticipantMap shipmentParticipantMapBack = getShipmentParticipantMapRandomSampleGenerator();

        participant.addShipmentMaps(shipmentParticipantMapBack);
        assertThat(participant.getShipmentMapses()).containsOnly(shipmentParticipantMapBack);
        assertThat(shipmentParticipantMapBack.getParticipant()).isEqualTo(participant);

        participant.removeShipmentMaps(shipmentParticipantMapBack);
        assertThat(participant.getShipmentMapses()).doesNotContain(shipmentParticipantMapBack);
        assertThat(shipmentParticipantMapBack.getParticipant()).isNull();

        participant.shipmentMapses(new HashSet<>(Set.of(shipmentParticipantMapBack)));
        assertThat(participant.getShipmentMapses()).containsOnly(shipmentParticipantMapBack);
        assertThat(shipmentParticipantMapBack.getParticipant()).isEqualTo(participant);

        participant.setShipmentMapses(new HashSet<>());
        assertThat(participant.getShipmentMapses()).doesNotContain(shipmentParticipantMapBack);
        assertThat(shipmentParticipantMapBack.getParticipant()).isNull();
    }

    @Test
    void countryTest() {
        Participant participant = getParticipantRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        participant.setCountry(countryBack);
        assertThat(participant.getCountry()).isEqualTo(countryBack);

        participant.country(null);
        assertThat(participant.getCountry()).isNull();
    }

    @Test
    void enrollmentsTest() {
        Participant participant = getParticipantRandomSampleGenerator();
        Enrollment enrollmentBack = getEnrollmentRandomSampleGenerator();

        participant.addEnrollments(enrollmentBack);
        assertThat(participant.getEnrollmentses()).containsOnly(enrollmentBack);
        assertThat(enrollmentBack.getParticipant()).isEqualTo(participant);

        participant.removeEnrollments(enrollmentBack);
        assertThat(participant.getEnrollmentses()).doesNotContain(enrollmentBack);
        assertThat(enrollmentBack.getParticipant()).isNull();

        participant.enrollmentses(new HashSet<>(Set.of(enrollmentBack)));
        assertThat(participant.getEnrollmentses()).containsOnly(enrollmentBack);
        assertThat(enrollmentBack.getParticipant()).isEqualTo(participant);

        participant.setEnrollmentses(new HashSet<>());
        assertThat(participant.getEnrollmentses()).doesNotContain(enrollmentBack);
        assertThat(enrollmentBack.getParticipant()).isNull();
    }

    @Test
    void customValuesTest() {
        Participant participant = getParticipantRandomSampleGenerator();
        ParticipantCustomValue participantCustomValueBack = getParticipantCustomValueRandomSampleGenerator();

        participant.addCustomValues(participantCustomValueBack);
        assertThat(participant.getCustomValueses()).containsOnly(participantCustomValueBack);
        assertThat(participantCustomValueBack.getParticipant()).isEqualTo(participant);

        participant.removeCustomValues(participantCustomValueBack);
        assertThat(participant.getCustomValueses()).doesNotContain(participantCustomValueBack);
        assertThat(participantCustomValueBack.getParticipant()).isNull();

        participant.customValueses(new HashSet<>(Set.of(participantCustomValueBack)));
        assertThat(participant.getCustomValueses()).containsOnly(participantCustomValueBack);
        assertThat(participantCustomValueBack.getParticipant()).isEqualTo(participant);

        participant.setCustomValueses(new HashSet<>());
        assertThat(participant.getCustomValueses()).doesNotContain(participantCustomValueBack);
        assertThat(participantCustomValueBack.getParticipant()).isNull();
    }

    @Test
    void dataManagersTest() {
        Participant participant = getParticipantRandomSampleGenerator();
        DataManager dataManagerBack = getDataManagerRandomSampleGenerator();

        participant.addDataManagers(dataManagerBack);
        assertThat(participant.getDataManagerses()).containsOnly(dataManagerBack);
        assertThat(dataManagerBack.getParticipantses()).containsOnly(participant);

        participant.removeDataManagers(dataManagerBack);
        assertThat(participant.getDataManagerses()).doesNotContain(dataManagerBack);
        assertThat(dataManagerBack.getParticipantses()).doesNotContain(participant);

        participant.dataManagerses(new HashSet<>(Set.of(dataManagerBack)));
        assertThat(participant.getDataManagerses()).containsOnly(dataManagerBack);
        assertThat(dataManagerBack.getParticipantses()).containsOnly(participant);

        participant.setDataManagerses(new HashSet<>());
        assertThat(participant.getDataManagerses()).doesNotContain(dataManagerBack);
        assertThat(dataManagerBack.getParticipantses()).doesNotContain(participant);
    }
}
