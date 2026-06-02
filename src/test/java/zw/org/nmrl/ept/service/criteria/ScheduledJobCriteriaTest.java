package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ScheduledJobCriteriaTest {

    @Test
    void newScheduledJobCriteriaHasAllFiltersNullTest() {
        var scheduledJobCriteria = new ScheduledJobCriteria();
        assertThat(scheduledJobCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void scheduledJobCriteriaFluentMethodsCreatesFiltersTest() {
        var scheduledJobCriteria = new ScheduledJobCriteria();

        setAllFilters(scheduledJobCriteria);

        assertThat(scheduledJobCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void scheduledJobCriteriaCopyCreatesNullFilterTest() {
        var scheduledJobCriteria = new ScheduledJobCriteria();
        var copy = scheduledJobCriteria.copy();

        assertThat(scheduledJobCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduledJobCriteria)
        );
    }

    @Test
    void scheduledJobCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var scheduledJobCriteria = new ScheduledJobCriteria();
        setAllFilters(scheduledJobCriteria);

        var copy = scheduledJobCriteria.copy();

        assertThat(scheduledJobCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(scheduledJobCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var scheduledJobCriteria = new ScheduledJobCriteria();

        assertThat(scheduledJobCriteria).hasToString("ScheduledJobCriteria{}");
    }

    private static void setAllFilters(ScheduledJobCriteria scheduledJobCriteria) {
        scheduledJobCriteria.id();
        scheduledJobCriteria.jobType();
        scheduledJobCriteria.status();
        scheduledJobCriteria.requestedBy();
        scheduledJobCriteria.requestedOn();
        scheduledJobCriteria.startedAt();
        scheduledJobCriteria.lastHeartbeat();
        scheduledJobCriteria.completedAt();
        scheduledJobCriteria.progressCompleted();
        scheduledJobCriteria.progressTotal();
        scheduledJobCriteria.distinct();
    }

    private static Condition<ScheduledJobCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getJobType()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRequestedBy()) &&
                condition.apply(criteria.getRequestedOn()) &&
                condition.apply(criteria.getStartedAt()) &&
                condition.apply(criteria.getLastHeartbeat()) &&
                condition.apply(criteria.getCompletedAt()) &&
                condition.apply(criteria.getProgressCompleted()) &&
                condition.apply(criteria.getProgressTotal()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ScheduledJobCriteria> copyFiltersAre(
        ScheduledJobCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getJobType(), copy.getJobType()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRequestedBy(), copy.getRequestedBy()) &&
                condition.apply(criteria.getRequestedOn(), copy.getRequestedOn()) &&
                condition.apply(criteria.getStartedAt(), copy.getStartedAt()) &&
                condition.apply(criteria.getLastHeartbeat(), copy.getLastHeartbeat()) &&
                condition.apply(criteria.getCompletedAt(), copy.getCompletedAt()) &&
                condition.apply(criteria.getProgressCompleted(), copy.getProgressCompleted()) &&
                condition.apply(criteria.getProgressTotal(), copy.getProgressTotal()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
