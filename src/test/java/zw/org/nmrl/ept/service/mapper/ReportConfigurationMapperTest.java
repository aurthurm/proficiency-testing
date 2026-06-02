package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ReportConfigurationAsserts.*;
import static zw.org.nmrl.ept.domain.ReportConfigurationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportConfigurationMapperTest {

    private ReportConfigurationMapper reportConfigurationMapper;

    @BeforeEach
    void setUp() {
        reportConfigurationMapper = new ReportConfigurationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReportConfigurationSample1();
        var actual = reportConfigurationMapper.toEntity(reportConfigurationMapper.toDto(expected));
        assertReportConfigurationAllPropertiesEquals(expected, actual);
    }
}
