package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.UserLoginHistory;
import zw.org.nmrl.ept.repository.UserLoginHistoryRepository;
import zw.org.nmrl.ept.service.UserLoginHistoryService;
import zw.org.nmrl.ept.service.dto.UserLoginHistoryDTO;
import zw.org.nmrl.ept.service.mapper.UserLoginHistoryMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.UserLoginHistory}.
 */
@Service
@Transactional
public class UserLoginHistoryServiceImpl implements UserLoginHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(UserLoginHistoryServiceImpl.class);

    private final UserLoginHistoryRepository userLoginHistoryRepository;

    private final UserLoginHistoryMapper userLoginHistoryMapper;

    public UserLoginHistoryServiceImpl(
        UserLoginHistoryRepository userLoginHistoryRepository,
        UserLoginHistoryMapper userLoginHistoryMapper
    ) {
        this.userLoginHistoryRepository = userLoginHistoryRepository;
        this.userLoginHistoryMapper = userLoginHistoryMapper;
    }

    @Override
    public UserLoginHistoryDTO save(UserLoginHistoryDTO userLoginHistoryDTO) {
        LOG.debug("Request to save UserLoginHistory : {}", userLoginHistoryDTO);
        UserLoginHistory userLoginHistory = userLoginHistoryMapper.toEntity(userLoginHistoryDTO);
        userLoginHistory = userLoginHistoryRepository.save(userLoginHistory);
        return userLoginHistoryMapper.toDto(userLoginHistory);
    }

    @Override
    public UserLoginHistoryDTO update(UserLoginHistoryDTO userLoginHistoryDTO) {
        LOG.debug("Request to update UserLoginHistory : {}", userLoginHistoryDTO);
        UserLoginHistory userLoginHistory = userLoginHistoryMapper.toEntity(userLoginHistoryDTO);
        userLoginHistory = userLoginHistoryRepository.save(userLoginHistory);
        return userLoginHistoryMapper.toDto(userLoginHistory);
    }

    @Override
    public Optional<UserLoginHistoryDTO> partialUpdate(UserLoginHistoryDTO userLoginHistoryDTO) {
        LOG.debug("Request to partially update UserLoginHistory : {}", userLoginHistoryDTO);

        return userLoginHistoryRepository
            .findById(userLoginHistoryDTO.getId())
            .map(existingUserLoginHistory -> {
                userLoginHistoryMapper.partialUpdate(existingUserLoginHistory, userLoginHistoryDTO);

                return existingUserLoginHistory;
            })
            .map(userLoginHistoryRepository::save)
            .map(userLoginHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserLoginHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get UserLoginHistory : {}", id);
        return userLoginHistoryRepository.findById(id).map(userLoginHistoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserLoginHistory : {}", id);
        userLoginHistoryRepository.deleteById(id);
    }
}
