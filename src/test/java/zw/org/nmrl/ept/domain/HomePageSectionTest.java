package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.HomePageSectionTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class HomePageSectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HomePageSection.class);
        HomePageSection homePageSection1 = getHomePageSectionSample1();
        HomePageSection homePageSection2 = new HomePageSection();
        assertThat(homePageSection1).isNotEqualTo(homePageSection2);

        homePageSection2.setId(homePageSection1.getId());
        assertThat(homePageSection1).isEqualTo(homePageSection2);

        homePageSection2 = getHomePageSectionSample2();
        assertThat(homePageSection1).isNotEqualTo(homePageSection2);
    }
}
