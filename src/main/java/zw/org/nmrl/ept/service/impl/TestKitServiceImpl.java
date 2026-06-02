package zw.org.nmrl.ept.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.TestKit;
import zw.org.nmrl.ept.repository.TestKitRepository;
import zw.org.nmrl.ept.service.TestKitService;
import zw.org.nmrl.ept.service.dto.TestKitDTO;
import zw.org.nmrl.ept.service.mapper.TestKitMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.TestKit}.
 */
@Service
@Transactional
public class TestKitServiceImpl implements TestKitService {

    private static final Logger LOG = LoggerFactory.getLogger(TestKitServiceImpl.class);

    private final TestKitRepository testKitRepository;

    private final TestKitMapper testKitMapper;

    public TestKitServiceImpl(TestKitRepository testKitRepository, TestKitMapper testKitMapper) {
        this.testKitRepository = testKitRepository;
        this.testKitMapper = testKitMapper;
    }

    @Override
    public TestKitDTO save(TestKitDTO testKitDTO) {
        LOG.debug("Request to save TestKit : {}", testKitDTO);
        TestKit testKit = testKitMapper.toEntity(testKitDTO);
        testKit = testKitRepository.save(testKit);
        return testKitMapper.toDto(testKit);
    }

    @Override
    public TestKitDTO update(TestKitDTO testKitDTO) {
        LOG.debug("Request to update TestKit : {}", testKitDTO);
        TestKit testKit = testKitMapper.toEntity(testKitDTO);
        testKit = testKitRepository.save(testKit);
        return testKitMapper.toDto(testKit);
    }

    @Override
    public Optional<TestKitDTO> partialUpdate(TestKitDTO testKitDTO) {
        LOG.debug("Request to partially update TestKit : {}", testKitDTO);

        return testKitRepository
            .findById(testKitDTO.getId())
            .map(existingTestKit -> {
                testKitMapper.partialUpdate(existingTestKit, testKitDTO);

                return existingTestKit;
            })
            .map(testKitRepository::save)
            .map(testKitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TestKitDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TestKits");
        return testKitRepository.findAll(pageable).map(testKitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestKitDTO> findOne(Long id) {
        LOG.debug("Request to get TestKit : {}", id);
        return testKitRepository.findById(id).map(testKitMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TestKit : {}", id);
        testKitRepository.deleteById(id);
    }
}
