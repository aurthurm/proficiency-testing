package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class CertificateBatchDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CertificateBatchDTO.class);
        CertificateBatchDTO certificateBatchDTO1 = new CertificateBatchDTO();
        certificateBatchDTO1.setId(1L);
        CertificateBatchDTO certificateBatchDTO2 = new CertificateBatchDTO();
        assertThat(certificateBatchDTO1).isNotEqualTo(certificateBatchDTO2);
        certificateBatchDTO2.setId(certificateBatchDTO1.getId());
        assertThat(certificateBatchDTO1).isEqualTo(certificateBatchDTO2);
        certificateBatchDTO2.setId(2L);
        assertThat(certificateBatchDTO1).isNotEqualTo(certificateBatchDTO2);
        certificateBatchDTO1.setId(null);
        assertThat(certificateBatchDTO1).isNotEqualTo(certificateBatchDTO2);
    }
}
