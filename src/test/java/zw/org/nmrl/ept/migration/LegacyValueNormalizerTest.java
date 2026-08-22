package zw.org.nmrl.ept.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import org.junit.jupiter.api.Test;

class LegacyValueNormalizerTest {

    @Test
    void preservesMysqlWallClockValuesWithoutTimezoneConversion() throws Exception {
        assertThat(LegacyValueNormalizer.normalize(Timestamp.valueOf("2026-08-22 11:42:03"))).isEqualTo("2026-08-22T11:42:03");
        assertThat(LegacyValueNormalizer.normalize(Date.valueOf("2026-08-22"))).isEqualTo("2026-08-22");
        assertThat(LegacyValueNormalizer.normalize(Time.valueOf("11:42:03"))).isEqualTo("11:42:03");
    }

    @Test
    void base64EncodesBinaryValues() throws Exception {
        assertThat(LegacyValueNormalizer.normalize(new byte[] { 0, 1, 2, 3 })).isEqualTo("AAECAw==");
    }
}
