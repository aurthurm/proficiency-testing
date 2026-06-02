package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.ScheduledJobTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ScheduledJobTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduledJob.class);
        ScheduledJob scheduledJob1 = getScheduledJobSample1();
        ScheduledJob scheduledJob2 = new ScheduledJob();
        assertThat(scheduledJob1).isNotEqualTo(scheduledJob2);

        scheduledJob2.setId(scheduledJob1.getId());
        assertThat(scheduledJob1).isEqualTo(scheduledJob2);

        scheduledJob2 = getScheduledJobSample2();
        assertThat(scheduledJob1).isNotEqualTo(scheduledJob2);
    }
}
