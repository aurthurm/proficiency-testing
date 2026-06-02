package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ReportConfiguration;
import zw.org.nmrl.ept.repository.ReportConfigurationRepository;
import zw.org.nmrl.ept.service.ReportConfigurationService;
import zw.org.nmrl.ept.service.dto.ReportConfigurationDTO;
import zw.org.nmrl.ept.service.mapper.ReportConfigurationMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ReportConfiguration}.
 */
@Service
@Transactional
public class ReportConfigurationServiceImpl implements ReportConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportConfigurationServiceImpl.class);

    private final ReportConfigurationRepository reportConfigurationRepository;

    private final ReportConfigurationMapper reportConfigurationMapper;

    public ReportConfigurationServiceImpl(
        ReportConfigurationRepository reportConfigurationRepository,
        ReportConfigurationMapper reportConfigurationMapper
    ) {
        this.reportConfigurationRepository = reportConfigurationRepository;
        this.reportConfigurationMapper = reportConfigurationMapper;
    }

    @Override
    public ReportConfigurationDTO save(ReportConfigurationDTO reportConfigurationDTO) {
        LOG.debug("Request to save ReportConfiguration : {}", reportConfigurationDTO);
        ReportConfiguration reportConfiguration = reportConfigurationMapper.toEntity(reportConfigurationDTO);
        reportConfiguration = reportConfigurationRepository.save(reportConfiguration);
        return reportConfigurationMapper.toDto(reportConfiguration);
    }

    @Override
    public ReportConfigurationDTO update(ReportConfigurationDTO reportConfigurationDTO) {
        LOG.debug("Request to update ReportConfiguration : {}", reportConfigurationDTO);
        ReportConfiguration reportConfiguration = reportConfigurationMapper.toEntity(reportConfigurationDTO);
        reportConfiguration = reportConfigurationRepository.save(reportConfiguration);
        return reportConfigurationMapper.toDto(reportConfiguration);
    }

    @Override
    public Optional<ReportConfigurationDTO> partialUpdate(ReportConfigurationDTO reportConfigurationDTO) {
        LOG.debug("Request to partially update ReportConfiguration : {}", reportConfigurationDTO);

        return reportConfigurationRepository
            .findById(reportConfigurationDTO.getId())
            .map(existingReportConfiguration -> {
                reportConfigurationMapper.partialUpdate(existingReportConfiguration, reportConfigurationDTO);

                return existingReportConfiguration;
            })
            .map(reportConfigurationRepository::save)
            .map(reportConfigurationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportConfigurationDTO> findAll() {
        LOG.debug("Request to get all ReportConfigurations");
        return reportConfigurationRepository
            .findAll()
            .stream()
            .map(reportConfigurationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReportConfigurationDTO> findOne(Long id) {
        LOG.debug("Request to get ReportConfiguration : {}", id);
        return reportConfigurationRepository.findById(id).map(reportConfigurationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ReportConfiguration : {}", id);
        reportConfigurationRepository.deleteById(id);
    }
}
