package org.gbsb.duncool.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.gbsb.duncool.dto.EnchantDTO;

import java.util.List;

@Mapper
public interface EnchantMapper {

    int insertEnchantInfo(EnchantDTO dto);

    int insertEnchantSlot(EnchantDTO dto);

    EnchantDTO getOneEnchant(EnchantDTO dto);

    List<EnchantDTO> getEnchantBySlot(String slotName);
}
