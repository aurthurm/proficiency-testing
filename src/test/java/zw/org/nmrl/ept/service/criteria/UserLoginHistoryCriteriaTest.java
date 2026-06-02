package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UserLoginHistoryCriteriaTest {

    @Test
    void newUserLoginHistoryCriteriaHasAllFiltersNullTest() {
        var userLoginHistoryCriteria = new UserLoginHistoryCriteria();
        assertThat(userLoginHistoryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void userLoginHistoryCriteriaFluentMethodsCreatesFiltersTest() {
        var userLoginHistoryCriteria = new UserLoginHistoryCriteria();

        setAllFilters(userLoginHistoryCriteria);

        assertThat(userLoginHistoryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void userLoginHistoryCriteriaCopyCreatesNullFilterTest() {
        var userLoginHistoryCriteria = new UserLoginHistoryCriteria();
        var copy = userLoginHistoryCriteria.copy();

        assertThat(userLoginHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(userLoginHistoryCriteria)
        );
    }

    @Test
    void userLoginHistoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var userLoginHistoryCriteria = new UserLoginHistoryCriteria();
        setAllFilters(userLoginHistoryCriteria);

        var copy = userLoginHistoryCriteria.copy();

        assertThat(userLoginHistoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(userLoginHistoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var userLoginHistoryCriteria = new UserLoginHistoryCriteria();

        assertThat(userLoginHistoryCriteria).hasToString("UserLoginHistoryCriteria{}");
    }

    private static void setAllFilters(UserLoginHistoryCriteria userLoginHistoryCriteria) {
        userLoginHistoryCriteria.id();
        userLoginHistoryCriteria.loginId();
        userLoginHistoryCriteria.loginContext();
        userLoginHistoryCriteria.loginStatus();
        userLoginHistoryCriteria.attemptedAt();
        userLoginHistoryCriteria.ipAddress();
        userLoginHistoryCriteria.browser();
        userLoginHistoryCriteria.operatingSystem();
        userLoginHistoryCriteria.sessionHash();
        userLoginHistoryCriteria.distinct();
    }

    private static Condition<UserLoginHistoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLoginId()) &&
                condition.apply(criteria.getLoginContext()) &&
                condition.apply(criteria.getLoginStatus()) &&
                condition.apply(criteria.getAttemptedAt()) &&
                condition.apply(criteria.getIpAddress()) &&
                condition.apply(criteria.getBrowser()) &&
                condition.apply(criteria.getOperatingSystem()) &&
                condition.apply(criteria.getSessionHash()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<UserLoginHistoryCriteria> copyFiltersAre(
        UserLoginHistoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLoginId(), copy.getLoginId()) &&
                condition.apply(criteria.getLoginContext(), copy.getLoginContext()) &&
                condition.apply(criteria.getLoginStatus(), copy.getLoginStatus()) &&
                condition.apply(criteria.getAttemptedAt(), copy.getAttemptedAt()) &&
                condition.apply(criteria.getIpAddress(), copy.getIpAddress()) &&
                condition.apply(criteria.getBrowser(), copy.getBrowser()) &&
                condition.apply(criteria.getOperatingSystem(), copy.getOperatingSystem()) &&
                condition.apply(criteria.getSessionHash(), copy.getSessionHash()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
