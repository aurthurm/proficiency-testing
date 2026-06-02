package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SampleReferenceResultTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static SampleReferenceResult getSampleReferenceResultSample1() {
        return new SampleReferenceResult().id(1L).qualitativeResult("qualitativeResult1").unit("unit1");
    }

    public static SampleReferenceResult getSampleReferenceResultSample2() {
        return new SampleReferenceResult().id(2L).qualitativeResult("qualitativeResult2").unit("unit2");
    }

    public static SampleReferenceResult getSampleReferenceResultRandomSampleGenerator() {
        return new SampleReferenceResult()
            .id(longCount.incrementAndGet())
            .qualitativeResult(UUID.randomUUID().toString())
            .unit(UUID.randomUUID().toString());
    }
}
