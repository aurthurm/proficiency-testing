package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ApiRequestLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ApiRequestLog getApiRequestLogSample1() {
        return new ApiRequestLog()
            .id(1L)
            .transactionId("transactionId1")
            .requestedBy("requestedBy1")
            .numberOfRecords(1)
            .requestType("requestType1")
            .testType("testType1")
            .apiUrl("apiUrl1")
            .dataFormat("dataFormat1");
    }

    public static ApiRequestLog getApiRequestLogSample2() {
        return new ApiRequestLog()
            .id(2L)
            .transactionId("transactionId2")
            .requestedBy("requestedBy2")
            .numberOfRecords(2)
            .requestType("requestType2")
            .testType("testType2")
            .apiUrl("apiUrl2")
            .dataFormat("dataFormat2");
    }

    public static ApiRequestLog getApiRequestLogRandomSampleGenerator() {
        return new ApiRequestLog()
            .id(longCount.incrementAndGet())
            .transactionId(UUID.randomUUID().toString())
            .requestedBy(UUID.randomUUID().toString())
            .numberOfRecords(intCount.incrementAndGet())
            .requestType(UUID.randomUUID().toString())
            .testType(UUID.randomUUID().toString())
            .apiUrl(UUID.randomUUID().toString())
            .dataFormat(UUID.randomUUID().toString());
    }
}
