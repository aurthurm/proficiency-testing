package zw.org.nmrl.ept.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import zw.org.nmrl.ept.domain.enumeration.ContentStatus;

/**
 * A DTO for the {@link zw.org.nmrl.ept.domain.FeedbackQuestion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FeedbackQuestionDTO implements Serializable {

    private Long id;

    @NotNull
    private String questionText;

    private Integer displayOrder;

    @NotNull
    private ContentStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeedbackQuestionDTO)) {
            return false;
        }

        FeedbackQuestionDTO feedbackQuestionDTO = (FeedbackQuestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, feedbackQuestionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FeedbackQuestionDTO{" +
            "id=" + getId() +
            ", questionText='" + getQuestionText() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
