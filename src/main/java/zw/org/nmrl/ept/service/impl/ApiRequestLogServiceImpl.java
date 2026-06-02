package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ApiRequestLog;
import zw.org.nmrl.ept.repository.ApiRequestLogRepository;
import zw.org.nmrl.ept.service.ApiRequestLogService;
import zw.org.nmrl.ept.service.dto.ApiRequestLogDTO;
import zw.org.nmrl.ept.service.mapper.ApiRequestLogMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ApiRequestLog}.
 */
@Service
@Transactional
public class ApiRequestLogServiceImpl implements ApiRequestLogService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiRequestLogServiceImpl.class);

    private final ApiRequestLogRepository apiRequestLogRepository;

    private final ApiRequestLogMapper apiRequestLogMapper;

    public ApiRequestLogServiceImpl(ApiRequestLogRepository apiRequestLogRepository, ApiRequestLogMapper apiRequestLogMapper) {
        this.apiRequestLogRepository = apiRequestLogRepository;
        this.apiRequestLogMapper = apiRequestLogMapper;
    }

    @Override
    public ApiRequestLogDTO save(ApiRequestLogDTO apiRequestLogDTO) {
        LOG.debug("Request to save ApiRequestLog : {}", apiRequestLogDTO);
        ApiRequestLog apiRequestLog = apiRequestLogMapper.toEntity(apiRequestLogDTO);
        apiRequestLog = apiRequestLogRepository.save(apiRequestLog);
        return apiRequestLogMapper.toDto(apiRequestLog);
    }

    @Override
    public ApiRequestLogDTO update(ApiRequestLogDTO apiRequestLogDTO) {
        LOG.debug("Request to update ApiRequestLog : {}", apiRequestLogDTO);
        ApiRequestLog apiRequestLog = apiRequestLogMapper.toEntity(apiRequestLogDTO);
        apiRequestLog = apiRequestLogRepository.save(apiRequestLog);
        return apiRequestLogMapper.toDto(apiRequestLog);
    }

    @Override
    public Optional<ApiRequestLogDTO> partialUpdate(ApiRequestLogDTO apiRequestLogDTO) {
        LOG.debug("Request to partially update ApiRequestLog : {}", apiRequestLogDTO);

        return apiRequestLogRepository
            .findById(apiRequestLogDTO.getId())
            .map(existingApiRequestLog -> {
                apiRequestLogMapper.partialUpdate(existingApiRequestLog, apiRequestLogDTO);

                return existingApiRequestLog;
            })
            .map(apiRequestLogRepository::save)
            .map(apiRequestLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApiRequestLogDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ApiRequestLogs");
        return apiRequestLogRepository.findAll(pageable).map(apiRequestLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiRequestLogDTO> findOne(Long id) {
        LOG.debug("Request to get ApiRequestLog : {}", id);
        return apiRequestLogRepository.findById(id).map(apiRequestLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ApiRequestLog : {}", id);
        apiRequestLogRepository.deleteById(id);
    }
}
