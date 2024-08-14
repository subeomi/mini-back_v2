package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.dto.ItemReinforceDTO;

public interface ItemService {

    ItemInfoDTO getOneItem(String itemId);

    String getIdByName(String itemName);

    void checkItemInfo(String itemId);

    JsonNode getReinforceItem(String itemId, String jobId);
}
