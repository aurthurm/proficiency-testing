package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.HomePageSection;
import zw.org.nmrl.ept.repository.HomePageSectionRepository;
import zw.org.nmrl.ept.service.HomePageSectionService;
import zw.org.nmrl.ept.service.dto.HomePageSectionDTO;
import zw.org.nmrl.ept.service.mapper.HomePageSectionMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.HomePageSection}.
 */
@Service
@Transactional
public class HomePageSectionServiceImpl implements HomePageSectionService {

    private static final Logger LOG = LoggerFactory.getLogger(HomePageSectionServiceImpl.class);

    private final HomePageSectionRepository homePageSectionRepository;

    private final HomePageSectionMapper homePageSectionMapper;

    public HomePageSectionServiceImpl(HomePageSectionRepository homePageSectionRepository, HomePageSectionMapper homePageSectionMapper) {
        this.homePageSectionRepository = homePageSectionRepository;
        this.homePageSectionMapper = homePageSectionMapper;
    }

    @Override
    public HomePageSectionDTO save(HomePageSectionDTO homePageSectionDTO) {
        LOG.debug("Request to save HomePageSection : {}", homePageSectionDTO);
        HomePageSection homePageSection = homePageSectionMapper.toEntity(homePageSectionDTO);
        homePageSection = homePageSectionRepository.save(homePageSection);
        return homePageSectionMapper.toDto(homePageSection);
    }

    @Override
    public HomePageSectionDTO update(HomePageSectionDTO homePageSectionDTO) {
        LOG.debug("Request to update HomePageSection : {}", homePageSectionDTO);
        HomePageSection homePageSection = homePageSectionMapper.toEntity(homePageSectionDTO);
        homePageSection = homePageSectionRepository.save(homePageSection);
        return homePageSectionMapper.toDto(homePageSection);
    }

    @Override
    public Optional<HomePageSectionDTO> partialUpdate(HomePageSectionDTO homePageSectionDTO) {
        LOG.debug("Request to partially update HomePageSection : {}", homePageSectionDTO);

        return homePageSectionRepository
            .findById(homePageSectionDTO.getId())
            .map(existingHomePageSection -> {
                homePageSectionMapper.partialUpdate(existingHomePageSection, homePageSectionDTO);

                return existingHomePageSection;
            })
            .map(homePageSectionRepository::save)
            .map(homePageSectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomePageSectionDTO> findAll() {
        LOG.debug("Request to get all HomePageSections");
        return homePageSectionRepository
            .findAll()
            .stream()
            .map(homePageSectionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HomePageSectionDTO> findOne(Long id) {
        LOG.debug("Request to get HomePageSection : {}", id);
        return homePageSectionRepository.findById(id).map(homePageSectionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete HomePageSection : {}", id);
        homePageSectionRepository.deleteById(id);
    }
}
