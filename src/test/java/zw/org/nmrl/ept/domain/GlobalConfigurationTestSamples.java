package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GlobalConfigurationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static GlobalConfiguration getGlobalConfigurationSample1() {
        return new GlobalConfiguration().id(1L).configKey("configKey1").description("description1");
    }

    public static GlobalConfiguration getGlobalConfigurationSample2() {
        return new GlobalConfiguration().id(2L).configKey("configKey2").description("description2");
    }

    public static GlobalConfiguration getGlobalConfigurationRandomSampleGenerator() {
        return new GlobalConfiguration()
            .id(longCount.incrementAndGet())
            .configKey(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
