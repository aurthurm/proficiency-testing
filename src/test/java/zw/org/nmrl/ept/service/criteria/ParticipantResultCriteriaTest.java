package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ParticipantResultCriteriaTest {

    @Test
    void newParticipantResultCriteriaHasAllFiltersNullTest() {
        var participantResultCriteria = new ParticipantResultCriteria();
        assertThat(participantResultCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void participantResultCriteriaFluentMethodsCreatesFiltersTest() {
        var participantResultCriteria = new ParticipantResultCriteria();

        setAllFilters(participantResultCriteria);

        assertThat(participantResultCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void participantResultCriteriaCopyCreatesNullFilterTest() {
        var participantResultCriteria = new ParticipantResultCriteria();
        var copy = participantResultCriteria.copy();

        assertThat(participantResultCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(participantResultCriteria)
        );
    }

    @Test
    void participantResultCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var participantResultCriteria = new ParticipantResultCriteria();
        setAllFilters(participantResultCriteria);

        var copy = participantResultCriteria.copy();

        assertThat(participantResultCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(participantResultCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var participantResultCriteria = new ParticipantResultCriteria();

        assertThat(participantResultCriteria).hasToString("ParticipantResultCriteria{}");
    }

    private static void setAllFilters(ParticipantResultCriteria participantResultCriteria) {
        participantResultCriteria.id();
        participantResultCriteria.reportedQualitativeResult();
        participantResultCriteria.reportedQuantitativeValue();
        participantResultCriteria.unit();
        participantResultCriteria.lotNumber();
        participantResultCriteria.expiryDate();
        participantResultCriteria.zScore();
        participantResultCriteria.calculatedScore();
        participantResultCriteria.comments();
        participantResultCriteria.assayId();
        participantResultCriteria.testKitId();
        participantResultCriteria.sampleId();
        participantResultCriteria.shipmentParticipantMapId();
        participantResultCriteria.distinct();
    }

    private static Condition<ParticipantResultCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReportedQualitativeResult()) &&
                condition.apply(criteria.getReportedQuantitativeValue()) &&
                condition.apply(criteria.getUnit()) &&
                condition.apply(criteria.getLotNumber()) &&
                condition.apply(criteria.getExpiryDate()) &&
                condition.apply(criteria.getzScore()) &&
                condition.apply(criteria.getCalculatedScore()) &&
                condition.apply(criteria.getComments()) &&
                condition.apply(criteria.getAssayId()) &&
                condition.apply(criteria.getTestKitId()) &&
                condition.apply(criteria.getSampleId()) &&
                condition.apply(criteria.getShipmentParticipantMapId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ParticipantResultCriteria> copyFiltersAre(
        ParticipantResultCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReportedQualitativeResult(), copy.getReportedQualitativeResult()) &&
                condition.apply(criteria.getReportedQuantitativeValue(), copy.getReportedQuantitativeValue()) &&
                condition.apply(criteria.getUnit(), copy.getUnit()) &&
                condition.apply(criteria.getLotNumber(), copy.getLotNumber()) &&
                condition.apply(criteria.getExpiryDate(), copy.getExpiryDate()) &&
                condition.apply(criteria.getzScore(), copy.getzScore()) &&
                condition.apply(criteria.getCalculatedScore(), copy.getCalculatedScore()) &&
                condition.apply(criteria.getComments(), copy.getComments()) &&
                condition.apply(criteria.getAssayId(), copy.getAssayId()) &&
                condition.apply(criteria.getTestKitId(), copy.getTestKitId()) &&
                condition.apply(criteria.getSampleId(), copy.getSampleId()) &&
                condition.apply(criteria.getShipmentParticipantMapId(), copy.getShipmentParticipantMapId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
