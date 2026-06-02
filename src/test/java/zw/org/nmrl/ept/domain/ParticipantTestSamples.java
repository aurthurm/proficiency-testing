package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParticipantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Participant getParticipantSample1() {
        return new Participant()
            .id(1L)
            .uniqueIdentifier("uniqueIdentifier1")
            .instituteName("instituteName1")
            .departmentName("departmentName1")
            .email("email1")
            .additionalEmail("additionalEmail1")
            .address("address1")
            .shippingAddress("shippingAddress1")
            .city("city1")
            .state("state1")
            .district("district1")
            .zip("zip1")
            .region("region1")
            .phone("phone1")
            .mobile("mobile1")
            .affiliation("affiliation1")
            .networkTier("networkTier1")
            .siteType("siteType1")
            .fundingSource("fundingSource1")
            .testingVolume(1L)
            .pepfarId("pepfarId1")
            .labDirectorName("labDirectorName1")
            .labDirectorEmail("labDirectorEmail1")
            .contactPersonName("contactPersonName1")
            .contactPersonEmail("contactPersonEmail1")
            .contactPersonPhone("contactPersonPhone1");
    }

    public static Participant getParticipantSample2() {
        return new Participant()
            .id(2L)
            .uniqueIdentifier("uniqueIdentifier2")
            .instituteName("instituteName2")
            .departmentName("departmentName2")
            .email("email2")
            .additionalEmail("additionalEmail2")
            .address("address2")
            .shippingAddress("shippingAddress2")
            .city("city2")
            .state("state2")
            .district("district2")
            .zip("zip2")
            .region("region2")
            .phone("phone2")
            .mobile("mobile2")
            .affiliation("affiliation2")
            .networkTier("networkTier2")
            .siteType("siteType2")
            .fundingSource("fundingSource2")
            .testingVolume(2L)
            .pepfarId("pepfarId2")
            .labDirectorName("labDirectorName2")
            .labDirectorEmail("labDirectorEmail2")
            .contactPersonName("contactPersonName2")
            .contactPersonEmail("contactPersonEmail2")
            .contactPersonPhone("contactPersonPhone2");
    }

    public static Participant getParticipantRandomSampleGenerator() {
        return new Participant()
            .id(longCount.incrementAndGet())
            .uniqueIdentifier(UUID.randomUUID().toString())
            .instituteName(UUID.randomUUID().toString())
            .departmentName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .additionalEmail(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .shippingAddress(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .district(UUID.randomUUID().toString())
            .zip(UUID.randomUUID().toString())
            .region(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .mobile(UUID.randomUUID().toString())
            .affiliation(UUID.randomUUID().toString())
            .networkTier(UUID.randomUUID().toString())
            .siteType(UUID.randomUUID().toString())
            .fundingSource(UUID.randomUUID().toString())
            .testingVolume(longCount.incrementAndGet())
            .pepfarId(UUID.randomUUID().toString())
            .labDirectorName(UUID.randomUUID().toString())
            .labDirectorEmail(UUID.randomUUID().toString())
            .contactPersonName(UUID.randomUUID().toString())
            .contactPersonEmail(UUID.randomUUID().toString())
            .contactPersonPhone(UUID.randomUUID().toString());
    }
}
