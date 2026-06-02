package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.CertificateBatchAsserts.*;
import static zw.org.nmrl.ept.domain.CertificateBatchTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CertificateBatchMapperTest {

    private CertificateBatchMapper certificateBatchMapper;

    @BeforeEach
    void setUp() {
        certificateBatchMapper = new CertificateBatchMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCertificateBatchSample1();
        var actual = certificateBatchMapper.toEntity(certificateBatchMapper.toDto(expected));
        assertCertificateBatchAllPropertiesEquals(expected, actual);
    }
}
