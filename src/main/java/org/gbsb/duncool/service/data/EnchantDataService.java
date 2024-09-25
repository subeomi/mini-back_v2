package org.gbsb.duncool.service.data;

import lombok.RequiredArgsConstructor;
import org.gbsb.duncool.dto.EnchantDTO;
import org.gbsb.duncool.mappers.EnchantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnchantDataService {

    private final EnchantMapper enchantMapper;

    @Transactional
    public void insertEnchantInfoData(EnchantDTO dto) {
        enchantMapper.insertEnchantInfo(dto);
    }

    @Transactional
    public void insertEnchantSlotData(EnchantDTO dto) {
        enchantMapper.insertEnchantSlot(dto);
    }
}
