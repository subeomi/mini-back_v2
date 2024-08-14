package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gbsb.duncool.dto.EnchantDTO;

public interface EnchantService {

    EnchantDTO getEnchant(EnchantDTO dto);

    EnchantDTO createEnchant(String itemName);
}
