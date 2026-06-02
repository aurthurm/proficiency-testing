package zw.org.nmrl.ept.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.org.nmrl.ept.domain.ModeOfReceipt;
import zw.org.nmrl.ept.repository.ModeOfReceiptRepository;
import zw.org.nmrl.ept.service.ModeOfReceiptService;
import zw.org.nmrl.ept.service.dto.ModeOfReceiptDTO;
import zw.org.nmrl.ept.service.mapper.ModeOfReceiptMapper;

/**
 * Service Implementation for managing {@link zw.org.nmrl.ept.domain.ModeOfReceipt}.
 */
@Service
@Transactional
public class ModeOfReceiptServiceImpl implements ModeOfReceiptService {

    private static final Logger LOG = LoggerFactory.getLogger(ModeOfReceiptServiceImpl.class);

    private final ModeOfReceiptRepository modeOfReceiptRepository;

    private final ModeOfReceiptMapper modeOfReceiptMapper;

    public ModeOfReceiptServiceImpl(ModeOfReceiptRepository modeOfReceiptRepository, ModeOfReceiptMapper modeOfReceiptMapper) {
        this.modeOfReceiptRepository = modeOfReceiptRepository;
        this.modeOfReceiptMapper = modeOfReceiptMapper;
    }

    @Override
    public ModeOfReceiptDTO save(ModeOfReceiptDTO modeOfReceiptDTO) {
        LOG.debug("Request to save ModeOfReceipt : {}", modeOfReceiptDTO);
        ModeOfReceipt modeOfReceipt = modeOfReceiptMapper.toEntity(modeOfReceiptDTO);
        modeOfReceipt = modeOfReceiptRepository.save(modeOfReceipt);
        return modeOfReceiptMapper.toDto(modeOfReceipt);
    }

    @Override
    public ModeOfReceiptDTO update(ModeOfReceiptDTO modeOfReceiptDTO) {
        LOG.debug("Request to update ModeOfReceipt : {}", modeOfReceiptDTO);
        ModeOfReceipt modeOfReceipt = modeOfReceiptMapper.toEntity(modeOfReceiptDTO);
        modeOfReceipt = modeOfReceiptRepository.save(modeOfReceipt);
        return modeOfReceiptMapper.toDto(modeOfReceipt);
    }

    @Override
    public Optional<ModeOfReceiptDTO> partialUpdate(ModeOfReceiptDTO modeOfReceiptDTO) {
        LOG.debug("Request to partially update ModeOfReceipt : {}", modeOfReceiptDTO);

        return modeOfReceiptRepository
            .findById(modeOfReceiptDTO.getId())
            .map(existingModeOfReceipt -> {
                modeOfReceiptMapper.partialUpdate(existingModeOfReceipt, modeOfReceiptDTO);

                return existingModeOfReceipt;
            })
            .map(modeOfReceiptRepository::save)
            .map(modeOfReceiptMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeOfReceiptDTO> findAll() {
        LOG.debug("Request to get all ModeOfReceipts");
        return modeOfReceiptRepository.findAll().stream().map(modeOfReceiptMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ModeOfReceiptDTO> findOne(Long id) {
        LOG.debug("Request to get ModeOfReceipt : {}", id);
        return modeOfReceiptRepository.findById(id).map(modeOfReceiptMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ModeOfReceipt : {}", id);
        modeOfReceiptRepository.deleteById(id);
    }
}
