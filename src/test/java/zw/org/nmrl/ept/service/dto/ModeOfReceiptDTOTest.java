package zw.org.nmrl.ept.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ModeOfReceiptDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ModeOfReceiptDTO.class);
        ModeOfReceiptDTO modeOfReceiptDTO1 = new ModeOfReceiptDTO();
        modeOfReceiptDTO1.setId(1L);
        ModeOfReceiptDTO modeOfReceiptDTO2 = new ModeOfReceiptDTO();
        assertThat(modeOfReceiptDTO1).isNotEqualTo(modeOfReceiptDTO2);
        modeOfReceiptDTO2.setId(modeOfReceiptDTO1.getId());
        assertThat(modeOfReceiptDTO1).isEqualTo(modeOfReceiptDTO2);
        modeOfReceiptDTO2.setId(2L);
        assertThat(modeOfReceiptDTO1).isNotEqualTo(modeOfReceiptDTO2);
        modeOfReceiptDTO1.setId(null);
        assertThat(modeOfReceiptDTO1).isNotEqualTo(modeOfReceiptDTO2);
    }
}
