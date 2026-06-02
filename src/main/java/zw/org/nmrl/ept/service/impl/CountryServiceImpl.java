package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.Country;
import zw.org.nmrl.ept.repository.CountryRepository;
import zw.org.nmrl.ept.service.CountryService;
import zw.org.nmrl.ept.service.dto.CountryDTO;
import zw.org.nmrl.ept.service.mapper.CountryMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.Country}.
 */
@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    private static final Logger LOG = LoggerFactory.getLogger(CountryServiceImpl.class);

    private final CountryRepository countryRepository;

    private final CountryMapper countryMapper;

    public CountryServiceImpl(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    @Override
    public CountryDTO save(CountryDTO countryDTO) {
        LOG.debug("Request to save Country : {}", countryDTO);
        Country country = countryMapper.toEntity(countryDTO);
        country = countryRepository.save(country);
        return countryMapper.toDto(country);
    }

    @Override
    public CountryDTO update(CountryDTO countryDTO) {
        LOG.debug("Request to update Country : {}", countryDTO);
        Country country = countryMapper.toEntity(countryDTO);
        country = countryRepository.save(country);
        return countryMapper.toDto(country);
    }

    @Override
    public Optional<CountryDTO> partialUpdate(CountryDTO countryDTO) {
        LOG.debug("Request to partially update Country : {}", countryDTO);

        return countryRepository
            .findById(countryDTO.getId())
            .map(existingCountry -> {
                countryMapper.partialUpdate(existingCountry, countryDTO);

                return existingCountry;
            })
            .map(countryRepository::save)
            .map(countryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryDTO> findAll() {
        LOG.debug("Request to get all Countries");
        return countryRepository.findAll().stream().map(countryMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CountryDTO> findOne(Long id) {
        LOG.debug("Request to get Country : {}", id);
        return countryRepository.findById(id).map(countryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Country : {}", id);
        countryRepository.deleteById(id);
    }
}
