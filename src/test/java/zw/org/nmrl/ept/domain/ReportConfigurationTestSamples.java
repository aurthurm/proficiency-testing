package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReportConfigurationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ReportConfiguration getReportConfigurationSample1() {
        return new ReportConfiguration()
            .id(1L)
            .reportHeader("reportHeader1")
            .logo("logo1")
            .logoRight("logoRight1")
            .layout("layout1")
            .format("format1")
            .topMargin(1)
            .instituteAddressPosition("instituteAddressPosition1");
    }

    public static ReportConfiguration getReportConfigurationSample2() {
        return new ReportConfiguration()
            .id(2L)
            .reportHeader("reportHeader2")
            .logo("logo2")
            .logoRight("logoRight2")
            .layout("layout2")
            .format("format2")
            .topMargin(2)
            .instituteAddressPosition("instituteAddressPosition2");
    }

    public static ReportConfiguration getReportConfigurationRandomSampleGenerator() {
        return new ReportConfiguration()
            .id(longCount.incrementAndGet())
            .reportHeader(UUID.randomUUID().toString())
            .logo(UUID.randomUUID().toString())
            .logoRight(UUID.randomUUID().toString())
            .layout(UUID.randomUUID().toString())
            .format(UUID.randomUUID().toString())
            .topMargin(intCount.incrementAndGet())
            .instituteAddressPosition(UUID.randomUUID().toString());
    }
}
