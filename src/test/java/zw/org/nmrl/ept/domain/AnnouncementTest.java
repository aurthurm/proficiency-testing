package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.AnnouncementTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class AnnouncementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Announcement.class);
        Announcement announcement1 = getAnnouncementSample1();
        Announcement announcement2 = new Announcement();
        assertThat(announcement1).isNotEqualTo(announcement2);

        announcement2.setId(announcement1.getId());
        assertThat(announcement1).isEqualTo(announcement2);

        announcement2 = getAnnouncementSample2();
        assertThat(announcement1).isNotEqualTo(announcement2);
    }
}
