package zw.org.nmrl.ept.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HomePageSectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static HomePageSection getHomePageSectionSample1() {
        return new HomePageSection()
            .id(1L)
            .section("section1")
            .type("type1")
            .title("title1")
            .link("link1")
            .fileRef("fileRef1")
            .icon("icon1")
            .displayOrder(1);
    }

    public static HomePageSection getHomePageSectionSample2() {
        return new HomePageSection()
            .id(2L)
            .section("section2")
            .type("type2")
            .title("title2")
            .link("link2")
            .fileRef("fileRef2")
            .icon("icon2")
            .displayOrder(2);
    }

    public static HomePageSection getHomePageSectionRandomSampleGenerator() {
        return new HomePageSection()
            .id(longCount.incrementAndGet())
            .section(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .link(UUID.randomUUID().toString())
            .fileRef(UUID.randomUUID().toString())
            .icon(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
