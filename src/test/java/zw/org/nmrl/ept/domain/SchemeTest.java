package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.AssayTestSamples.*;
import static zw.org.nmrl.ept.domain.CertificateTemplateTestSamples.*;
import static zw.org.nmrl.ept.domain.CorrectiveActionTestSamples.*;
import static zw.org.nmrl.ept.domain.EnrollmentTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeConfigurationTestSamples.*;
import static zw.org.nmrl.ept.domain.SchemeTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentTestSamples.*;
import static zw.org.nmrl.ept.domain.TestKitTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class SchemeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Scheme.class);
        Scheme scheme1 = getSchemeSample1();
        Scheme scheme2 = new Scheme();
        assertThat(scheme1).isNotEqualTo(scheme2);

        scheme2.setId(scheme1.getId());
        assertThat(scheme1).isEqualTo(scheme2);

        scheme2 = getSchemeSample2();
        assertThat(scheme1).isNotEqualTo(scheme2);
    }

    @Test
    void certificateTemplateTest() {
        Scheme scheme = getSchemeRandomSampleGenerator();
        CertificateTemplate certificateTemplateBack = getCertificateTemplateRandomSampleGenerator();

        scheme.setCertificateTemplate(certificateTemplateBack);
        assertThat(scheme.getCertificateTemplate()).isEqualTo(certificateTemplateBack);

        scheme.certificateTemplate(null);
        assertThat(scheme.getCertificateTemplate()).isNull();
    }

    @Test
    void configurationsTest() {
        Scheme scheme = getSchemeRandomSampleGenerator();
        SchemeConfiguration schemeConfigurationBack = getSchemeConfigurationRandomSampleGenerator();

        scheme.addConfigurations(schemeConfigurationBack);
        assertThat(scheme.getConfigurationses()).containsOnly(schemeConfigurationBack);
        assertThat(schemeConfigurationBack.getScheme()).isEqualTo(scheme);

        scheme.removeConfigurations(schemeConfigurationBack);
        assertThat(scheme.getConfigurationses()).doesNotContain(schemeConfigurationBack);
        assertThat(schemeConfigurationBack.getScheme()).isNull();

        scheme.configurationses(new HashSet<>(Set.of(schemeConfigurationBack)));
        assertThat(scheme.getConfigurationses()).containsOnly(schemeConfigurationBack);
        assertThat(schemeConfigurationBack.getScheme()).isEqualTo(scheme);

        scheme.setConfigurationses(new HashSet<>());
        assertThat(scheme.getConfigurationses()).doesNotContain(schemeConfigurationBack);
        assertThat(schemeConfigurationBack.getScheme()).isNull();
    }

    @Test
    void correctiveActionsTest() {
        Scheme scheme = getSchemeRandomSampleGenerator();
        CorrectiveAction correctiveActionBack = getCorrectiveActionRandomSampleGenerator();

        scheme.addCorrectiveActions(correctiveActionBack);
        assertThat(scheme.getCorrectiveActionses()).containsOnly(correctiveActionBack);
        assertThat(correctiveActionBack.getScheme()).isEqualTo(scheme);

        scheme.removeCorrectiveActions(correctiveActionBack);
        assertThat(scheme.getCorrectiveActionses()).doesNotContain(correctiveActionBack);
        assertThat(correctiveActionBack.getScheme()).isNull();

        scheme.correctiveActionses(new HashSet<>(Set.of(correctiveActionBack)));
        assertThat(scheme.getCorrectiveActionses()).containsOnly(correctiveActionBack);
        assertThat(correctiveActionBack.getScheme()).isEqualTo(scheme);

        scheme.setCorrectiveActionses(new HashSet<>());
        assertThat(scheme.getCorrectiveActionses()).doesNotContain(correctiveActionBack);
        assertThat(correctiveActionBack.getScheme()).isNull();
    }

    @Test
    void assaysTest() {
        Scheme scheme = getSchemeRandomSampleGenerator();
        Assay assayBack = getAssayRandomSampleGenerator();

        scheme.addAssays(assayBack);
        assertThat(scheme.getAssayses()).containsOnly(assayBack);
        assertThat(assayBack.getScheme()).isEqualTo(scheme);

        scheme.removeAssays(assayBack);
        assertThat(scheme.getAssayses()).doesNotContain(assayBack);
        assertThat(assayBack.getScheme()).isNull();

        scheme.assayses(new HashSet<>(Set.of(assayBack)));
        assertThat(scheme.getAssayses()).containsOnly(assayBack);
        assertThat(assayBack.getScheme()).isEqualTo(scheme);

        scheme.setAssayses(new HashSet<>());
        assertThat(scheme.getAssayses()).doesNotContain(assayBack);
        assertThat(assayBack.getScheme()).isNull();
    }

    @Test
    void testKitsTest() {
        Scheme scheme = getSchemeRandomSampleGenerator();
        TestKit testKitBack = getTestKitRandomSampleGenerator();

        scheme.addTestKits(testKitBack);
        assertThat(scheme.getTestKitses()).containsOnly(testKitBack);
        assertThat(testKitBack.getScheme()).isEqualTo(scheme);

        scheme.removeTestKits(testKitBack);
        assertThat(scheme.getTestKitses()).doesNotContain(testKitBack);
        assertThat(testKitBack.getScheme()).isNull();

        scheme.testKitses(new HashSet<>(Set.of(testKitBack)));
        assertThat(scheme.getTestKitses()).containsOnly(testKitBack);
        assertThat(testKitBack.getScheme()).isEqualTo(scheme);

        scheme.setTestKitses(new HashSet<>());
        assertThat(scheme.getTestKitses()).doesNotContain(testKitBack);
        assertThat(testKitBack.getScheme()).isNull();
    }

    @Test
    void shipmentsTest() {
        Scheme scheme = getSchemeRandomSampleGenerator();
        Shipment shipmentBack = getShipmentRandomSampleGenerator();

        scheme.addShipments(shipmentBack);
        assertThat(scheme.getShipmentses()).containsOnly(shipmentBack);
        assertThat(shipmentBack.getScheme()).isEqualTo(scheme);

        scheme.removeShipments(shipmentBack);
        assertThat(scheme.getShipmentses()).doesNotContain(shipmentBack);
        assertThat(shipmentBack.getScheme()).isNull();

        scheme.shipmentses(new HashSet<>(Set.of(shipmentBack)));
        assertThat(scheme.getShipmentses()).containsOnly(shipmentBack);
        assertThat(shipmentBack.getScheme()).isEqualTo(scheme);

        scheme.setShipmentses(new HashSet<>());
        assertThat(scheme.getShipmentses()).doesNotContain(shipmentBack);
        assertThat(shipmentBack.getScheme()).isNull();
    }

    @Test
    void enrollmentsTest() {
        Scheme scheme = getSchemeRandomSampleGenerator();
        Enrollment enrollmentBack = getEnrollmentRandomSampleGenerator();

        scheme.addEnrollments(enrollmentBack);
        assertThat(scheme.getEnrollmentses()).containsOnly(enrollmentBack);
        assertThat(enrollmentBack.getScheme()).isEqualTo(scheme);

        scheme.removeEnrollments(enrollmentBack);
        assertThat(scheme.getEnrollmentses()).doesNotContain(enrollmentBack);
        assertThat(enrollmentBack.getScheme()).isNull();

        scheme.enrollmentses(new HashSet<>(Set.of(enrollmentBack)));
        assertThat(scheme.getEnrollmentses()).containsOnly(enrollmentBack);
        assertThat(enrollmentBack.getScheme()).isEqualTo(scheme);

        scheme.setEnrollmentses(new HashSet<>());
        assertThat(scheme.getEnrollmentses()).doesNotContain(enrollmentBack);
        assertThat(enrollmentBack.getScheme()).isNull();
    }
}
