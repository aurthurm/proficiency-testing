package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.EnrollmentTestSamples.*;
import static zw.org.nmrl.ept.domain.ParticipantTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class EnrollmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Enrollment.class);
        Enrollment enrollment1 = getEnrollmentSample1();
        Enrollment enrollment2 = new Enrollment();
        assertThat(enrollment1).isNotEqualTo(enrollment2);

        enrollment2.setId(enrollment1.getId());
        assertThat(enrollment1).isEqualTo(enrollment2);

        enrollment2 = getEnrollmentSample2();
        assertThat(enrollment1).isNotEqualTo(enrollment2);
    }

    @Test
    void participantTest() {
        Enrollment enrollment = getEnrollmentRandomSampleGenerator();
        Participant participantBack = getParticipantRandomSampleGenerator();

        enrollment.setParticipant(participantBack);
        assertThat(enrollment.getParticipant()).isEqualTo(participantBack);

        enrollment.participant(null);
        assertThat(enrollment.getParticipant()).isNull();
    }

    @Test
    void schemeTest() {
        Enrollment enrollment = getEnrollmentRandomSampleGenerator();
        Scheme schemeBack = getSchemeRandomSampleGenerator();

        enrollment.setScheme(schemeBack);
        assertThat(enrollment.getScheme()).isEqualTo(schemeBack);

        enrollment.scheme(null);
        assertThat(enrollment.getScheme()).isNull();
    }
}
