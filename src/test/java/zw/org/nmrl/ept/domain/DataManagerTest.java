package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.CountryTestSamples.*;
import static zw.org.nmrl.ept.domain.DataManagerTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class DataManagerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataManager.class);
        DataManager dataManager1 = getDataManagerSample1();
        DataManager dataManager2 = new DataManager();
        assertThat(dataManager1).isNotEqualTo(dataManager2);

        dataManager2.setId(dataManager1.getId());
        assertThat(dataManager1).isEqualTo(dataManager2);

        dataManager2 = getDataManagerSample2();
        assertThat(dataManager1).isNotEqualTo(dataManager2);
    }

    @Test
    void countryTest() {
        DataManager dataManager = getDataManagerRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        dataManager.setCountry(countryBack);
        assertThat(dataManager.getCountry()).isEqualTo(countryBack);

        dataManager.country(null);
        assertThat(dataManager.getCountry()).isNull();
    }

    @Test
    void participantsTest() {
        DataManager dataManager = getDataManagerRandomSampleGenerator();
        Participant participantBack = getParticipantRandomSampleGenerator();

        dataManager.addParticipants(participantBack);
        assertThat(dataManager.getParticipantses()).containsOnly(participantBack);

        dataManager.removeParticipants(participantBack);
        assertThat(dataManager.getParticipantses()).doesNotContain(participantBack);

        dataManager.participantses(new HashSet<>(Set.of(participantBack)));
        assertThat(dataManager.getParticipantses()).containsOnly(participantBack);

        dataManager.setParticipantses(new HashSet<>());
        assertThat(dataManager.getParticipantses()).doesNotContain(participantBack);
    }
}
