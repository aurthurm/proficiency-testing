package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParticipantResultTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ParticipantResult getParticipantResultSample1() {
        return new ParticipantResult()
            .id(1L)
            .reportedQualitativeResult("reportedQualitativeResult1")
            .unit("unit1")
            .lotNumber("lotNumber1")
            .comments("comments1");
    }

    public static ParticipantResult getParticipantResultSample2() {
        return new ParticipantResult()
            .id(2L)
            .reportedQualitativeResult("reportedQualitativeResult2")
            .unit("unit2")
            .lotNumber("lotNumber2")
            .comments("comments2");
    }

    public static ParticipantResult getParticipantResultRandomSampleGenerator() {
        return new ParticipantResult()
            .id(longCount.incrementAndGet())
            .reportedQualitativeResult(UUID.randomUUID().toString())
            .unit(UUID.randomUUID().toString())
            .lotNumber(UUID.randomUUID().toString())
            .comments(UUID.randomUUID().toString());
    }
}
