package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShipmentParticipantMapCriteriaTest {

    @Test
    void newShipmentParticipantMapCriteriaHasAllFiltersNullTest() {
        var shipmentParticipantMapCriteria = new ShipmentParticipantMapCriteria();
        assertThat(shipmentParticipantMapCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void shipmentParticipantMapCriteriaFluentMethodsCreatesFiltersTest() {
        var shipmentParticipantMapCriteria = new ShipmentParticipantMapCriteria();

        setAllFilters(shipmentParticipantMapCriteria);

        assertThat(shipmentParticipantMapCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void shipmentParticipantMapCriteriaCopyCreatesNullFilterTest() {
        var shipmentParticipantMapCriteria = new ShipmentParticipantMapCriteria();
        var copy = shipmentParticipantMapCriteria.copy();

        assertThat(shipmentParticipantMapCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(shipmentParticipantMapCriteria)
        );
    }

    @Test
    void shipmentParticipantMapCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shipmentParticipantMapCriteria = new ShipmentParticipantMapCriteria();
        setAllFilters(shipmentParticipantMapCriteria);

        var copy = shipmentParticipantMapCriteria.copy();

        assertThat(shipmentParticipantMapCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(shipmentParticipantMapCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shipmentParticipantMapCriteria = new ShipmentParticipantMapCriteria();

        assertThat(shipmentParticipantMapCriteria).hasToString("ShipmentParticipantMapCriteria{}");
    }

    private static void setAllFilters(ShipmentParticipantMapCriteria shipmentParticipantMapCriteria) {
        shipmentParticipantMapCriteria.id();
        shipmentParticipantMapCriteria.responseStatus();
        shipmentParticipantMapCriteria.shipmentReceiptDate();
        shipmentParticipantMapCriteria.shipmentTestDate();
        shipmentParticipantMapCriteria.shipmentTestReportDate();
        shipmentParticipantMapCriteria.submittedAt();
        shipmentParticipantMapCriteria.evaluatedAt();
        shipmentParticipantMapCriteria.isExcluded();
        shipmentParticipantMapCriteria.isResponseLate();
        shipmentParticipantMapCriteria.isPtTestNotPerformed();
        shipmentParticipantMapCriteria.ptTestNotPerformedComments();
        shipmentParticipantMapCriteria.supervisorApproved();
        shipmentParticipantMapCriteria.participantSupervisor();
        shipmentParticipantMapCriteria.userComment();
        shipmentParticipantMapCriteria.shipmentScore();
        shipmentParticipantMapCriteria.documentationScore();
        shipmentParticipantMapCriteria.finalResult();
        shipmentParticipantMapCriteria.evaluationComment();
        shipmentParticipantMapCriteria.isFollowup();
        shipmentParticipantMapCriteria.manualOverride();
        shipmentParticipantMapCriteria.qcStatus();
        shipmentParticipantMapCriteria.qcDate();
        shipmentParticipantMapCriteria.qcDoneBy();
        shipmentParticipantMapCriteria.syncedToMobile();
        shipmentParticipantMapCriteria.syncedOn();
        shipmentParticipantMapCriteria.resultsId();
        shipmentParticipantMapCriteria.capaRecordsId();
        shipmentParticipantMapCriteria.modeOfReceiptId();
        shipmentParticipantMapCriteria.notTestedReasonId();
        shipmentParticipantMapCriteria.shipmentId();
        shipmentParticipantMapCriteria.participantId();
        shipmentParticipantMapCriteria.distinct();
    }

    private static Condition<ShipmentParticipantMapCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getResponseStatus()) &&
                condition.apply(criteria.getShipmentReceiptDate()) &&
                condition.apply(criteria.getShipmentTestDate()) &&
                condition.apply(criteria.getShipmentTestReportDate()) &&
                condition.apply(criteria.getSubmittedAt()) &&
                condition.apply(criteria.getEvaluatedAt()) &&
                condition.apply(criteria.getIsExcluded()) &&
                condition.apply(criteria.getIsResponseLate()) &&
                condition.apply(criteria.getIsPtTestNotPerformed()) &&
                condition.apply(criteria.getPtTestNotPerformedComments()) &&
                condition.apply(criteria.getSupervisorApproved()) &&
                condition.apply(criteria.getParticipantSupervisor()) &&
                condition.apply(criteria.getUserComment()) &&
                condition.apply(criteria.getShipmentScore()) &&
                condition.apply(criteria.getDocumentationScore()) &&
                condition.apply(criteria.getFinalResult()) &&
                condition.apply(criteria.getEvaluationComment()) &&
                condition.apply(criteria.getIsFollowup()) &&
                condition.apply(criteria.getManualOverride()) &&
                condition.apply(criteria.getQcStatus()) &&
                condition.apply(criteria.getQcDate()) &&
                condition.apply(criteria.getQcDoneBy()) &&
                condition.apply(criteria.getSyncedToMobile()) &&
                condition.apply(criteria.getSyncedOn()) &&
                condition.apply(criteria.getResultsId()) &&
                condition.apply(criteria.getCapaRecordsId()) &&
                condition.apply(criteria.getModeOfReceiptId()) &&
                condition.apply(criteria.getNotTestedReasonId()) &&
                condition.apply(criteria.getShipmentId()) &&
                condition.apply(criteria.getParticipantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ShipmentParticipantMapCriteria> copyFiltersAre(
        ShipmentParticipantMapCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getResponseStatus(), copy.getResponseStatus()) &&
                condition.apply(criteria.getShipmentReceiptDate(), copy.getShipmentReceiptDate()) &&
                condition.apply(criteria.getShipmentTestDate(), copy.getShipmentTestDate()) &&
                condition.apply(criteria.getShipmentTestReportDate(), copy.getShipmentTestReportDate()) &&
                condition.apply(criteria.getSubmittedAt(), copy.getSubmittedAt()) &&
                condition.apply(criteria.getEvaluatedAt(), copy.getEvaluatedAt()) &&
                condition.apply(criteria.getIsExcluded(), copy.getIsExcluded()) &&
                condition.apply(criteria.getIsResponseLate(), copy.getIsResponseLate()) &&
                condition.apply(criteria.getIsPtTestNotPerformed(), copy.getIsPtTestNotPerformed()) &&
                condition.apply(criteria.getPtTestNotPerformedComments(), copy.getPtTestNotPerformedComments()) &&
                condition.apply(criteria.getSupervisorApproved(), copy.getSupervisorApproved()) &&
                condition.apply(criteria.getParticipantSupervisor(), copy.getParticipantSupervisor()) &&
                condition.apply(criteria.getUserComment(), copy.getUserComment()) &&
                condition.apply(criteria.getShipmentScore(), copy.getShipmentScore()) &&
                condition.apply(criteria.getDocumentationScore(), copy.getDocumentationScore()) &&
                condition.apply(criteria.getFinalResult(), copy.getFinalResult()) &&
                condition.apply(criteria.getEvaluationComment(), copy.getEvaluationComment()) &&
                condition.apply(criteria.getIsFollowup(), copy.getIsFollowup()) &&
                condition.apply(criteria.getManualOverride(), copy.getManualOverride()) &&
                condition.apply(criteria.getQcStatus(), copy.getQcStatus()) &&
                condition.apply(criteria.getQcDate(), copy.getQcDate()) &&
                condition.apply(criteria.getQcDoneBy(), copy.getQcDoneBy()) &&
                condition.apply(criteria.getSyncedToMobile(), copy.getSyncedToMobile()) &&
                condition.apply(criteria.getSyncedOn(), copy.getSyncedOn()) &&
                condition.apply(criteria.getResultsId(), copy.getResultsId()) &&
                condition.apply(criteria.getCapaRecordsId(), copy.getCapaRecordsId()) &&
                condition.apply(criteria.getModeOfReceiptId(), copy.getModeOfReceiptId()) &&
                condition.apply(criteria.getNotTestedReasonId(), copy.getNotTestedReasonId()) &&
                condition.apply(criteria.getShipmentId(), copy.getShipmentId()) &&
                condition.apply(criteria.getParticipantId(), copy.getParticipantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
