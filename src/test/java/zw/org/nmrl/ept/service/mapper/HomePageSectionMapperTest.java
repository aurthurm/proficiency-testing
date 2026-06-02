package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.HomePageSectionAsserts.*;
import static zw.org.nmrl.ept.domain.HomePageSectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HomePageSectionMapperTest {

    private HomePageSectionMapper homePageSectionMapper;

    @BeforeEach
    void setUp() {
        homePageSectionMapper = new HomePageSectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHomePageSectionSample1();
        var actual = homePageSectionMapper.toEntity(homePageSectionMapper.toDto(expected));
        assertHomePageSectionAllPropertiesEquals(expected, actual);
    }
}
