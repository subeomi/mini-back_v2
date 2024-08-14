package org.gbsb.duncool.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.gbsb.duncool.dto.EnchantDTO;

@Mapper
public interface EnchantMapper {

    int insertEnchantInfo(EnchantDTO dto);

    int insertEnchantSlot(EnchantDTO dto);

    EnchantDTO getOneEnchant(EnchantDTO dto);
}
