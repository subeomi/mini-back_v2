package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gbsb.duncool.dto.EnchantDTO;
import org.gbsb.duncool.dto.PriceInfoDTO;

public interface AuctionService {

    JsonNode getEquipPrice(JsonNode equip);

    ObjectNode getSwitchingPrice(ObjectNode switching, String jobId);

    JsonNode getAvatarPrice(JsonNode avatar);

    JsonNode getCreaturePrice(JsonNode creature);

    PriceInfoDTO getAucItemId(String itemId);

    PriceInfoDTO getAucEnchantDTO(EnchantDTO dto);
}
