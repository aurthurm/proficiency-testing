package zw.org.nmrl.ept.service.mapper;

import org.mapstruct.*;
import zw.org.nmrl.ept.domain.ModeOfReceipt;
import zw.org.nmrl.ept.service.dto.ModeOfReceiptDTO;

/**
 * Mapper for the entity {@link ModeOfReceipt} and its DTO {@link ModeOfReceiptDTO}.
 */
@Mapper(componentModel = "spring")
public interface ModeOfReceiptMapper extends EntityMapper<ModeOfReceiptDTO, ModeOfReceipt> {}
