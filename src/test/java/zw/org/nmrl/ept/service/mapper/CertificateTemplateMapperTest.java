package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.CertificateTemplateAsserts.*;
import static zw.org.nmrl.ept.domain.CertificateTemplateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CertificateTemplateMapperTest {

    private CertificateTemplateMapper certificateTemplateMapper;

    @BeforeEach
    void setUp() {
        certificateTemplateMapper = new CertificateTemplateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCertificateTemplateSample1();
        var actual = certificateTemplateMapper.toEntity(certificateTemplateMapper.toDto(expected));
        assertCertificateTemplateAllPropertiesEquals(expected, actual);
    }
}
