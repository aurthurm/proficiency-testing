package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.FeedbackQuestion;
import zw.org.nmrl.ept.service.dto.FeedbackQuestionDTO;

/**
 * Mapper for the entity {@link FeedbackQuestion} and its DTO {@link FeedbackQuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface FeedbackQuestionMapper extends EntityMapper<FeedbackQuestionDTO, FeedbackQuestion> {}
