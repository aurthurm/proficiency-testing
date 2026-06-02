package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserLoginHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static UserLoginHistory getUserLoginHistorySample1() {
        return new UserLoginHistory()
            .id(1L)
            .loginId("loginId1")
            .loginContext("loginContext1")
            .ipAddress("ipAddress1")
            .browser("browser1")
            .operatingSystem("operatingSystem1")
            .sessionHash("sessionHash1");
    }

    public static UserLoginHistory getUserLoginHistorySample2() {
        return new UserLoginHistory()
            .id(2L)
            .loginId("loginId2")
            .loginContext("loginContext2")
            .ipAddress("ipAddress2")
            .browser("browser2")
            .operatingSystem("operatingSystem2")
            .sessionHash("sessionHash2");
    }

    public static UserLoginHistory getUserLoginHistoryRandomSampleGenerator() {
        return new UserLoginHistory()
            .id(longCount.incrementAndGet())
            .loginId(UUID.randomUUID().toString())
            .loginContext(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString())
            .browser(UUID.randomUUID().toString())
            .operatingSystem(UUID.randomUUID().toString())
            .sessionHash(UUID.randomUUID().toString());
    }
}
