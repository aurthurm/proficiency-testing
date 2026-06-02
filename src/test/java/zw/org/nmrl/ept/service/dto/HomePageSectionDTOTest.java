package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class HomePageSectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HomePageSectionDTO.class);
        HomePageSectionDTO homePageSectionDTO1 = new HomePageSectionDTO();
        homePageSectionDTO1.setId(1L);
        HomePageSectionDTO homePageSectionDTO2 = new HomePageSectionDTO();
        assertThat(homePageSectionDTO1).isNotEqualTo(homePageSectionDTO2);
        homePageSectionDTO2.setId(homePageSectionDTO1.getId());
        assertThat(homePageSectionDTO1).isEqualTo(homePageSectionDTO2);
        homePageSectionDTO2.setId(2L);
        assertThat(homePageSectionDTO1).isNotEqualTo(homePageSectionDTO2);
        homePageSectionDTO1.setId(null);
        assertThat(homePageSectionDTO1).isNotEqualTo(homePageSectionDTO2);
    }
}
