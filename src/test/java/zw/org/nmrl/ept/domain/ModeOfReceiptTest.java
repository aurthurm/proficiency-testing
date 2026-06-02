package zw.org.nmrl.ept.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zw.org.nmrl.ept.domain.ModeOfReceiptTestSamples.*;

import org.junit.jupiter.api.Test;
import zw.org.nmrl.ept.web.rest.TestUtil;

class ModeOfReceiptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ModeOfReceipt.class);
        ModeOfReceipt modeOfReceipt1 = getModeOfReceiptSample1();
        ModeOfReceipt modeOfReceipt2 = new ModeOfReceipt();
        assertThat(modeOfReceipt1).isNotEqualTo(modeOfReceipt2);

        modeOfReceipt2.setId(modeOfReceipt1.getId());
        assertThat(modeOfReceipt1).isEqualTo(modeOfReceipt2);

        modeOfReceipt2 = getModeOfReceiptSample2();
        assertThat(modeOfReceipt1).isNotEqualTo(modeOfReceipt2);
    }
}
