package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gbsb.duncool.dto.EnchantDTO;
import org.gbsb.duncool.dto.PriceInfoDTO;

public interface AuctionService {

    ObjectNode getEquipPrice(ObjectNode equip);

    ObjectNode getSwitchingPrice(ObjectNode switching, String jobId);

    ObjectNode getAvatarPrice(ObjectNode avatar);

    ObjectNode getCreaturePrice(ObjectNode creature);

    PriceInfoDTO getAucItemId(String itemId);

    PriceInfoDTO getAucEnchantDTO(EnchantDTO dto);
}
