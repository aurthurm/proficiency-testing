package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.FeedbackQuestion;
import zw.org.nmrl.ept.repository.FeedbackQuestionRepository;
import zw.org.nmrl.ept.service.FeedbackQuestionService;
import zw.org.nmrl.ept.service.dto.FeedbackQuestionDTO;
import zw.org.nmrl.ept.service.mapper.FeedbackQuestionMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.FeedbackQuestion}.
 */
@Service
@Transactional
public class FeedbackQuestionServiceImpl implements FeedbackQuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackQuestionServiceImpl.class);

    private final FeedbackQuestionRepository feedbackQuestionRepository;

    private final FeedbackQuestionMapper feedbackQuestionMapper;

    public FeedbackQuestionServiceImpl(
        FeedbackQuestionRepository feedbackQuestionRepository,
        FeedbackQuestionMapper feedbackQuestionMapper
    ) {
        this.feedbackQuestionRepository = feedbackQuestionRepository;
        this.feedbackQuestionMapper = feedbackQuestionMapper;
    }

    @Override
    public FeedbackQuestionDTO save(FeedbackQuestionDTO feedbackQuestionDTO) {
        LOG.debug("Request to save FeedbackQuestion : {}", feedbackQuestionDTO);
        FeedbackQuestion feedbackQuestion = feedbackQuestionMapper.toEntity(feedbackQuestionDTO);
        feedbackQuestion = feedbackQuestionRepository.save(feedbackQuestion);
        return feedbackQuestionMapper.toDto(feedbackQuestion);
    }

    @Override
    public FeedbackQuestionDTO update(FeedbackQuestionDTO feedbackQuestionDTO) {
        LOG.debug("Request to update FeedbackQuestion : {}", feedbackQuestionDTO);
        FeedbackQuestion feedbackQuestion = feedbackQuestionMapper.toEntity(feedbackQuestionDTO);
        feedbackQuestion = feedbackQuestionRepository.save(feedbackQuestion);
        return feedbackQuestionMapper.toDto(feedbackQuestion);
    }

    @Override
    public Optional<FeedbackQuestionDTO> partialUpdate(FeedbackQuestionDTO feedbackQuestionDTO) {
        LOG.debug("Request to partially update FeedbackQuestion : {}", feedbackQuestionDTO);

        return feedbackQuestionRepository
            .findById(feedbackQuestionDTO.getId())
            .map(existingFeedbackQuestion -> {
                feedbackQuestionMapper.partialUpdate(existingFeedbackQuestion, feedbackQuestionDTO);

                return existingFeedbackQuestion;
            })
            .map(feedbackQuestionRepository::save)
            .map(feedbackQuestionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackQuestionDTO> findAll() {
        LOG.debug("Request to get all FeedbackQuestions");
        return feedbackQuestionRepository
            .findAll()
            .stream()
            .map(feedbackQuestionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeedbackQuestionDTO> findOne(Long id) {
        LOG.debug("Request to get FeedbackQuestion : {}", id);
        return feedbackQuestionRepository.findById(id).map(feedbackQuestionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FeedbackQuestion : {}", id);
        feedbackQuestionRepository.deleteById(id);
    }
}
