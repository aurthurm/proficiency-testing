package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.DataManager;
import zw.org.nmrl.ept.repository.DataManagerRepository;
import zw.org.nmrl.ept.service.DataManagerService;
import zw.org.nmrl.ept.service.dto.DataManagerDTO;
import zw.org.nmrl.ept.service.mapper.DataManagerMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.DataManager}.
 */
@Service
@Transactional
public class DataManagerServiceImpl implements DataManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(DataManagerServiceImpl.class);

    private final DataManagerRepository dataManagerRepository;

    private final DataManagerMapper dataManagerMapper;

    public DataManagerServiceImpl(DataManagerRepository dataManagerRepository, DataManagerMapper dataManagerMapper) {
        this.dataManagerRepository = dataManagerRepository;
        this.dataManagerMapper = dataManagerMapper;
    }

    @Override
    public DataManagerDTO save(DataManagerDTO dataManagerDTO) {
        LOG.debug("Request to save DataManager : {}", dataManagerDTO);
        DataManager dataManager = dataManagerMapper.toEntity(dataManagerDTO);
        dataManager = dataManagerRepository.save(dataManager);
        return dataManagerMapper.toDto(dataManager);
    }

    @Override
    public DataManagerDTO update(DataManagerDTO dataManagerDTO) {
        LOG.debug("Request to update DataManager : {}", dataManagerDTO);
        DataManager dataManager = dataManagerMapper.toEntity(dataManagerDTO);
        dataManager = dataManagerRepository.save(dataManager);
        return dataManagerMapper.toDto(dataManager);
    }

    @Override
    public Optional<DataManagerDTO> partialUpdate(DataManagerDTO dataManagerDTO) {
        LOG.debug("Request to partially update DataManager : {}", dataManagerDTO);

        return dataManagerRepository
            .findById(dataManagerDTO.getId())
            .map(existingDataManager -> {
                dataManagerMapper.partialUpdate(existingDataManager, dataManagerDTO);

                return existingDataManager;
            })
            .map(dataManagerRepository::save)
            .map(dataManagerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DataManagerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DataManagers");
        return dataManagerRepository.findAll(pageable).map(dataManagerMapper::toDto);
    }

    public Page<DataManagerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return dataManagerRepository.findAllWithEagerRelationships(pageable).map(dataManagerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DataManagerDTO> findOne(Long id) {
        LOG.debug("Request to get DataManager : {}", id);
        return dataManagerRepository.findOneWithEagerRelationships(id).map(dataManagerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DataManager : {}", id);
        dataManagerRepository.deleteById(id);
    }
}
