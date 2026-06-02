package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.AssayTestSamples.*;
import static zw.org.nmrl.ept.domain.SampleReferenceResultTestSamples.*;
import static zw.org.nmrl.ept.domain.ShipmentSampleTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class SampleReferenceResultTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SampleReferenceResult.class);
        SampleReferenceResult sampleReferenceResult1 = getSampleReferenceResultSample1();
        SampleReferenceResult sampleReferenceResult2 = new SampleReferenceResult();
        assertThat(sampleReferenceResult1).isNotEqualTo(sampleReferenceResult2);

        sampleReferenceResult2.setId(sampleReferenceResult1.getId());
        assertThat(sampleReferenceResult1).isEqualTo(sampleReferenceResult2);

        sampleReferenceResult2 = getSampleReferenceResultSample2();
        assertThat(sampleReferenceResult1).isNotEqualTo(sampleReferenceResult2);
    }

    @Test
    void assayTest() {
        SampleReferenceResult sampleReferenceResult = getSampleReferenceResultRandomSampleGenerator();
        Assay assayBack = getAssayRandomSampleGenerator();

        sampleReferenceResult.setAssay(assayBack);
        assertThat(sampleReferenceResult.getAssay()).isEqualTo(assayBack);

        sampleReferenceResult.assay(null);
        assertThat(sampleReferenceResult.getAssay()).isNull();
    }

    @Test
    void sampleTest() {
        SampleReferenceResult sampleReferenceResult = getSampleReferenceResultRandomSampleGenerator();
        ShipmentSample shipmentSampleBack = getShipmentSampleRandomSampleGenerator();

        sampleReferenceResult.setSample(shipmentSampleBack);
        assertThat(sampleReferenceResult.getSample()).isEqualTo(shipmentSampleBack);

        sampleReferenceResult.sample(null);
        assertThat(sampleReferenceResult.getSample()).isNull();
    }
}
