package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShipmentCriteriaTest {

    @Test
    void newShipmentCriteriaHasAllFiltersNullTest() {
        var shipmentCriteria = new ShipmentCriteria();
        assertThat(shipmentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void shipmentCriteriaFluentMethodsCreatesFiltersTest() {
        var shipmentCriteria = new ShipmentCriteria();

        setAllFilters(shipmentCriteria);

        assertThat(shipmentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void shipmentCriteriaCopyCreatesNullFilterTest() {
        var shipmentCriteria = new ShipmentCriteria();
        var copy = shipmentCriteria.copy();

        assertThat(shipmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(shipmentCriteria)
        );
    }

    @Test
    void shipmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shipmentCriteria = new ShipmentCriteria();
        setAllFilters(shipmentCriteria);

        var copy = shipmentCriteria.copy();

        assertThat(shipmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(shipmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shipmentCriteria = new ShipmentCriteria();

        assertThat(shipmentCriteria).hasToString("ShipmentCriteria{}");
    }

    private static void setAllFilters(ShipmentCriteria shipmentCriteria) {
        shipmentCriteria.id();
        shipmentCriteria.code();
        shipmentCriteria.shipmentDate();
        shipmentCriteria.responseDeadline();
        shipmentCriteria.responsesOpen();
        shipmentCriteria.autoCloseAtDeadline();
        shipmentCriteria.allowEditingResponse();
        shipmentCriteria.issuingAuthority();
        shipmentCriteria.coordinatorName();
        shipmentCriteria.coordinatorEmail();
        shipmentCriteria.coordinatorPhone();
        shipmentCriteria.numberOfSamples();
        shipmentCriteria.maxScore();
        shipmentCriteria.status();
        shipmentCriteria.reportsGeneratedAt();
        shipmentCriteria.finalizedAt();
        shipmentCriteria.samplesId();
        shipmentCriteria.participantMapsId();
        shipmentCriteria.distributionId();
        shipmentCriteria.schemeId();
        shipmentCriteria.certificateBatchesId();
        shipmentCriteria.distinct();
    }

    private static Condition<ShipmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getShipmentDate()) &&
                condition.apply(criteria.getResponseDeadline()) &&
                condition.apply(criteria.getResponsesOpen()) &&
                condition.apply(criteria.getAutoCloseAtDeadline()) &&
                condition.apply(criteria.getAllowEditingResponse()) &&
                condition.apply(criteria.getIssuingAuthority()) &&
                condition.apply(criteria.getCoordinatorName()) &&
                condition.apply(criteria.getCoordinatorEmail()) &&
                condition.apply(criteria.getCoordinatorPhone()) &&
                condition.apply(criteria.getNumberOfSamples()) &&
                condition.apply(criteria.getMaxScore()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getReportsGeneratedAt()) &&
                condition.apply(criteria.getFinalizedAt()) &&
                condition.apply(criteria.getSamplesId()) &&
                condition.apply(criteria.getParticipantMapsId()) &&
                condition.apply(criteria.getDistributionId()) &&
                condition.apply(criteria.getSchemeId()) &&
                condition.apply(criteria.getCertificateBatchesId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ShipmentCriteria> copyFiltersAre(ShipmentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getShipmentDate(), copy.getShipmentDate()) &&
                condition.apply(criteria.getResponseDeadline(), copy.getResponseDeadline()) &&
                condition.apply(criteria.getResponsesOpen(), copy.getResponsesOpen()) &&
                condition.apply(criteria.getAutoCloseAtDeadline(), copy.getAutoCloseAtDeadline()) &&
                condition.apply(criteria.getAllowEditingResponse(), copy.getAllowEditingResponse()) &&
                condition.apply(criteria.getIssuingAuthority(), copy.getIssuingAuthority()) &&
                condition.apply(criteria.getCoordinatorName(), copy.getCoordinatorName()) &&
                condition.apply(criteria.getCoordinatorEmail(), copy.getCoordinatorEmail()) &&
                condition.apply(criteria.getCoordinatorPhone(), copy.getCoordinatorPhone()) &&
                condition.apply(criteria.getNumberOfSamples(), copy.getNumberOfSamples()) &&
                condition.apply(criteria.getMaxScore(), copy.getMaxScore()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getReportsGeneratedAt(), copy.getReportsGeneratedAt()) &&
                condition.apply(criteria.getFinalizedAt(), copy.getFinalizedAt()) &&
                condition.apply(criteria.getSamplesId(), copy.getSamplesId()) &&
                condition.apply(criteria.getParticipantMapsId(), copy.getParticipantMapsId()) &&
                condition.apply(criteria.getDistributionId(), copy.getDistributionId()) &&
                condition.apply(criteria.getSchemeId(), copy.getSchemeId()) &&
                condition.apply(criteria.getCertificateBatchesId(), copy.getCertificateBatchesId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
