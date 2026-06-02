package zw.org.nmrl.ept.service.mapper;

import static zw.org.nmrl.ept.domain.ModeOfReceiptAsserts.*;
import static zw.org.nmrl.ept.domain.ModeOfReceiptTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModeOfReceiptMapperTest {

    private ModeOfReceiptMapper modeOfReceiptMapper;

    @BeforeEach
    void setUp() {
        modeOfReceiptMapper = new ModeOfReceiptMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getModeOfReceiptSample1();
        var actual = modeOfReceiptMapper.toEntity(modeOfReceiptMapper.toDto(expected));
        assertModeOfReceiptAllPropertiesEquals(expected, actual);
    }
}
