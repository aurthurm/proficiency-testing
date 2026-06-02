package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CertificateTemplateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static CertificateTemplate getCertificateTemplateSample1() {
        return new CertificateTemplate().id(1L).fileRef("fileRef1");
    }

    public static CertificateTemplate getCertificateTemplateSample2() {
        return new CertificateTemplate().id(2L).fileRef("fileRef2");
    }

    public static CertificateTemplate getCertificateTemplateRandomSampleGenerator() {
        return new CertificateTemplate().id(longCount.incrementAndGet()).fileRef(UUID.randomUUID().toString());
    }
}
