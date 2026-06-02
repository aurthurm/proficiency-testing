package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ParticipantCriteriaTest {

    @Test
    void newParticipantCriteriaHasAllFiltersNullTest() {
        var participantCriteria = new ParticipantCriteria();
        assertThat(participantCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void participantCriteriaFluentMethodsCreatesFiltersTest() {
        var participantCriteria = new ParticipantCriteria();

        setAllFilters(participantCriteria);

        assertThat(participantCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void participantCriteriaCopyCreatesNullFilterTest() {
        var participantCriteria = new ParticipantCriteria();
        var copy = participantCriteria.copy();

        assertThat(participantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(participantCriteria)
        );
    }

    @Test
    void participantCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var participantCriteria = new ParticipantCriteria();
        setAllFilters(participantCriteria);

        var copy = participantCriteria.copy();

        assertThat(participantCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(participantCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var participantCriteria = new ParticipantCriteria();

        assertThat(participantCriteria).hasToString("ParticipantCriteria{}");
    }

    private static void setAllFilters(ParticipantCriteria participantCriteria) {
        participantCriteria.id();
        participantCriteria.uniqueIdentifier();
        participantCriteria.instituteName();
        participantCriteria.departmentName();
        participantCriteria.email();
        participantCriteria.additionalEmail();
        participantCriteria.address();
        participantCriteria.shippingAddress();
        participantCriteria.city();
        participantCriteria.state();
        participantCriteria.district();
        participantCriteria.zip();
        participantCriteria.region();
        participantCriteria.phone();
        participantCriteria.mobile();
        participantCriteria.affiliation();
        participantCriteria.networkTier();
        participantCriteria.siteType();
        participantCriteria.fundingSource();
        participantCriteria.testingVolume();
        participantCriteria.pepfarId();
        participantCriteria.latitude();
        participantCriteria.longitude();
        participantCriteria.labDirectorName();
        participantCriteria.labDirectorEmail();
        participantCriteria.contactPersonName();
        participantCriteria.contactPersonEmail();
        participantCriteria.contactPersonPhone();
        participantCriteria.status();
        participantCriteria.shipmentMapsId();
        participantCriteria.countryId();
        participantCriteria.enrollmentsId();
        participantCriteria.customValuesId();
        participantCriteria.dataManagersId();
        participantCriteria.distinct();
    }

    private static Condition<ParticipantCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getUniqueIdentifier()) &&
                condition.apply(criteria.getInstituteName()) &&
                condition.apply(criteria.getDepartmentName()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getAdditionalEmail()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getShippingAddress()) &&
                condition.apply(criteria.getCity()) &&
                condition.apply(criteria.getState()) &&
                condition.apply(criteria.getDistrict()) &&
                condition.apply(criteria.getZip()) &&
                condition.apply(criteria.getRegion()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getMobile()) &&
                condition.apply(criteria.getAffiliation()) &&
                condition.apply(criteria.getNetworkTier()) &&
                condition.apply(criteria.getSiteType()) &&
                condition.apply(criteria.getFundingSource()) &&
                condition.apply(criteria.getTestingVolume()) &&
                condition.apply(criteria.getPepfarId()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getLabDirectorName()) &&
                condition.apply(criteria.getLabDirectorEmail()) &&
                condition.apply(criteria.getContactPersonName()) &&
                condition.apply(criteria.getContactPersonEmail()) &&
                condition.apply(criteria.getContactPersonPhone()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getShipmentMapsId()) &&
                condition.apply(criteria.getCountryId()) &&
                condition.apply(criteria.getEnrollmentsId()) &&
                condition.apply(criteria.getCustomValuesId()) &&
                condition.apply(criteria.getDataManagersId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ParticipantCriteria> copyFiltersAre(ParticipantCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getUniqueIdentifier(), copy.getUniqueIdentifier()) &&
                condition.apply(criteria.getInstituteName(), copy.getInstituteName()) &&
                condition.apply(criteria.getDepartmentName(), copy.getDepartmentName()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getAdditionalEmail(), copy.getAdditionalEmail()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getShippingAddress(), copy.getShippingAddress()) &&
                condition.apply(criteria.getCity(), copy.getCity()) &&
                condition.apply(criteria.getState(), copy.getState()) &&
                condition.apply(criteria.getDistrict(), copy.getDistrict()) &&
                condition.apply(criteria.getZip(), copy.getZip()) &&
                condition.apply(criteria.getRegion(), copy.getRegion()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getMobile(), copy.getMobile()) &&
                condition.apply(criteria.getAffiliation(), copy.getAffiliation()) &&
                condition.apply(criteria.getNetworkTier(), copy.getNetworkTier()) &&
                condition.apply(criteria.getSiteType(), copy.getSiteType()) &&
                condition.apply(criteria.getFundingSource(), copy.getFundingSource()) &&
                condition.apply(criteria.getTestingVolume(), copy.getTestingVolume()) &&
                condition.apply(criteria.getPepfarId(), copy.getPepfarId()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getLabDirectorName(), copy.getLabDirectorName()) &&
                condition.apply(criteria.getLabDirectorEmail(), copy.getLabDirectorEmail()) &&
                condition.apply(criteria.getContactPersonName(), copy.getContactPersonName()) &&
                condition.apply(criteria.getContactPersonEmail(), copy.getContactPersonEmail()) &&
                condition.apply(criteria.getContactPersonPhone(), copy.getContactPersonPhone()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getShipmentMapsId(), copy.getShipmentMapsId()) &&
                condition.apply(criteria.getCountryId(), copy.getCountryId()) &&
                condition.apply(criteria.getEnrollmentsId(), copy.getEnrollmentsId()) &&
                condition.apply(criteria.getCustomValuesId(), copy.getCustomValuesId()) &&
                condition.apply(criteria.getDataManagersId(), copy.getDataManagersId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
