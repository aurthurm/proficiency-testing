package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ScheduledJobDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduledJobDTO.class);
        ScheduledJobDTO scheduledJobDTO1 = new ScheduledJobDTO();
        scheduledJobDTO1.setId(1L);
        ScheduledJobDTO scheduledJobDTO2 = new ScheduledJobDTO();
        assertThat(scheduledJobDTO1).isNotEqualTo(scheduledJobDTO2);
        scheduledJobDTO2.setId(scheduledJobDTO1.getId());
        assertThat(scheduledJobDTO1).isEqualTo(scheduledJobDTO2);
        scheduledJobDTO2.setId(2L);
        assertThat(scheduledJobDTO1).isNotEqualTo(scheduledJobDTO2);
        scheduledJobDTO1.setId(null);
        assertThat(scheduledJobDTO1).isNotEqualTo(scheduledJobDTO2);
    }
}
