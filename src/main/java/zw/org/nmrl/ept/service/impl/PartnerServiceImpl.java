package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.Partner;
import zw.org.nmrl.ept.repository.PartnerRepository;
import zw.org.nmrl.ept.service.PartnerService;
import zw.org.nmrl.ept.service.dto.PartnerDTO;
import zw.org.nmrl.ept.service.mapper.PartnerMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.Partner}.
 */
@Service
@Transactional
public class PartnerServiceImpl implements PartnerService {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerServiceImpl.class);

    private final PartnerRepository partnerRepository;

    private final PartnerMapper partnerMapper;

    public PartnerServiceImpl(PartnerRepository partnerRepository, PartnerMapper partnerMapper) {
        this.partnerRepository = partnerRepository;
        this.partnerMapper = partnerMapper;
    }

    @Override
    public PartnerDTO save(PartnerDTO partnerDTO) {
        LOG.debug("Request to save Partner : {}", partnerDTO);
        Partner partner = partnerMapper.toEntity(partnerDTO);
        partner = partnerRepository.save(partner);
        return partnerMapper.toDto(partner);
    }

    @Override
    public PartnerDTO update(PartnerDTO partnerDTO) {
        LOG.debug("Request to update Partner : {}", partnerDTO);
        Partner partner = partnerMapper.toEntity(partnerDTO);
        partner = partnerRepository.save(partner);
        return partnerMapper.toDto(partner);
    }

    @Override
    public Optional<PartnerDTO> partialUpdate(PartnerDTO partnerDTO) {
        LOG.debug("Request to partially update Partner : {}", partnerDTO);

        return partnerRepository
            .findById(partnerDTO.getId())
            .map(existingPartner -> {
                partnerMapper.partialUpdate(existingPartner, partnerDTO);

                return existingPartner;
            })
            .map(partnerRepository::save)
            .map(partnerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerDTO> findAll() {
        LOG.debug("Request to get all Partners");
        return partnerRepository.findAll().stream().map(partnerMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PartnerDTO> findOne(Long id) {
        LOG.debug("Request to get Partner : {}", id);
        return partnerRepository.findById(id).map(partnerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Partner : {}", id);
        partnerRepository.deleteById(id);
    }
}
