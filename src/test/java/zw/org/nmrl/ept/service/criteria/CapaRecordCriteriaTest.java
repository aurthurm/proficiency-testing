package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CapaRecordCriteriaTest {

    @Test
    void newCapaRecordCriteriaHasAllFiltersNullTest() {
        var capaRecordCriteria = new CapaRecordCriteria();
        assertThat(capaRecordCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void capaRecordCriteriaFluentMethodsCreatesFiltersTest() {
        var capaRecordCriteria = new CapaRecordCriteria();

        setAllFilters(capaRecordCriteria);

        assertThat(capaRecordCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void capaRecordCriteriaCopyCreatesNullFilterTest() {
        var capaRecordCriteria = new CapaRecordCriteria();
        var copy = capaRecordCriteria.copy();

        assertThat(capaRecordCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(capaRecordCriteria)
        );
    }

    @Test
    void capaRecordCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var capaRecordCriteria = new CapaRecordCriteria();
        setAllFilters(capaRecordCriteria);

        var copy = capaRecordCriteria.copy();

        assertThat(capaRecordCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(capaRecordCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var capaRecordCriteria = new CapaRecordCriteria();

        assertThat(capaRecordCriteria).hasToString("CapaRecordCriteria{}");
    }

    private static void setAllFilters(CapaRecordCriteria capaRecordCriteria) {
        capaRecordCriteria.id();
        capaRecordCriteria.rootCause();
        capaRecordCriteria.actionTaken();
        capaRecordCriteria.actionDate();
        capaRecordCriteria.status();
        capaRecordCriteria.followUpDate();
        capaRecordCriteria.correctiveActionId();
        capaRecordCriteria.shipmentParticipantMapId();
        capaRecordCriteria.distinct();
    }

    private static Condition<CapaRecordCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRootCause()) &&
                condition.apply(criteria.getActionTaken()) &&
                condition.apply(criteria.getActionDate()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getFollowUpDate()) &&
                condition.apply(criteria.getCorrectiveActionId()) &&
                condition.apply(criteria.getShipmentParticipantMapId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CapaRecordCriteria> copyFiltersAre(CapaRecordCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRootCause(), copy.getRootCause()) &&
                condition.apply(criteria.getActionTaken(), copy.getActionTaken()) &&
                condition.apply(criteria.getActionDate(), copy.getActionDate()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getFollowUpDate(), copy.getFollowUpDate()) &&
                condition.apply(criteria.getCorrectiveActionId(), copy.getCorrectiveActionId()) &&
                condition.apply(criteria.getShipmentParticipantMapId(), copy.getShipmentParticipantMapId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
