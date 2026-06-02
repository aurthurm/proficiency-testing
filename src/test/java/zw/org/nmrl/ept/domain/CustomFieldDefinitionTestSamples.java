package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CustomFieldDefinitionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CustomFieldDefinition getCustomFieldDefinitionSample1() {
        return new CustomFieldDefinition().id(1L).fieldKey("fieldKey1").label("label1").displayOrder(1);
    }

    public static CustomFieldDefinition getCustomFieldDefinitionSample2() {
        return new CustomFieldDefinition().id(2L).fieldKey("fieldKey2").label("label2").displayOrder(2);
    }

    public static CustomFieldDefinition getCustomFieldDefinitionRandomSampleGenerator() {
        return new CustomFieldDefinition()
            .id(longCount.incrementAndGet())
            .fieldKey(UUID.randomUUID().toString())
            .label(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
