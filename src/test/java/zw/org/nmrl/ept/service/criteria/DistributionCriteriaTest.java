package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DistributionCriteriaTest {

    @Test
    void newDistributionCriteriaHasAllFiltersNullTest() {
        var distributionCriteria = new DistributionCriteria();
        assertThat(distributionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void distributionCriteriaFluentMethodsCreatesFiltersTest() {
        var distributionCriteria = new DistributionCriteria();

        setAllFilters(distributionCriteria);

        assertThat(distributionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void distributionCriteriaCopyCreatesNullFilterTest() {
        var distributionCriteria = new DistributionCriteria();
        var copy = distributionCriteria.copy();

        assertThat(distributionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(distributionCriteria)
        );
    }

    @Test
    void distributionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var distributionCriteria = new DistributionCriteria();
        setAllFilters(distributionCriteria);

        var copy = distributionCriteria.copy();

        assertThat(distributionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(distributionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var distributionCriteria = new DistributionCriteria();

        assertThat(distributionCriteria).hasToString("DistributionCriteria{}");
    }

    private static void setAllFilters(DistributionCriteria distributionCriteria) {
        distributionCriteria.id();
        distributionCriteria.code();
        distributionCriteria.distributionDate();
        distributionCriteria.status();
        distributionCriteria.shipmentsId();
        distributionCriteria.distinct();
    }

    private static Condition<DistributionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getDistributionDate()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getShipmentsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DistributionCriteria> copyFiltersAre(
        DistributionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getDistributionDate(), copy.getDistributionDate()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getShipmentsId(), copy.getShipmentsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
