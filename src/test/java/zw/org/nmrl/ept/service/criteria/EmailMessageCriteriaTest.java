package zw.org.nmrl.ept.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EmailMessageCriteriaTest {

    @Test
    void newEmailMessageCriteriaHasAllFiltersNullTest() {
        var emailMessageCriteria = new EmailMessageCriteria();
        assertThat(emailMessageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void emailMessageCriteriaFluentMethodsCreatesFiltersTest() {
        var emailMessageCriteria = new EmailMessageCriteria();

        setAllFilters(emailMessageCriteria);

        assertThat(emailMessageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void emailMessageCriteriaCopyCreatesNullFilterTest() {
        var emailMessageCriteria = new EmailMessageCriteria();
        var copy = emailMessageCriteria.copy();

        assertThat(emailMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(emailMessageCriteria)
        );
    }

    @Test
    void emailMessageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var emailMessageCriteria = new EmailMessageCriteria();
        setAllFilters(emailMessageCriteria);

        var copy = emailMessageCriteria.copy();

        assertThat(emailMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(emailMessageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var emailMessageCriteria = new EmailMessageCriteria();

        assertThat(emailMessageCriteria).hasToString("EmailMessageCriteria{}");
    }

    private static void setAllFilters(EmailMessageCriteria emailMessageCriteria) {
        emailMessageCriteria.id();
        emailMessageCriteria.fromEmail();
        emailMessageCriteria.fromName();
        emailMessageCriteria.replyTo();
        emailMessageCriteria.toEmail();
        emailMessageCriteria.cc();
        emailMessageCriteria.bcc();
        emailMessageCriteria.subject();
        emailMessageCriteria.attachmentRef();
        emailMessageCriteria.status();
        emailMessageCriteria.failureType();
        emailMessageCriteria.failureReason();
        emailMessageCriteria.queuedOn();
        emailMessageCriteria.sentAt();
        emailMessageCriteria.retryCount();
        emailMessageCriteria.templateId();
        emailMessageCriteria.distinct();
    }

    private static Condition<EmailMessageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFromEmail()) &&
                condition.apply(criteria.getFromName()) &&
                condition.apply(criteria.getReplyTo()) &&
                condition.apply(criteria.getToEmail()) &&
                condition.apply(criteria.getCc()) &&
                condition.apply(criteria.getBcc()) &&
                condition.apply(criteria.getSubject()) &&
                condition.apply(criteria.getAttachmentRef()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getFailureType()) &&
                condition.apply(criteria.getFailureReason()) &&
                condition.apply(criteria.getQueuedOn()) &&
                condition.apply(criteria.getSentAt()) &&
                condition.apply(criteria.getRetryCount()) &&
                condition.apply(criteria.getTemplateId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EmailMessageCriteria> copyFiltersAre(
        EmailMessageCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFromEmail(), copy.getFromEmail()) &&
                condition.apply(criteria.getFromName(), copy.getFromName()) &&
                condition.apply(criteria.getReplyTo(), copy.getReplyTo()) &&
                condition.apply(criteria.getToEmail(), copy.getToEmail()) &&
                condition.apply(criteria.getCc(), copy.getCc()) &&
                condition.apply(criteria.getBcc(), copy.getBcc()) &&
                condition.apply(criteria.getSubject(), copy.getSubject()) &&
                condition.apply(criteria.getAttachmentRef(), copy.getAttachmentRef()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getFailureType(), copy.getFailureType()) &&
                condition.apply(criteria.getFailureReason(), copy.getFailureReason()) &&
                condition.apply(criteria.getQueuedOn(), copy.getQueuedOn()) &&
                condition.apply(criteria.getSentAt(), copy.getSentAt()) &&
                condition.apply(criteria.getRetryCount(), copy.getRetryCount()) &&
                condition.apply(criteria.getTemplateId(), copy.getTemplateId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
